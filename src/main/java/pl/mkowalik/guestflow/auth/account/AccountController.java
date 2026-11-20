package pl.mkowalik.guestflow.auth.account;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.mkowalik.guestflow.auth.account.dto.AccountRequest;
import pl.mkowalik.guestflow.auth.account.dto.AccountResponse;
import pl.mkowalik.guestflow.auth.jwt.JwtService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
public class AccountController {
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    @Value("${jwt.ttl-minutes}") private long ttlMinutes;

   AccountController(AuthenticationManager authManager, JwtService jwtService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    @PostMapping("login")
    public AccountResponse login(@RequestBody AccountRequest request) {
        Authentication authentication = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        String token = jwtService.generateToken(
            Map.of("role",role),
            request.username(),
            ttlMinutes * 60_000
        );
        
        return new AccountResponse(token, ttlMinutes);
    }
    
}
