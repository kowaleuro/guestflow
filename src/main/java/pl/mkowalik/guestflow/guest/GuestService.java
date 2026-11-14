package pl.mkowalik.guestflow.guest;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.mkowalik.guestflow.guest.dto.CreateGuestRequest;
import pl.mkowalik.guestflow.guest.dto.ResponseGuest;
import pl.mkowalik.guestflow.guest.enums.GuestStatus;
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
    public List<ResponseGuest> getBySponsor(Long sponsorId){
        sponsorService.getEntityById(sponsorId); // check if sponsor exist, otherwise 404
        List<Guest> guests = repository.findBySponsorId(sponsorId);
        return GuestMapper.toResponseGuests(guests);
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
        Guest guest = new Guest(req.name(), req.email(), req.type());
        
        if (req.sponsorEmail() != null){
            guest.assignSponsor(sponsorService.getEntityByEmail(req.sponsorEmail()));
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
