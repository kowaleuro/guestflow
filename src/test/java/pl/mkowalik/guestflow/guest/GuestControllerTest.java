package pl.mkowalik.guestflow.guest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import pl.mkowalik.guestflow.guest.dto.ResponseGuest;
import pl.mkowalik.guestflow.guest.exceptions.GuestNotFoundException;
import org.springframework.http.MediaType;
import static org.mockito.BDDMockito.given;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GuestController.class)
public class GuestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    GuestService guestService;

    @Test
    void getGuestById_returns200AndJson_whenGuestExists() throws Exception {
        Long guestId = 1L;
        given(guestService.getById(guestId))
            .willReturn(new ResponseGuest(guestId, "Kowalski", "kowalski@example.com", GuestStatus.PENDING_APPROVAL));

        mockMvc.perform(get("/guest/{id}", guestId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(guestId))
            .andExpect(jsonPath("$.name").value("Kowalski"))
            .andExpect(jsonPath("$.email").value("kowalski@example.com"))
            .andExpect(jsonPath("$.status").value(GuestStatus.PENDING_APPROVAL.name()));
    }
    @Test
    void getGuestByIdreturns404AndMessage_whenItemDoesNotExist() throws Exception {
        Long guestId = 99L;
        given(guestService.getById(guestId))
            .willThrow(new GuestNotFoundException(guestId));

        mockMvc.perform(get("/guest/{id}", guestId))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.title").value("Not Found"))
            .andExpect(jsonPath("$.detail").value("Guest user with id: 99 does not exist."));
    }
}
