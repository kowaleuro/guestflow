package pl.mkowalik.guestflow.guest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.mkowalik.guestflow.guest.dto.CreateGuestRequest;
import pl.mkowalik.guestflow.guest.dto.ResponseGuest;
import pl.mkowalik.guestflow.guest.exceptions.*;
import pl.mkowalik.guestflow.sponsor.SponsorService;

@Service
public class GuestService {

    private final SponsorService sponsorService;
    private final GuestRepository repository;

    GuestService(GuestRepository repository, SponsorService sponsorService) {
        this.repository = repository;
        this.sponsorService = sponsorService;
    }

    public Guest getEntityById(Long id){
        Guest guest = repository.findById(id)
            .orElseThrow(() -> new GuestNotFoundException(id));
        return guest;
    }

    @Transactional(readOnly = true)
    public ResponseGuest getById(Long id){
        Guest guest = getEntityById(id);
        return GuestMapper.toResponseGuest(guest);
    }

    @Transactional
    public ResponseGuest register(CreateGuestRequest req){
        if (repository.existsByEmail(req.email())){
            throw new GuestConflictException(req.email());
        }
        Guest guest = new Guest(req.name(), req.email(), GuestStatus.PENDING_APPROVAL);

        if (req.sponsorId() != null){
            guest.setSponsor(sponsorService.getEntityById(req.sponsorId()));
        }
        return GuestMapper.toResponseGuest(repository.save(guest));
    }

    @Transactional
    public ResponseGuest approve(Long id) {
        Guest guest = repository.findById(id)
            .orElseThrow(() -> new GuestNotFoundException(id));
        if (guest.getStatus() != GuestStatus.PENDING_APPROVAL){
            throw new GuestApprovedStatusConflictException(id);
        }
        guest.setStatus(GuestStatus.APPROVED);
        return GuestMapper.toResponseGuest(guest);
    }
}
