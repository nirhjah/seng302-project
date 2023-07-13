package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import nz.ac.canterbury.seng302.tab.mail.EmailService;
import nz.ac.canterbury.seng302.tab.repository.ActivityRepository;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
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
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class ViewActivityStatisticIntegrationTests {
    @SpyBean
    private UserService userService;
    @SpyBean
    private TeamService teamService;
    @SpyBean
    private ActivityService activityService;
    @Autowired
    private ApplicationContext applicationContext;
    private UserRepository userRepository;
    private TeamRepository teamRepository;
    private ActivityRepository activityRepository;
    @Autowired
    private MockMvc mockMvc;
    private User user;
    private Team team;
    @Before
    public void setup() throws IOException {
        userRepository = applicationContext.getBean(UserRepository.class);
        teamRepository = applicationContext.getBean(TeamRepository.class);
        activityRepository = applicationContext.getBean(ActivityRepository.class);

        userRepository.deleteAll();
        teamRepository.deleteAll();
        activityRepository.deleteAll();

        TaskScheduler taskScheduler = applicationContext.getBean(TaskScheduler.class);
        EmailService emailService = applicationContext.getBean(EmailService.class);
        PasswordEncoder passwordEncoder = applicationContext.getBean(PasswordEncoder.class);

        userService = Mockito.spy(new UserService(userRepository, taskScheduler, emailService, passwordEncoder));
        teamService = Mockito.spy(new TeamService(teamRepository));
        activityService = Mockito.spy(new ActivityService(activityRepository));

        User user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", new Location(null, null, null, "CHCH", null, "NZ"));
        userRepository.save(user);

        Team team = new Team("A-Team", "Soccer", new Location(null, null, null, "CHCH", null, "NZ"));
        teamRepository.save(team);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userService.getCurrentUser()).thenReturn(Optional.of(user));
    }

    @Given("I am viewing my activities or team activities")
    public void i_am_viewing_my_activities_or_team_activities() throws Exception {

        Activity game = new Activity(ActivityType.Game, null, "Personal Activity",
                LocalDateTime.of(2030, 1, 1, 6, 30),
                LocalDateTime.of(2030, 1, 1, 8, 30), user,
                new Location(null, null, null, "CHCH", null, "NZ"));

        activityRepository.save(game);
        game.setActivityOwner(user);
        game.setTeam(team);

        activityService.updateOrAddActivity(game);
        mockMvc.perform(get("/view-activities").param("page","1"))
                .andExpect(status().isOk());
    }

    @When("I click on an activity")
    public void i_click_on_an_activity() {
        throw new io.cucumber.java.PendingException();
    }

    @Then("I can see the details of that activity together with its statistics")
    public void i_can_see_the_details_of_that_activity_together_with_its_statistics() {
        throw new io.cucumber.java.PendingException();
    }

    @Given("I am viewing an activity that is a game or friendly with lineups")
    public void i_am_viewing_an_activity_that_is_a_game_or_friendly_with_lineups() {
        throw new io.cucumber.java.PendingException();
    }

    @When("there are statistics about scoring players")
    public void there_are_statistics_about_scoring_players() {
        throw new io.cucumber.java.PendingException();
    }

    @Then("I can see the time that player scored next to their icon on the line-up")
    public void i_can_see_the_time_that_player_scored_next_to_their_icon_on_the_line_up() {
        throw new io.cucumber.java.PendingException();
    }

    @Given("I am viewing the activity statistics of activities that are games or friendlies and have lineups")
    public void i_am_viewing_the_activity_statistics_of_activities_that_are_games_or_friendlies_and_have_lineups() {
        throw new io.cucumber.java.PendingException();
    }

    @When("there are statistics about substitute players")
    public void there_are_statistics_about_substitute_players() {
        throw new io.cucumber.java.PendingException();
    }

    @Then("I can see the icon, name and time of substitution of the player")
    public void i_can_see_the_icon_name_and_time_of_substitution_of_the_player() {
        throw new io.cucumber.java.PendingException();
    }

    @Given("I am viewing an activity")
    public void i_am_viewing_an_activity() {
        throw new io.cucumber.java.PendingException();
    }

    @When("that activity has statistics and facts with times")
    public void that_activity_has_statistics_and_facts_with_times() {
        throw new io.cucumber.java.PendingException();
    }

    @Then("they are listed and sorted by their time in ascending order")
    public void they_are_listed_and_sorted_by_their_time_in_ascending_order() {
        throw new io.cucumber.java.PendingException();
    }
}
