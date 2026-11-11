package pl.mkowalik.guestflow.sponsor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateSponsorRequest(
    @NotBlank 
    String name,
    @NotBlank @Email
    String email
) {
    
}
