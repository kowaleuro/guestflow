package pl.mkowalik.guestflow.auth.account;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import pl.mkowalik.guestflow.auth.account.enums.AccountRole;
import pl.mkowalik.guestflow.guest.dto.CreateGuestRequest;
import pl.mkowalik.guestflow.sponsor.dto.CreateSponsorRequest;

@Service
public class AccountService {
    
    private final AccountRepository accountRepository;

    AccountService(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

    @Transactional
    public  Account createAndSaveAccount(String username, String passwordHash, AccountRole role){
        if (accountRepository.existsByUsername(username)){
            throw new RuntimeException();
        }
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
        return createAndSaveAccount(request.email(), null, AccountRole.SPONSOR);
    }

    

}
