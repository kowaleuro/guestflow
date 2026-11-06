package pl.mkowalik.guestflow.guest;

import pl.mkowalik.guestflow.guest.dto.GetGuest;

final class GuestMapper {
    private GuestMapper() {}

    static GetGuest toGuest(Guest g) {
        return new GetGuest(
            g.getId(),
            g.getName(),
            g.getEmail(),
            g.getStatus()
        );
    }
}
