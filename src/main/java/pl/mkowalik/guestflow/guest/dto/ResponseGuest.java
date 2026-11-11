package pl.mkowalik.guestflow.guest.dto;

import pl.mkowalik.guestflow.guest.GuestStatus;
import pl.mkowalik.guestflow.sponsor.dto.SponsorSummary;

public record ResponseGuest(
    Long id,
    String name,
    String email,
    GuestStatus status,
    SponsorSummary sponsorSummary
){
    public ResponseGuest(Long id, String name, String email, GuestStatus status){
        this(id, name, email, status, null);
    }
}
