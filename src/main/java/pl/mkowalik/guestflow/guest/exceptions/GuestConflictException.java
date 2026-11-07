package pl.mkowalik.guestflow.guest.exceptions;
import pl.mkowalik.guestflow.globalExceptions.NotFoundException;
public class GuestConflictException extends NotFoundException{

    public GuestConflictException(String email) {
        super(String.format("Guest user with email: %s already exists.",email));
    }
}
