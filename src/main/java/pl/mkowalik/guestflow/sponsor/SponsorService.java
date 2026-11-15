package pl.mkowalik.guestflow.sponsor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.mkowalik.guestflow.auth.account.Account;
import pl.mkowalik.guestflow.auth.account.AccountService;
import pl.mkowalik.guestflow.sponsor.dto.CreateSponsorRequest;
import pl.mkowalik.guestflow.sponsor.dto.ResponseSponsor;
import pl.mkowalik.guestflow.sponsor.exceptions.SponsorConflictException;
import pl.mkowalik.guestflow.sponsor.exceptions.SponsorNotFoundException;

@Service
public class SponsorService {
    
    private final SponsorRepository sponsorRepository;
    private final AccountService accountService;

    SponsorService(SponsorRepository sponsorRepository, AccountService accountService){
        this.sponsorRepository = sponsorRepository;
        this.accountService = accountService;
    }

    public Sponsor getEntityById(Long id){
        Sponsor sponsor = sponsorRepository.findById(id)
            .orElseThrow(() -> new SponsorNotFoundException(id));
        return sponsor;
    }

    public Sponsor getEntityByEmail(String email){
        Sponsor sponsor = sponsorRepository.findByEmail(email)
            .orElseThrow(() -> new SponsorNotFoundException(email));
        return sponsor;
    }

    @Transactional (readOnly = true)
    public ResponseSponsor getById(Long id){
        Sponsor sponsor = getEntityById(id);
        return SponsorMapper.toResponseSponsor(sponsor);
    }

    @Transactional
    public ResponseSponsor create(CreateSponsorRequest req){
        if (sponsorRepository.existsByEmail(req.email())){
            throw new SponsorConflictException(req.email());
        }
        Account account = accountService.createSponsorAccount(req);
        Sponsor sponsor = new Sponsor(account, req.name(), req.email());
        return SponsorMapper.toResponseSponsor(sponsorRepository.save(sponsor));
    }
}
