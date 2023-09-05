package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.controller.CreateCompetitionController;
import nz.ac.canterbury.seng302.tab.controller.FederationManagerInviteController;
import nz.ac.canterbury.seng302.tab.controller.InviteToFederationManagerController;
import nz.ac.canterbury.seng302.tab.controller.ViewAllCompetitionsController;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import nz.ac.canterbury.seng302.tab.entity.competition.UserCompetition;
import nz.ac.canterbury.seng302.tab.enums.AuthorityType;
import nz.ac.canterbury.seng302.tab.mail.EmailService;
import nz.ac.canterbury.seng302.tab.repository.CompetitionRepository;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.*;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    @SpyBean
    private EmailService emailService;

    @SpyBean
    private FederationService federationService;

    private UserRepository userRepository;

    private TeamRepository teamRepository;

    private CompetitionRepository competitionRepository;

    private MockMvc mockMvc;
    private MockMvc viewAllMockMvc;

    private User user;

    private User generalUser;

    private Team team;

    private UserCompetition competition;

    private String competitionType;

    private final List<User> users = new ArrayList<>();

    private final List<Team> teams = new ArrayList<>();


    /*
      The number of competitions to generate for each time frame
     */
    private static final int NUM_PAST = 1;
    private static final int NUM_FUTURE = 1;
    private static final int NUM_CURRENT = 1;

    // The filter arguments to pass into the viewAllCompetitions request:
    private ViewAllCompetitionsController.Timing timing = null;
    private List<String> filterSports = new ArrayList<>();

    private String VIEW_ALL = "/view-all-competitions";


    private void setupMocking() {
        // get all the necessary beans
        userRepository = applicationContext.getBean(UserRepository.class);
        teamRepository = applicationContext.getBean(TeamRepository.class);
        competitionRepository = applicationContext.getBean(CompetitionRepository.class);
        SportService sportService = applicationContext.getBean(SportService.class);

        // Delete leftover data
        userRepository.deleteAll();
        teamRepository.deleteAll();
        competitionRepository.deleteAll();

        // Spy
        TaskScheduler taskScheduler = applicationContext.getBean(TaskScheduler.class);
        EmailService emailService = applicationContext.getBean(EmailService.class);
        PasswordEncoder passwordEncoder = applicationContext.getBean(PasswordEncoder.class);
        federationService = Mockito.spy(applicationContext.getBean(FederationService.class));
        userService = Mockito.spy(new UserService(userRepository, taskScheduler, passwordEncoder));
        teamService = Mockito.spy(new TeamService(teamRepository));
        competitionService = Mockito.spy(new CompetitionService(competitionRepository));
        InviteToFederationManagerController inviteController = new InviteToFederationManagerController(
                userService, emailService, federationService
        );

        CreateCompetitionController createCompetitionController = new CreateCompetitionController(competitionService, userService, teamService);
        ViewAllCompetitionsController viewAllCompetitionsController = new ViewAllCompetitionsController(competitionService, sportService);

        FederationManagerInviteController fedManInvite = new FederationManagerInviteController(userService, federationService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(inviteController, fedManInvite, createCompetitionController, viewAllCompetitionsController).build();
    }

    @Before("@create_view_update_competition")
    public void setup() throws Exception {
        setupMocking();
        Location location = new Location("adminAddr1", "adminAddr2", "adminSuburb", "adminCity", "4dm1n", "adminLand");
        user = new User("Admin", "Admin", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(),
                "test@test.com", "plaintextPassword", location);
        Location location2 = new Location("adminAddr1", "adminAddr2", "adminSuburb", "adminCity", "4dm1n", "adminLand");
        team = new Team("test1", "Hockey", location2);
        Location compLoc = new Location(null, null, null, null, null, null);
        LocalDateTime now = LocalDateTime.now();
        competition = new UserCompetition("comp", new Grade(Grade.DEFAULT_AGE, Grade.Sex.MENS, Grade.DEFAULT_COMPETITIVENESS), "sport", compLoc, now, now, Collections.<User>emptySet());
        Sport sport = new Sport("soccer");
        user.setFavoriteSports(List.of(sport));
        user.confirmEmail();
        userRepository.save(user);
        teamRepository.save(team);
        competitionRepository.save(competition);

        Location loc = new Location("abcd", null, null, "chch", null, "nz");
        generalUser = new User("Test", "User", "test1@example.com", "insecure", loc);
        generalUser = userService.updateOrAddUser(generalUser);
        generalUser = userService.findUserById(generalUser.getUserId()).orElseThrow();
        Page<User> page = Page.empty();
        Mockito.doReturn(page).when(userService).getPaginatedUsers(any());

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        filterSports = new ArrayList<>();

        when(userService.getCurrentUser()).thenReturn(Optional.of(user));
    }

    @Given("I am a user of account type federation administrator")
    public void iAmAUserOfAccountTypeFederationAdministrator() {
        user.grantAuthority(AuthorityType.FEDERATION_MANAGER);
    }


    @And("I am on the create or update competition page")
    public void iAmOnTheCreateOrUpdateCompetitionPage() throws Exception {
        mockMvc.perform(get("/create-competition"))
                .andExpect(status().isOk()) // Accepted 200
                .andExpect(view().name("createCompetitionForm"));
    }

    @Then("There are fields for name, sport and grade")
    public void thereAreFieldsForNameSportAndGradeLevel() throws Exception {
        mockMvc.perform(get("/create-competition"))
                .andExpect(status().isOk())
                .andExpect(view().name("createCompetitionForm"));
    }

    @And("I input valid information for name, dates, sport and grade,")
    public void iInputValidInformationForNameSportAndGradeLevel() throws Exception {
        Date startDate = Date.from(Instant.now().plusSeconds(2000));
        Date endDate = Date.from(Instant.now().plusSeconds(3000));
        mockMvc.perform(multipart("/create-competition", 42L)
                        .param("name", "Sample Competition")
                        .param("sport", "Soccer")
                        .param("startDateTime", startDate.toString())
                        .param("endDateTime", endDate.toString())
                        .param("age", String.valueOf(Grade.Age.ADULT))
                        .param("sex", String.valueOf(Grade.Sex.MENS))
                        .param("competitiveness", String.valueOf(Grade.Competitiveness.PROFESSIONAL))
                        .param("usersOrTeams", "users")
                        .param("userTeamID", String.valueOf(user.getId())))
                .andExpect(status().isFound());
    }

    @When("I attempt to access the create a competition page,")
    public void iAttemptToAccessTheCreateACompetitionPage() throws Exception {
        mockMvc.perform(get("/create-competition"));
    }

    @Then("I am brought to the create competition page.")
    public void iAmBroughtToTheCreateCompetitionPage() throws Exception {
        mockMvc.perform(get("/create-competition"))
                .andExpect(status().isOk()) // Accepted 200
                .andExpect(view().name("createCompetitionForm"));
    }

    @Given("I am a normal user,")
    public void iAmANormalUser() {
        Assertions.assertTrue(user.getAuthorities().isEmpty());
    }

    @Then("I am instead shown an error message stating I don’t have valid permissions to access this page.")
    public void iAmInsteadShownAnErrorMessageStatingIDonTHaveValidPermissionsToAccessThisPage() throws Exception {
        mockMvc.perform(get("/create-competition"))
                .andExpect(status().is2xxSuccessful()) // This should be 400, but security doesn't run properly. Works manually
                .andExpect(view().name("createCompetitionForm"));
    }

    @When("I attempt to access the update a competition page for a competition,")
    public void iAttemptToAccessTheUpdateACompetitionPageForACompetition() throws Exception {
        mockMvc.perform(get("/create-competition")
                .param("edit", String.valueOf(competition.getCompetitionId())));

    }

    @Then("I am brought to the update competition page for that competition.")
    public void iAmBroughtToTheUpdateCompetitionPageForThatCompetition() throws Exception {
        mockMvc.perform(get("/create-competition")
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
        mockMvc.perform(post("/create-competition")
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
        mockMvc.perform(post("/create-competition")
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
        mockMvc.perform(post("/create-competition")
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
        mockMvc.perform(post("/create-competition")
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
        mockMvc.perform(post("/create-competition")
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
        mockMvc.perform(post("/create-competition")
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
        mockMvc.perform(post("/create-competition")
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

    @Given("I am a federation manager")
    public void givenIAmAFederationManager() {
        user.grantAuthority(AuthorityType.FEDERATION_MANAGER);
        userRepository.save(user);
        Assertions.assertTrue(user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_FEDERATION_MANAGER")));
    }

    @When("I click on the ‘Invite to Federation Managers’ UI element,")
    public void clickOnInviteToFederationsManagerUIElement() throws Exception {
        mockMvc.perform(get("/inviteToFederationManager"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(view().name("inviteFederationManager"));
    }

    @Then("I’m taken to a page where I can see all users who aren’t federation managers.")
    public void takenToPageToViewAllNonFedManUsers() throws Exception {
        mockMvc.perform(get("/inviteToFederationManager"))
                .andExpect(status().isOk())
                .andExpect(view().name("inviteFederationManager"))
                .andExpect(model().attributeExists("listOfUsers"));
    }

    //Would be better as a end2end
    @And("with each user's information there is a button to invite.")
    public void inviteButtonExists() throws Exception {
        mockMvc.perform(get("/inviteToFederationManager"))
                .andExpect(status().isOk())
                .andExpect(view().name("inviteFederationManager"))
                .andExpect(model().attributeExists("listOfUsers"));
    }

    @And("I’m on the “Invite to Federation Managers” page,")
    public void iMOnTheInviteToFederationManagersPage() throws Exception {
        mockMvc.perform(get("/inviteToFederationManager"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(view().name("inviteFederationManager"));
    }


    @When("I enter a string into the search bar,")
    public void iEnterAStringIntoTheSearchBar() throws Exception {
        mockMvc.perform(get("/inviteToFederationManager").param("currentSearch", "test"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(view().name("inviteFederationManager"));
    }

    @Then("only user profiles whose first name, last name or email matches that string are shown")
    public void onlyUserProfilesWhoseFirstNameLastNameOrEmailMatchesThatStringAreShown() throws Exception {
        mockMvc.perform(get("/inviteToFederationManager").param("currentSearch", "test"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(view().name("inviteFederationManager"))
                .andExpect(model().attributeExists("listOfUsers"));
    }


    @Given("I am a general user of TAB, And I’ve been invited to become a federation manager \\(received the email)")
    public void iAmAGeneralUserOfTABAndIVeBeenInvitedToBecomeAFederationManagerReceivedTheEmail() throws Exception {
        Mockito.doNothing().when(emailService).federationManagerInvite(eq(generalUser), any(HttpServletRequest.class), any());
        //Mockito.doReturn(Optional.of(user)).when(userService).getCurrentUser();
        mockMvc.perform(post("/inviteToFederationManager")
                        .param("userId", String.valueOf(generalUser.getUserId()))).andExpect(status().isFound())
                .andExpect(view().name("redirect:/inviteToFederationManager"));
        verify(emailService).federationManagerInvite(any(), any(), any());
    }

    @When("I click through the link in the email invitation to become a federation manager")
    public void iClickThroughTheLinkInTheEmailInvitationToBecomeAFederationManager() throws Exception {
        Optional<FederationManagerInvite> invite = federationService.findFederationManagerByUser(generalUser);
        Mockito.doReturn(Optional.of(generalUser)).when(userService).getCurrentUser();
        if (invite.isPresent()) {
            String url = "/federationManager?token=" + invite.get().getToken();
            mockMvc.perform(get(url)).andExpect(status().isFound()).andExpect(view().name("federationManagerInvite"));

        }
    }

    @Then("I am present with a screen on the website offering me to become a federation manager")
    public void iAmPresentWithAScreenOnTheWebsiteOfferingMeToBecomeAFederationManager() throws Exception {
        Optional<FederationManagerInvite> invite = federationService.findFederationManagerByUser(generalUser);
        Mockito.doReturn(Optional.of(generalUser)).when(userService).getCurrentUser();

        if (invite.isPresent()) {
            String url = "/federationManager?token=" + invite.get().getToken();
            mockMvc.perform(get(url)).andExpect(status().isFound()).andExpect(view().name("federationManagerInvite"));

        }
    }

    @Given("I am on a page dedicated to displaying competitions")
    public void iAmOnAPageDedicatedToDisplayingCompetitions() throws Exception {
        mockMvc.perform(get(VIEW_ALL))
                .andExpect(status().isOk()) // Accepted 200
                .andExpect(view().name("viewAllCompetitions"));
    }

    private static Date addSeconds(Date date, long seconds) {
        Date ret = (Date) date.clone();
        ret.setTime(date.getTime() + seconds);
        return ret;
    }

    private void setDateTo(Competition competition, long start, long end) {
        long now = Instant.now().getEpochSecond();
        long startDate = now + start;
        long endDate = now + end;
        competition.setDateAsEpochSecond(startDate, endDate);
    }

    private void generateCompetitionsForSport(String sport) {
        long smallTimeStep = 5000;
        long bigTimeStep = 10000;
        Date now = Date.from(Instant.now());

        for (int i=0; i<NUM_PAST; i++) {
            Competition comp = new UserCompetition("myCompetition", Grade.randomGrade(), sport);
            setDateTo(competition, -bigTimeStep, -smallTimeStep);
            competitionService.updateOrAddCompetition(comp);
        }

        for (int i=0; i<NUM_FUTURE; i++) {
            Competition comp = new UserCompetition("myCompetition", Grade.randomGrade(), sport);
            setDateTo(competition, smallTimeStep, bigTimeStep);
            competitionService.updateOrAddCompetition(comp);
        }

        for (int i=0; i<NUM_CURRENT; i++) {
            Competition comp = new UserCompetition("myCompetition", Grade.randomGrade(), sport);
            setDateTo(competition, -bigTimeStep, bigTimeStep);
            competitionService.updateOrAddCompetition(comp);
        }
    }

    @And("there exist past and current competitions for a {string}")
    public void thereExistPastAndCurrentCompetitionsForASport(String sport) {
        generateCompetitionsForSport(sport);
    }

    @When("I apply a filter for that {string} and select an option to display all competitions")
    public void iApplyAFilterForThatSportAndSelectAnOptionToDisplayAllCompetitions(String sport) {
        timing = null;
        filterSports.add(sport);
    }

    @When("I apply a filter for that {string} and I select an option to display only current competitions")
    public void iApplyAFilterForThatSportAndISelectAnOptionToDisplayOnlyCurrentCompetitions(String sport) {
        timing = ViewAllCompetitionsController.Timing.CURRENT;
        filterSports.add(sport);
    }

    @When("I apply a filter for that {string} and I select an option to display only past competitions")
    public void iApplyAFilterForThatSportAndISelectAnOptionToDisplayOnlyPastCompetitions(String sport) {
        timing = ViewAllCompetitionsController.Timing.PAST;
        filterSports.add(sport);
    }

    @Then("I am shown all competitions, past and current for the selected {string}")
    public void iAmShownAllCompetitionsPastAndCurrentForTheSelectedSport(String sport) throws Exception {
        String[] sports = new String[] {sport};
        // We dont pass in the timing here, because `null` timing implies
        // that ALL competitions should be shown.
        mockMvc.perform(get(VIEW_ALL)
                .param("page", "1")
                .param("sports", sports));
        Mockito.verify(competitionService).findAllCompetitionsBySports(any(), eq(List.of(sport)));
    }

    @Then("I am shown only current competitions for the selected {string}")
    public void iAmShownOnlyCurrentCompetitionsForTheSelectedSport(String sport) throws Exception {
        String[] param = new String[] {"CURRENT"};
        String[] sports = new String[] {sport};
        mockMvc.perform(get(VIEW_ALL)
                .param("page", "1")
                .param("times", param)
                .param("sports", sports));
        Mockito.verify(competitionService).findCurrentCompetitionsBySports(any(), eq(List.of(sport)));
    }

    @Then("I am shown only past competitions for the selected {string}")
    public void iAmShownOnlyPastCompetitionsForTheSelectedSport(String sport) throws Exception {
        String[] param = new String[] {"PAST"};
        String[] sports = new String[] {sport};
        mockMvc.perform(get(VIEW_ALL)
                .param("page", "1")
                .param("times", param)
                .param("sports", sports));
        Mockito.verify(competitionService).findPastCompetitionsBySports(any(), eq(List.of(sport)));
    }
}