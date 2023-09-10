package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.controller.CreateActivityController;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import nz.ac.canterbury.seng302.tab.enums.Role;
import nz.ac.canterbury.seng302.tab.repository.*;
import nz.ac.canterbury.seng302.tab.service.*;
import nz.ac.canterbury.seng302.tab.validator.ActivityFormValidators;
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
import org.springframework.validation.FieldError;

import javax.xml.xpath.XPathExpressionException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    private final LocalDateTime invalidStartDateTime = LocalDateTime.now().minus(50, ChronoUnit.DAYS);

    private final LocalDateTime invalidEndDateTime = LocalDateTime.now().minus(49, ChronoUnit.DAYS);

    private final String DEFAULT_DESCRIPTION = "Description 123";

    private final String DEFAULT_ADDR_LINE_1 = "20 Kirkwood Ave";

    private final String DEFAULT_POSTCODE = "8041";

    private final String DEFAULT_CITY = "Christchurch";

    private final String DEFAULT_COUNTRY = "New Zealand";

    private final Location DEFAULT_LOCATION = new Location(DEFAULT_ADDR_LINE_1, null, null, DEFAULT_CITY, DEFAULT_POSTCODE, DEFAULT_COUNTRY);

    private ActivityType chosenActivityType;

    private String teamCreationDateTime;

    private int maxDescLength;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");


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
        activityService = Mockito.spy(new ActivityService(activityRepository, lineUpRepository, lineUpPositionRepository));
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
        Location testLocation = new Location(null, null, null, "CHCH", null, "NZ");
        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(),
                "johndoe@example.com", "Password123!", testLocation);
        team = new Team("team900", "programming");

        userRepository.save(user);
        user = userService.getUser(user.getUserId());

        teamRepository.save(team);
        team = teamService.getTeam(team.getTeamId());
        team.setRole(user, Role.MANAGER);
        teamService.updateTeam(team);
        team = teamService.getTeam(team.getTeamId());
        teamCreationDateTime = team.getCreationDate().format(formatter);

        System.out.println(user);
        System.out.println(team);

        when(userService.getCurrentUser()).thenReturn(Optional.of(user));
        when(teamService.findTeamsWithUser(any())).thenReturn(Collections.singletonList(team));
        when(teamService.getTeam(team.getTeamId())).thenReturn(team);
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
                .andExpect(view().name("create-activity"));
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
                        .param("description", DEFAULT_DESCRIPTION)
                        .param("startDateTime", String.valueOf(startDateTime))
                        .param("endDateTime", String.valueOf(endDateTime))
                        .param("country", DEFAULT_COUNTRY)
                        .param("city", DEFAULT_CITY)
                        .param("postcode", DEFAULT_POSTCODE)
                        .param("addressLine1", DEFAULT_ADDR_LINE_1))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:./view-activity?activityID=1"));
    }

    @Then("an activity is created into the system")
    public void anActivityIsCreatedIntoTheSystem() {
        verify(activityService, times(1)).updateOrAddActivity(any());
    }

    @And("I see the details of the activity")
    public void iSeeTheDetailsOfTheActivity() throws Exception {
        mockMvc.perform(multipart("/create-activity")
                        .param("team", String.valueOf(team.getTeamId()))
                        .param("activityType", String.valueOf(defaultActivityType))
                        .param("description", DEFAULT_DESCRIPTION)
                        .param("startDateTime", String.valueOf(startDateTime))
                        .param("endDateTime", String.valueOf(endDateTime))
                        .param("country", DEFAULT_COUNTRY)
                        .param("city", DEFAULT_CITY)
                        .param("postcode", DEFAULT_POSTCODE)
                        .param("addressLine1", DEFAULT_ADDR_LINE_1))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:./view-activity?activityID=2"))
                .andExpect(xpath("//h3[@text='" + team.getName() + "']").exists())
                .andExpect(xpath("//h2[@text='" + defaultActivityType.name() + "']").exists())
                .andExpect(xpath("//h3[@text='" + DEFAULT_DESCRIPTION + "']").exists())
                .andExpect(xpath("//h3[@text='" + startDateTime.format(formatter).concat(" - ")
                        .concat(endDateTime.format(formatter)) + "']").exists())
                .andExpect(xpath("//h3[@text='" + DEFAULT_LOCATION + "']").exists());
    }

    @Then("I can select any of the teams I am managing or coaching")
    public void iCanSelectAnyOfTheTeamsIAmManagingOrCoaching() throws Exception {
        mockMvc.perform(get("/create-activity"))
                .andExpect(xpath("//option[@value='" + team.getTeamId() + "']").exists());
    }

    @And("I can select no team if the activity does not involve a team")
    public void iCanSelectNoTeamIfTheActivityDoesNotInvolveATeam() throws Exception {
        mockMvc.perform(multipart("/create-activity")
                        .param("activityType", String.valueOf(ActivityType.Other))
                        .param("description", DEFAULT_DESCRIPTION)
                        .param("startDateTime", String.valueOf(startDateTime))
                        .param("endDateTime", String.valueOf(endDateTime))
                        .param("country", DEFAULT_COUNTRY)
                        .param("city", DEFAULT_CITY)
                        .param("postcode", DEFAULT_POSTCODE)
                        .param("addressLine1", DEFAULT_ADDR_LINE_1))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:./view-activity?activityID=1"));
    }


    @Then("I can select from game, friendly, training, competition and other")
    public void iCanSelectFromGameFriendlyTrainingCompetitionAndOther() throws Exception {
        mockMvc.perform(get("/create-activity"))
                .andExpect(xpath("//option[@value='" + ActivityType.Game + "']").exists())
                .andExpect(xpath("//option[@value='" + ActivityType.Friendly + "']").exists())
                .andExpect(xpath("//option[@value='" + ActivityType.Training + "']").exists())
                .andExpect(xpath("//option[@value='" + ActivityType.Competition + "']").exists())
                .andExpect(xpath("//option[@value='" + ActivityType.Other + "']").exists());
    }

    @And("I select {string} as the activity type")
    public void iSelectActivityTypeAsTheActivityType(String activityType) {
        chosenActivityType = ActivityType.valueOf(activityType);
    }

    @And("I do not select a team")
    public void iDoNotSelectATeam() throws Exception {
        mockMvc.perform(multipart("/create-activity")
                        .param("activityType", String.valueOf(chosenActivityType))
                        .param("description", DEFAULT_DESCRIPTION)
                        .param("startDateTime", String.valueOf(startDateTime))
                        .param("endDateTime", String.valueOf(endDateTime))
                        .param("country", DEFAULT_COUNTRY)
                        .param("city", DEFAULT_CITY)
                        .param("postcode", DEFAULT_POSTCODE)
                        .param("addressLine1", DEFAULT_ADDR_LINE_1))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("redirect:./create-activity"));
    }

    @Then("an error message tells me I must select a team for that activity type")
    public void anErrorMessageTellsMeIMustSelectATeamForThatActivityType() throws Exception {
        mockMvc.perform(multipart("/create-activity")
                        .param("activityType", String.valueOf(chosenActivityType))
                        .param("description", DEFAULT_DESCRIPTION)
                        .param("startDateTime", String.valueOf(startDateTime))
                        .param("endDateTime", String.valueOf(endDateTime))
                        .param("country", DEFAULT_COUNTRY)
                        .param("city", DEFAULT_CITY)
                        .param("postcode", DEFAULT_POSTCODE)
                        .param("addressLine1", DEFAULT_ADDR_LINE_1))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("redirect:./create-activity"))
                .andExpect(xpath("//div[@text='" + ActivityFormValidators.TEAM_REQUIRED_MSG + "']").exists());;
    }

    @When("I do not select the activity type")
    public void iDoNotSelectTheActivityType() throws Exception {
        mockMvc.perform(multipart("/create-activity")
                        .param("description", DEFAULT_DESCRIPTION)
                        .param("startDateTime", String.valueOf(startDateTime))
                        .param("endDateTime", String.valueOf(endDateTime))
                        .param("country", DEFAULT_COUNTRY)
                        .param("city", DEFAULT_CITY)
                        .param("postcode", DEFAULT_POSTCODE)
                        .param("addressLine1", DEFAULT_ADDR_LINE_1))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("redirect:./create-activity"));
    }

    @Then("an error message tells me I must select an activity type")
    public void anErrorMessageTellsMeIMustSelectAnActivityType() throws Exception {
        mockMvc.perform(multipart("/create-activity")
                        .param("description", DEFAULT_DESCRIPTION)
                        .param("startDateTime", String.valueOf(startDateTime))
                        .param("endDateTime", String.valueOf(endDateTime))
                        .param("country", DEFAULT_COUNTRY)
                        .param("city", DEFAULT_CITY)
                        .param("postcode", DEFAULT_POSTCODE)
                        .param("addressLine1", DEFAULT_ADDR_LINE_1))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("redirect:./create-activity"))
                .andExpect(xpath("//div[@text='" + ActivityFormValidators.ACTIVITY_TYPE_MSG + "']").exists());
    }

    @And("I enter an empty description")
    public void iEnterAnEmptyDescription() throws Exception {
        mockMvc.perform(multipart("/create-activity")
                        .param("team", String.valueOf(team.getTeamId()))
                        .param("activityType", String.valueOf(defaultActivityType))
                        .param("description", "")
                        .param("startDateTime", String.valueOf(startDateTime))
                        .param("endDateTime", String.valueOf(endDateTime))
                        .param("country", DEFAULT_COUNTRY)
                        .param("city", DEFAULT_CITY)
                        .param("postcode", DEFAULT_POSTCODE)
                        .param("addressLine1", DEFAULT_ADDR_LINE_1))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("redirect:./create-activity"));
    }

    @Then("an error message tells me the description is invalid")
    public void anErrorMessageTellsMeTheDescriptionIsInvalid() throws Exception {
        mockMvc.perform(multipart("/create-activity")
                        .param("team", String.valueOf(team.getTeamId()))
                        .param("activityType", String.valueOf(defaultActivityType))
                        .param("description", "123456789!@#$%^&*()")
                        .param("startDateTime", String.valueOf(startDateTime))
                        .param("endDateTime", String.valueOf(endDateTime))
                        .param("country", DEFAULT_COUNTRY)
                        .param("city", DEFAULT_CITY)
                        .param("postcode", DEFAULT_POSTCODE)
                        .param("addressLine1", DEFAULT_ADDR_LINE_1))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("redirect:./create-activity"))
                .andExpect(xpath("//div[@text='" + ActivityFormValidators.DESC_CONTAINS_INVALID_CHAR_MSG + "']").exists());
    }

    @And("I enter a description made of numbers or non-alphabetical characters only")
    public void iEnterADescriptionMadeOfNumbersOrNonAlphabeticalCharactersOnly() throws Exception {
        mockMvc.perform(multipart("/create-activity")
                        .param("team", String.valueOf(team.getTeamId()))
                        .param("activityType", String.valueOf(defaultActivityType))
                        .param("description", "123456789!@#$%^&*()")
                        .param("startDateTime", String.valueOf(startDateTime))
                        .param("endDateTime", String.valueOf(endDateTime))
                        .param("country", DEFAULT_COUNTRY)
                        .param("city", DEFAULT_CITY)
                        .param("postcode", DEFAULT_POSTCODE)
                        .param("addressLine1", DEFAULT_ADDR_LINE_1))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("redirect:./create-activity"));
    }

    @And("I enter a description longer than {int} characters")
    public void iEnterADescriptionLongerThanCharacters(int chars) throws Exception {
        maxDescLength = chars;
        mockMvc.perform(multipart("/create-activity")
                        .param("team", String.valueOf(team.getTeamId()))
                        .param("activityType", String.valueOf(defaultActivityType))
                        .param("description", "1".repeat(chars + 1))
                        .param("startDateTime", String.valueOf(startDateTime))
                        .param("endDateTime", String.valueOf(endDateTime))
                        .param("country", DEFAULT_COUNTRY)
                        .param("city", DEFAULT_CITY)
                        .param("postcode", DEFAULT_POSTCODE)
                        .param("addressLine1", DEFAULT_ADDR_LINE_1))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("redirect:./create-activity"));
    }

    @And("I do not provide both a start and an end time")
    public void iDoNotProvideBothAStartAndAnEndTime() throws Exception {
        mockMvc.perform(multipart("/create-activity")
                        .param("team", String.valueOf(team.getTeamId()))
                        .param("activityType", String.valueOf(defaultActivityType))
                        .param("description", DEFAULT_DESCRIPTION)
                        .param("country", DEFAULT_COUNTRY)
                        .param("city", DEFAULT_CITY)
                        .param("postcode", DEFAULT_POSTCODE)
                        .param("addressLine1", DEFAULT_ADDR_LINE_1))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("redirect:./create-activity"));
    }

    @Then("an error message tells me the start and end time are compulsory")
    public void anErrorMessageTellsMeTheStartAndEndTimeAreCompulsory() throws Exception {
        mockMvc.perform(multipart("/create-activity")
                        .param("team", String.valueOf(team.getTeamId()))
                        .param("activityType", String.valueOf(defaultActivityType))
                        .param("description", DEFAULT_DESCRIPTION)
                        .param("country", DEFAULT_COUNTRY)
                        .param("city", DEFAULT_CITY)
                        .param("postcode", DEFAULT_POSTCODE)
                        .param("addressLine1", DEFAULT_ADDR_LINE_1))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("redirect:./create-activity"))
                .andExpect(xpath("//div[@text='" + ActivityFormValidators.START_DATE_REQUIRED_MSG + "']").exists())
                .andExpect(xpath("//div[@text='" + ActivityFormValidators.END_DATE_REQUIRED_MSG + "']").exists());
    }


    @Then("an error message tells me the end date is before the start date")
    public void anErrorMessageTellsMeTheEndDateIsBeforeTheStartDate() throws Exception {
        mockMvc.perform(multipart("/create-activity")
                        .param("team", String.valueOf(team.getTeamId()))
                        .param("activityType", String.valueOf(defaultActivityType))
                        .param("description", DEFAULT_DESCRIPTION)
                        .param("startDateTime", String.valueOf(endDateTime))
                        .param("endDateTime", String.valueOf(startDateTime))
                        .param("country", DEFAULT_COUNTRY)
                        .param("city", DEFAULT_CITY)
                        .param("postcode", DEFAULT_POSTCODE)
                        .param("addressLine1", DEFAULT_ADDR_LINE_1))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("redirect:./create-activity"))
                .andExpect(xpath("//div[@text='" + ActivityFormValidators.END_BEFORE_START_MSG + "']").exists());
    }

    @And("I enter either a start or an end time before the team creation date")
    public void iEnterEitherAStartOrAnEndTimeBeforeTheTeamCreationDate() throws Exception {
        mockMvc.perform(multipart("/create-activity")
                        .param("team", String.valueOf(team.getTeamId()))
                        .param("activityType", String.valueOf(defaultActivityType))
                        .param("description", DEFAULT_DESCRIPTION)
                        .param("startDateTime", String.valueOf(invalidStartDateTime))
                        .param("endDateTime", String.valueOf(invalidEndDateTime))
                        .param("country", DEFAULT_COUNTRY)
                        .param("city", DEFAULT_CITY)
                        .param("postcode", DEFAULT_POSTCODE)
                        .param("addressLine1", DEFAULT_ADDR_LINE_1))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("redirect:./create-activity"));
    }



    @Then("an error message tells me the description is too short")
    public void anErrorMessageTellsMeTheDescriptionIsTooShort() throws Exception {
        mockMvc.perform(multipart("/create-activity")
                        .param("team", String.valueOf(team.getTeamId()))
                        .param("activityType", String.valueOf(defaultActivityType))
                        .param("description", "")
                        .param("startDateTime", String.valueOf(startDateTime))
                        .param("endDateTime", String.valueOf(endDateTime))
                        .param("country", DEFAULT_COUNTRY)
                        .param("city", DEFAULT_CITY)
                        .param("postcode", DEFAULT_POSTCODE)
                        .param("addressLine1", DEFAULT_ADDR_LINE_1))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("redirect:./create-activity"))
                .andExpect(xpath("//div[@text='" + ActivityFormValidators.NO_DESC_MSG + "']").exists());
    }

    @Then("an error message tells me the description is too long")
    public void anErrorMessageTellsMeTheDescriptionIsTooLong() throws Exception {
        mockMvc.perform(multipart("/create-activity")
                        .param("team", String.valueOf(team.getTeamId()))
                        .param("activityType", String.valueOf(defaultActivityType))
                        .param("description", "1".repeat(maxDescLength + 1))
                        .param("startDateTime", String.valueOf(startDateTime))
                        .param("endDateTime", String.valueOf(endDateTime))
                        .param("country", DEFAULT_COUNTRY)
                        .param("city", DEFAULT_CITY)
                        .param("postcode", DEFAULT_POSTCODE)
                        .param("addressLine1", DEFAULT_ADDR_LINE_1))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("redirect:./create-activity"))
                .andExpect(xpath("//div[@text='" + ActivityFormValidators.DESC_TOO_LONG_MSG + "']").exists());
    }

    @When("I enter the activity start and end time with the end time before the start time")
    public void iEnterTheActivityStartAndEndTimeWithTheEndTimeBeforeTheStartTime() throws Exception {
        mockMvc.perform(multipart("/create-activity")
                        .param("team", String.valueOf(team.getTeamId()))
                        .param("activityType", String.valueOf(defaultActivityType))
                        .param("description", DEFAULT_DESCRIPTION)
                        .param("startDateTime", String.valueOf(endDateTime))
                        .param("endDateTime", String.valueOf(startDateTime))
                        .param("country", DEFAULT_COUNTRY)
                        .param("city", DEFAULT_CITY)
                        .param("postcode", DEFAULT_POSTCODE)
                        .param("addressLine1", DEFAULT_ADDR_LINE_1))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("redirect:./create-activity"));
    }

    @Then("an error message that includes the team's creation date tells me the dates are prior to the team’s creation date")
    public void anErrorMessageThatIncludesTheTeamSCreationDateTellsMeTheDatesArePriorToTheTeamSCreationDate() throws Exception {
        mockMvc.perform(multipart("/create-activity")
                        .param("team", String.valueOf(team.getTeamId()))
                        .param("activityType", String.valueOf(defaultActivityType))
                        .param("description", DEFAULT_DESCRIPTION)
                        .param("startDateTime", String.valueOf(invalidStartDateTime))
                        .param("endDateTime", String.valueOf(invalidEndDateTime))
                        .param("country", DEFAULT_COUNTRY)
                        .param("city", DEFAULT_CITY)
                        .param("postcode", DEFAULT_POSTCODE)
                        .param("addressLine1", DEFAULT_ADDR_LINE_1))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("redirect:./create-activity"))
                .andExpect(xpath("//div[@text='" + ActivityFormValidators.ACTIVITY_BEFORE_TEAM_CREATION + teamCreationDateTime + "']").exists());
    }
}
