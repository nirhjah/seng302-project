package nz.ac.canterbury.seng302.tab.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;


import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
public class CreateTeamFormControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService mockUserService;

    @MockBean
    private TeamService mockTeamService;

    static final Long TEAM_ID = 1L;

    @BeforeEach
    void beforeEach() {
        User testUser = new User("Test", "User", "test@email.com", "awfulPassword");

        when(mockUserService.getCurrentUser()).thenReturn(Optional.of(testUser));

    }

    /**
     * All fields are valid according to the regex
     * @throws Exception thrown if Mocking fails
     */
    @Test
    void creatingTeam_whenAllFieldsValid_redirectToTeamProfile() throws Exception {
        
        Team mockTeam = new Team("Test", "Test", "Test");
        mockTeam.setTeamId(TEAM_ID);
        when(mockTeamService.getTeam(TEAM_ID)).thenReturn(null);
        when(mockTeamService.addTeam(any())).thenReturn(mockTeam);
        mockMvc.perform(post("/createTeam", 42L)
                .param("name", "{test.team 1}")
                .param("sport", "hockey-team a'b")
                .param("location", "christchurch"))
            .andExpect(status().isFound())
            .andExpect(redirectedUrlPattern("**/profile?teamID="+TEAM_ID));
    }
    /**
     * All fields are valid according to the regex
     * @throws Exception thrown if Mocking fails
     */
    @Test
    void editingTeam_whenAllFieldsValid_redirectToTeamProfile() throws Exception {

        Team mockTeam = new Team("Test", "Test", "Test");
        mockTeam.setTeamId(TEAM_ID);
        when(mockTeamService.getTeam(TEAM_ID)).thenReturn(mockTeam);
        when(mockTeamService.updateTeam(any())).thenReturn(mockTeam);

        mockMvc.perform(post("/createTeam", 42L)
                .param("teamID", TEAM_ID.toString())
                .param("name", "{test.team 1}")
                .param("sport", "hockey-team a'b")
                .param("location", "christchurch"))
            .andExpect(status().isFound())
            .andExpect(redirectedUrlPattern("**/profile?teamID="+TEAM_ID));
    }

    /**
     * The Team Name is Invalid according to the regex, as it contains invalid char: ^
     * @throws Exception thrown if Mocking fails
     */
    @Test
    void whenTeamNameFieldIsInvalid_return302() throws Exception {
        mockMvc.perform(post("/createTeam", 42L)
                .param("teamID", "1")
                .param("name", "test^team")
                .param("sport", "hockey")
                .param("location", "christchurch"))
            .andExpect(status().isFound())
            .andExpect(view().name("redirect:./createTeam?invalid_input=1&edit=1"));
    }

    /**
     * The Sport name is Invalid as it containings invalid char: #
     * @throws Exception thrown if Mocking fails
     */
    @Test
    void whenSportFieldIsInvalid_return302() throws Exception {
        mockMvc.perform(post("/createTeam", 42L)
                .param("teamID", "1")
                .param("name", "test")
                .param("sport", "###")
                .param("location", "christchurch"))
            .andExpect(status().isFound())
            .andExpect(view().name("redirect:./createTeam?invalid_input=1&edit=1"));
    }

    /**
     * The Location name is invalid as it contains invalid char: $
     * @throws Exception thrown if Mocking fails
     */
    @Test
    void whenLocationIsInvalid_return302() throws Exception {
        mockMvc.perform(post("/createTeam", 42L).param("teamID", "1")
                        .param("name", "test").param("sport", "hockey")
                        .param("location", "$sumner$")).andExpect(status().isFound())
                .andExpect(view().name("redirect:./createTeam?invalid_input=1&edit=1"));
    }

    /**
     * The Team name is invalid as it contains invalid chars (which are valid for sport and location): - '
     * @throws Exception thrown if Mocking fails
     */
    @Test
    void whenTeamNameFieldIsInvalidWithCharsValidForSport_return302() throws Exception {
        mockMvc.perform(post("/createTeam", 42L).param("teamID", "1")
                        .param("name", "test-team's").param("sport", "hockey")
                        .param("location", "christchurch")).andExpect(status().isFound())
                .andExpect(view().name("redirect:./createTeam?invalid_input=1&edit=1"));
    }

    /**
     * The sport name is invalid as it contains invalid chars (which are valid for team):  . { } and numbers
     * @throws Exception thrown if Mocking fails
     */
    @Test
    void whenSportFieldIsInvalidWithCharsValidForTeam_return302() throws Exception {
        mockMvc.perform(post("/createTeam", 42L).param("teamID", "1")
                        .param("name", "test").param("sport", "123.123{123}")
                        .param("location", "christchurch")).andExpect(status().isFound())
                .andExpect(view().name("redirect:./createTeam?invalid_input=1&edit=1"));
    }

    /**
     * The location name is invalid as it contains invalid chars (which are valid for team):  . { } and numbers
     * @throws Exception thrown if Mocking fails
     */
    @Test
    void whenLocationIsInvalidWithCharsValidForTeam__return302() throws Exception {
        mockMvc.perform(post("/createTeam", 42L).param("teamID", "1")
                        .param("name", "test").param("sport", "hockey")
                        .param("location", "abc123'{}.a")).andExpect(status().isFound())
                .andExpect(view().name("redirect:./createTeam?invalid_input=1&edit=1"));
    }
}
