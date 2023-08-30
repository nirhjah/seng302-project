package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.controller.CreateActivityController;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import nz.ac.canterbury.seng302.tab.repository.*;
import nz.ac.canterbury.seng302.tab.service.*;
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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@ContextConfiguration(classes = IntegrationTestConfigurations.class)
public class U27CreateActivityFeature {


    @Autowired
    private ApplicationContext applicationContext;


    @SpyBean
    private UserService userService;

    @SpyBean
    private TeamService teamService;

    @SpyBean
    private ActivityService activityService;

    @SpyBean
    private FormationService formationService;

    @SpyBean
    private LineUpService lineUpService;

    @SpyBean
    private LineUpPositionService lineUpPositionService;

    private UserRepository userRepository;

    private TeamRepository teamRepository;

    private ActivityRepository activityRepository;

    private FormationRepository formationRepository;

    private LineUpRepository lineUpRepository;

    private LineUpPositionRepository lineUpPositionRepository;

    private MockMvc mockMvc;

    private User user;

    private Team team;

    private final ActivityType defaultActivityType = ActivityType.Other;

    private final LocalDateTime startDateTime = LocalDateTime.now().plus(1, ChronoUnit.DAYS);

    private final LocalDateTime endDateTime = LocalDateTime.now().plus(2, ChronoUnit.DAYS);

    private final String DEFAULT_ADDR_LINE_1 = "20 Kirkwood Ave";

    private final String DEFAULT_POSTCODE = "8041";

    private final String DEFAULT_CITY = "Christchurch";

    private final String DEFAULT_COUNTRY = "New Zealand";


    private void setupMocking() {
        // get all the necessary beans
        userRepository = applicationContext.getBean(UserRepository.class);
        teamRepository = applicationContext.getBean(TeamRepository.class);
        activityRepository = applicationContext.getBean(ActivityRepository.class);
        formationRepository = applicationContext.getBean(FormationRepository.class);
        lineUpRepository = applicationContext.getBean(LineUpRepository.class);
        lineUpPositionRepository = applicationContext.getBean(LineUpPositionRepository.class);

        // Spy
        TaskScheduler taskScheduler = applicationContext.getBean(TaskScheduler.class);
        PasswordEncoder passwordEncoder = applicationContext.getBean(PasswordEncoder.class);

        userService = Mockito.spy(new UserService(userRepository, taskScheduler, passwordEncoder));
        teamService = Mockito.spy(new TeamService(teamRepository));
        activityService = Mockito.spy(new ActivityService(activityRepository));
        formationService = Mockito.spy(new FormationService(formationRepository));
        lineUpService = Mockito.spy(new LineUpService(lineUpRepository));
        lineUpPositionService = Mockito.spy(new LineUpPositionService(lineUpPositionRepository));


        this.mockMvc = MockMvcBuilders.standaloneSetup( new CreateActivityController(teamService, userService, activityService, formationService, lineUpService, lineUpPositionService)).build();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Delete leftover data
        formationRepository.deleteAll();
        lineUpRepository.deleteAll();
        lineUpPositionRepository.deleteAll();
        activityRepository.deleteAll();
        teamRepository.deleteAll();
        userRepository.deleteAll();

    }

    @Before("@create_activity")
    public void setup() throws Exception {
        setupMocking();
        Location location = new Location("adminAddr1", "adminAddr2", "adminSuburb", "adminCity", "4dm1n", "adminLand");
        user = new User("Admin", "Admin", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(),
                "test@test.com", "plaintextPassword", location);
        Location location2 = new Location("adminAddr1", "adminAddr2", "adminSuburb", "adminCity", "4dm1n", "adminLand");
        team = new Team("test1", "Hockey", location2);
//        teamRepository.save(team);
      /*  User userActual = userService.getUser(user.getUserId());
        Team teamActual = teamService.getTeam(team.getTeamId());
        System.out.println(teamService.getTeam(team.getTeamId()).getTeamManagers());*/

        //Mock User
        when(userService.getCurrentUser()).thenReturn(Optional.of(user));


        when(teamService.findTeamsWithUser(any())).thenReturn(Arrays.asList(team));
//        team = spy(team);
//        doReturn(true).when(team).isManager(any());
        when(team.isManager(any())).thenReturn(Boolean.TRUE);
        when(teamService.getTeam(anyLong())).thenReturn(team);

        // Generic Team for testing
      /*  teamActual = spy(teamActual);
        when(teamActual.isManager(user)).thenReturn(Boolean.TRUE);
        when(teamService.getTeam(anyLong())).thenReturn(teamActual);*/
    }

    @Given("I am anywhere on the system,")
    public void iAmAnywhereOnTheSystem() throws Exception {
        mockMvc.perform(get("/home"));
    }

    @When("I click on a UI element to create an activity,")
    public void iClickOnAUIElementToCreateAnActivity() throws Exception {
        mockMvc.perform(get("/create-activity"));
    }

    @Then("I see a form to create an activity.")
    public void iSeeAFormToCreateAnActivity() throws Exception {
        mockMvc.perform(get("/create-activity"))
                .andExpect(status().isOk()) // Accepted 200
                .andExpect(view().name("createActivityForm"));
    }

    @Given("I am on the create activity page")
    public void iAmOnTheCreateActivityPage() throws Exception {
        iSeeAFormToCreateAnActivity();
    }

    @When("I enter valid values for the team it relates to, the activity type, a short description, and the activity start and end time and location and create the activity")
    public void iEnterValidValuesForTheTeamItRelatesToTheActivityTypeAShortDescriptionAndTheActivityStartAndEndTimeAndLocationAndCreateTheActivity() throws Exception {

        mockMvc.perform(multipart("/create-activity")
                        .param("team", String.valueOf(team.getTeamId()))
                        .param("activityType", String.valueOf(defaultActivityType))
                        .param("description", String.valueOf(Grade.Age.ADULT))
                        .param("startDateTime", String.valueOf(startDateTime))
                        .param("endDateTime", String.valueOf(endDateTime))
                        .param("country", DEFAULT_COUNTRY)
                        .param("city", DEFAULT_CITY)
                        .param("postcode", DEFAULT_POSTCODE)
                        .param("addressLine1", DEFAULT_ADDR_LINE_1))
                .andExpect(status().isFound())
                .andExpect(view().name("viewActivityForm"));
    }

    @When("I hit the create activity button")
    public void iHitTheCreateActivityButton() {
    }

    @Then("an activity is created into the system")
    public void anActivityIsCreatedIntoTheSystem() {
        verify(activityService, times(1)).updateOrAddActivity(any());
    }

    @And("I see the details of the activity")
    public void iSeeTheDetailsOfTheActivity() {
    }

    @When("I am asked to select the team the activity relates to")
    public void iAmAskedToSelectTheTeamTheActivityRelatesTo() {
    }

    @Then("I can select any of the teams I am managing or coaching")
    public void iCanSelectAnyOfTheTeamsIAmManagingOrCoaching() {
    }

    @And("I can select no team if the activity does not involve a team")
    public void iCanSelectNoTeamIfTheActivityDoesNotInvolveATeam() {
    }

    @When("I am asked to select the activity type")
    public void iAmAskedToSelectTheActivityType() {
    }

    @Then("I can select from: {string}, {string}, {string}, {string}, {string}")
    public void iCanSelectFrom(String arg0, String arg1, String arg2, String arg3, String arg4) {
    }

    @And("I select {string} or {string} as the activity type")
    public void iSelectOrAsTheActivityType(String arg0, String arg1) {
    }

    @And("I do not select a team")
    public void iDoNotSelectATeam() {
    }

    @Then("an error message tells me I must select a team for that activity type")
    public void anErrorMessageTellsMeIMustSelectATeamForThatActivityType() {
    }

    @When("I do not select the activity type")
    public void iDoNotSelectTheActivityType() {
    }

    @Then("an error message tells me I must select an activity type")
    public void anErrorMessageTellsMeIMustSelectAnActivityType() {
    }

    @And("I enter an empty description")
    public void iEnterAnEmptyDescription() {
    }

    @Then("an error message tells me the description is invalid")
    public void anErrorMessageTellsMeTheDescriptionIsInvalid() {
    }

    @And("I enter a description made of numbers or non-alphabetical characters only")
    public void iEnterADescriptionMadeOfNumbersOrNonAlphabeticalCharactersOnly() {
    }

    @And("I enter a description longer than {int} characters")
    public void iEnterADescriptionLongerThanCharacters(int arg0) {
    }

    @And("I do not provide both a start and an end time")
    public void iDoNotProvideBothAStartAndAnEndTime() {
    }

    @Then("an error message tells me the start and end time are compulsory")
    public void anErrorMessageTellsMeTheStartAndEndTimeAreCompulsory() {
    }

    @And("I enter the activity start and end time")
    public void iEnterTheActivityStartAndEndTime() {
    }

    @And("I enter an end time before the start time")
    public void iEnterAnEndTimeBeforeTheStartTime() {
    }

    @Then("an error message tells me the end date is before the start date")
    public void anErrorMessageTellsMeTheEndDateIsBeforeTheStartDate() {
    }

    @And("I enter either a start or an end time before the team creation date")
    public void iEnterEitherAStartOrAnEndTimeBeforeTheTeamCreationDate() {
    }

    @Then("an error message tells me the dates are prior to the team’s creation date")
    public void anErrorMessageTellsMeTheDatesArePriorToTheTeamSCreationDate() {
    }

    @And("the team’s creation date is shown")
    public void theTeamSCreationDateIsShown() {
    }


}
