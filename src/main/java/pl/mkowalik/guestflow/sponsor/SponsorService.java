package pl.mkowalik.guestflow.sponsor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.mkowalik.guestflow.sponsor.dto.CreateSponsorRequest;
import pl.mkowalik.guestflow.sponsor.dto.ResponseSponsor;
import pl.mkowalik.guestflow.sponsor.exceptions.SponsorConflictException;
import pl.mkowalik.guestflow.sponsor.exceptions.SponsorNotFoundException;

@Service
public class SponsorService {
    
    private final SponsorRepository sponsorRepository;
    
    SponsorService(SponsorRepository sponsorRepository){
        this.sponsorRepository = sponsorRepository;
    }

    @Transactional (readOnly = true)
    public ResponseSponsor getById(Long id){
        Sponsor sponsor = sponsorRepository.findById(id)
            .orElseThrow(() -> new SponsorNotFoundException(id));
        return SponsorMapper.toResponseSponsor(sponsor);
    }

    @Transactional
    public ResponseSponsor create(CreateSponsorRequest req){
        if (sponsorRepository.existsByEmail(req.email())){
            throw new SponsorConflictException(req.email());
        }
        Sponsor sponsor = new Sponsor(req.name(), req.email());
        return SponsorMapper.toResponseSponsor(sponsorRepository.save(sponsor));
    }

}
