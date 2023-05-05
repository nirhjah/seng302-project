package nz.ac.canterbury.seng302.tab.unit.controller;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.SportService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UpdatePasswordControllerTests {

    private static final String USER_FNAME = "Test";
    private static final String USER_LNAME = "User";
    private static final String USER_EMAIL = "test@email.org";
    private static final String USER_DOB = "2000-01-01";
    private static final String USER_PWORD = "super_insecure";
    private static final String USER_ADDRESS_LINE_1 = "1 Street Road";
    private static final String USER_ADDRESS_LINE_2 = "A";
    private static final String USER_SUBURB = "Riccarton";
    private static final String USER_POSTCODE = "8000";
    private static final String USER_CITY = "Christchurch";
    private static final String USER_COUNTRY = "New Zealand";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService mockUserService;

    private String token;

    @BeforeEach
    void beforeEach() throws IOException {
        Date userDOB;
        try {
            // Have to catch a constant parse exception annoyingly
            userDOB = new SimpleDateFormat("YYYY-mm-dd").parse(USER_DOB);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Location testLocation = new Location(USER_ADDRESS_LINE_1, USER_ADDRESS_LINE_2, USER_SUBURB, USER_CITY, USER_POSTCODE, USER_COUNTRY);

        User testUser = new User(USER_FNAME, USER_LNAME, userDOB, USER_EMAIL, USER_PWORD, testLocation);
        //generate a token for user
        testUser.generateToken(mockUserService, 1);

        token = testUser.getToken();

        when(mockUserService.getCurrentUser()).thenReturn(Optional.of(testUser));
        when(mockUserService.emailIsInUse(anyString())).thenReturn(false);
        when(mockUserService.findByToken(token)).thenReturn(Optional.of(testUser));

    }


    /**
     * Test getting the update password page with an invalid token
     * @throws Exception thrown if Mocking fails
     */
    @Test
    public void testUpdatePasswordPageWithInvalidToken_return302() throws Exception {
        mockMvc.perform(get("/update-password/" + "x")
        ).andExpect(status().isFound()).andExpect(view().name("redirect:/login"));

    }


    /**
     * Test when an invalid password is submitted
     *
     * @throws Exception thrown if Mocking fails
     */
    @Test
    @WithMockUser()
    void whenPasswordIsInvalid_return302() throws
            Exception {
        String URL = "/update-password/" + token;
        mockMvc.perform(post(URL)
                        .param("password", "a"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("updatePassword"));
    }
}
