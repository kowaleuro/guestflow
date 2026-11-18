package pl.mkowalik.guestflow.auth.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pl.mkowalik.guestflow.auth.account.AccountService;

@Component
public class JwtFilter extends OncePerRequestFilter{

    JwtService jwtService;
    AccountService accountService;
    HandlerExceptionResolver handlerExceptionResolver;

     public JwtFilter(JwtService jwtService, AccountService accountService, HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtService = jwtService;
        this.accountService = accountService;
        this.handlerExceptionResolver = handlerExceptionResolver;
 }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            processToken(request);
        } catch (Exception e) {
               // Pass exceptions to response
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
        filterChain.doFilter(request, response);
    }

     private void processToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        // Extract Bearer Token
        final String jwtToken = authHeader.substring(7);

        if (jwtService.isTokenExpired(jwtToken)) {
            return;
        }

        String userName = jwtService.getUserName(jwtToken);

        if (userName == null) {
            return;
        }

        // Get existing authentication instance
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            return;
        }

        // Authenticate and create authentication instance
        UserDetails userDetails = accountService.loadUserByUsername(userName);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
            userDetails.getAuthorities());

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        // Store authentication token for application to use
        SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    
}
