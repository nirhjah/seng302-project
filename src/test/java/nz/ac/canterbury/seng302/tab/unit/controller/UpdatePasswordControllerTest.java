package nz.ac.canterbury.seng302.tab.unit.controller;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static nz.ac.canterbury.seng302.tab.controller.UpdatePasswordController.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.hibernate.cfg.NotYetImplementedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UpdatePasswordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService mockUserService;

    @MockBean
    private UserRepository mockUserRepository;

    // Default values
    private static final String USER_FNAME = "Test";
    private static final String USER_LNAME = "User";
    private static final String USER_DOB = "2000-01-01";
    private static final String USER_EMAIL = "test@example.org";
    private static final String USER_PWORD = "super_insecure";
    private static final String USER_CITY = "Christchurch";
    private static final String USER_COUNTRY = "New Zealand";
    
    private static final String NEW_PWORD = "B4ttery_St4ple";

    @BeforeEach
    void beforeEach() throws IOException {
        Date userDOB;
        try {
            // Have to catch a constant parse exception annoyingly
            userDOB = new SimpleDateFormat("YYYY-mm-dd").parse(USER_DOB);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Location testLocation = new Location(null, null, null, USER_CITY,
                null, USER_COUNTRY);
        User testUser = new User(USER_FNAME, USER_LNAME, userDOB, USER_EMAIL, USER_PWORD, testLocation);

        when(mockUserService.getCurrentUser()).thenReturn(Optional.of(testUser));
    }

    /**
     * A basic GET check for the form
     */
    @Test
    void canAccessUpdatePassword() throws Exception {
        mockMvc.perform(get("/updatePassword"))
                .andExpect(status().isOk());
    }

    /**
     * A basic test if the form can be posted
     */
    @Test
    void updatePassword_validForm_succeeds() throws Exception {
        mockMvc.perform(post("/updatePassword")
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
        mockMvc.perform(post("/updatePassword")
            .param("oldPassword", "TheWrongPassword")
            .param("newPassword", NEW_PWORD)
            .param("confirmPassword", NEW_PWORD)
            ).andExpect(content().string(contains(WRONG_OLD_PASSWORD_MSG)));
    }

    /**
     * U22 AC3 - Given I am on the change password form,
     *              and I enter two different passwords in “new” and “retype password” fields,
     *              when I hit the save button,
     *              then an error message tells me the passwords do not match.
     */
    @Test
    void updatePassword_newAndRetypeDoNotMatch_fails() throws Exception {
        mockMvc.perform(post("/updatePassword")
            .param("oldPassword", USER_PWORD)
            .param("newPassword", NEW_PWORD)
            .param("confirmPassword", "wrong")
            ).andExpect(status().isBadRequest())
            .andExpect(content().string(contains(PASSWORD_MISMATCH_MSG)));
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
     */
    @Test
    void updatePassword_newPasswordIsWeak_fails() throws Exception {
        throw new NotYetImplementedException();
        // mockMvc.perform(post("/updatePassword")
        //     .param("oldPassword", USER_PWORD)
        //     .param("newPassword", NEW_PWORD)
        //     .param("confirmPassword", NEW_PWORD)
        //     ).andExpect(status().isBadRequest())
        //     .andExpect(content().string(contains(PASSWORD_MISMATCH_MSG)));
    }
}
