package pl.mkowalik.guestflow.sponsor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateSponsorRequest(
    @NotBlank 
    String name,
    @NotBlank @Email
    String email,
    @NotBlank @Size(min = 8)
    String password
) {
    
}
