package it.smartcommunitylabdhub.core.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import it.smartcommunitylabdhub.authorization.config.KeyStoreConfig;
import it.smartcommunitylabdhub.authorization.services.AuthorizableAwareEntityService;
import it.smartcommunitylabdhub.commons.config.ApplicationProperties;
import it.smartcommunitylabdhub.commons.config.SecurityProperties;
import it.smartcommunitylabdhub.commons.config.SecurityProperties.JwtAuthenticationProperties;
import it.smartcommunitylabdhub.commons.models.entities.project.Project;
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

    @Autowired
    AuthorizableAwareEntityService<Project> projectAuthHelper;

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
            JwtAuthenticationProvider coreJwtAuthProvider = new JwtAuthenticationProvider(
                coreJwtDecoder(
                    applicationProperties.getEndpoint(),
                    applicationProperties.getName(),
                    keyStoreConfig.getJWKSetKeyStore().getJwk()
                )
            );
            coreJwtAuthProvider.setJwtAuthenticationConverter(coreJwtAuthenticationConverter("authorities"));

            // Create authentication Manager
            securityChain.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt -> jwt.authenticationManager(new ProviderManager(coreJwtAuthProvider)))
            );

            if (properties.isJwtAuthEnabled()) {
                JwtAuthenticationProperties jwtProps = properties.getJwt();
                // rebuild auth manager to include external jwt provider
                JwtAuthenticationProvider externalJwtAuthProvider = new JwtAuthenticationProvider(
                    externalJwtDecoder(jwtProps.getIssuerUri(), jwtProps.getAudience())
                );

                externalJwtAuthProvider.setJwtAuthenticationConverter(
                    externalJwtAuthenticationConverter(jwtProps.getClaim(), projectAuthHelper)
                );

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

    /**
     * Internal basic auth
     */
    public static UserDetailsService userDetailsService(String username, String password) {
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

    public static JwtDecoder coreJwtDecoder(String issuer, String audience, JWK jwk) throws JOSEException {
        //we support only RSA keys
        if (!(jwk instanceof RSAKey)) {
            throw new IllegalArgumentException("the provided key is not suitable for token authentication");
        }

        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(jwk.toRSAKey().toRSAPublicKey()).build();

        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);

        OAuth2TokenValidator<Jwt> audienceValidator = new JwtClaimValidator<List<String>>(
            JwtClaimNames.AUD,
            (aud -> aud != null && aud.contains(audience))
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

    public static JwtAuthenticationConverter coreJwtAuthenticationConverter(String claim) {
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

            //TODO evaluate refreshing project authorities via helper

            return authorities;
        });
        return converter;
    }

    /**
     * External auth via JWT
     */
    public static JwtDecoder externalJwtDecoder(String issuer, String audience) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withIssuerLocation(issuer).build();

        OAuth2TokenValidator<Jwt> audienceValidator = new JwtClaimValidator<List<String>>(
            JwtClaimNames.AUD,
            (aud -> aud != null && aud.contains(audience))
        );

        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);
        jwtDecoder.setJwtValidator(withAudience);

        return jwtDecoder;
    }

    public static JwtAuthenticationConverter externalJwtAuthenticationConverter(
        String claim,
        AuthorizableAwareEntityService<Project> projectAuthHelper
    ) {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter((Jwt source) -> {
            if (source == null) return null;

            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            //read roles from token
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

            //inject roles from ownership of projects
            if (projectAuthHelper != null && StringUtils.hasText(source.getSubject())) {
                projectAuthHelper
                    .findIdsByCreatedBy(source.getSubject())
                    .forEach(p -> {
                        //derive a scoped ADMIN role
                        authorities.add(new SimpleGrantedAuthority(p + ":ROLE_ADMIN"));
                    });
            }
            return authorities;
        });
        return converter;
    }
}
