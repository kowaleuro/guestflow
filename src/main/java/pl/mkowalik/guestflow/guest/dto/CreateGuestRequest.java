package pl.mkowalik.guestflow.guest.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pl.mkowalik.guestflow.guest.enums.GuestType;

public record CreateGuestRequest(
    @NotBlank String name,
    @Email @NotBlank String email,
    @NotNull GuestType type,
    @Email String sponsorEmail
){
    public CreateGuestRequest(String name, String email, GuestType type){
        this(name, email, type, null);
    }

    @JsonIgnore
    @AssertFalse(message = "sponsorEmail can only be set for SPONSORED guests")
    public boolean isSponsorEmailSetNotSponsoredType() {
        boolean hasSponsor = sponsorEmail != null && !sponsorEmail.isBlank();
        return (hasSponsor && type != GuestType.SPONSORED);
    }
}
