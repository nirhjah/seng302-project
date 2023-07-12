package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.controller.MyTeamsController;
import nz.ac.canterbury.seng302.tab.controller.ProfileFormController;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.mail.EmailService;
import nz.ac.canterbury.seng302.tab.repository.FormationRepository;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.FormationService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@ContextConfiguration(classes = IntegrationTestConfigurations.class)
public class U32CreateFormationFeature {

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
    private FormationRepository formationRepository;

    @Autowired
    private FormationService formationService;

    @Autowired
    private TeamService teamService;

    private User user;

    private Team team;

    private Formation formation;

    @Before("@create_formation")
    public void setup() throws IOException {

        userRepository = applicationContext.getBean(UserRepository.class);

        TaskScheduler taskScheduler = applicationContext.getBean(TaskScheduler.class);
        EmailService emailService = applicationContext.getBean(EmailService.class);
        PasswordEncoder passwordEncoder = applicationContext.getBean(PasswordEncoder.class);

        userService = Mockito.spy(new UserService(userRepository, taskScheduler, emailService, passwordEncoder));

        this.mockMvc = MockMvcBuilders.standaloneSetup(new ProfileFormController(userService, teamService, formationService)).build();

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        teamRepository.deleteAll();
        userRepository.deleteAll();
        formationRepository.deleteAll();
        Location testLocation = new Location(null, null, null, "CHCH", null, "NZ");
        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", testLocation);
        team = new Team("Team1", "Hockey", new Location(null, null, null, "chch", null, "nz"));
        teamRepository.save(team);
        userRepository.save(user);
        formation = new Formation("1-4-4-2", teamRepository.findById(1).get());
        formationRepository.save(formation);

        when(userService.getCurrentUser()).thenReturn(Optional.of(user));

    }

    @Given("I am on my team’s profile")
    @WithMockUser()
    public void iAmOnMyTeamSProfile() throws Exception {
        mockMvc.perform(get("/profile").param("teamID", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @When("I click on a UI element to see all the team’s formations")
    @WithMockUser()
    public void iClickOnAUIElementToSeeAllTheTeamSFormations() {

    }

    @Then("I see a list of all formations for that team")
    @WithMockUser()
    public void iSeeAListOfAllFormationsForThatTeam() throws Exception {
        MvcResult result = mockMvc.perform(get("/profile").param("teamID", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        List<Formation> formationList = (List<Formation>) result.getModelAndView().getModel().get("formations");
        Assertions.assertEquals(1, formationList.size());
        Assertions.assertEquals(formation, formationList.get(0));
    }

    @Given("I am on my team’s formation page")
    public void iAmOnMyTeamSFormationPage() throws Exception {
        iAmOnMyTeamSProfile();
        iClickOnAUIElementToSeeAllTheTeamSFormations();
    }

    @When("I click on a UI element to create a new line-up")
    public void iClickOnAUIElementToCreateANewLineUp() {

    }

    @Then("I see a graphical representation of a sport pitch corresponding to the sport of that team")
    public void iSeeAGraphicalRepresentationOfASportPitchCorrespondingToTheSportOfThatTeam() {
    }

    @Given("I set up the number of players per sector")
    public void iSetUpTheNumberOfPlayersPerSector() {
    }

    @When("the number of players per sector is invalid \\(I.e. does not respect the pattern of a number followed by a dash except for the last number), or is empty,")
    public void theNumberOfPlayersPerSectorIsInvalidIEDoesNotRespectThePatternOfANumberFollowedByADashExceptForTheLastNumberOrIsEmpty() {
    }

    @Then("an error message tells me that the formation is invalid.")
    public void anErrorMessageTellsMeThatTheFormationIsInvalid() {
    }

    @Given("I am on the formation creation page")
    public void iAmOnTheFormationCreationPage() {
    }

    @When("I specify a number of players per sector on the pitch in the form of dash separated numbers starting from the back line up to the front line on the pitch")
    public void iSpecifyANumberOfPlayersPerSectorOnThePitchInTheFormOfDashSeparatedNumbersStartingFromTheBackLineUpToTheFrontLineOnThePitch() {
    }

    @Then("a formation is generated that matches the formation string")
    public void aFormationIsGeneratedThatMatchesTheFormationString() {
    }

    @Given("I have correctly set up a formation with the number of players per sector")
    public void iHaveCorrectlySetUpAFormationWithTheNumberOfPlayersPerSector() {
    }

    @When("I click on the create formation button")
    public void iClickOnTheCreateFormationButton() {
    }

    @Then("the formation is persisted in the system")
    public void theFormationIsPersistedInTheSystem() {
    }
}
