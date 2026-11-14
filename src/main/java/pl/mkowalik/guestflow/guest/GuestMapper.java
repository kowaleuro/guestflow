package pl.mkowalik.guestflow.guest;

import java.util.List;

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

    static List<ResponseGuest> toResponseGuests(List<Guest> guests) {
        return  guests.stream()
            .map(GuestMapper::toResponseGuest)
            .toList();
    }
}
