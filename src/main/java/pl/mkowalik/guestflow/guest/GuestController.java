package pl.mkowalik.guestflow.guest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import pl.mkowalik.guestflow.guest.dto.CreateGuestRequest;
import pl.mkowalik.guestflow.guest.dto.ResponseGuest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/guest")
public class GuestController {
    private final GuestService service;

    GuestController(GuestService service){
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ResponseGuest register(@Valid @RequestBody CreateGuestRequest request){
        return service.register(request);
    }

    @GetMapping("/{id}")
    public ResponseGuest getGuest(@PathVariable Long id) {
        return service.getById(id);
    }
}
