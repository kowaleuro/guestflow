package pl.mkowalik.guestflow.guest;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.mkowalik.guestflow.guest.dto.CreateGuestRequest;
import pl.mkowalik.guestflow.guest.dto.ResponseGuest;
import pl.mkowalik.guestflow.guest.exceptions.*;

@Service
public class GuestService {

    private final GuestRepository repository;
    GuestService(GuestRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public ResponseGuest getById(Long id){
        Guest guest = repository.findById(id)
            .orElseThrow(() -> new GuestNotFoundException(id));
        return GuestMapper.toResponseGuest(guest);
    }

    @Transactional
    public ResponseGuest register(CreateGuestRequest req){
        if (repository.existsByEmail(req.email())){
            throw new GuestConflictException(req.email());
        }
        Guest guest = new Guest(req.name(), req.email(), GuestStatus.PENDING_APPROVAL);
        return GuestMapper.toResponseGuest(repository.save(guest));
    }

    public @Nullable Object approve(Long guestId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'approve'");
    }
}
