package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import nz.ac.canterbury.seng302.tab.controller.*;
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
import nz.ac.canterbury.seng302.tab.service.FormationService;
import nz.ac.canterbury.seng302.tab.service.FactService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class ViewMyActivitiesIntegrationTests {

    @SpyBean
    private UserService userService;

    @SpyBean
    private ActivityService activityService;

    @SpyBean
    private TeamService teamService;

    @SpyBean
    private FactService factService;

    @Autowired
    private FormationService formationService;

    private UserRepository userRepository;

    private TeamRepository teamRepository;

    private FactRepository factRepository;

    private ActivityRepository activityRepository;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MockMvc mockMvc;

    private User user;

    private Team team1;

    private Team team2;

    private Team selectedTeam;

    private Date dateFirst;

    private Date dateMiddle;

    private Date dateLast;

    private  List<Date> testDates;

    private final List<Activity> activityList = new ArrayList<>();


    @Before("@view_my_activities")
    public void setup() throws IOException {
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
        this.mockMvc = MockMvcBuilders.standaloneSetup(new ViewActivitiesController(userService, activityService, teamService), new HomeFormController(userService, teamService), new ProfileFormController(userService, teamService, activityService, factService, formationService)).build();

        userRepository.deleteAll();
        teamRepository.deleteAll();
        for (Activity activity: activityRepository.findAll()) {
            activity.setActivityOwner(null);
            activity.setTeam(null);
            activityService.updateOrAddActivity(activity);
        }
        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", new Location(null, null, null, "CHCH", null, "NZ"));
        userRepository.save(user);
        team1 = new Team("A-Team", "Soccer", new Location(null, null, null, "CHCH", null, "NZ"));
        team2 = new Team("B-Team", "Hockey", new Location(null, null, null, "CHCH", null, "NZ"));
        teamRepository.save(team1);
        teamRepository.save(team2);

        dateFirst = new GregorianCalendar(2024, Calendar.JANUARY, 1).getTime();
        dateMiddle = new GregorianCalendar(2024, Calendar.FEBRUARY, 1).getTime();
        dateLast = new GregorianCalendar(2024, Calendar.MARCH, 1).getTime();
        testDates = Arrays.asList(dateFirst, dateMiddle, dateLast);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        doReturn(Optional.of(user)).when(userService).getCurrentUser();
    }

    @Given("I have personal and team activities")
    public void iHavePersonalAndTeamActivities() {
        for (Date date: testDates) {
            Activity activity1 = new Activity(ActivityType.Friendly, null, "Test description",
                    date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), null,
                    new Location(null, null, null, "CHCH", null, "NZ"));
            Activity activity2 = new Activity(ActivityType.Friendly, null, "Test description",
                date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), null,
                    new Location(null, null, null, "CHCH", null, "NZ"));
            Activity activity3 = new Activity(ActivityType.Friendly, null, "Test description",
                    date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), null,
                    new Location(null, null, null, "CHCH", null, "NZ"));
            activityRepository.save(activity1);
            activityRepository.save(activity2);
            activityRepository.save(activity3);
            activity1.setActivityOwner(user);
            activity2.setActivityOwner(user);
            activity3.setActivityOwner(user);
            activity2.setTeam(team1);
            activity3.setTeam(team2);
            activityService.updateOrAddActivity(activity1);
            activityService.updateOrAddActivity(activity2);
            activityService.updateOrAddActivity(activity3);
            activityList.add(activity1);
            activityList.add(activity2);
            activityList.add(activity3);
        }
    }

    @Given("I am on the home form")
    public void iAmOnTheHomeForm() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().is3xxRedirection()).andExpect(view().name("redirect:./home"));
    }

    @When("I click the profile box and then select the My Activities button")
    public void iClickTheProfileBoxAndThenSelectTheMyActivitiesButton() throws Exception {
        mockMvc.perform(get("/view-activities").param("page","1"))
                .andExpect(status().isOk());

    }

    @Then("I see a list of all my activities, both team and personal")
    public void iSeeAListOfAllTheActivitiesBothTeamAndPersonal() throws Exception {
        MvcResult result = mockMvc.perform(get("/view-activities").param("page", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<Activity> actualActivities = (List<Activity>) result.getModelAndView().getModel().get("activities");
        Assertions.assertEquals(activityList.size(), actualActivities.size());

        // Can't directly denote equality due to funky database formatting
        List<Long> expectedIds = activityList.stream()
                .map(Activity::getId)
                .toList();

        List<Long> actualIds = actualActivities.stream()
                .map(Activity::getId)
                .toList();

        Assertions.assertTrue(expectedIds.containsAll(actualIds));
    }



    @When("I load the my activities form")
    public void iLoadTheMyActivitiesForm() throws Exception {
        mockMvc.perform(get("/view-activities").param("page","1"))
                .andExpect(status().isOk());
    }

    @Then("Personal activities are shown first")
    public void personalActivitiesAreShownFirst() throws Exception {
        MvcResult result = mockMvc.perform(get("/view-activities").param("page", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<Activity> actualActivities = (List<Activity>) result.getModelAndView().getModel().get("activities");
        for (int i = 0; i < 3; i++) {
            Activity activity = actualActivities.get(i);
            Assertions.assertNull(activity.getTeam());
        }
    }

    @Then("Team activities are grouped in alphabetical order")
    public void teamActivitiesAreGroupedInAlphabeticalOrder() throws Exception {
        MvcResult result = mockMvc.perform(get("/view-activities").param("page", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<Activity> actualActivities = (List<Activity>) result.getModelAndView().getModel().get("activities");
        String teamName = "";
        for (Activity activity: actualActivities) {
            if (activity.getTeam() != null) {
                if (!teamName.equals(activity.getTeam().getName())) {
                    int greater = teamName.compareTo(activity.getTeam().getName());
                    Assertions.assertFalse(greater > 0);
                    teamName = activity.getTeam().getName();
                }
            }
        }
    }

    @Then("Grouped activities are sorted by time in ascending order")
    public void groupedActivitiesAreSortedByTimeInAscendingOrder() throws Exception {
        MvcResult result = mockMvc.perform(get("/view-activities").param("page", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<Activity> actualActivities = (List<Activity>) result.getModelAndView().getModel().get("activities");
        LocalDateTime previousTime = null;
        String previousName = null;

        for (Activity activity : actualActivities) {
            LocalDateTime currentTime = activity.getActivityStart();
            String currentName = activity.getTeam() != null ? activity.getTeam().getName() : null;
            if (previousTime != null && currentName == previousName) {
                Assertions.assertTrue(currentTime.compareTo(previousTime) >= 0);
            }
            previousTime = currentTime;
            previousName = currentName;
        }
    }

    @Given("I have a mix of {int} personal and team activities")
    public void iHaveAMixOfPersonalAndTeamActivities(int numActvities) {
        for (int i = 0; i < numActvities; i++) {
            Activity activity1 = new Activity(ActivityType.Friendly, null, "Test description",
                    dateFirst.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), dateFirst.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), null,
                    new Location(null, null, null, "CHCH", null, "NZ"));
            activityRepository.save(activity1);
            activity1.setActivityOwner(user);
            activityService.updateOrAddActivity(activity1);
            activityList.add(activity1);
        }
    }

    @Then("{int} activities are shown")
    public void activitiesAreShown(int totalActivities) throws Exception {
        MvcResult result = mockMvc.perform(get("/view-activities").param("page", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<Activity> actualActivities = (List<Activity>) result.getModelAndView().getModel().get("activities");
        Assertions.assertEquals(10, actualActivities.size());
    }

    @And("I am on the my activities form")
    public void iAmOnTheMyActivitiesForm() throws Exception {
        mockMvc.perform(get("/view-activities").param("page", "1"))
                .andExpect(status().isOk()).andExpect(view().name("viewActivities"));

    }

    @When("I click on an activity team name")
    public void iClickOnAnActivity() throws Exception {
        MvcResult result = mockMvc.perform(get("/view-activities").param("page", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<Activity> actualActivities = (List<Activity>) result.getModelAndView().getModel().get("activities");
        for (Activity activity: actualActivities) {
            if (activity.getTeam() != null) {
                selectedTeam = activity.getTeam();
            }
        }
    }

    @Then("I'm taken to the teams profile page")
    public void iMTakenToTheTeamsProfilePage() throws Exception {
        Team teamMock = mock(Team.class);
        when(teamMock.isManager(user)).thenReturn(false);
        MvcResult result = mockMvc.perform(get("/profile").param("teamID", selectedTeam.getTeamId().toString()))
                .andExpect(status().isOk()).andExpect(view().name("viewTeamForm"))
                .andReturn();
        Assertions.assertEquals(selectedTeam.getTeamId(), result.getModelAndView().getModel().get("teamID"));
    }

    @And("pagination is active")
    public void paginationIsActive() throws Exception {
        Integer expectedPage = 2;
        MvcResult result = mockMvc.perform(get("/view-activities").param("page", expectedPage.toString()))
                .andExpect(status().isOk()).andExpect(view().name("viewActivities"))
                .andReturn();
        Assertions.assertEquals(expectedPage, result.getModelAndView().getModel().get("page"));
    }
}

