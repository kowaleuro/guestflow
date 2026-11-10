package pl.mkowalik.guestflow.guest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import pl.mkowalik.guestflow.guest.dto.CreateGuestRequest;
import pl.mkowalik.guestflow.guest.dto.ResponseGuest;
import pl.mkowalik.guestflow.guest.exceptions.GuestApprovedStatusConflictException;
import pl.mkowalik.guestflow.guest.exceptions.GuestConflictException;
import pl.mkowalik.guestflow.guest.exceptions.GuestNotFoundException;
import org.springframework.http.MediaType;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GuestController.class)
class GuestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    GuestService guestService;

    private static final Long GUEST_ID    = 1L;
    private static final String GUEST_NAME  = "Jan Kowals";
    private static final String GUEST_EMAIL = "jano@example.com";
    private static final String BASE_URL = "http://localhost";

    // ---------- getById ----------

    @Test
    void getGuestById_returns200AndJson_whenGuestExists() throws Exception {
        given(guestService.getById(GUEST_ID))
            .willReturn(new ResponseGuest(GUEST_ID, GUEST_NAME, GUEST_EMAIL, GuestStatus.PENDING_APPROVAL));

        mockMvc.perform(get("/guest/{id}", GUEST_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(GUEST_ID))
            .andExpect(jsonPath("$.name").value(GUEST_NAME))
            .andExpect(jsonPath("$.email").value(GUEST_EMAIL))
            .andExpect(jsonPath("$.status").value("PENDING_APPROVAL"));
    }

    @Test
    void getGuestById_returns404AndProblemDetail_whenGuestDoesNotExist() throws Exception {
        given(guestService.getById(GUEST_ID))
            .willThrow(new GuestNotFoundException(GUEST_ID));

        String errorMessage = String.format("Guest user with id: %s does not exist.",GUEST_ID);
        mockMvc.perform(get("/guest/{id}", GUEST_ID))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.title").value("Not Found"))
            .andExpect(jsonPath("$.detail").value(errorMessage));
    }

    // ---------- register ----------

    @Test
    void postRegister_returns201AndJson_whenRegistered() throws Exception {
        CreateGuestRequest request = new CreateGuestRequest(GUEST_NAME,GUEST_EMAIL);
        given(guestService.register(request))
            .willReturn(new ResponseGuest(GUEST_ID, GUEST_NAME, GUEST_EMAIL, GuestStatus.PENDING_APPROVAL));

        String jsonBody = String.format(
            """
                {
                "name": "%s", "email": "%s"
                }
            """,
            GUEST_NAME, GUEST_EMAIL
        ); 
        mockMvc.perform(post("/guest")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonBody))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "%s/guest/%s".formatted(BASE_URL,GUEST_ID)))
            .andExpect(jsonPath("$.id").value(GUEST_ID))
            .andExpect(jsonPath("$.name").value(GUEST_NAME))
            .andExpect(jsonPath("$.email").value(GUEST_EMAIL))
            .andExpect(jsonPath("$.status").value("PENDING_APPROVAL"));
    }

    @Test
    void postRegister_returns409AndProblemDetail_whenConflict() throws Exception {
        CreateGuestRequest request = new CreateGuestRequest(GUEST_NAME,GUEST_EMAIL);
        given(guestService.register(request))
            .willThrow(new GuestConflictException(GUEST_EMAIL));
        
        String jsonBody = String.format(
            """
                {
                "name": "%s", "email": "%s"
                }
            """,
            GUEST_NAME, GUEST_EMAIL
        ); 
        
        String errorMessage = String.format("Guest user with email: %s already exists.",GUEST_EMAIL);
        mockMvc.perform(post("/guest")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonBody))
            .andExpect(status().isConflict())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.title").value("Conflict"))
            .andExpect(jsonPath("$.detail").value(errorMessage));
    }

    @Test
    void postRegister_returns400andProblemDetail_whenEmailInvalid() throws Exception {
        String jsonBody = String.format(
            """
                {
                "name": "%s", "email": "%s"
                }
            """,
            GUEST_NAME, "guest.Test"
        ); 

        mockMvc.perform(post("/guest")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(guestService);
    }

    // ---------- approve ----------

    @Test
    void approve_returns200AndJson_whenStatusChangedToApproved() throws Exception {
        given(guestService.approve(GUEST_ID))
            .willReturn(new ResponseGuest(GUEST_ID, GUEST_NAME, GUEST_EMAIL, GuestStatus.APPROVED));

        mockMvc.perform(post("/guest/{id}/approve", GUEST_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(GUEST_ID))
            .andExpect(jsonPath("$.name").value(GUEST_NAME))
            .andExpect(jsonPath("$.email").value(GUEST_EMAIL))
            .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void approve_returns404AndProblemDetail_whenGuestNotFound() throws Exception {
        given(guestService.approve(GUEST_ID))
            .willThrow(new GuestNotFoundException(GUEST_ID));

        String errorMessage = String.format("Guest user with id: %s does not exist.",GUEST_ID);
        
        mockMvc.perform(post("/guest/{id}/approve",GUEST_ID))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.title").value("Not Found"))
            .andExpect(jsonPath("$.detail").value(errorMessage));
    }

    @Test
    void approve_returns409AndProblemDetail_whenGuestAlreadyApproved() throws Exception {
        given(guestService.approve(GUEST_ID))
            .willThrow(new GuestApprovedStatusConflictException(GUEST_ID));

        String errorMessage = String.format("Guest user with id: %s is already approved.",GUEST_ID);
        
        mockMvc.perform(post("/guest/{id}/approve",GUEST_ID))
            .andExpect(status().isConflict())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.title").value("Conflict"))
            .andExpect(jsonPath("$.detail").value(errorMessage));
    }


}
