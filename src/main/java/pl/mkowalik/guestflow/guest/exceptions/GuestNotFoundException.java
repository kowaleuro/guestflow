package pl.mkowalik.guestflow.guest.exceptions;
import pl.mkowalik.guestflow.globalExceptions.NotFoundException;

public class GuestNotFoundException extends NotFoundException {

    public GuestNotFoundException(Long id) {
        super(String.format("Guest user with id: %s does not exist", id));
    }
}
