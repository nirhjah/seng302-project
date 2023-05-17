package nz.ac.canterbury.seng302.tab.unit.controller;

import static nz.ac.canterbury.seng302.tab.controller.UpdatePasswordController.PASSWORD_MISMATCH_MSG;
import static nz.ac.canterbury.seng302.tab.controller.UpdatePasswordController.WRONG_OLD_PASSWORD_MSG;
import static nz.ac.canterbury.seng302.tab.validator.UserFormValidators.WEAK_PASSWORD_MESSAGE;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UpdatePasswordControllerTest {

    User testUser;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService mockUserService;

    @MockBean
    private UserRepository mockUserRepository;

    @MockBean
    private PasswordEncoder mockPasswordEncoder;

    // Default values
    private static final String USER_FNAME = "Test";
    private static final String USER_LNAME = "User";
    private static final String USER_DOB = "2000-01-01";
    private static final String USER_EMAIL = "test@example.org";
    private static final String USER_PWORD = "super_insecure";
    private static final String USER_CITY = "Christchurch";
    private static final String USER_COUNTRY = "New Zealand";
    
    private static final String NEW_PWORD = "B4ttery_St4ple";
    private static final String HASHED_PW = "iHaveBeenHashed";

    @BeforeEach
    void beforeEach() throws IOException {
        // * Mock the login
        Date userDOB;
        try {
            // Have to catch a constant parse exception annoyingly
            userDOB = new SimpleDateFormat("YYYY-mm-dd").parse(USER_DOB);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Location testLocation = new Location(null, null, null, USER_CITY,
                null, USER_COUNTRY);
        testUser = new User(USER_FNAME, USER_LNAME, userDOB, USER_EMAIL, USER_PWORD, testLocation);
        when(mockUserService.getCurrentUser()).thenReturn(Optional.of(testUser));

        // * Mock the password encoder
        // Mock the 'password hash'
        when(mockPasswordEncoder.encode(anyString())).thenReturn(HASHED_PW);
        // The 'compared' string is a simple equals
        when(mockPasswordEncoder.matches(anyString(), anyString()))
            .then((ans) -> ans.getArgument(0).equals(ans.getArgument(1)));
    }

    /**
     * A basic GET check for the form
     */
    @Test
    void canAccessUpdatePassword() throws Exception {
        mockMvc.perform(get("/update-password"))
                .andExpect(status().isOk());
    }

    /**
     * A basic test if the form can be posted
     */
    @Test
    void updatePassword_validForm_succeeds() throws Exception {
        mockMvc.perform(post("/update-password")
                .param("oldPassword", USER_PWORD)
                .param("newPassword", NEW_PWORD)
                .param("confirmPassword", NEW_PWORD)
            ).andExpect(status().is3xxRedirection());
    }

    /**
     * U22 AC2 - Given I am on the change password form,
     *              and I enter an old password that does not match the password in file,
     *              then an error message tells me the old password is wrong.
     */
    @Test
    void updatePassword_oldPasswordDoesNotMatch_fails() throws Exception {
        mockMvc.perform(post("/update-password")
                .param("oldPassword", "TheWrongPassword")
                .param("newPassword", NEW_PWORD)
                .param("confirmPassword", NEW_PWORD)
            ).andExpect(status().isBadRequest())
            .andExpect(content().string(containsString(WRONG_OLD_PASSWORD_MSG)));
    }

    /**
     * U22 AC3 - Given I am on the change password form,
     *              and I enter two different passwords in “new” and “retype password” fields,
     *              when I hit the save button,
     *              then an error message tells me the passwords do not match.
     */
    @Test
    void updatePassword_newAndRetypeDoNotMatch_fails() throws Exception {
        mockMvc.perform(post("/update-password")
                .param("oldPassword", USER_PWORD)
                .param("newPassword", NEW_PWORD)
                .param("confirmPassword", "wrong")
            ).andExpect(status().isBadRequest())
            .andExpect(content().string(containsString(PASSWORD_MISMATCH_MSG)));
    }

    /**
     * U22 AC4 - Given I am on the change password form,
     *          and I enter a weak password
     *              (e.g., contains any other fields from the user profile form,
     *              is below 8 char long,
     *              does not contain a variation of different types of characters),
     *          when I hit the save button,
     *          then an error message tells me the password is too weak
     *          and provides me with the requirements for a strong password
     * 
     * A 'weak password' definition wasn't given by the PO, but for /register we decided on it needing:
     * - 8+ characters 
     * - At least 1 uppercase letter
     * - At least 1 lowercase letter
     * - At least 1 number
     * - At least 1 symbol (non-letter and non-number)
     */
    @ParameterizedTest
    @ValueSource(strings = {"", "a", "1", "!", "aB1$", "Controller", "Cheezits1", "corn_c0b", "ABCD123!"})
    void updatePassword_newPasswordIsWeak_fails(String password) throws Exception {
        mockMvc.perform(post("/update-password")
            .param("oldPassword", USER_PWORD)
            .param("newPassword", password)
            .param("confirmPassword", password)
            ).andExpect(status().isBadRequest())
            .andExpect(content().string(containsString(WEAK_PASSWORD_MESSAGE)));
    }

    /**
     * U22 AC5 - Given I am on the change password form,
     *          when I enter fully compliant details,
     *      ==> then my password is updated,
     *          and an email is sent to my email address to confirm that my password was updated.
     */
    @Test
    @Disabled("""
            Recently, 'change password' and 'send email' was rolled into the single method `UserService#updatePassword()`.
            Because of this, you can no longer test either of these outcomes individually with mocking.
            HOW TO FIX: Either get complex spys and mocks set up (I tried, it just caused ContextErrors),
                            OR delete this test.
            """)
    void updatePassword_validForm_passwordIsUpdated() throws Exception {
        mockMvc.perform(post("/update-password")
                .param("oldPassword", USER_PWORD)
                .param("newPassword", NEW_PWORD)
                .param("confirmPassword", NEW_PWORD)
            ).andExpect(status().is3xxRedirection());
        
        assertEquals(HASHED_PW, testUser.getPassword(), "Password was not updated");
        verify(mockUserService, times(1)).updateOrAddUser(testUser);
    }

    /**
     * U22 AC5 - Given I am on the change password form,
     *          when I enter fully compliant details,
     *          then my password is updated,
     *    ==>   and an email is sent to my email address to confirm that my password was updated.
     */
    @Test
    void updatePassword_validForm_emailIsSent() throws Exception {
        mockMvc.perform(post("/update-password")
                .param("oldPassword", USER_PWORD)
                .param("newPassword", NEW_PWORD)
                .param("confirmPassword", NEW_PWORD)
            ).andExpect(status().is3xxRedirection());

        // The method `updatePassword()` will send an email.
        verify(mockUserService, times(1)).updatePassword(any(), anyString());
    }
    
}
