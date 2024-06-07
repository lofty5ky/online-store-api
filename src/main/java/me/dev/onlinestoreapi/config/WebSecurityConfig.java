package me.dev.onlinestoreapi.config;

import lombok.RequiredArgsConstructor;
import me.dev.onlinestoreapi.constant.Constant;
import me.dev.onlinestoreapi.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    @Value("${api.prefix}")
    private String apiPrefix;
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(request -> {
                    request.requestMatchers(String.format("%s/users/register", apiPrefix),
                                    String.format("%s/users/login", apiPrefix)).permitAll()

                            .requestMatchers(HttpMethod.GET,
                                    String.format("%s/roles**", apiPrefix)).permitAll()

                            .requestMatchers(HttpMethod.GET,
                                    String.format("%s/categories**", apiPrefix)).permitAll()

                            .requestMatchers(HttpMethod.GET,
                                    String.format("%s/categories/**", apiPrefix)).permitAll()

                            .requestMatchers(HttpMethod.POST,
                                    String.format("%s/categories/**", apiPrefix)).hasAnyRole(Constant.ADMIN_ROLE)

                            .requestMatchers(HttpMethod.PUT,
                                    String.format("%s/categories/**", apiPrefix)).hasAnyRole(Constant.ADMIN_ROLE)

                            .requestMatchers(HttpMethod.DELETE,
                                    String.format("%s/categories/**", apiPrefix)).hasAnyRole(Constant.ADMIN_ROLE)

                            .requestMatchers(HttpMethod.GET,
                                    String.format("%s/products**", apiPrefix)).permitAll()

                            .requestMatchers(HttpMethod.GET,
                                    String.format("%s/products/**", apiPrefix)).permitAll()

                            .requestMatchers(HttpMethod.GET,
                                    String.format("%s/products/images/*", apiPrefix)).permitAll()

                            .requestMatchers(HttpMethod.POST,
                                    String.format("%s/products**", apiPrefix)).hasAnyRole(Constant.ADMIN_ROLE)

                            .requestMatchers(HttpMethod.PUT,
                                    String.format("%s/products/**", apiPrefix)).hasAnyRole(Constant.ADMIN_ROLE)

                            .requestMatchers(HttpMethod.DELETE,
                                    String.format("%s/products/**", apiPrefix)).hasAnyRole(Constant.ADMIN_ROLE)


                            .requestMatchers(HttpMethod.POST,
                                    String.format("%s/orders/**", apiPrefix)).hasAnyRole(Constant.USER_ROLE)

                            .requestMatchers(HttpMethod.GET,
                                    String.format("%s/orders/**", apiPrefix)).permitAll()

                            .requestMatchers(HttpMethod.PUT,
                                    String.format("%s/orders/**", apiPrefix)).hasRole(Constant.ADMIN_ROLE)

                            .requestMatchers(HttpMethod.DELETE,
                                    String.format("%s/orders/**", apiPrefix)).hasRole(Constant.ADMIN_ROLE)

                            .requestMatchers(HttpMethod.POST,
                                    String.format("%s/order_details/**", apiPrefix)).hasAnyRole(Constant.USER_ROLE)

                            .requestMatchers(HttpMethod.GET,
                                    String.format("%s/order_details/**", apiPrefix)).permitAll()

                            .requestMatchers(HttpMethod.PUT,
                                    String.format("%s/order_details/**", apiPrefix)).hasRole(Constant.ADMIN_ROLE)

                            .requestMatchers(HttpMethod.DELETE,
                                    String.format("%s/order_details/**", apiPrefix)).hasRole(Constant.ADMIN_ROLE)
                            .anyRequest().authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable);

        httpSecurity.cors(httpSecurityCorsConfigurer -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(List.of("*"));
            configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
            configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
            configuration.setExposedHeaders(List.of("x-auth-token"));
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);
            httpSecurityCorsConfigurer.configurationSource(source);
        });

        return httpSecurity.build();
    }
}
