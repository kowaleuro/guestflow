package pl.mkowalik.guestflow.guest.exceptions;

import pl.mkowalik.guestflow.globalExceptions.ConflictException;

public class GuestApprovedStatusConflictException extends ConflictException{

    public GuestApprovedStatusConflictException(Long id) {
        super(String.format("Guest user with id: %s is already approved.",id));
    }
}
