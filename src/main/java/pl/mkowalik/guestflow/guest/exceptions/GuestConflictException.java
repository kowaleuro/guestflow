package pl.mkowalik.guestflow.guest.exceptions;
import pl.mkowalik.guestflow.globalExceptions.ConflictException;
public class GuestConflictException extends ConflictException{

    public GuestConflictException(String email) {
        super(String.format("Guest user with email: %s already exists.",email));
    }
}
