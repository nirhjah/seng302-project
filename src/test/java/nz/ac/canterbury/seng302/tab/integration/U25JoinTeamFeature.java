package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class U25JoinTeamFeature {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @MockBean
    private UserService mockUserService = mock(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamService teamService;

    private User user;

    private Team team;



    @Before
    public void setup() throws IOException {
        teamRepository.deleteAll();
        userRepository.deleteAll();
        Location testLocation = new Location(null, null, null, "CHCH", null, "NZ");
        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", testLocation);
        team = new Team("Team1", "Hockey", new Location(null, null, null, "chch", null, "nz"));
        teamRepository.save(team);
        userRepository.save(user);


        Mockito.when(mockUserService.getCurrentUser()).thenReturn(Optional.of(user));
       // Mockito.when(mockUserService.userJoinTeam(any(), any())).thenReturn(Optional.of(user));


    }

    @Given("I am on the home page")
    @WithMockUser
    public void i_am_on_the_home_page() throws Exception {

        mockMvc.perform(get("/home")).andExpect(status().isOk());
    }

    @When("I click the my teams button")
    @WithMockUser()
    public void i_click_the_my_teams_button() throws Exception {
        mockMvc.perform(get("/my-teams").param("page", "1"));
    }

    @Then("I see the my teams page")
    @WithMockUser()
    public void i_see_the_my_teams_page() throws Exception {
        mockMvc.perform(get("/my-teams").param("page", "1")).andExpect(status().isFound());
    }

    @Given("I am on the my teams page")
    public void i_am_on_the_my_teams_page() throws Exception {
        mockMvc.perform(get("/my-teams").param("page", "1"));


    }


    @When("I input a valid team invitation token")
    public void i_input_a_valid_team_invitation_token() throws Exception {
        //doNothing().when(mockUserService).userJoinTeam(user, team);


        mockMvc.perform(post("/my-teams", 42L)
                .with(csrf())
                .param("token", team.getToken())).andExpect(status().isFound());

        userService.userJoinTeam(user, team);
       // Mockito.when(mockUserService.getCurrentUser()).thenReturn(Optional.of(user));
       // Mockito.when(mockUserService.userJoinTeam(user, team)).thenReturn(Optional.of(user));
        System.out.println("Token" + team.getToken());
        System.out.println("teams users" + team.getTeamMembers());
        System.out.println("Users teams: " + user.getJoinedTeams());

    }

    @Then("I am added as a member to that team")
    public void i_am_added_as_a_member_to_that_team() {
        System.out.println("here");
        System.out.println(user.getJoinedTeams());
        Assertions.assertTrue(user.getJoinedTeams().size() > 0);


    }

    @Given("I have joined a new team")
    public void i_have_joined_a_new_team() {

        userService.userJoinTeam(user, team);

    }

    @Then("I see the team I just joined")
    public void i_see_the_team_i_just_joined() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();

    }


}

