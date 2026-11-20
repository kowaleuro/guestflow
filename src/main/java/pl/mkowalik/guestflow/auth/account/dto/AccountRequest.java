package pl.mkowalik.guestflow.auth.account.dto;

import jakarta.validation.constraints.NotBlank;

public record AccountRequest(
    @NotBlank
    String username,
    String password
) {}
