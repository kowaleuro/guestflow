package pl.mkowalik.guestflow.sponsor.exceptions;

import pl.mkowalik.guestflow.globalExceptions.ConflictException;

public class SponsorConflictException extends ConflictException{
    public SponsorConflictException(String email){
        super(String.format("Sponsor with email: %s already exists.",email));
    }
    
}
