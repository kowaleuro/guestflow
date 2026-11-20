package pl.mkowalik.guestflow.auth.account;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.transaction.annotation.Transactional;
import pl.mkowalik.guestflow.auth.account.enums.AccountRole;
import pl.mkowalik.guestflow.guest.dto.CreateGuestRequest;
import pl.mkowalik.guestflow.sponsor.dto.CreateSponsorRequest;
@Service
public class AccountService implements UserDetailsService{
    
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder){
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public  Account createAndSaveAccount(String username, String password, AccountRole role){
        if (accountRepository.existsByUsername(username)){
            throw new RuntimeException();
        }
        String passwordHash = passwordEncoder.encode(password); 
        Account account = new Account(username, passwordHash, role);
        accountRepository.save(account);
        return account;
    }

    // ---------- Guest ----------
    public Account createGuestAccount(CreateGuestRequest request){
        return createAndSaveAccount(request.email(), null, AccountRole.GUEST);
    }

    // ---------- Sponsor ----------
    public Account createSponsorAccount(CreateSponsorRequest request){
        return createAndSaveAccount(request.email(), request.password(), AccountRole.SPONSOR);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(username));
        
        return User.withUsername(account.getUsername())
            .password(account.getPasswordHash() == null ? "" : account.getPasswordHash())
            .roles(account.getRole().name())
            .build();
    }

    

}
