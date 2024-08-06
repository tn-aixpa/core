package it.smartcommunitylabdhub.core.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import it.smartcommunitylabdhub.authorization.config.KeyStoreConfig;
import it.smartcommunitylabdhub.commons.config.ApplicationProperties;
import it.smartcommunitylabdhub.commons.config.SecurityProperties;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@Slf4j
public class SecurityConfig {

    public static final String API_PREFIX = "/api";

    @Autowired
    ApplicationProperties applicationProperties;

    @Autowired
    SecurityProperties properties;

    @Value("${security.api.cors.origins}")
    private String corsOrigins;

    @Value("${management.server.port}")
    private int managementPort;

    @Value("${management.endpoints.web.base-path}")
    private String managementBasePath;

    @Value("${jwt.client-id}")
    private String clientId;

    @Value("${jwt.client-secret}")
    private String clientSecret;

    @Autowired
    KeyStoreConfig keyStoreConfig;

    @Bean("apiSecurityFilterChain")
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        HttpSecurity securityChain = http
            .securityMatcher(getApiRequestMatcher())
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers(getApiRequestMatcher()).hasRole("USER").anyRequest().authenticated();
            })
            // disable request cache
            .requestCache(requestCache -> requestCache.disable())
            //disable csrf
            .csrf(csrf -> csrf.disable())
            // we don't want a session for these endpoints, each request should be evaluated
            .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // allow cors
        securityChain.cors(cors -> {
            if (StringUtils.hasText(corsOrigins)) {
                cors.configurationSource(corsConfigurationSource(corsOrigins));
            } else {
                cors.disable();
            }
        });

        //authentication (when configured)
        if (properties.isRequired()) {
            //always enable internal jwt auth provider
            JwtAuthenticationProvider coreJwtAuthProvider = new JwtAuthenticationProvider(coreJwtDecoder());
            coreJwtAuthProvider.setJwtAuthenticationConverter(coreJwtAuthenticationConverter());

            // Create authentication Manager
            securityChain.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt -> jwt.authenticationManager(new ProviderManager(coreJwtAuthProvider)))
            );

            if (properties.isJwtAuthEnabled()) {
                // rebuild auth manager to include external jwt provider
                JwtAuthenticationProvider externalJwtAuthProvider = new JwtAuthenticationProvider(externalJwtDecoder());

                externalJwtAuthProvider.setJwtAuthenticationConverter(externalJwtAuthenticationConverter());

                securityChain.oauth2ResourceServer(oauth2 ->
                    oauth2.jwt(jwt ->
                        jwt.authenticationManager(new ProviderManager(coreJwtAuthProvider, externalJwtAuthProvider))
                    )
                );
            }

            //enable basic if required
            if (properties.isBasicAuthEnabled()) {
                securityChain
                    .httpBasic(basic -> basic.authenticationEntryPoint(new Http403ForbiddenEntryPoint()))
                    .userDetailsService(
                        userDetailsService(properties.getBasic().getUsername(), properties.getBasic().getPassword())
                    );
            }

            //disable anonymous
            securityChain.anonymous(anon -> anon.disable());
        } else {
            //assign both USER and ADMIN to anon user to bypass all scoped permission checks
            securityChain.anonymous(anon -> {
                anon.authorities("ROLE_USER", "ROLE_ADMIN");
                anon.principal("anonymous");
            });
        }

        securityChain.exceptionHandling(handling -> {
            handling
                .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
                .accessDeniedHandler(new AccessDeniedHandlerImpl()); // use 403
        });

        return securityChain.build();
    }

    /**
     * Internal basic auth
     */
    public UserDetailsService userDetailsService(String username, String password) {
        //create admin user with full permissions
        UserDetails admin = User
            .withDefaultPasswordEncoder()
            .username(username)
            .password(password)
            .roles("ADMIN", "USER")
            .build();

        return new InMemoryUserDetailsManager(admin);
    }

    /**
     * Internal auth via JWT
     */

    private JwtDecoder coreJwtDecoder() throws JOSEException {
        JWK jwk = keyStoreConfig.getJWKSetKeyStore().getJwk();

        //we support only RSA keys
        if (!(jwk instanceof RSAKey)) {
            throw new IllegalArgumentException("the provided key is not suitable for token authentication");
        }

        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(jwk.toRSAKey().toRSAPublicKey()).build();

        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(
            applicationProperties.getEndpoint()
        );

        OAuth2TokenValidator<Jwt> audienceValidator = new JwtClaimValidator<List<String>>(
            JwtClaimNames.AUD,
            (aud -> aud != null && aud.contains(applicationProperties.getName()))
        );

        //access tokens *do not contain* at_hash, those are refresh
        OAuth2TokenValidator<Jwt> accessTokenValidator = new JwtClaimValidator<String>(
            IdTokenClaimNames.AT_HASH,
            (Objects::isNull)
        );

        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(
            withIssuer,
            audienceValidator,
            accessTokenValidator
        );
        jwtDecoder.setJwtValidator(validator);

        return jwtDecoder;
    }

    private JwtAuthenticationConverter coreJwtAuthenticationConverter() {
        String claim = "authorities";
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter((Jwt source) -> {
            if (source == null) return null;

            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            if (StringUtils.hasText(claim) && source.hasClaim(claim)) {
                List<String> roles = source.getClaimAsStringList(claim);
                if (roles != null) {
                    roles.forEach(r -> {
                        //use as is
                        authorities.add(new SimpleGrantedAuthority(r));
                    });
                }
            }

            return authorities;
        });
        return converter;
    }

    /**
     * External auth via JWT
     */
    private JwtDecoder externalJwtDecoder() {
        SecurityProperties.JwtAuthenticationProperties jwtProps = properties.getJwt();
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withIssuerLocation(jwtProps.getIssuerUri()).build();

        OAuth2TokenValidator<Jwt> audienceValidator = new JwtClaimValidator<List<String>>(
            JwtClaimNames.AUD,
            (aud -> aud != null && aud.contains(jwtProps.getAudience()))
        );

        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(jwtProps.getIssuerUri());
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);
        jwtDecoder.setJwtValidator(withAudience);

        return jwtDecoder;
    }

    private JwtAuthenticationConverter externalJwtAuthenticationConverter() {
        SecurityProperties.JwtAuthenticationProperties jwtProps = properties.getJwt();
        String claim = jwtProps.getClaim();
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter((Jwt source) -> {
            if (source == null) return null;

            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            if (StringUtils.hasText(claim) && source.hasClaim(claim)) {
                List<String> roles = source.getClaimAsStringList(claim);
                if (roles != null) {
                    roles.forEach(r -> {
                        if ("ROLE_ADMIN".equals(r) || r.contains(":")) {
                            //use as is
                            authorities.add(new SimpleGrantedAuthority(r));
                        } else {
                            //derive a scoped USER role
                            authorities.add(new SimpleGrantedAuthority(r + ":ROLE_USER"));
                        }
                    });
                }
            }

            return authorities;
        });
        return converter;
    }

    @Bean("authSecurityFilterChain")
    public SecurityFilterChain authSecurityFilterChain(HttpSecurity http) throws Exception {
        HttpSecurity securityChain = http
            .securityMatcher(getAuthRequestMatcher())
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers(getAuthRequestMatcher()).hasRole("USER").anyRequest().authenticated();
            })
            // disable request cache
            .requestCache(requestCache -> requestCache.disable())
            //disable csrf
            .csrf(csrf -> csrf.disable())
            // we don't want a session for these endpoints, each request should be evaluated
            .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // allow cors
        securityChain.cors(cors -> {
            if (StringUtils.hasText(corsOrigins)) {
                cors.configurationSource(corsConfigurationSource(corsOrigins));
            } else {
                cors.disable();
            }
        });

        //authentication (when configured)
        if (StringUtils.hasText(clientId) && StringUtils.hasText(clientSecret)) {
            //enable basic
            securityChain
                .httpBasic(basic -> basic.authenticationEntryPoint(new Http403ForbiddenEntryPoint()))
                .userDetailsService(userDetailsService(clientId, clientSecret));
        }

        //assign both USER and ADMIN to anon user to bypass all scoped permission checks
        securityChain.anonymous(anon -> {
            anon.authorities("ROLE_USER", "ROLE_ADMIN");
            anon.principal("anonymous");
        });

        securityChain.exceptionHandling(handling -> {
            handling
                .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
                .accessDeniedHandler(new AccessDeniedHandlerImpl()); // use 403
        });

        return securityChain.build();
    }

    @Bean("h2SecurityFilterChain")
    public SecurityFilterChain h2SecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher(new AntPathRequestMatcher("/h2-console/**"))
            .authorizeHttpRequests(auth -> {
                auth.anyRequest().permitAll();
            })
            //disable csrf
            .csrf(csrf -> csrf.disable())
            // we don't want a session for these endpoints, each request should be evaluated
            .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // enable frame options
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .build();
    }

    @Bean("coreSecurityFilterChain")
    public SecurityFilterChain coreSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> {
                auth.anyRequest().permitAll();
            })
            // disable request cache
            .requestCache(requestCache -> requestCache.disable())
            .build();
    }

    @Bean("monitoringSecurityFilterChain")
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // match only actuator endpoints
        return http
            .securityMatcher(getManagementRequestMatcher())
            .authorizeHttpRequests(auth -> {
                auth.anyRequest().permitAll();
            })
            .exceptionHandling(handling -> handling.authenticationEntryPoint(new Http403ForbiddenEntryPoint()))
            .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .build();
    }

    public RequestMatcher getManagementRequestMatcher() {
        return (HttpServletRequest request) -> managementPort == request.getLocalPort();
    }

    public RequestMatcher getApiRequestMatcher() {
        return new AntPathRequestMatcher(API_PREFIX + "/**");
    }

    public RequestMatcher getAuthRequestMatcher() {
        return new AntPathRequestMatcher("/auth/**");
    }

    private CorsConfigurationSource corsConfigurationSource(String origins) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(new ArrayList<>(StringUtils.commaDelimitedListToSet(origins)));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("authorization", "range"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication()).map(a -> a.getName());
    }
}
