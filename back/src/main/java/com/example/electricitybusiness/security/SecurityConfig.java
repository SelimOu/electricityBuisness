package com.example.electricitybusiness.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final SimpleCorsFilter simpleCorsFilter;

    public SecurityConfig(JwtFilter jwtFilter, SimpleCorsFilter simpleCorsFilter) {
        this.jwtFilter = jwtFilter;
        this.simpleCorsFilter = simpleCorsFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // Do not delegate CORS handling to Spring Security here; use a servlet filter (SimpleCorsFilter)
    // that short-circuits OPTIONS preflight and sets Access-Control headers.
    http.csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            // Allow CORS preflight via CorsUtils helper
            .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
            // Also permit OPTIONS explicitly as a fallback
            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            // Public endpoints (read-only) used by the frontend map and listings
            .antMatchers(HttpMethod.GET, "/api/bornes", "/api/lieux", "/api/medias").permitAll()
            // Authentication endpoints remain public
            .antMatchers("/api/auth/**").permitAll()
            // (no temporary admin-permit in production)
            // Everything else requires authentication
            .anyRequest().authenticated();

    // Register our SimpleCorsFilter explicitly in the security chain before the UsernamePasswordAuthenticationFilter
    // so preflight OPTIONS requests are handled and returned immediately.
    http.addFilterBefore(simpleCorsFilter, UsernamePasswordAuthenticationFilter.class);
    http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // SimpleCorsFilter (a @Component) will handle CORS preflight and set headers.
    // We avoid defining CorsConfigurationSource/WebMvcConfigurer to prevent Spring
    // from auto-registering its own CorsFilter that previously rejected the preflight.
    @Bean
    public FilterRegistrationBean<SimpleCorsFilter> simpleCorsFilterRegistration(SimpleCorsFilter simpleCorsFilter) {
        FilterRegistrationBean<SimpleCorsFilter> reg = new FilterRegistrationBean<>(simpleCorsFilter);
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE);
        reg.addUrlPatterns("/*");
        return reg;
    }
}
