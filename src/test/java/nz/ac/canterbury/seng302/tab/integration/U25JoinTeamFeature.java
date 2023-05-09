package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class U25JoinTeamFeature {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private UserService mockUserService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;
    @MockBean
    private TeamService mockTeamService;

    private User user;

    private Team team;



    @BeforeEach
    public void beforeEach() throws IOException {
        Location testLocation = new Location(null, null, null, "CHCH", null, "NZ");
        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", testLocation);
        userRepository.save(user);


        team = new Team("TestTeam", "Hockey", testLocation);
        teamRepository.save(team);

        doNothing().when(team).generateToken(mockTeamService);




        Mockito.when(mockUserService.getCurrentUser()).thenReturn(Optional.of(user));

    }



    @Given("I am on the home page")
    @WithMockUser()
    public void i_am_on_the_home_page() throws Exception {
        mockMvc.perform(get("/home")).andExpect(status().isOk());
    }

    @When("I click the my teams button")
    @WithMockUser()
    public void i_click_the_my_teams_button() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        mockMvc.perform(get("/my-teams")).andExpect(status().isFound());
    }

    @Then("I see the my teams page")
    @WithMockUser()
    public void i_see_the_my_teams_page() throws Exception {
        mockMvc.perform(get("/my-teams")).andExpect(status().isFound());
    }


    @Given("I am on the my teams page")
    @WithMockUser()
    public void i_am_on_the_my_teams_page() throws Exception {
        mockMvc.perform(get("/my-teams")).andExpect(status().isFound());
    }

    @When("I input a valid team invitation token")
    @WithMockUser()
    public void i_input_a_valid_team_invitation_token() throws Exception {

        if (team == null) {
            team = new Team("TestTeam", "Hockey", new Location(null, null, null, "chch", null, "nz"));
        }

        if (user == null) {
            user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", new Location(null, null, null, "chch", null, "nz"));

        }


        user.joinTeam(team);
       /* mockMvc.perform(post("/my-teams", 42L)
                .with(csrf())
                .param("token", team.getToken()));*/
    }

    @Then("I am added as a member to that team")
    @WithMockUser()
    public void i_am_added_as_a_member_to_that_team() {

        Assertions.assertTrue(user.getJoinedTeams().size() > 0);

    }


    @When("I input an invalid team invitation token")
    @WithMockUser()
    public void i_input_an_invalid_team_invitation_token() throws Exception {
        mockMvc.perform(post("/my-teams", 42L)
                .with(csrf())
                .param("token", "invalidtoken"));

    }

    @Then("An error message tells me the token is invalid")
    @WithMockUser()
    public void an_error_message_tells_me_the_token_is_invalid() throws Exception {
        mockMvc.perform(post("/my-teams", 42L)
                .with(csrf())
                .param("token", "invalidtoken")).andExpect(status().isBadRequest()).andExpect(redirectedUrl("/my-teams?page=1"));

    }



    @Given("I have joined a new team")
    public void i_have_joined_a_new_team() throws IOException {

        if (team == null) {
            team = new Team("TestTeam", "Hockey", new Location(null, null, null, "chch", null, "nz"));
        }

        if (user == null) {
            user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", new Location(null, null, null, "chch", null, "nz"));

        }
        user.joinTeam(team);
    }

    @Then("I see the team I just joined")
    public void i_see_the_team_i_just_joined() {
        Assertions.assertTrue(user.getJoinedTeams().size() > 0);
    }



}
