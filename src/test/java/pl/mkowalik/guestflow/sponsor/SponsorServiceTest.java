package pl.mkowalik.guestflow.sponsor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import pl.mkowalik.guestflow.sponsor.dto.CreateSponsorRequest;
import pl.mkowalik.guestflow.sponsor.dto.ResponseSponsor;
import pl.mkowalik.guestflow.sponsor.exceptions.SponsorConflictException;
import pl.mkowalik.guestflow.sponsor.exceptions.SponsorNotFoundException;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

@ExtendWith(MockitoExtension.class)
public class SponsorServiceTest {
    private static final Long SPONSOR_ID    = 1L;
    private static final String SPONSOR_NAME  = "sponsor Sponsor";
    private static final String SPONSOR_EMAIL = "sponsor@example.com";

    @Mock
    SponsorRepository sponsorRepository;

    @InjectMocks
    SponsorService service;

    @Captor
    ArgumentCaptor<Sponsor> sponsorCaptor;

    // ---------- getById ----------

    @Test
    void getById_returnsResponse_whenSponsorExists() {
        Sponsor sponsor = new Sponsor(SPONSOR_NAME, SPONSOR_EMAIL);
        sponsor.setId(SPONSOR_ID);
        given(sponsorRepository.findById(SPONSOR_ID))
            .willReturn(Optional.of(sponsor));
        
        ResponseSponsor result = service.getById(SPONSOR_ID);

        assertThat(result)
            .isEqualTo(new ResponseSponsor(SPONSOR_ID, SPONSOR_NAME, SPONSOR_EMAIL));
    }

    @Test
    void getById_throwsNotFound_whenSponsorNotFound() {
        given(sponsorRepository.findById(SPONSOR_ID))
            .willReturn(Optional.empty());

        String errorMessage = String.format("Sponsor with id: %s does not exist.",SPONSOR_ID);
        assertThatThrownBy(() -> service.getById(SPONSOR_ID))
            .isInstanceOf(SponsorNotFoundException.class)
            .hasMessage(errorMessage);
    }

    // ---------- create ----------

    @Test
    void create_savesSponsorAndReturnsResponse_whenEmailIsNew() {
        CreateSponsorRequest request = new CreateSponsorRequest(SPONSOR_NAME,SPONSOR_EMAIL);
        
        given(sponsorRepository.existsByEmail(SPONSOR_EMAIL))
            .willReturn(false);
        
        given(sponsorRepository.save(any(Sponsor.class)))
            .willAnswer(invocation -> {
                Sponsor saved = invocation.getArgument(0);
                saved.setId(SPONSOR_ID);
                return saved;
            });


        ResponseSponsor result = service.create(request);

        then(sponsorRepository).should().save(sponsorCaptor.capture());
        Sponsor saved = sponsorCaptor.getValue();

        assertThat(saved.getName()).isEqualTo(SPONSOR_NAME);
        assertThat(saved.getEmail()).isEqualTo(SPONSOR_EMAIL);

        assertThat(result)
            .isEqualTo(new ResponseSponsor(SPONSOR_ID, SPONSOR_NAME, SPONSOR_EMAIL));
    }

    @Test
    void create_throwsConflict_whenEmailAlreadyExist() {
        CreateSponsorRequest request = new CreateSponsorRequest(SPONSOR_NAME,SPONSOR_EMAIL);
        
        given(sponsorRepository.existsByEmail(request.email()))
            .willReturn(true);
                
        String errorMessage = String.format("Sponsor with email: %s already exists.",SPONSOR_EMAIL);
        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(SponsorConflictException.class)
            .hasMessage(errorMessage);
        
        then(sponsorRepository).should(never()).save(any());
    }

}
