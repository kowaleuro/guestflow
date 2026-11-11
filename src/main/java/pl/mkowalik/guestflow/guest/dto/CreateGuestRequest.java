package pl.mkowalik.guestflow.guest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateGuestRequest(
    @NotBlank String name,
    @Email @NotBlank String email,
    Long sponsorId
){
    public CreateGuestRequest(String name, String email){
        this(name, email, null);
    }
}
