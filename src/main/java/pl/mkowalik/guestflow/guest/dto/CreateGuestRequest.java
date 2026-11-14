package pl.mkowalik.guestflow.guest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import pl.mkowalik.guestflow.guest.enums.GuestType;

public record CreateGuestRequest(
    @NotBlank String name,
    @Email @NotBlank String email,
    GuestType type,
    @Email String sponsorEmail
){
    public CreateGuestRequest(String name, String email, GuestType type){
        this(name, email, type, null);
    }
}
