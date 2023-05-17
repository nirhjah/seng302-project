package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.controller.MyTeamsController;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.mail.EmailService;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@AutoConfigureMockMvc(addFilters = false)
//@SpringBootTest
@ContextConfiguration(classes = IntegrationTestConfigurations.class)
public class U25JoinTeamFeature {

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private UserService userService;

    @Autowired
    private ApplicationContext applicationContext;


    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamService teamService;

    private User user;

    private Team team;

    @Before("@join_team")
    public void setup() throws IOException {
        System.out.println("U25 EXECUTING ????????");

        userRepository = applicationContext.getBean(UserRepository.class);

        TaskScheduler taskScheduler = applicationContext.getBean(TaskScheduler.class);
        EmailService emailService = applicationContext.getBean(EmailService.class);
        PasswordEncoder passwordEncoder = applicationContext.getBean(PasswordEncoder.class);

        userService = Mockito.spy(new UserService(userRepository, taskScheduler, emailService, passwordEncoder));

        this.mockMvc = MockMvcBuilders.standaloneSetup(new MyTeamsController(userService, teamService, teamRepository)).build();

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        teamRepository.deleteAll();
        userRepository.deleteAll();
        Location testLocation = new Location(null, null, null, "CHCH", null, "NZ");
        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", testLocation);
        team = new Team("Team1", "Hockey", new Location(null, null, null, "chch", null, "nz"));
        teamRepository.save(team);
        userRepository.save(user);

        when(userService.getCurrentUser()).thenReturn(Optional.of(user));

    }

    @Given("I am on the home page")
    @WithMockUser()
    public void i_am_on_the_home_page() throws Exception {
        mockMvc.perform(get("/home"));
    }

    @When("I click the my teams button")
    @WithMockUser()
    public void i_click_the_my_teams_button() throws Exception {
        mockMvc.perform(get("/my-teams").param("page", "1"));
    }

    @Then("I see the my teams page")
    @WithMockUser()
    public void i_see_the_my_teams_page() throws Exception {
        mockMvc.perform(get("/my-teams").param("page", "1")).andExpect(status().isOk());
    }

    @Given("I am on the my teams page")
    @WithMockUser()
    public void i_am_on_the_my_teams_page() throws Exception {
        mockMvc.perform(get("/my-teams").param("page", "1"));
    }


    @When("I input a valid team invitation token")
    @WithMockUser()
    public void i_input_a_valid_team_invitation_token() throws Exception {
        mockMvc.perform(post("/my-teams", 42L)
                .with(csrf())
                .param("token", team.getToken())).andExpect(status().isFound());
    }

    @Then("I am added as a member to that team")
    @WithMockUser()
    public void i_am_added_as_a_member_to_that_team() {
        Assertions.assertTrue(user.getJoinedTeams().size() > 0);
    }

    @Given("I have joined a new team")
    @WithMockUser()
    public void i_have_joined_a_new_team() throws Exception {
        mockMvc.perform(post("/my-teams", 42L)
                .with(csrf())
                .param("token", team.getToken())).andExpect(status().isFound());

    }

    @Then("I see the team I just joined")
    @WithMockUser()
    public void i_see_the_team_i_just_joined() {
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
        verify(userService, times(0)).userJoinTeam(any(User.class), any(Team.class));
        Assertions.assertTrue(user.getJoinedTeams().size() == 0);
    }


}

