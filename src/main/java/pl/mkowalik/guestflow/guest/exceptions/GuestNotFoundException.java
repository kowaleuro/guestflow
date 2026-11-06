package pl.mkowalik.guestflow.guest.exceptions;

public class GuestNotFoundException extends RuntimeException {

    public GuestNotFoundException(Long id) {
        super(String.format("Guest user with id: %s does not exist", id));
    }
}
