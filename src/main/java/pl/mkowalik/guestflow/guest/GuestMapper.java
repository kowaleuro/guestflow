package pl.mkowalik.guestflow.guest;

import pl.mkowalik.guestflow.guest.dto.ResponseGuest;

final class GuestMapper {
    private GuestMapper() {}

    static ResponseGuest toResponseGuest(Guest g) {
        return new ResponseGuest(
            g.getId(),
            g.getName(),
            g.getEmail(),
            g.getStatus()
        );
    }
}
