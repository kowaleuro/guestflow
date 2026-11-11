package pl.mkowalik.guestflow.sponsor.exceptions;

import pl.mkowalik.guestflow.globalExceptions.NotFoundException;

public class SponsorNotFoundException extends NotFoundException{
    public SponsorNotFoundException(Long id){
        super(String.format("Sponsor with id: %s does not exist.", id));
    }
}
