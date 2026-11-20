package pl.mkowalik.guestflow.auth.account.dto;

public record AccountResponse(
    String token,
    long expiresInMinutes
) {}
