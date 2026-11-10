package pl.mkowalik.guestflow.guest;


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

import pl.mkowalik.guestflow.guest.dto.CreateGuestRequest;
import pl.mkowalik.guestflow.guest.dto.ResponseGuest;
import pl.mkowalik.guestflow.guest.exceptions.GuestApprovedStatusConflictException;
import pl.mkowalik.guestflow.guest.exceptions.GuestConflictException;
import pl.mkowalik.guestflow.guest.exceptions.GuestNotFoundException;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

@ExtendWith(MockitoExtension.class)
class GuestServiceTest {
    private static final Long GUEST_ID    = 1L;
    private static final String GUEST_NAME  = "Jan Kowals";
    private static final String GUEST_EMAIL = "jano@example.com";

    @Mock
    GuestRepository repository;

    @InjectMocks
    GuestService service;

    @Captor
    ArgumentCaptor<Guest> guestCaptor;

    // ---------- getById ----------

    @Test
    void getById_returnsResponse_whenGuestExists() {
        Guest guest = new Guest(GUEST_NAME, GUEST_EMAIL, GuestStatus.PENDING_APPROVAL);
        guest.setId(GUEST_ID);
        given(repository.findById(GUEST_ID))
            .willReturn(Optional.of(guest));
        
        ResponseGuest result = service.getById(GUEST_ID);

        assertThat(result)
            .isEqualTo(new ResponseGuest(GUEST_ID, GUEST_NAME, GUEST_EMAIL, GuestStatus.PENDING_APPROVAL));
    }

    @Test
    void getById_throwsNotFound_whenGuestNotFound() {
        given(repository.findById(GUEST_ID))
            .willReturn(Optional.empty());

        String errorMessage = String.format("Guest user with id: %s does not exist.",GUEST_ID);
        assertThatThrownBy(() -> service.getById(GUEST_ID))
            .isInstanceOf(GuestNotFoundException.class)
            .hasMessage(errorMessage);
    }

    // ---------- register ----------

    @Test
    void register_throwsConflict_whenEmailAlreadyExist() {
        CreateGuestRequest request = new CreateGuestRequest(GUEST_NAME,GUEST_EMAIL);
        
        given(repository.existsByEmail(request.email()))
            .willReturn(true);
                
        String errorMessage = String.format("Guest user with email: %s already exists.",GUEST_EMAIL);
        assertThatThrownBy(() -> service.register(request))
            .isInstanceOf(GuestConflictException.class)
            .hasMessage(errorMessage);
        
        then(repository).should(never()).save(any());
    }

    @Test
    void register_savesPendingGuestAndReturnsResponse_whenEmailIsNew() {
        CreateGuestRequest request = new CreateGuestRequest(GUEST_NAME,GUEST_EMAIL);
        
        given(repository.existsByEmail(GUEST_EMAIL))
            .willReturn(false);
        
        given(repository.save(any(Guest.class)))
            .willAnswer(invocation -> {
                Guest saved = invocation.getArgument(0);
                saved.setId(GUEST_ID);
                return saved;
            });


        ResponseGuest result = service.register(request);

        then(repository).should().save(guestCaptor.capture());
        Guest saved = guestCaptor.getValue();

        assertThat(saved.getName()).isEqualTo(GUEST_NAME);
        assertThat(saved.getEmail()).isEqualTo(GUEST_EMAIL);
        assertThat(saved.getStatus()).isEqualTo(GuestStatus.PENDING_APPROVAL);

        assertThat(result)
            .isEqualTo(new ResponseGuest(GUEST_ID, GUEST_NAME, GUEST_EMAIL, GuestStatus.PENDING_APPROVAL));
    }

    // ---------- approve ----------

     @Test
    void approve_changeStatusAndReturnsResponse_whenGuestExistAndIsNotApproved() {
        Guest guest = new Guest(GUEST_NAME, GUEST_EMAIL, GuestStatus.PENDING_APPROVAL);
        guest.setId(GUEST_ID);
        
        given(repository.findById(GUEST_ID))
            .willReturn(Optional.of(guest));
        
        ResponseGuest result = service.approve(GUEST_ID);
        assertThat(guest.getStatus()).isEqualTo(GuestStatus.APPROVED);
        assertThat(result)
            .isEqualTo(new ResponseGuest(GUEST_ID, GUEST_NAME, GUEST_EMAIL, GuestStatus.APPROVED));
    }

    @Test
    void approve_throwsNotFound_whenGuestNotFound() {
        Guest guest = new Guest(GUEST_NAME, GUEST_EMAIL, GuestStatus.APPROVED);
        guest.setId(GUEST_ID);
        
        given(repository.findById(GUEST_ID))
            .willReturn(Optional.of(guest));
        
        String errorMessage = String.format("Guest user with id: %s is already approved.",GUEST_ID);

        assertThatThrownBy(() -> service.approve(GUEST_ID))
            .isInstanceOf(GuestApprovedStatusConflictException.class)
            .hasMessage(errorMessage);
    }

    @Test
    void approve_throwsConflict_whenGuestStatusIsApproved() {
        given(repository.findById(GUEST_ID))
            .willReturn(Optional.empty());

        String errorMessage = String.format("Guest user with id: %s does not exist.",GUEST_ID);
        assertThatThrownBy(() -> service.approve(GUEST_ID))
            .isInstanceOf(GuestNotFoundException.class)
            .hasMessage(errorMessage);
    }

}
