package it.smartcommunitylabdhub.core.config;

import it.smartcommunitylabdhub.commons.config.SecurityProperties;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
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
    SecurityProperties properties;

    @Value("${security.api.cors.origins}")
    private String corsOrigins;

    @Value("${management.server.port}")
    private int managementPort;

    @Value("${management.endpoints.web.base-path}")
    private String managementBasePath;

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
            if (properties.isBasicAuthEnabled()) {
                securityChain
                    .httpBasic(basic -> basic.authenticationEntryPoint(new Http403ForbiddenEntryPoint()))
                    .userDetailsService(userDetailsService());
            }
            if (properties.isJwtAuthEnabled()) {
                securityChain.oauth2ResourceServer(oauth2 ->
                    oauth2.jwt(jwt -> jwt.decoder(jwtDecoder()).jwtAuthenticationConverter(jwtAuthenticationConverter())
                    )
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

    public UserDetailsService userDetailsService() {
        //create admin user with full permissions
        UserDetails admin = User
            .withDefaultPasswordEncoder()
            .username(properties.getBasic().getUsername())
            .password(properties.getBasic().getPassword())
            .roles("ADMIN", "USER")
            .build();

        return new InMemoryUserDetailsManager(admin);
    }

    private JwtDecoder jwtDecoder() {
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

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
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
