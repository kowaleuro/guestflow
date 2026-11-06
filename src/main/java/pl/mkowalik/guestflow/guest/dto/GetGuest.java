package pl.mkowalik.guestflow.guest.dto;

import pl.mkowalik.guestflow.guest.GuestStatus;

public record GetGuest(
    Long id,
    String name,
    String email,
    GuestStatus status
){}
