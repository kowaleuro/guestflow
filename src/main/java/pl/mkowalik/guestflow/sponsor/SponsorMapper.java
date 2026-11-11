package pl.mkowalik.guestflow.sponsor;

import pl.mkowalik.guestflow.sponsor.dto.ResponseSponsor;

final class SponsorMapper {
    private SponsorMapper() {}

    static ResponseSponsor toResponseSponsor(Sponsor s) {
        return new ResponseSponsor(
            s.getId(),
            s.getName(),
            s.getEmail()
        );
    }
}
