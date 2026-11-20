package pl.mkowalik.guestflow.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import pl.mkowalik.guestflow.auth.account.AccountService;
import pl.mkowalik.guestflow.auth.jwt.JwtFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    
    JwtFilter jwtFilter;
    AccountService accountService;
    
    public SecurityConfiguration(JwtFilter jwtFilter, AccountService accountService) {
        // Auto inject dependent beans
        this.jwtFilter = jwtFilter;
        this.accountService = accountService;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain basicAuthSecurityFilterChain(HttpSecurity http) throws Exception {
        HttpSecurity securedHttp = http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(request -> {
                // Public (landing pages, error, login/registration)
                request.requestMatchers("/auth/**", "/error").permitAll();
                request.requestMatchers(HttpMethod.POST, "/guest").permitAll();     // guest registration
                request.requestMatchers(HttpMethod.POST, "/sponsor").permitAll();
                // Protected by Role
                request.requestMatchers(HttpMethod.POST, "/guest/*/approve").hasRole("SPONSOR");
                request.requestMatchers("/guest/getSponsored/**").hasRole("SPONSOR");
                request.anyRequest().authenticated();
            });
        
        return securedHttp.sessionManagement( session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

}
