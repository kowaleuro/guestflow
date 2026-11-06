package pl.mkowalik.guestflow.guest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.mkowalik.guestflow.guest.dto.CreateGuestRequest;
import pl.mkowalik.guestflow.guest.dto.GetGuest;
import pl.mkowalik.guestflow.guest.exceptions.*;

@Service
public class GuestService {

    private final GuestRepository repository;
    GuestService(GuestRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public GetGuest register(CreateGuestRequest req){
        if (repository.existsByEmail(req.email())){
            throw new GuestConflictException(req.email());
        }
        Guest guest = new Guest(req.name(), req.email(), GuestStatus.PENDING_APPROVAL);
        return GuestMapper.toGuest(repository.save(guest));
    }

    @Transactional(readOnly = true)
    public GetGuest getById(Long id){
        Guest guest = repository.findById(id)
            .orElseThrow(() -> new GuestNotFoundException(id));
        return GuestMapper.toGuest(guest);
    }
}
