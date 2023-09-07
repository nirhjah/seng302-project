package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.controller.*;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import nz.ac.canterbury.seng302.tab.mail.EmailService;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@ContextConfiguration(classes = IntegrationTestConfigurations.class)
public class U33EditLineupForGameFeature {

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
    private FormationService formationService;

    @Autowired
    private LineUpService lineUpService;

    @Autowired
    private LineUpPositionService lineUpPositionService;


    private User user;

    private Formation formation;

    private Team team;

    private Activity activity;
    private ActivityRepository activityRepository;

    private Map<String, Formation> formationMap = new HashMap<>();
    private long formationId = 0;
    private LineUpPositionRepository lineUpPositionRepository;
    private LineUpRepository lineUpRepository;

    @Before("@edit_lineup_for_game_integration")
    public void setup() throws IOException {

        teamRepository = applicationContext.getBean(TeamRepository.class);
        userRepository = applicationContext.getBean(UserRepository.class);
        activityRepository = applicationContext.getBean(ActivityRepository.class);
        lineUpPositionRepository = applicationContext.getBean(LineUpPositionRepository.class);
        lineUpRepository = applicationContext.getBean(LineUpRepository.class);

        TaskScheduler taskScheduler = applicationContext.getBean(TaskScheduler.class);
        EmailService emailService = applicationContext.getBean(EmailService.class);
        PasswordEncoder passwordEncoder = applicationContext.getBean(PasswordEncoder.class);
        FederationService federationService = applicationContext.getBean(FederationService.class);

        userService = Mockito.spy(new UserService(userRepository, taskScheduler, passwordEncoder));
        teamService = Mockito.spy(new TeamService(teamRepository));
        activityService = Mockito.spy(new ActivityService(activityRepository, lineUpRepository, lineUpPositionRepository));


        this.mockMvc = MockMvcBuilders.standaloneSetup(new CreateActivityController(
                teamService, userService, activityService, formationService, lineUpService, lineUpPositionService
        )).build();


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
        userRepository.save(user);



        activity = new Activity(ActivityType.Game, null, "Test description",
                LocalDateTime.of(2023, 1,1,6,30),  LocalDateTime.of(2023, 1,1,8,30), null,
                new Location(null, null, null, "CHCH", null, "NZ"));
        activityRepository.save(activity);
        activity.setActivityOwner(user);
        activity.setTeam(team);



        activityService.updateOrAddActivity(activity);

        formation = new Formation("4-5-6", team);
        formationId += 1;
        formationMap.put(formation.getFormation(), formation);
        formationService.addOrUpdateFormation(formation);

        when(userService.getCurrentUser()).thenReturn(Optional.of(user));
        when(teamService.getTeam(Long.parseLong(team.getTeamId().toString()))).thenReturn(team);


    }

    @Given("I am the manager of a team")
    public void i_am_the_manager_of_a_team() {
        Assertions.assertTrue(team.isManager(user));
    }

    @Given("viewing the edit page for a team activity for that team")
    public void viewing_the_edit_page_for_a_team_activity_for_that_team() throws Exception {
        when(activityService.findActivityById(activity.getId())).thenReturn(activity);
        mockMvc.perform(get("/createActivity?edit={id}", activity.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("createActivityForm"));
    }

    @Given("the activity has type game or friendly")
    public void the_activity_has_type_game_or_friendly() {
        Assertions.assertSame(activity.getActivityType().toString(), ActivityType.Game.toString());
    }

    @Given("the team has a formation \"4-5-6\"")
    public void the_team_has_a_formation() {
        Assertions.assertTrue(formationService.getFormation(1).isPresent());

    }

    @When("I attempt to cancel editing the activity")
    public void i_attempt_to_cancel_editing_the_activity() throws Exception {
        mockMvc.perform(get("/view-activity?activityID={id}", activity.getId()));
    }

    @Then("the activity will return to the state it was prior to editing")
    public void the_activity_will_return_to_the_state_it_was_prior_to_editing() {
        assertFalse(activity.getFormation().isPresent());
    }

    @When("I select the line-up \"4-5-6\" from the list of existing team formations")
    public void i_select_the_line_up_from_the_list_of_existing_team_formations() {
        activity.setFormation(formation);
        activityRepository.save(activity);
    }

    @Then("the saved activity has the formation \"4-5-6\"")
    public void the_saved_activity_has_the_formation() {
        Assertions.assertNotNull(activity.getFormation());
    }



}
