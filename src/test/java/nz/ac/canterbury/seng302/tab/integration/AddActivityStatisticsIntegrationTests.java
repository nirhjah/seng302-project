package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.controller.ViewActivitiesController;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import nz.ac.canterbury.seng302.tab.mail.EmailService;
import nz.ac.canterbury.seng302.tab.repository.ActivityRepository;
import nz.ac.canterbury.seng302.tab.repository.FactRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class AddActivityStatisticsIntegrationTests {

    @SpyBean
    private UserService userService;

    @SpyBean
    private ActivityService activityService;

    @SpyBean
    private TeamService teamService;

    @SpyBean
    private FactService factService;
    @Autowired
    private MockMvc mockMvc;
    private UserRepository userRepository;
    private TeamRepository teamRepository;
    private FactRepository factRepository;
    private ActivityRepository activityRepository;

    private Activity game;

    private Team team;

    private Date date;
    private User user;

    private MvcResult result;

    @Autowired
    private ApplicationContext applicationContext;

    @Before("@Add_activity_statistics")
    public void beforeEach() throws IOException {

        userRepository = applicationContext.getBean(UserRepository.class);
        teamRepository = applicationContext.getBean(TeamRepository.class);
        activityRepository = applicationContext.getBean(ActivityRepository.class);
        factRepository = applicationContext.getBean(FactRepository.class);

        TaskScheduler taskScheduler = applicationContext.getBean(TaskScheduler.class);
        EmailService emailService = applicationContext.getBean(EmailService.class);
        PasswordEncoder passwordEncoder = applicationContext.getBean(PasswordEncoder.class);

        userService = Mockito.spy(new UserService(userRepository, taskScheduler, emailService, passwordEncoder));
        teamService = Mockito.spy(new TeamService(teamRepository));
        activityService = Mockito.spy(new ActivityService(activityRepository));
        factService = Mockito.spy(new FactService(factRepository));

        this.mockMvc = MockMvcBuilders.standaloneSetup(new ViewActivitiesController(userService, activityService, teamService)).build();

        Location testLocation = new Location(null, null, null, "CHCH", null, "NZ");
        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "testing@gmail.com", "Password123!", testLocation);
        team = new Team("A-Team", "Soccer", new Location(null, null, null, "CHCH", null, "NZ"));
        date = new GregorianCalendar(2024, Calendar.JANUARY, 1).getTime();

    }

    @Given("there is an activity type of game or friendly")
    public void there_is_an_activity_type_of_game_or_friendly() throws Exception {
        game = new Activity(ActivityType.Game, team, "Test description",
                date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), user,
                new Location(null, null, null, "CHCH", null, "NZ"));
        activityRepository.save(game);
//        List<Activity> activityList= activityService.getAllTeamActivities(team);
//        Activity activity = activityList.get(0);
//        Assertions.assertNotNull(activity);
//        Assertions.assertEquals(ActivityType.Game, activity.getActivityType());

        result=mockMvc.perform(get("/view-activity").param("activityID", String.valueOf(game.getId())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

    }

    @When("I am adding a substitution,")
    public void i_am_adding_a_substitution() {
    }

    @Then("I must specify the player who was taken off, the one who was put on and the time that this occured.")
    public void i_must_specify_the_player_who_was_taken_off_the_one_who_was_put_on_and_the_time_that_this_occured() {
    }
    @When("I select the person who scored")
    public void i_select_the_person_who_scored() {
    }

    @Then("they must be a member of the team")
    public void they_must_be_a_member_of_the_team() {
    }

    @When("I specify a time for the substitution")
    public void i_specify_a_time_for_the_substitution() {
    }

    @Then("it must be within the timing of the game.")
    public void it_must_be_within_the_timing_of_the_game() {
    }

    @When("I specify a time")
    public void i_specify_a_time() {
    }

    @Then("it is the amount of hours, minutes, seconds into the activity")
    public void it_is_the_amount_of_hours_minutes_seconds_into_the_activity() {
    }
}
