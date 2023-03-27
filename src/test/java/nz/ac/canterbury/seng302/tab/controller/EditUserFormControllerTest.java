package nz.ac.canterbury.seng302.tab.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class EditUserFormControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService mockUserService;

    @MockBean
    private UserRepository mockUserRepository;

    private static final String URL = "/editUser";

    // Parameter names
    private static final String P_FNAME = "firstName";
    private static final String P_LNAME = "lastName";
    private static final String P_EMAIL = "email";
    private static final String P_DOB = "dateOfBirth";
    // Default values
    private static final String USER_FNAME = "Test";
    private static final String USER_LNAME = "User";
    private static final String USER_EMAIL = "test@email.org";
    private static final String USER_DOB = "2000-01-01";
    private static final String USER_PWORD = "super_insecure";

    @BeforeEach
    void beforeEach() {
        Date userDOB;
        try {
            // Have to catch a constant parse exception annoyingly
            userDOB = new SimpleDateFormat("YYYY-mm-dd").parse(USER_DOB);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        User testUser = new User(USER_FNAME, USER_LNAME, userDOB, USER_EMAIL, USER_PWORD);

        when(mockUserService.getCurrentUser()).thenReturn(Optional.of(testUser));
        when(mockUserService.emailIsInUse(anyString())).thenReturn(false);

    }

    @Test
    @WithMockUser()
    void givenUserIsLoggedIn_ThenTheyCanAccessTheForm() throws Exception {
        mockMvc.perform(get(URL))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser()
    void givenUserHasValidName_ThenFormIsSaved() throws Exception {
        mockMvc.perform(
                        post(URL)
                                .param(P_FNAME, "Cave")
                                .param(P_LNAME, "Johnson")
                                .param(P_EMAIL, USER_EMAIL)
                                .param(P_DOB, USER_DOB).param("tags", ""))
                .andExpect(redirectedUrl("user-info/self"));

        verify(mockUserService, times(1)).updateOrAddUser(any());
    }

    @Test
    @WithMockUser()
    void givenNameIsNonEnglish_ThenFormIsSaved() throws Exception {
        mockMvc.perform(
                        post(URL)
                                .param(P_FNAME, "Zoë")
                                .param(P_LNAME, "François-Johnson")
                                .param(P_EMAIL, USER_EMAIL)
                                .param(P_DOB, USER_DOB).param("tags", ""))
                .andExpect(status().is3xxRedirection());

        verify(mockUserService, times(1)).updateOrAddUser(any());
    }

    @Test
    @WithMockUser()
    void givenNameContainsNumbers_ThenFormIsRejected() throws Exception {
        mockMvc.perform(
                        post(URL)
                                .param(P_FNAME, "Ch3353")
                                .param(P_LNAME, "L0vr")
                                .param(P_EMAIL, USER_EMAIL)
                                .param(P_DOB, USER_DOB))
                .andExpect(status().isBadRequest());

        verify(mockUserService, times(0)).updateOrAddUser(any());
    }

    @Test
    @WithMockUser()
    void givenNameContainsSymbols_ThenFormIsRejected() throws Exception {
        mockMvc.perform(
                        post(URL)
                                .param(P_FNAME, "xX_eP!C_Te$t_X><")
                                .param(P_LNAME, "$o_Very_Cool")
                                .param(P_EMAIL, USER_EMAIL)
                                .param(P_DOB, USER_DOB))
                .andExpect(status().isBadRequest());

        verify(mockUserService, times(0)).updateOrAddUser(any());
    }

    @Test
    @WithMockUser()
    void givenEmailIsNotValid_ThenFormIsRejected() throws Exception {
        mockMvc.perform(
                        post(URL)
                                .param(P_FNAME, USER_FNAME)
                                .param(P_LNAME, USER_LNAME)
                                .param(P_EMAIL, "a@b")
                                .param(P_DOB, USER_DOB))
                .andExpect(status().isBadRequest());

        verify(mockUserService, times(0)).updateOrAddUser(any());
    }

    @Test
    @WithMockUser()
    void givenUserIsYoungerThan13_ThenFormIsRejected() throws Exception {
        LocalDate date = LocalDate.now();
        String dateString = String.format("%s-%s-%s",
                date.getYear() - 10,
                date.getMonthValue(),
                date.getDayOfMonth());
        mockMvc.perform(
                        post(URL)
                                .param(P_FNAME, USER_FNAME)
                                .param(P_LNAME, USER_LNAME)
                                .param(P_EMAIL, USER_EMAIL)
                                .param(P_DOB, dateString))
                .andExpect(status().isBadRequest());

        verify(mockUserService, times(0)).updateOrAddUser(any());
    }

    @Test
    @WithMockUser()
    void givenUserEntersEmailAlreadyInUse_ThenFormIsRejected() throws Exception {
        final String IN_USE_EMAIL = "company-email@email.com";
        when(mockUserService.emailIsUsedByAnother(any(), anyString())).thenReturn(true);
        mockMvc.perform(
                        post(URL)
                                .param(P_FNAME, USER_FNAME)
                                .param(P_LNAME, USER_LNAME)
                                .param(P_EMAIL, IN_USE_EMAIL)
                                .param(P_DOB, USER_DOB))
                .andExpect(status().isBadRequest());

        verify(mockUserService, times(0)).updateOrAddUser(any());
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    void givenUserChangesTheirEmail_ThenFormIsSaved_AndUserIsLoggedOut() throws Exception {
        mockMvc.perform(
                        post(URL)
                                .param(P_FNAME, USER_FNAME)
                                .param(P_LNAME, USER_LNAME)
                                .param(P_EMAIL, "new@email.com")
                                .param(P_DOB, USER_DOB).param("tags", ""))
                .andExpect(redirectedUrl("login"));

        verify(mockUserService, times(1)).updateOrAddUser(any());
    }

    /*
     * ! CAN NOT TEST: "When email is changed, then you are logged out."
     * Even though we are logged out by the controller (manually testable),
     * and we have the `unauthenticated()` ResultMatcher, logging out inside
     * the controller doesn't work.
     * Therefore, we test the redirect URL.
     */

    @Test
    @WithMockUser()
    public void addFavouriteSport_submitForm_saveToDatabase() throws Exception {
        mockMvc.perform(post(URL).param(P_FNAME, USER_FNAME)
                .param(P_LNAME, USER_LNAME)
                .param(P_EMAIL, USER_EMAIL)
                .param(P_DOB, USER_DOB)
                .param("tags","Hockey", "Football")).andExpect(redirectedUrl("user-info/self"));
        verify(mockUserService, times(1)).updateOrAddUser(any());
    }

    @Test
    @WithMockUser()
    public void removeFavouriteSport_submitForm_saveToDatabase() throws Exception {
        mockMvc.perform(post(URL).param(P_FNAME, USER_FNAME)
                .param(P_LNAME, USER_LNAME)
                .param(P_EMAIL, USER_EMAIL)
                .param(P_DOB, USER_DOB)
                .param("tags","Hockey")).andExpect(redirectedUrl("user-info/self"));
        verify(mockUserService, times(1)).updateOrAddUser(any());
    }

    @Test
    @WithMockUser()
    public void removeAllFavouriteSport_submitForm_saveToDatabase() throws Exception {
        mockMvc.perform(post(URL).param(P_FNAME, USER_FNAME)
                .param(P_LNAME, USER_LNAME)
                .param(P_EMAIL, USER_EMAIL)
                .param(P_DOB, USER_DOB)
                .param("tags","")).andExpect(redirectedUrl("user-info/self"));
        verify(mockUserService, times(1)).updateOrAddUser(any());
    }

    @Test
    @WithMockUser()
    public void AddFavouriteSportWithInvalidName_submitForm_saveToDatabase() throws Exception {
        mockMvc.perform(post(URL).param(P_FNAME, USER_FNAME)
                .param(P_LNAME, USER_LNAME)
                .param(P_EMAIL, USER_EMAIL)
                .param(P_DOB, USER_DOB)
                .param("tags","678")).andExpect(status().isFound()).andExpect(redirectedUrl("/editUser"));
        verify(mockUserService, times(0)).updateOrAddUser(any());
    }

    @Test
    @WithMockUser()
    public void AddMultipleFavouriteSportWithInvalidName_submitForm_saveToDatabase() throws Exception {
        mockMvc.perform(post(URL).param(P_FNAME, USER_FNAME)
                .param(P_LNAME, USER_LNAME)
                .param(P_EMAIL, USER_EMAIL)
                .param(P_DOB, USER_DOB)
                .param("tags","678", "%^&*")).andExpect(status().isFound()).andExpect(redirectedUrl("/editUser"));
        verify(mockUserService, times(0)).updateOrAddUser(any());
    }
}

