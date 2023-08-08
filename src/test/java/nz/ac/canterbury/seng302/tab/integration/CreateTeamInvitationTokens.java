package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.controller.*;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.repository.*;
import nz.ac.canterbury.seng302.tab.service.*;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@ContextConfiguration(classes = IntegrationTestConfigurations.class)
public class CreateTeamInvitationTokens {

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

    @Autowired
    private ActivityService activityService;

    @Autowired
    private FactService factService;

    @Autowired
    private FormationService formationService;


    private User user;

    private Team team;

    private String originalTeamToken;


    @Before("@create_team_invitation_tokens")
    public void setup() throws IOException {

        teamRepository = applicationContext.getBean(TeamRepository.class);
        userRepository = applicationContext.getBean(UserRepository.class);

        TaskScheduler taskScheduler = applicationContext.getBean(TaskScheduler.class);
        PasswordEncoder passwordEncoder = applicationContext.getBean(PasswordEncoder.class);

        userService = Mockito.spy(new UserService(userRepository, taskScheduler, passwordEncoder));
        teamService = Mockito.spy(new TeamService(teamRepository));

        this.mockMvc = MockMvcBuilders.standaloneSetup(new ProfileFormController(userService, teamService, activityService, factService, formationService)).build();

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        teamRepository.deleteAll();
        userRepository.deleteAll();
        Location testLocation = new Location(null, null, null, "CHCH", null, "NZ");
        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", testLocation);
        team = new Team("test1", "Hockey", new Location(null, null, null, "chch", null, "nz"), user);

        teamRepository.save(team);
        originalTeamToken = team.getToken();
        userRepository.save(user);

        when(userService.getCurrentUser()).thenReturn(Optional.of(user));
        when(teamService.getTeam(Long.parseLong(team.getTeamId().toString()))).thenReturn(team);
    }

    @Given("I manage a team")
    public void i_manage_a_team() {
        Assertions.assertTrue(team.isManager(user));
    }

    @When("I am viewing the profile page of that team")
    public void i_am_viewing_the_profile_page_of_that_team() throws Exception {
        mockMvc.perform(get("/profile")
                        .param("teamID", "1"))

                .andExpect(view().name("viewTeamForm"));
    }

    @Then("I can see a unique secret token for my team that is exactly 12 char long with a combination of letters and numbers, but no special characters")
    public void i_can_see_a_unique_secret_token_for_my_team_that_is_exactly_char_long_with_a_combination_of_letters_and_numbers_but_no_special_characters() throws Exception {
        mockMvc.perform(get("/profile")
                        .param("teamID", "1"))
                .andExpect(MockMvcResultMatchers.model().attribute("displayToken", team.getToken()));
    }

    @Given("I am on the team profile page")
    public void i_am_on_the_team_profile_page() throws Exception {
        mockMvc.perform(get("/profile")
                        .param("teamID", "1"))
                .andExpect(view().name("viewTeamForm"));
    }

    @When("I generate a new secret token for my team")
    public void i_generate_a_new_secret_token_for_my_team() throws Exception {
        team.generateToken(teamService);
    }

    @Then("a new token is generated, and this token is unique across the system and is not a repeat of the previous token")
    public void a_new_token_is_generated_and_this_token_is_unique_across_the_system_and_is_not_a_repeat_of_the_previous_token() {
        Assertions.assertNotEquals(team.getToken(), originalTeamToken);
    }
}
