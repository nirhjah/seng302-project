package nz.ac.canterbury.seng302.tab.unit.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

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

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.SportService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;

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

    private static final Long TEAM_ID = 777L;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService mockUserService;

    @MockBean
    private TeamService mockTeamService;

    @MockBean
    private SportService mockSportService;

    private User user;

    private Team team;


    @BeforeEach
    void beforeEach() throws IOException {
        Location userLocation = new Location(USER_ADDRESS_LINE_1, USER_ADDRESS_LINE_2, USER_SUBURB, USER_CITY, USER_POSTCODE, USER_COUNTRY);
        Location teamLocation = new Location("3 Test Lane", "5 Mock Road", "Ilam", "Christchurch", "8041", "New Zealand");
        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", userLocation);
        team = spy(new Team("testName", "testSport", teamLocation, user));
        doReturn(TEAM_ID).when(team).getTeamId();

        when(mockUserService.getCurrentUser()).thenReturn(Optional.of(user));
        when(mockUserService.emailIsInUse(anyString())).thenReturn(false);
        when(mockTeamService.addTeam(team)).thenReturn(team);
        when(mockTeamService.getTeam(team.getTeamId())).thenReturn(team);
        when(mockTeamService.updateTeam(any())).thenReturn(team);
    }


    /** Try to get the Create Team form */
    @Test
    void getCreateTeamForm() throws Exception {
        mockMvc.perform(get("/create-team"))
                .andExpect(status().isOk())
                .andExpect(view().name("createTeamForm"));
    }

    /** Try to get the Edit Team form */
    @Test
    void getEditTeamForm() throws Exception {
        mockMvc.perform(get("/create-team")
                    .param("edit", TEAM_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("createTeamForm"));
    }

    /** Check if the Edit Team form is pre-populated */
    @Test
    void getEditTeamForm_formIsPrepopulated() throws Exception {
        mockMvc.perform(get("/create-team")
                    .param("edit", TEAM_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("createTeamForm"))
                .andExpect(content().string(containsString(team.getName())))
                .andExpect(content().string(containsString(team.getSport())))
                .andExpect(content().string(containsString(team.getLocation().getAddressLine1())))
                .andExpect(content().string(containsString(team.getLocation().getAddressLine2())))
                .andExpect(content().string(containsString(team.getLocation().getCity())))
                .andExpect(content().string(containsString(team.getLocation().getCountry())))
                .andExpect(content().string(containsString(team.getLocation().getSuburb())))
                .andExpect(content().string(containsString(team.getLocation().getPostcode())));
    }

    /**
     * Miminimum amount of fields filled with valid input for post request
     *
     * @throws Exception thrown if Mocking fails
     */
    @Test
    void whenAFieldsValid_return302() throws Exception {
        mockMvc.perform(post("/create-team")
                        .param("teamID", String.valueOf(TEAM_ID))
                        .param("name", "name")
                        .param("sport", "hockey-team a'b")
                        .param("addressLine1", "addressline1")
                        .param("addressLine2", "addressline2")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "fghj")
                        .param("suburb", "ilam"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:./team-info?teamID="+TEAM_ID));
    }

    /**
     * The Team Name is Invalid according to the regex, as it contains invalid
     * char: ^
     *
     * @throws Exception thrown if Mocking fails
     */
    @Test
    void whenTeamNameFieldIsInvalid_return302() throws Exception {
        mockMvc.perform(post("/create-team", 42L)
                        .param("teamID", String.valueOf(TEAM_ID))
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
        mockMvc.perform(post("/create-team", 42L)
                        .param("teamID", String.valueOf(TEAM_ID))
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
        mockMvc.perform(post("/create-team", 42L)
                        .param("teamID", String.valueOf(TEAM_ID))
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
        mockMvc.perform(post("/create-team", 42L)
                        .param("teamID", String.valueOf(TEAM_ID))
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
        mockMvc.perform(post("/create-team", 42L)
                        .param("teamID", String.valueOf(TEAM_ID))
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
        mockMvc.perform(post("/create-team", 42L)
                        .param("teamID", String.valueOf(TEAM_ID))
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
        mockMvc.perform(post("/create-team", 42L)
                        .param("teamID", String.valueOf(TEAM_ID))
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
        mockMvc.perform(post("/create-team", 42L)
                        .param("teamID", String.valueOf(TEAM_ID))
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
        mockMvc.perform(post("/create-team", 42L)
                        .param("teamID", String.valueOf(TEAM_ID))
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
        mockMvc.perform(post("/create-team", 42L)
                        .param("teamID", String.valueOf(TEAM_ID))
                        .param("name", "name")
                        .param("sport", "hockey-team a'b")
                        .param("addressLine1", "")
                        .param("addressLine2", "addressline2")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "fghj")
                        .param("suburb", "ilam"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:./team-info?teamID=" + TEAM_ID));
    }

    /**
     * Team will be created and redirected to profile form when address line 2 is empty
     *
     * @throws Exception
     */
    @Test
    void whenAllFieldsAreValidButAddressLine2IsEmpty__return302() throws Exception {
        mockMvc.perform(post("/create-team", 42L)
                        .param("teamID", String.valueOf(TEAM_ID))
                        .param("name", "name")
                        .param("sport", "hockey-team a'b")
                        .param("addressLine1", "addressline1")
                        .param("addressLine2", "")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "fghj")
                        .param("suburb", "ilam"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:./team-info?teamID=" + TEAM_ID));
    }

    /**
     * Team will be created and redirected to profile form when suburb is empty
     *
     * @throws Exception
     */
    @Test
    void whenAllFieldsAreValidButSuburbIsEmpty__return302() throws Exception {
        mockMvc.perform(post("/create-team", 42L)
                        .param("teamID", String.valueOf(TEAM_ID))
                        .param("name", "name")
                        .param("sport", "hockey-team a'b")
                        .param("addressLine1", "addressline1")
                        .param("addressLine2", "addressline1")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "fghj")
                        .param("suburb", ""))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:./team-info?teamID=" + TEAM_ID));
    }

    /**
     * Team will be created and redirected to profile form when postcode is empty
     *
     * @throws Exception
     */
    @Test
    void whenAllFieldsAreValidButPostcodeIsEmpty__return302() throws Exception {
        mockMvc.perform(post("/create-team", 42L)
                        .param("teamID", String.valueOf(TEAM_ID))
                        .param("name", "name")
                        .param("sport", "hockey-team a'b")
                        .param("addressLine1", "addressline1")
                        .param("addressLine2", "addressline2")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "")
                        .param("suburb", ""))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:./team-info?teamID=" + TEAM_ID));
    }

    /**
     * Team will be created and redirected to profile form when optional fields are empty
     *
     * @throws Exception
     */
    @Test
    void whenAllOptionalFieldsAreEmpty__return302() throws Exception {
        mockMvc.perform(post("/create-team", 42L)
                        .param("teamID", String.valueOf(TEAM_ID))
                        .param("name", "name")
                        .param("sport", "hockey-team a'b")
                        .param("addressLine1", "")
                        .param("addressLine2", "")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "")
                        .param("suburb", ""))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:./team-info?teamID=" + TEAM_ID));

    }

    @Test
    void whenSportIsNewAndValid_checkThatItWasSaved() throws Exception {
        mockMvc.perform(post("/create-team", 42L)
                        .param("teamID", String.valueOf(TEAM_ID))
                        .param("name", "{}.test.{team1}")
                        .param("sport", " '-hockey-team a'b")
                        .param("addressLine1", "")
                        .param("addressLine2", "")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "7897")
                        .param("suburb", ""))
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("**/team-info?teamID=" + TEAM_ID));
        verify(mockSportService, times(1)).addSport(any());
    }

    @Test
    public void testGeneratingNewToken() throws Exception {
        mockMvc.perform(post("/generateTeamToken", 42L)
                        .param("teamID", String.valueOf(TEAM_ID)))
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("**/team-info?teamID=" + TEAM_ID));

        verify(mockTeamService, times(1)).updateTeam(any());
        Assertions.assertNotNull(team.getToken());
    }

    /** 
     * Tests whether the rendered form contains the appropriate values when an error occurs
    */
    @Test
    void whenFormIsInvalid_formRemainsPopulated() throws Exception {
        String badName = "INV@L!D_N@ME";
        String badSport = "BEING SUPER EV!L";
        String badAddr1 = "PalaceOfDoingBadThings";
        String badAddr2 = "RentalFlatOfDoingSlightlyDeviousThings";
        String badCity = "Eviltropolis";
        String badCountry = "Evil States of America";
        String badPostcode = "I love posting code";
        String badSuburb = "Where all the villains live";
        mockMvc.perform(post("/create-team")
                        .param("teamID", String.valueOf(TEAM_ID))
                        .param("name", badName)
                        .param("sport", badSport)
                        .param("addressLine1", badAddr1)
                        .param("addressLine2", badAddr2)
                        .param("city", badCity)
                        .param("country", badCountry)
                        .param("postcode", badPostcode)
                        .param("suburb", badSuburb))
                .andExpect(status().isBadRequest())     // This only works if we submit a bad request
                .andExpect(content().string(containsString(badName)))
                .andExpect(content().string(containsString(badSport)))
                .andExpect(content().string(containsString(badAddr1)))
                .andExpect(content().string(containsString(badAddr2)))
                .andExpect(content().string(containsString(badCity)))
                .andExpect(content().string(containsString(badCountry)))
                .andExpect(content().string(containsString(badPostcode)))
                .andExpect(content().string(containsString(badSuburb)));
    }

    // =================================================================
    // The following tests address a rendering issue, where a failed
    // POST causes the returned form to say the opposite of the page
    // "Create Team" <=> "Edit Team" 
    @Test
    void createTeam_onGet_pageSaysCreateTeam() throws Exception {
        mockMvc.perform(get("/create-team"))
            .andExpect(status().isOk())
            // "Create Team" should be in the title & header, and have a "Submit" button.
            .andExpect(model().attribute("isEditing", false))
            .andExpect(content().string(containsString("<title>Create Team</title>")))
            // I can't get a Hamcrest regex to just match "element with Create Team", so
            // I need the extra attributes which might make it flakey
            .andExpect(content().string(containsString("<h1 data-cy=\"header\">Create Team</h1>")))
            .andExpect(content().string(containsString("<button type=\"submit\">Create</button>")));
    }

    @Test
    void createTeam_onSubmissionFail_stillSaysCreateTeam() throws Exception {
        mockMvc.perform(post("/create-team"))    // A blank form is invalid
            .andExpect(status().isBadRequest())
            // "Create Team" should be in the title & header, and have a "Submit" button.
            .andExpect(model().attribute("isEditing", false))
            .andExpect(content().string(containsString("<title>Create Team</title>")))
            .andExpect(content().string(containsString("<h1 data-cy=\"header\">Create Team</h1>")))
            .andExpect(content().string(containsString("<button type=\"submit\">Create</button>")));
    }

    @Test
    void editTeam_onGet_pageSaysEditTeam() throws Exception {
        doReturn(team).when(mockTeamService).getTeam(TEAM_ID);
        mockMvc.perform(get("/create-team")
                        .param("edit", TEAM_ID.toString()))
            .andExpect(status().isOk())
            // "Edit Team" should be in the title & header, and have a "Save" button.
            .andExpect(model().attribute("isEditing", true))
            .andExpect(content().string(containsString("<title>Edit Team</title>")))
            .andExpect(content().string(containsString("<h1 data-cy=\"header\">Edit Team</h1>")))
            .andExpect(content().string(containsString("<button type=\"submit\">Save</button>")));
    }

    @Test
    void editTeam_onSubmissionFail_stillSaysEditTeam() throws Exception {
        doReturn(team).when(mockTeamService).getTeam(TEAM_ID);
        mockMvc.perform(post("/create-team")
                            .param("teamID", TEAM_ID.toString()))
            .andExpect(status().isBadRequest())
            // "Edit Team" should be in the title & header, and have a "Save" button.
            .andExpect(model().attribute("isEditing", true))
            .andExpect(content().string(containsString("<title>Edit Team</title>")))
            .andExpect(content().string(containsString("<h1 data-cy=\"header\">Edit Team</h1>")))
            .andExpect(content().string(containsString("<button type=\"submit\">Save</button>")));
    }


}
