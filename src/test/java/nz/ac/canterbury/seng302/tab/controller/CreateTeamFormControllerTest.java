package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;

import nz.ac.canterbury.seng302.tab.service.SportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CreateTeamFormControllerTest {
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

    @MockBean
    private TeamService mockTeamService;

    @MockBean
    private SportService mockSportService;

    static final Long TEAM_ID = 1L;

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
        when(mockUserService.getCurrentUser()).thenReturn(Optional.of(testUser));
        when(mockUserService.emailIsInUse(anyString())).thenReturn(false);
    }

    /**
     * Miminimum amount of fields filled with valid input for post request
     *
     * @throws Exception thrown if Mocking fails
     */
    @Test
    void whenAFieldsValid_return302() throws Exception {
        mockMvc.perform(post("/createTeam", 42L)
                        .param("teamID", "1")
                        .param("name", "name")
                        .param("sport", "hockey-team a'b")
                        .param("addressLine1", "addressline1")
                        .param("addressLine2", "addressline2")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "fghj").param("suburb", "ilam"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:./profile?teamID=1"));
    }

    /**
     * The Team Name is Invalid according to the regex, as it contains invalid
     * char: ^
     *
     * @throws Exception thrown if Mocking fails
     */
    @Test
    void whenTeamNameFieldIsInvalid_return302() throws Exception {
        mockMvc.perform(post("/createTeam", 42L)
                        .param("teamID", "1")
                        .param("name", "test^team")
                        .param("sport", "hockey")
                        .param("addressLine1", "addressline1")
                        .param("addressLine2", "addressline2")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "fghj")
                        .param("suburb", "ilam"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:./createTeam?invalid_input=1&edit=1"));
    }

    /**
     * The Sport name is Invalid as it containings invalid char: #
     *
     * @throws Exception thrown if Mocking fails
     */
    @Test
    void whenSportFieldIsInvalid_return302() throws Exception {
        mockMvc.perform(post("/createTeam", 42L).param("teamID", "1")
                        .param("name", "test")
                        .param("sport", "###")
                        .param("addressLine1", "addressline1")
                        .param("addressLine2", "addressline2")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "fghj")
                        .param("suburb", "ilam"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:./createTeam?invalid_input=1&edit=1"));
    }

    /**
     * The City name is invalid as it contains invalid char: $
     *
     * @throws Exception thrown if Mocking fails
     */
    @Test
    void whenCityIsInvalid_return302() throws Exception {
        mockMvc.perform(post("/createTeam", 42L).param("teamID", "1")
                .param("name", "test")
                .param("sport", "hockey")
                .param("addressLine1", "addressline1")
                .param("addressLine2", "addressline2")
                .param("city", "$Christchurch")
                .param("country", "New Zealand")
                .param("postcode", "fghj")
                .param("suburb", "ilam"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:./createTeam?invalid_input=1&edit=1"));
    }

    /**
     * The Team name is invalid as it contains invalid chars (which are valid for
     * sport and location): - '
     *
     * @throws Exception thrown if Mocking fails
     */
    @Test
    void whenTeamNameFieldIsInvalidWithCharsValidForSport_return302() throws
            Exception {
        mockMvc.perform(post("/createTeam", 42L)
                .param("teamID", "1")
                .param("name", "test-team's")
                .param("sport", "hockey")
                .param("addressLine1", "addressline1")
                .param("addressLine2", "addressline2")
                .param("city", "Christchurch")
                .param("country", "New Zealand")
                .param("postcode", "fghj")
                .param("suburb", "ilam"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:./createTeam?invalid_input=1&edit=1"));
    }

    /**
     * The sport name is invalid as it contains invalid chars (which are valid for
     * team): . { } and numbers
     *
     * @throws Exception thrown if Mocking fails
     */
    @Test
    void whenSportFieldIsInvalidWithCharsValidForTeam_return302() throws
            Exception {
        mockMvc.perform(post("/createTeam", 42L)
                        .param("teamID", "1")
                        .param("name", "test")
                        .param("sport", "123.123{123}")
                .param("addressLine1", "addressline1")
                .param("addressLine2", "addressline2")
                .param("city", "Christchurch")
                .param("country", "New Zealand")
                .param("postcode", "fghj")
                .param("suburb", "ilam"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:./createTeam?invalid_input=1&edit=1"));
    }

    /**
     * The city name is invalid as it contains invalid chars (which are valid
     * for team): . { } and numbers
     *
     * @throws Exception thrown if Mocking fails
     */
    @Test
    void whenCityIsInvalidWithCharsValidForTeam__return302() throws Exception {
        mockMvc.perform(post("/createTeam", 42L).param("teamID", "1")
                        .param("name", "test").param("sport", "hockey")                 .param("location", "abc123'{}.a")
                .param("addressLine1", "abc123'{}.a1")
                .param("addressLine2", "addressline2")
                .param("city", "Christchurch")
                .param("country", "N^&*ew Zealand")
                .param("postcode", "56fghj")
                .param("suburb", "^&*ilam")).andExpect(status().isFound())
                .andExpect(view().name("redirect:./createTeam?invalid_input=1&edit=1"));
    }

    /**
     * The country name is invalid as it contains invalid chars (which are valid
     * for team): . { } and numbers
     *
     * @throws Exception thrown if Mocking fails
     */
    @Test
    void whenCountryIsInvalidWithCharsValidForTeam__return302() throws Exception {
        mockMvc.perform(post("/createTeam", 42L).param("teamID", "1")
                        .param("name", "test").param("sport", "hockey")
                        .param("addressLine1", "abc123'{}.a1")
                        .param("addressLine2", "addressline2")
                        .param("city", "Christchurch")
                        .param("country", "45678New Zealand")
                        .param("postcode", "56fghj")
                        .param("suburb", "ilam")).andExpect(status().isFound())
                .andExpect(view().name("redirect:./createTeam?invalid_input=1&edit=1"));
    }

    /**
     * The view will be redirected to the url with invalid input as the suburb contains invalid chars
     *
     * @throws Exception
     */
    @Test
    void whenSuburbIsInvalidWithCharsValidForTeam__return302() throws Exception {
        mockMvc.perform(post("/createTeam", 42L).param("teamID", "1")
                        .param("name", "test").param("sport", "hockey")
                        .param("addressLine1", "addressline1")
                        .param("addressLine2", "addressline2")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "56fghj")
                        .param("suburb", "ilam^")).andExpect(status().isFound())
                .andExpect(view().name("redirect:./createTeam?invalid_input=1&edit=1"));
    }

    /**
     * The view will be redirected to the url with invalid input as the postcode contains invalid chars
     *
     * @throws Exception
     */
    @Test
    void whenPostcodeIsInvalidWithCharsValidForTeam__return302() throws Exception {
        mockMvc.perform(post("/createTeam", 42L).param("teamID", "1")
                        .param("name", "test")
                        .param("sport", "hockey")
                        .param("addressLine1", "addressline1")
                        .param("addressLine2", "addressline2")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "56$%^fghj")
                        .param("suburb", "ilam")).andExpect(status().isFound())
                .andExpect(view().name("redirect:./createTeam?invalid_input=1&edit=1"));
    }

    /**
     * Team will be created and redirected to profile form when address line 1 is empty
     *
     * @throws Exception
     */
    @Test
    void whenAllFieldsAreValidButAddressLine1IsEmpty__return302() throws Exception {
        mockMvc.perform(post("/createTeam", 42L)
                        .param("teamID", "1")
                        .param("name", "name")
                        .param("sport", "hockey-team a'b")
                        .param("addressLine1", "")
                        .param("addressLine2", "addressline2")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "fghj").param("suburb", "ilam"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:./profile?teamID=1"));
    }

    /**
     * Team will be created and redirected to profile form when address line 2 is empty
     * @throws Exception
     */
    @Test
    void whenAllFieldsAreValidButAddressLine2IsEmpty__return302() throws Exception {
        mockMvc.perform(post("/createTeam", 42L)
                        .param("teamID", "1")
                        .param("name", "name")
                        .param("sport", "hockey-team a'b")
                        .param("addressLine1", "addressline1")
                        .param("addressLine2", "")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "fghj").param("suburb", "ilam"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:./profile?teamID=1"));
    }

    /**
     * Team will be created and redirected to profile form when suburb is empty
     *
     * @throws Exception
     */
    @Test
    void whenAllFieldsAreValidButSuburbIsEmpty__return302() throws Exception {
        mockMvc.perform(post("/createTeam", 42L)
                        .param("teamID", "1")
                        .param("name", "name")
                        .param("sport", "hockey-team a'b")
                        .param("addressLine1", "addressline1")
                        .param("addressLine2", "addressline1")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "fghj").param("suburb", ""))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:./profile?teamID=1"));
    }

    /**
     * Team will be created and redirected to profile form when postcode is empty
     *
     * @throws Exception
     */
    @Test
    void whenAllFieldsAreValidButPostcodeIsEmpty__return302() throws Exception {
        mockMvc.perform(post("/createTeam", 42L)
                        .param("teamID", "1")
                        .param("name", "name")
                        .param("sport", "hockey-team a'b")
                        .param("addressLine1", "addressline1")
                        .param("addressLine2", "addressline2")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "").param("suburb", ""))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:./profile?teamID=1"));
    }

    /**
     * Team will be created and redirected to profile form when optional fields are empty
     *
     * @throws Exception
     */
    @Test
    void whenAllOptionalFieldsAreEmpty__return302() throws Exception{
        mockMvc.perform(post("/createTeam", 42L)
                        .param("teamID", "1")
                        .param("name", "name")
                        .param("sport", "hockey-team a'b")
                        .param("addressLine1", "")
                        .param("addressLine2", "")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "").param("suburb", ""))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:./profile?teamID=1"));

    }

    @Test
    void whenSportIsNewAndValid_checkThatItWasSaved() throws Exception {
        mockMvc.perform(post("/createTeam", 42L)
                        .param("teamID", TEAM_ID.toString())
                        .param("name", "test.{team1}")
                        .param("sport", "hockey-team a'b")
                        .param("addressLine1", "")
                        .param("addressLine2", "")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "7897").param("suburb", ""))
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("**/profile?teamID="+TEAM_ID));
        verify(mockSportService, times(1)).addSport(any());
    }
}
