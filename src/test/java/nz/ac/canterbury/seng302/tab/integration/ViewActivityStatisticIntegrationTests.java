package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.controller.*;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Fact.Fact;
import nz.ac.canterbury.seng302.tab.entity.Fact.Substitution;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.time.ZoneId;
import java.util.*;

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
    @SpyBean
    private LineUpService lineUpService;

    @SpyBean
    private LineUpPositionService lineUpPositionService;

    @SpyBean
    private FactService factService;
    @Autowired
    private ApplicationContext applicationContext;
    private UserRepository userRepository;
    private TeamRepository teamRepository;
    private ActivityRepository activityRepository;

    private LineUpRepository lineUpRepository;

    private LineUpPositionRepository lineUpPositionRepository;

    private MvcResult result;

    private FactRepository factRespository;

    private FormationRepository formationRepository;
    @Autowired
    private MockMvc mockMvc;
    private User user;
    private Team team;

    private Activity game;

    private Date date;

    private Fact fact;
    private Fact fact1;
    private Fact fact2;




    @Before("@ViewActivityStatistics")
    public void setup() throws IOException {
        userRepository = applicationContext.getBean(UserRepository.class);
        teamRepository = applicationContext.getBean(TeamRepository.class);
        activityRepository = applicationContext.getBean(ActivityRepository.class);
        factRespository = applicationContext.getBean(FactRepository.class);
        lineUpRepository= applicationContext.getBean(LineUpRepository.class);
        lineUpPositionRepository= applicationContext.getBean(LineUpPositionRepository.class);
        formationRepository = applicationContext.getBean(FormationRepository.class);
        userRepository.deleteAll();
        teamRepository.deleteAll();
        activityRepository.deleteAll();

        TaskScheduler taskScheduler = applicationContext.getBean(TaskScheduler.class);
        EmailService emailService = applicationContext.getBean(EmailService.class);
        PasswordEncoder passwordEncoder = applicationContext.getBean(PasswordEncoder.class);

        userService = Mockito.spy(new UserService(userRepository, taskScheduler, passwordEncoder));
        teamService = Mockito.spy(new TeamService(teamRepository));
        factService= Mockito.spy(new FactService(factRespository));
        lineUpService=Mockito.spy(new LineUpService(lineUpRepository, formationRepository, lineUpPositionRepository, userRepository));
        lineUpPositionService = Mockito.spy(new LineUpPositionService(lineUpPositionRepository));
        activityService = Mockito.spy(new ActivityService(activityRepository, lineUpRepository, lineUpPositionRepository, factService, lineUpService, lineUpPositionService));
        this.mockMvc = MockMvcBuilders.standaloneSetup(new ViewActivitiesController(userService, activityService, teamService), new HomeFormController(userService, teamService, activityService), new ViewActivityController(userService,activityService,teamService,factService, lineUpService,lineUpPositionService)).build();

        Location testLocation = new Location(null, null, null, "CHCH", null, "NZ");
        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "testing@gmail.com", "Password123!", testLocation);
        team = new Team("A-Team", "Soccer", new Location(null, null, null, "CHCH", null, "NZ"));
        date = new GregorianCalendar(2024, Calendar.JANUARY, 1).getTime();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userService.getCurrentUser()).thenReturn(Optional.of(user));
    }

    @Given("I am viewing my activities or team activities")
    public void i_am_viewing_my_activities_or_team_activities() throws Exception {
        game = new Activity(ActivityType.Game, team, "Test description",
                date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), user,
                new Location(null, null, null, "CHCH", null, "NZ"));
        List<Fact> factList = new ArrayList<>();
        factList.add(new Fact("Someone fell over", "25", game));
        factList.add(new Fact("Someone fell over again", "3", game));
        factList.add(new Fact("Someone fell over yet again", "42", game));
        factList.add(new Substitution("Player was taken off", "40", game, user, user));
        factList.add(new Fact("Testing scrollable feature", "15", game));

        game.addFactList(factList);
        activityRepository.save(game);

        mockMvc.perform(get("/view-activities").param("page", "1"))
                .andExpect(status().isOk());
    }

    @When("I click on an activity")
    public void i_click_on_an_activity() throws Exception {
         result=mockMvc.perform(get("/view-activity").param("activityID", String.valueOf(game.getId())))
                 .andExpect(MockMvcResultMatchers.status().isOk())
                 .andReturn();
    }

    @SuppressWarnings("unchecked")
    @Then("I can see the details of that activity together with its statistics")
    public void i_can_see_the_details_of_that_activity_together_with_its_statistics() {
        Activity actualActivity = (Activity) result.getModelAndView().getModel().get("activity");
        Assertions.assertEquals(game.getId(), actualActivity.getId());
        Assertions.assertEquals(game.getDescription(), actualActivity.getDescription());

        List<Fact> actualFacts= (List<Fact>) result.getModelAndView().getModel().get("activityFacts");
        Assertions.assertEquals(game.getFactList().size(), actualFacts.size());
    }

    @Given("I am viewing an activity that is a game or friendly with lineups")
    public void i_am_viewing_an_activity_that_is_a_game_or_friendly_with_lineups() throws Exception {
        game = new Activity(ActivityType.Game, team, "Test description",
                date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), user,
                new Location(null, null, null, "CHCH", null, "NZ"));
        List<Fact> factList = new ArrayList<>();
        factList.add(new Fact("Someone fell over", "25", game));
        factList.add(new Fact("Someone fell over again", "30", game));
        factList.add(new Fact("Someone fell over yet again", "42", game));
        factList.add(new Substitution("Player was taken off", "4", game, user, user));
        factList.add(new Fact("Testing scrollable feature", "12", game));

        game.addFactList(factList);
        activityRepository.save(game);
        result=mockMvc.perform(get("/view-activity").param("activityID", String.valueOf(game.getId())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Activity actualActivity = (Activity) result.getModelAndView().getModel().get("activity");
        Assertions.assertEquals(game.getActivityType(), actualActivity.getActivityType());
    }

    @When("there are statistics about substitute players")
    public void there_are_statistics_about_substitute_players() {
        List<Substitution> actualSubstitution= (List<Substitution>) result.getModelAndView().getModel().get("activitySubstitutions");
        Assertions.assertNotNull(actualSubstitution);
        Assertions.assertEquals(1, actualSubstitution.size());


    }

    @Then("I can see the icon, name and time of substitution of the player")
    public void i_can_see_the_icon_name_and_time_of_substitution_of_the_player() {
        List<Substitution> actualSubstitution= (List<Substitution>) result.getModelAndView().getModel().get("activitySubstitutions");
        String description= actualSubstitution.get(0).getDescription();
        User userOff= actualSubstitution.get(0).getPlayerOff();
        String time = actualSubstitution.get(0).getTimeOfEvent();

        Assertions.assertNotNull(userOff);
        Assertions.assertEquals("Player was taken off",description);
        Assertions.assertEquals(user.getFirstName(), userOff.getFirstName());
        Assertions.assertEquals(user.getLastName(), userOff.getLastName());
        Assertions.assertEquals("4", time);


    }

    @Given("I am viewing an activity")
    public void i_am_viewing_an_activity() throws Exception {
        game = new Activity(ActivityType.Game, team, "Test description",
                date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), user,
                new Location(null, null, null, "CHCH", null, "NZ"));
        List<Fact> factList = new ArrayList<>();
        factList.add(new Fact("Someone fell over", "25", game));
        factList.add(new Fact("Someone fell over again", "30", game));
        factList.add(new Fact("Someone fell over yet again", "42", game));
        factList.add(new Substitution("Player was taken off", "45", game, user, user));
        factList.add(new Fact("Testing scrollable feature", "5", game));

        game.addFactList(factList);
        activityRepository.save(game);
        result=mockMvc.perform(get("/view-activity").param("activityID", String.valueOf(game.getId())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        Activity activity = (Activity) result.getModelAndView().getModel().get("activity");
        Assertions.assertNotNull(activity);
    }

    @When("that activity has statistics and facts with times")
    public void that_activity_has_statistics_and_facts_with_times() {
        List<Fact> actualFacts= (List<Fact>) result.getModelAndView().getModel().get("activityFacts");
        List<Substitution> actualSubstitution= (List<Substitution>) result.getModelAndView().getModel().get("activitySubstitutions");
        Assertions.assertNotNull(actualSubstitution);
        Assertions.assertNotNull(actualFacts);

    }

    @Then("they are listed and sorted by their time in ascending order")
    public void they_are_listed_and_sorted_by_their_time_in_ascending_order() {

    }

    @When("that activity has facts recorded")
    public void thatActivityHasFactsRecorded() {
        fact = new Fact("CCH", "1", game);
        fact1 = new Fact("CHC", null, game);
        fact2 = new Fact("CHC", "6", game);
    }

    @Then("they are listed and sorted by their time in ascending order, with the facts with no time associated appearing first.")
    public void theyAreListedAndSortedByTheirTimeInAscendingOrderWithTheFactsWithNoTimeAssociatedAppearingFirst() {
        Assertions.assertEquals(List.of(fact1, fact, fact2), factService.sortFactTimesAscending(List.of(fact, fact1, fact2)));
    }
}
