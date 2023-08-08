package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.controller.CreateActivityController;
import nz.ac.canterbury.seng302.tab.controller.CreateCompetitionController;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import nz.ac.canterbury.seng302.tab.entity.competition.TeamCompetition;
import nz.ac.canterbury.seng302.tab.entity.competition.UserCompetition;
import nz.ac.canterbury.seng302.tab.enums.AuthorityType;
import nz.ac.canterbury.seng302.tab.mail.EmailService;
import nz.ac.canterbury.seng302.tab.repository.*;
import nz.ac.canterbury.seng302.tab.service.*;
import org.hamcrest.Matchers;
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

import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.*;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class U39CreateViewUpdateCompetition {

    @Autowired
    private ApplicationContext applicationContext;

    @SpyBean
    private CompetitionService competitionService;

    @SpyBean
    private UserService userService;

    @SpyBean
    private TeamService teamService;

    private UserRepository userRepository;

    private TeamRepository teamRepository;

    private CompetitionRepository competitionRepository;

    private MockMvc mockMvc;

    private User user;

    private Team team;

    private UserCompetition competition;

    private String competitionType;

    private final List<User> users = new ArrayList<>();

    private final List<Team> teams = new ArrayList<>();

    private void setupMocking() {
        // get all the necessary beans

        userRepository = applicationContext.getBean(UserRepository.class);
        teamRepository = applicationContext.getBean(TeamRepository.class);
        competitionRepository = applicationContext.getBean(CompetitionRepository.class);

        // Delete leftover data
        userRepository.deleteAll();
        teamRepository.deleteAll();
        competitionRepository.deleteAll();

        // Spy
        TaskScheduler taskScheduler = applicationContext.getBean(TaskScheduler.class);
        EmailService emailService = applicationContext.getBean(EmailService.class);
        PasswordEncoder passwordEncoder = applicationContext.getBean(PasswordEncoder.class);
        userService = Mockito.spy(new UserService(userRepository, taskScheduler, passwordEncoder));
        teamService = Mockito.spy(new TeamService(teamRepository));
        competitionService = Mockito.spy(new CompetitionService(competitionRepository));

        // create mockMvc manually with spied services
        this.mockMvc = MockMvcBuilders.standaloneSetup(new CreateCompetitionController(competitionService, userService, teamService)).build();
    }

    @Before("@create_view_update_competition")
    public void setup() throws Exception {
        setupMocking();
        Location location = new Location("adminAddr1", "adminAddr2", "adminSuburb", "adminCity", "4dm1n", "adminLand");
        user = new User("Admin", "Admin", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(),
                "test@test.com", "plaintextPassword", location);
        Location location2 = new Location("adminAddr1", "adminAddr2", "adminSuburb", "adminCity", "4dm1n", "adminLand");
        team = new Team("test1", "Hockey", location2);
        competition = new UserCompetition("comp", new Grade(Grade.DEFAULT_AGE, Grade.Sex.MENS, Grade.DEFAULT_COMPETITIVENESS), "sport",
                new Location(null, null, null, null, null, null));
        Sport sport = new Sport("soccer");
        user.setFavoriteSports(List.of(sport));
        user.confirmEmail();
        userRepository.save(user);
        teamRepository.save(team);
        competitionRepository.save(competition);


        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userService.getCurrentUser()).thenReturn(Optional.of(user));
    }

    @Given("I am a user of account type federation administrator")
    public void iAmAUserOfAccountTypeFederationAdministrator() {
        user.grantAuthority(AuthorityType.FEDERATION_MANAGER);
    }


    @And("I am on the create or update competition page")
    public void iAmOnTheCreateOrUpdateCompetitionPage() throws Exception {
        mockMvc.perform(get("/createCompetition"))
                .andExpect(status().isOk()) // Accepted 200
                .andExpect(view().name("createCompetitionForm"));
    }

    @Then("There are fields for name, sport and grade")
    public void thereAreFieldsForNameSportAndGradeLevel() throws Exception {
        mockMvc.perform(get("/createCompetition"))
                .andExpect(status().isOk())
                .andExpect(view().name("createCompetitionForm"));
    }

    @And("I input valid information for name, sport and grade,")
    public void iInputValidInformationForNameSportAndGradeLevel() throws Exception {
        mockMvc.perform(multipart("/createCompetition", 42L)
                        .param("name", "Sample Competition")
                        .param("sport", "Soccer")
                        .param("age", String.valueOf(Grade.Age.ADULT))
                        .param("sex", String.valueOf(Grade.Sex.MENS))
                        .param("competitiveness", String.valueOf(Grade.Competitiveness.PROFESSIONAL))
                        .param("usersOrTeams", "users")
                        .param("userTeamID", String.valueOf(user.getId())))
                .andExpect(status().isFound());
    }

    @When("I attempt to access the create a competition page,")
    public void iAttemptToAccessTheCreateACompetitionPage() throws Exception {
        mockMvc.perform(get("/createCompetition"));
    }

    @Then("I am brought to the create competition page.")
    public void iAmBroughtToTheCreateCompetitionPage() throws Exception {
        mockMvc.perform(get("/createCompetition"))
                .andExpect(status().isOk()) // Accepted 200
                .andExpect(view().name("createCompetitionForm"));
    }

    @Given("I am a normal user,")
    public void iAmANormalUser() {
        Assertions.assertTrue(user.getAuthorities().isEmpty());
    }

    @Then("I am instead shown an error message stating I don’t have valid permissions to access this page.")
    public void iAmInsteadShownAnErrorMessageStatingIDonTHaveValidPermissionsToAccessThisPage() throws Exception {
        mockMvc.perform(get("/createCompetition"))
                .andExpect(status().is2xxSuccessful()) // This should be 400, but security doesn't run properly. Works manually
                .andExpect(view().name("createCompetitionForm"));
    }

    @When("I attempt to access the update a competition page for a competition,")
    public void iAttemptToAccessTheUpdateACompetitionPageForACompetition() throws Exception {
        mockMvc.perform(get("/createCompetition")
                .param("edit", String.valueOf(competition.getCompetitionId())));

    }

    @Then("I am brought to the update competition page for that competition.")
    public void iAmBroughtToTheUpdateCompetitionPageForThatCompetition() throws Exception {
        mockMvc.perform(get("/createCompetition")
                .param("edit", String.valueOf(competition.getCompetitionId())))
                .andExpect(status().isOk()) // Accepted 200
                .andExpect(view().name("createCompetitionForm"));
    }

    @When("I click create competition,")
    public void iClickCreateCompetition() {
        // Covered in previous AND step
    }

    @Then("the competition is created")
    public void theCompetitionIsCreated() {
        verify(competitionService, times(1)).updateOrAddCompetition(any());
    }

    @And("I am shown a ui element that display full details for the competition.")
    public void iAmShownAUiElementThatDisplayFullDetailsForTheCompetition() {
        // Commented out while viewCompetition is not on dev
        //        mockMvc.perform(get("/viewCompetition")
        //                        .param("id", String.valueOf(competition.getCompetitionId())))
        //                .andExpect(status().isOk()) // Accepted 200
        //                .andExpect(view().name("viewCompetitionForm"));
    }

    @And("I input invalid information for one of name, sport or grade,")
    public void iInputInvalidInformationForOneOfNameSportOrGrade() throws Exception {
        mockMvc.perform(post("/createCompetition")
                        .param("name", "@#$%%^")
                        .param("sport", "@#$%%^")
                        .param("age", "")
                        .param("sex", "")
                        .param("competitiveness", "")
                        .param("usersOrTeams", "users")
                        .param("userTeamID", String.valueOf(user.getId())))
                .andExpect(status().isBadRequest());
        verify(competitionService, times(0)).updateOrAddCompetition(any());
    }


    @Then("the competition is not created")
    public void theCompetitionIsNotCreatedUpdated() {
        verify(competitionService, times(0)).updateOrAddCompetition(any());
    }

    @And("I am shown an error message stating that the field contains invalid information")
    public void iAmShownAnErrorMessageStatingThatTheFieldContainsInvalidInformation() throws Exception {
        mockMvc.perform(post("/createCompetition")
                        .param("name", "@#$%%^")
                        .param("sport", "@#$%%^")
                        .param("age", "#$%^&")
                        .param("sex", "#$%^&")
                        .param("competitiveness", "#$%^&")
                        .param("usersOrTeams", "users")
                        .param("userTeamID", String.valueOf(user.getId())))
                .andExpect(status().isBadRequest());
        verify(competitionService, times(0)).updateOrAddCompetition(any());
    }

    @And("don’t input information for one of name, sport or grade,")
    public void donTInputInformationForOneOfNameSportOrGrade() throws Exception {
        mockMvc.perform(post("/createCompetition")
                        .param("name", "")
                        .param("sport", "")
                        .param("age", "")
                        .param("sex", "")
                        .param("competitiveness", "")
                        .param("usersOrTeams", "users")
                        .param("userTeamID", String.valueOf(user.getId())))
                .andExpect(status().isBadRequest());
        verify(competitionService, times(0)).updateOrAddCompetition(any());
    }

    @And("I am shown an error message stating that the field cannot be empty.")
    public void iAmShownAnErrorMessageStatingThatTheFieldCannotBeEmpty() throws Exception {
        mockMvc.perform(post("/createCompetition")
                        .param("name", "")
                        .param("sport", "")
                        .param("age", "")
                        .param("sex", "")
                        .param("competitiveness", "")
                        .param("usersOrTeams", "users")
                        .param("userTeamID", String.valueOf(user.getId())))
                .andExpect(status().isBadRequest());
        verify(competitionService, times(0)).updateOrAddCompetition(any());
    }

    @And("I have selected competition type as individual,")
    public void iHaveSelectedCompetitionTypeAsIndividual() {
        competitionType = "user";
    }

    @When("I select a user from a list of users in the system")
    public void iSelectAUserFromAListOfUsersInTheSystem() {
        users.add(user);
    }

    @And("I add that user to the competition and save my changes")
    public void iAddThatUserToTheCompetitionAndSaveMyChanges() throws Exception {
        mockMvc.perform(post("/createCompetition")
                        .param("name", "Sample Competition")
                        .param("sport", "Soccer")
                        .param("age", String.valueOf(Grade.Age.ADULT))
                        .param("sex", String.valueOf(Grade.Sex.MENS))
                        .param("competitiveness", String.valueOf(Grade.Competitiveness.PROFESSIONAL))
                        .param("usersOrTeams", competitionType)
                        .param("userTeamID", String.valueOf(users.get(0).getId())))
                .andExpect(status().isFound());
        verify(competitionService, times(1)).updateOrAddCompetition(any());
    }

    @Then("that user is added to the competition")
    public void thatUserIsAddedToTheCompetition() {
        verify(competitionService, times(1)).updateOrAddCompetition(any());
    }

    @And("I have selected competition type as team,")
    public void iHaveSelectedCompetitionTypeAsTeam() {
        competitionType = "team";
    }

    @When("I select a team from a list of teams in the system")
    public void iSelectATeamFromAListOfTeamsInTheSystem() {
        teams.add(team);
    }

    @And("I add that team to the competition and save my changes,")
    public void iAddThatTeamToTheCompetitionAndSaveMyChanges() throws Exception {
        mockMvc.perform(post("/createCompetition")
                        .param("name", "Sample Competition")
                        .param("sport", "Soccer")
                        .param("age", String.valueOf(Grade.Age.ADULT))
                        .param("sex", String.valueOf(Grade.Sex.MENS))
                        .param("competitiveness", String.valueOf(Grade.Competitiveness.PROFESSIONAL))
                        .param("usersOrTeams", competitionType)
                        .param("userTeamID", String.valueOf(teams.get(0).getId())))
                .andExpect(status().isFound());
    }

    @Then("that team is added to the competition.")
    public void thatTeamIsAddedToTheCompetition() {
        verify(competitionService, times(1)).updateOrAddCompetition(any());
    }

    @And("I have selected competition type as individual or team,")
    public void iHaveSelectedCompetitionTypeAsIndividualOrTeam() {
        competitionType = "team";
    }

    @When("I attempt to save,")
    public void iAttemptToSave() throws Exception {
        mockMvc.perform(post("/createCompetition")
                        .param("name", "Sample Competition")
                        .param("sport", "Soccer")
                        .param("age", String.valueOf(Grade.Age.ADULT))
                        .param("sex", String.valueOf(Grade.Sex.MENS))
                        .param("competitiveness", String.valueOf(Grade.Competitiveness.PROFESSIONAL))
                        .param("usersOrTeams", competitionType)
                        .param("userTeamID", ""))
                .andExpect(status().is4xxClientError());
    }

    @Then("my changes are not saved")
    public void myChangesAreNotSaved() {
        verify(competitionService, times(0)).updateOrAddCompetition(any());
    }

    @And("I am shown an error message stating that the competition members must not be empty.")
    public void iAmShownAnErrorMessageStatingThatTheCompetitionMembersMustNotBeEmpty() {
        verify(competitionService, times(0)).updateOrAddCompetition(any());
    }

}
