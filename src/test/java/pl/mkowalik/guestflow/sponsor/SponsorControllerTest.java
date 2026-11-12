package pl.mkowalik.guestflow.sponsor;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import pl.mkowalik.guestflow.sponsor.dto.CreateSponsorRequest;
import pl.mkowalik.guestflow.sponsor.dto.ResponseSponsor;
import pl.mkowalik.guestflow.sponsor.exceptions.SponsorConflictException;
import pl.mkowalik.guestflow.sponsor.exceptions.SponsorNotFoundException;
import org.springframework.http.MediaType;


@WebMvcTest(SponsorController.class)
public class SponsorControllerTest {
    
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    SponsorService sponsorService;

    private static final Long SPONSOR_ID    = 1L;
    private static final String SPONSOR_NAME  = "sponsor Sponsor";
    private static final String SPONSOR_EMAIL = "sponsor@example.com";
    private static final String BASE_URL = "http://localhost";

    // ---------- getById ----------

    @Test
    void getSponsorById_returns200AndJson_whenSponsorExists() throws Exception {
        given(sponsorService.getById(SPONSOR_ID))
            .willReturn(new ResponseSponsor(SPONSOR_ID, SPONSOR_NAME, SPONSOR_EMAIL));

        mockMvc.perform(get("/sponsor/{id}", SPONSOR_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(SPONSOR_ID))
            .andExpect(jsonPath("$.name").value(SPONSOR_NAME))
            .andExpect(jsonPath("$.email").value(SPONSOR_EMAIL));
    }

    @Test
    void getSponsorById_returns404AndProblemDetail_whenSponsorDoesNotExist() throws Exception {
        given(sponsorService.getById(SPONSOR_ID))
            .willThrow(new SponsorNotFoundException(SPONSOR_ID));

        String errorMessage = String.format("Sponsor with id: %s does not exist.",SPONSOR_ID);
        mockMvc.perform(get("/sponsor/{id}", SPONSOR_ID))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.title").value("Not Found"))
            .andExpect(jsonPath("$.detail").value(errorMessage));
    }

    // ---------- create ----------

    @Test
    void postCreate_returns201AndJson_whenCreated() throws Exception {
        CreateSponsorRequest request = new CreateSponsorRequest(SPONSOR_NAME,SPONSOR_EMAIL);
        given(sponsorService.create(request))
            .willReturn(new ResponseSponsor(SPONSOR_ID, SPONSOR_NAME, SPONSOR_EMAIL));

        String jsonBody = String.format(
            """
                {
                "name": "%s", "email": "%s"
                }
            """,
            SPONSOR_NAME, SPONSOR_EMAIL
        ); 
        mockMvc.perform(post("/sponsor")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonBody))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "%s/sponsor/%s".formatted(BASE_URL,SPONSOR_ID)))
            .andExpect(jsonPath("$.id").value(SPONSOR_ID))
            .andExpect(jsonPath("$.name").value(SPONSOR_NAME))
            .andExpect(jsonPath("$.email").value(SPONSOR_EMAIL));
    }

    @Test
    void postCreate_returns409AndProblemDetail_whenConflict() throws Exception {
        CreateSponsorRequest request = new CreateSponsorRequest(SPONSOR_NAME,SPONSOR_EMAIL);
        given(sponsorService.create(request))
            .willThrow(new SponsorConflictException(SPONSOR_EMAIL));
        
        String jsonBody = String.format(
            """
                {
                "name": "%s", "email": "%s"
                }
            """,
            SPONSOR_NAME, SPONSOR_EMAIL
        ); 
        
        String errorMessage = String.format("Sponsor with email: %s already exists.",SPONSOR_EMAIL);
        mockMvc.perform(post("/sponsor")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonBody))
            .andExpect(status().isConflict())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.title").value("Conflict"))
            .andExpect(jsonPath("$.detail").value(errorMessage));
    }

    @Test
    void postCreate_returns400andProblemDetail_whenEmailInvalid() throws Exception {
        String jsonBody = String.format(
            """
                {
                "name": "%s", "email": "%s"
                }
            """,
            SPONSOR_NAME, "sponsor.Test"
        ); 

        mockMvc.perform(post("/sponsor")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(sponsorService);
    }

}
