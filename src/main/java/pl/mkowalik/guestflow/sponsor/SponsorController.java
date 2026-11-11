package pl.mkowalik.guestflow.sponsor;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import pl.mkowalik.guestflow.sponsor.dto.CreateSponsorRequest;
import pl.mkowalik.guestflow.sponsor.dto.ResponseSponsor;

@RestController
@RequestMapping("/sponsor")
public class SponsorController {
    private final SponsorService service;

    SponsorController(SponsorService service){
        this.service = service;
    }

    //--------- MAPPINGS ---------

    @PostMapping
    ResponseEntity <ResponseSponsor> create(@Valid @RequestBody CreateSponsorRequest request){
        ResponseSponsor created = service.create(request);
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.id())
            .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseSponsor getSponsor(@PathVariable Long id) {
        return service.getById(id);
    }
}
