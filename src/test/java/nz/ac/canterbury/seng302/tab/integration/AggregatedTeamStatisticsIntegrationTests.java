package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.controller.HomeFormController;
import nz.ac.canterbury.seng302.tab.controller.ProfileFormController;
import nz.ac.canterbury.seng302.tab.controller.ViewActivitiesController;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.mail.EmailService;
import nz.ac.canterbury.seng302.tab.repository.ActivityRepository;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.FactService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class AggregatedTeamStatisticsIntegrationTests {

    @SpyBean
    private UserService userService;

    @SpyBean
    private ActivityService activityService;

    @SpyBean
    private TeamService teamService;

    @SpyBean
    private FactService factService;

    private UserRepository userRepository;

    private TeamRepository teamRepository;

    private ActivityRepository activityRepository;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MockMvc mockMvc;

    Team team;

    User user;


    @Before
    public void setup() throws IOException {
        userRepository = applicationContext.getBean(UserRepository.class);
        teamRepository = applicationContext.getBean(TeamRepository.class);
        activityRepository = applicationContext.getBean(ActivityRepository.class);

        TaskScheduler taskScheduler = applicationContext.getBean(TaskScheduler.class);
        EmailService emailService = applicationContext.getBean(EmailService.class);
        PasswordEncoder passwordEncoder = applicationContext.getBean(PasswordEncoder.class);
        userService = Mockito.spy(new UserService(userRepository, taskScheduler, emailService, passwordEncoder));

        teamService = Mockito.spy(new TeamService(teamRepository));

        activityService = Mockito.spy(new ActivityService(activityRepository));

        factService = Mockito.spy(new FactService());

        this.mockMvc = MockMvcBuilders.standaloneSetup(new ViewActivitiesController(userService, activityService, teamService), new HomeFormController(userService, teamService), new ProfileFormController(userService, teamService, activityService, factService)).build();

        userRepository.deleteAll();
        teamRepository.deleteAll();
        for (Activity activity: activityRepository.findAll()) {
            activityService.updateOrAddActivity(activity);
        }
        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", new Location(null, null, null, "CHCH", null, "NZ"));
        userRepository.save(user);
        team = new Team("A-Team", "Soccer", new Location(null, null, null, "CHCH", null, "NZ"));
        teamRepository.save(team);


        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userService.getCurrentUser()).thenReturn(Optional.of(user));
    }


    @Given("I am on the team profile page")
    public void iAmOnTheTeamProfilePage() throws Exception {
        mockMvc.perform(get("/profile").param("teamID", team.getTeamId().toString()));
    }

    @When("I click on a UI element to view the aggregated team statistics")
    public void iClickOnAUIElementToViewTheAggregatedTeamStatistics() throws Exception {
        mockMvc.perform(get("/myStats"));
    }

    @Then("I will see the team statistics")
    public void iWillSeeTheTeamStatistics() throws Exception {
        mockMvc.perform(get("/myStats")).andExpect(status().isOk());
    }
}
