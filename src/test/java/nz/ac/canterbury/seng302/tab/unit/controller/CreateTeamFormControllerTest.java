package nz.ac.canterbury.seng302.tab.unit.controller;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;

import nz.ac.canterbury.seng302.tab.service.SportService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CreateTeamFormControllerTest {
    private static final String USER_ADDRESS_LINE_1 = "1 Street Road";
    private static final String USER_ADDRESS_LINE_2 = "A";
    private static final String USER_SUBURB = "Riccarton";
    private static final String USER_POSTCODE = "8000";
    private static final String USER_CITY = "Christchurch";
    private static final String USER_COUNTRY = "New Zealand";

    private static final long TEAM_ID = 777;
    private static final long USER_ID = 999;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService mockUserService;

    @MockBean
    private TeamService mockTeamService;

    @MockBean
    private SportService mockSportService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    private User user;

    private Team team;


    @BeforeEach
    void beforeEach() throws IOException {
        userRepository.deleteAll();
        teamRepository.deleteAll();
        Location testLocation = new Location(USER_ADDRESS_LINE_1, USER_ADDRESS_LINE_2, USER_SUBURB, USER_CITY, USER_POSTCODE, USER_COUNTRY);

        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", testLocation);
        team = spy(new Team("test", "Rugby", new Location("3 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand"), user));
        doReturn(TEAM_ID).when(team).getTeamId();

        when(mockUserService.getCurrentUser()).thenReturn(Optional.of(user));
        when(mockUserService.emailIsInUse(anyString())).thenReturn(false);
        when(mockTeamService.getTeam(team.getTeamId())).thenReturn(team);
        when(mockTeamService.updateTeam(any())).thenReturn(team);
    }

    /**
     * Miminimum amount of fields filled with valid input for post request
     *
     * @throws Exception thrown if Mocking fails
     */
    @Test
    void whenAFieldsValid_return302() throws Exception {
        mockMvc.perform(post("/createTeam", 42L)
                        .param("teamID", String.valueOf(team.getTeamId()))
                        .param("name", "name")
                        .param("sport", "hockey-team a'b")
                        .param("addressLine1", "addressline1")
                        .param("addressLine2", "addressline2")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "fghj")
                        .param("suburb", "ilam"))
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
                        .param("teamID", String.valueOf(team.getTeamId()))
                        .param("name", "test^team")
                        .param("sport", "hockey")
                        .param("addressLine1", "addressline1")
                        .param("addressLine2", "addressline2")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "fghj")
                        .param("suburb", "ilam"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("createTeamForm"));
    }

    /**
     * The Sport name is Invalid as it containings invalid char: #
     *
     * @throws Exception thrown if Mocking fails
     */
    @Test
    void whenSportFieldIsInvalid_return302() throws Exception {
        mockMvc.perform(post("/createTeam", 42L)
                        .param("teamID", String.valueOf(team.getTeamId()))
                        .param("name", "test")
                        .param("sport", "###")
                        .param("addressLine1", "addressline1")
                        .param("addressLine2", "addressline2")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "fghj")
                        .param("suburb", "ilam"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("createTeamForm"));
    }

    /**
     * The City name is invalid as it contains invalid char: $
     *
     * @throws Exception thrown if Mocking fails
     */
    @Test
    void whenCityIsInvalid_return400() throws Exception {
        mockMvc.perform(post("/createTeam", 42L)
                        .param("teamID", String.valueOf(team.getTeamId()))
                        .param("name", "test")
                        .param("sport", "hockey")
                        .param("addressLine1", "addressline1")
                        .param("addressLine2", "addressline2")
                        .param("city", "$Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "fghj")
                        .param("suburb", "ilam"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("createTeamForm"));
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
                        .param("teamID", String.valueOf(team.getTeamId()))
                        .param("name", "test-team's")
                        .param("sport", "hockey")
                        .param("addressLine1", "addressline1")
                        .param("addressLine2", "addressline2")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "fghj")
                        .param("suburb", "ilam"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("createTeamForm"));
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
                        .param("teamID", String.valueOf(team.getTeamId()))
                        .param("name", "test")
                        .param("sport", "123.123{123}")
                        .param("addressLine1", "addressline1")
                        .param("addressLine2", "addressline2")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "fghj")
                        .param("suburb", "ilam"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("createTeamForm"));
    }

    /**
     * The city name is invalid as it contains invalid chars (which are valid
     * for team): . { } and numbers
     *
     * @throws Exception thrown if Mocking fails
     */
    @Test
    void whenCityIsInvalidWithCharsValidForTeam__return400() throws Exception {
        mockMvc.perform(post("/createTeam", 42L)
                        .param("teamID", String.valueOf(team.getTeamId()))
                        .param("name", "test")
                        .param("sport", "hockey")
                        .param("location", "abc123'{}.a")
                        .param("addressLine1", "abc123'{}.a1")
                        .param("addressLine2", "addressline2")
                        .param("city", "Christchurch")
                        .param("country", "N^&*ew Zealand")
                        .param("postcode", "56fghj")
                        .param("suburb", "^&*ilam")).andExpect(status().isBadRequest())
                .andExpect(view().name("createTeamForm"));
    }

    /**
     * The country name is invalid as it contains invalid chars (which are valid
     * for team): . { } and numbers
     *
     * @throws Exception thrown if Mocking fails
     */
    @Test
    void whenCountryIsInvalidWithCharsValidForTeam__return400() throws Exception {
        mockMvc.perform(post("/createTeam", 42L)
                        .param("teamID", String.valueOf(team.getTeamId()))
                        .param("name", "test")
                        .param("sport", "hockey")
                        .param("addressLine1", "abc123'{}.a1")
                        .param("addressLine2", "addressline2")
                        .param("city", "Christchurch")
                        .param("country", "45678New Zealand")
                        .param("postcode", "56fghj")
                        .param("suburb", "ilam")).andExpect(status().isBadRequest())
                .andExpect(view().name("createTeamForm"));
    }

    /**
     * The view will be redirected to the url with invalid input as the suburb contains invalid chars
     *
     * @throws Exception
     */
    @Test
    void whenSuburbIsInvalidWithCharsValidForTeam__return400() throws Exception {
        mockMvc.perform(post("/createTeam", 42L)
                        .param("teamID", String.valueOf(team.getTeamId()))
                        .param("name", "test")
                        .param("sport", "hockey")
                        .param("addressLine1", "addressline1")
                        .param("addressLine2", "addressline2")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "56fghj")
                        .param("suburb", "ilam^")).andExpect(status().isBadRequest())
                .andExpect(view().name("createTeamForm"));
    }

    /**
     * The view will be redirected to the url with invalid input as the postcode contains invalid chars
     *
     * @throws Exception
     */
    @Test
    void whenPostcodeIsInvalidWithCharsValidForTeam__return400() throws Exception {
        mockMvc.perform(post("/createTeam", 42L)
                        .param("teamID", String.valueOf(team.getTeamId()))
                        .param("name", "test")
                        .param("sport", "hockey")
                        .param("addressLine1", "addressline1")
                        .param("addressLine2", "addressline2")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "56$%^fghj")
                        .param("suburb", "ilam")).andExpect(status().isBadRequest())
                .andExpect(view().name("createTeamForm"));
    }

    /**
     * Team will be created and redirected to profile form when address line 1 is empty
     *
     * @throws Exception
     */
    @Test
    void whenAllFieldsAreValidButAddressLine1IsEmpty__return302() throws Exception {
        mockMvc.perform(post("/createTeam", 42L)
                        .param("teamID", String.valueOf(team.getTeamId()))
                        .param("name", "name")
                        .param("sport", "hockey-team a'b")
                        .param("addressLine1", "")
                        .param("addressLine2", "addressline2")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "fghj")
                        .param("suburb", "ilam"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:./profile?teamID=" + team.getTeamId()));
    }

    /**
     * Team will be created and redirected to profile form when address line 2 is empty
     *
     * @throws Exception
     */
    @Test
    void whenAllFieldsAreValidButAddressLine2IsEmpty__return302() throws Exception {
        mockMvc.perform(post("/createTeam", 42L)
                        .param("teamID", String.valueOf(team.getTeamId()))
                        .param("name", "name")
                        .param("sport", "hockey-team a'b")
                        .param("addressLine1", "addressline1")
                        .param("addressLine2", "")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "fghj")
                        .param("suburb", "ilam"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:./profile?teamID=" + team.getTeamId()));
    }

    /**
     * Team will be created and redirected to profile form when suburb is empty
     *
     * @throws Exception
     */
    @Test
    void whenAllFieldsAreValidButSuburbIsEmpty__return302() throws Exception {
        mockMvc.perform(post("/createTeam", 42L)
                        .param("teamID", String.valueOf(team.getTeamId()))
                        .param("name", "name")
                        .param("sport", "hockey-team a'b")
                        .param("addressLine1", "addressline1")
                        .param("addressLine2", "addressline1")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "fghj")
                        .param("suburb", ""))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:./profile?teamID=" + team.getTeamId()));
    }

    /**
     * Team will be created and redirected to profile form when postcode is empty
     *
     * @throws Exception
     */
    @Test
    void whenAllFieldsAreValidButPostcodeIsEmpty__return302() throws Exception {
        mockMvc.perform(post("/createTeam", 42L)
                        .param("teamID", String.valueOf(team.getTeamId()))
                        .param("name", "name")
                        .param("sport", "hockey-team a'b")
                        .param("addressLine1", "addressline1")
                        .param("addressLine2", "addressline2")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "")
                        .param("suburb", ""))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:./profile?teamID=" + team.getTeamId()));
    }

    /**
     * Team will be created and redirected to profile form when optional fields are empty
     *
     * @throws Exception
     */
    @Test
    void whenAllOptionalFieldsAreEmpty__return302() throws Exception {
        mockMvc.perform(post("/createTeam", 42L)
                        .param("teamID", String.valueOf(team.getTeamId()))
                        .param("name", "name")
                        .param("sport", "hockey-team a'b")
                        .param("addressLine1", "")
                        .param("addressLine2", "")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "")
                        .param("suburb", ""))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:./profile?teamID=" + team.getTeamId()));

    }

    @Test
    void whenSportIsNewAndValid_checkThatItWasSaved() throws Exception {
        mockMvc.perform(post("/createTeam", 42L)
                        .param("teamID", String.valueOf(team.getTeamId()))
                        .param("name", "{}.test.{team1}")
                        .param("sport", " '-hockey-team a'b")
                        .param("addressLine1", "")
                        .param("addressLine2", "")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "7897")
                        .param("suburb", ""))
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("**/profile?teamID=" + team.getTeamId()));
        verify(mockSportService, times(1)).addSport(any());
    }

    @Test
    public void testGeneratingNewToken() throws Exception {
        mockMvc.perform(post("/generateTeamToken", 42L)
                        .param("teamID", String.valueOf(team.getTeamId())))
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("**/profile?teamID=" + team.getTeamId()));

        verify(mockTeamService, times(1)).updateTeam(any());
        Assertions.assertNotNull(team.getToken());


    }


}
