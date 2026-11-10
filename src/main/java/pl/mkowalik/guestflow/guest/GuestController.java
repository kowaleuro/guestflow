package pl.mkowalik.guestflow.guest;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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

    //--------- MAPPINGS ---------

    @PostMapping
    ResponseEntity <ResponseGuest> register(@Valid @RequestBody CreateGuestRequest request){
        ResponseGuest created = service.register(request);
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.id())
            .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseGuest getGuest(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping("/{id}/approve")
    public ResponseGuest approve(@PathVariable Long id) {
        return service.approve(id);
    }
}
