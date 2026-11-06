package pl.mkowalik.guestflow.guest.exceptions;

public class GuestConflictException extends RuntimeException{

    public GuestConflictException(String email) {
        super(String.format("Guest user with email: %s already exists.",email));
    }
    
}
