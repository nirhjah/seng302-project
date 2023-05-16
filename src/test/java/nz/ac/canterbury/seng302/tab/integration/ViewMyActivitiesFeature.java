
package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.end2end.PlaywrightBrowser;
import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.mail.EmailService;
import nz.ac.canterbury.seng302.tab.repository.ActivityRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.io.IOException;
import java.time.ZoneId;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class ViewMyActivitiesFeature {

    private UserRepository userRepository;

    private TeamRepository teamRepository;

    private ActivityRepository activityRepository;

    @SpyBean
    private UserService userService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MockMvc mockMvc;

    private User user;

    private User userOther;

    private String token;

    private Team team1;

    private Team team2;

    private Date dateFirst;

    private Date dateMiddle;

    private Date dateLast;

    private List<Date> testDates = Arrays.asList(dateFirst, dateMiddle, dateLast);

    private List<Activity> activityList = new ArrayList<>();


    @Before
    public void setup() throws IOException {
        userRepository = applicationContext.getBean(UserRepository.class);

        this.mockMvc = MockMvcBuilders.standaloneSetup().build();

        userRepository.deleteAll();
        Location testLocation = new Location(null, null, null, "CHCH", null, "NZ");
        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", testLocation);
        userOther = new User("Jane", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "janedoe@example.com", "Password123!", testLocation);

        team1 = new Team("A-Team", "Soccer", testLocation, user);
        team2 = new Team("B-Team", "Hockey", testLocation, userOther);
        user.joinTeam(team2);
        userRepository.save(user);
        userRepository.save(userOther);
        teamRepository.save(team1);
        teamRepository.save(team2);
        dateFirst = new GregorianCalendar(2024, Calendar.JANUARY, 1).getTime();
        dateMiddle = new GregorianCalendar(2024, Calendar.FEBRUARY, 1).getTime();
        dateLast = new GregorianCalendar(2024, Calendar.MARCH, 1).getTime();

        user.generateToken(userService, 1);
        token = user.getToken();
        userRepository.save(user);
    }

    @Given("I have personal and team activities")
    public void iHavePersonalAndTeamActivities() {
        for (Date date: testDates) {
            Activity activity1 = new Activity(Activity.ActivityType.Friendly, null, "Test description",
                    date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), user);
            Activity activity2 = new Activity(Activity.ActivityType.Friendly, team1, "Test description",
                    date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), userOther);
            Activity activity3 = new Activity(Activity.ActivityType.Friendly, team2, "Test description",
                    date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), user);
            activityRepository.save(activity1);
            activityRepository.save(activity2);
            activityRepository.save(activity3);
            activityList.add(activity1);
            activityList.add(activity2);
            activityList.add(activity3);
        }
    }

    @Given("I am on the home form")
    public void iAmOnTheHomeForm() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk());
    }

    @When("I click the profile box and then select the My Activities button")
    public void iClickTheProfileBoxAndThenSelectTheMyActivitiesButton() throws Exception {
        mockMvc.perform(get("/view-activities").param("page","1"))
                .andExpect(status().isOk());

    }

    @Then("I see a list of all my activities, both team and personal")
    public void iSeeAListOfAllTheActivitiesBothTeamAndPersonal() throws Exception {
        MvcResult result = mockMvc.perform(get("/view-activities").param("page","1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("activities"))
                .andReturn();
        Assertions.assertEquals(result.getAsyncResult(), activityList);
    }



    @When("I load the my activities form")
    public void iLoadTheMyActivitiesForm() {
    }

    @Then("Personal activities are shown first")
    public void personalActivitiesAreShownFirst() {
    }

    @Given("I have team activities")
    public void iHaveTeamActivities() {
    }

    @Then("Team activities are grouped in alphabetical order")
    public void teamActivitiesAreGroupedInAlphabeticalOrder() {
    }

    @Then("Grouped activities are sorted by time in ascending order")
    public void groupedActivitiesAreSortedByTimeInAscendingOrder() {
    }

    @Given("I have a mix of {int} personal and team activities")
    public void iHaveAMixOfPersonalAndTeamActivities(int arg0) {
    }

    @Then("{int} activities are shown")
    public void activitiesAreShown(int arg0) {
    }

    @And("Clicking the pagination next button brings me to the next page")
    public void clickingThePaginationNextButtonBringsMeToTheNextPage() {
    }

    @And("I am on the my activities form")
    public void iAmOnTheMyActivitiesForm() {
    }

    @When("I click on an activity")
    public void iClickOnAnActivity() {
    }

    @Then("I'm taken to the teams profile page")
    public void iMTakenToTheTeamsProfilePage() {
    }
}



