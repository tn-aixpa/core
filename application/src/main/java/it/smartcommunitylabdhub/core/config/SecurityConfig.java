package it.smartcommunitylabdhub.core.config;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

    @Bean("apiSecurityFilterChain")
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher(getRequestMatcher())
            .authorizeHttpRequests(auth -> {
                // if (properties.isRequired()) {
                auth
                    .requestMatchers(getRequestMatcher())
                    .hasRole("USER")
                    .requestMatchers("/error")
                    .permitAll()
                    .anyRequest()
                    .authenticated();
                // } else {
                //     auth.anyRequest().permitAll();
                // }
            })
            // disable request cache
            .requestCache(requestCache -> requestCache.disable())
            .exceptionHandling(handling -> {
                handling
                    .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
                    .accessDeniedHandler(new AccessDeniedHandlerImpl()); // use 403
            })
            //disable csrf
            .csrf(csrf -> csrf.disable())
            // we don't want a session for these endpoints, each request should be evaluated
            .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // allow cors
        http.cors(cors -> {
            if (StringUtils.hasText(corsOrigins)) {
                cors.configurationSource(corsConfigurationSource(corsOrigins));
            } else {
                cors.disable();
            }
        });

        //authentication (when configured)
        if (properties.isRequired()) {
            if (properties.isBasicAuthEnabled()) {
                http.httpBasic(withDefaults()).userDetailsService(userDetailsService());
            }
            if (properties.isJwtAuthEnabled()) {
                http.oauth2ResourceServer(oauth2 ->
                    oauth2.jwt(jwt -> jwt.decoder(jwtDecoder()).jwtAuthenticationConverter(jwtAuthenticationConverter())
                    )
                );
            }
        } else {
            //assign both USER and ADMIN to anon user to bypass all scoped permission checks
            http.anonymous(anon -> anon.authorities("ROLE_USER", "ROLE_ADMIN"));
        }

        return http.build();
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
                        if (r.contains(":")) {
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

    @Bean
    public SecurityFilterChain coreSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> {
                auth.anyRequest().permitAll();
            })
            // disable request cache
            .requestCache(requestCache -> requestCache.disable())
            .build();
    }

    public RequestMatcher getRequestMatcher() {
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
}
