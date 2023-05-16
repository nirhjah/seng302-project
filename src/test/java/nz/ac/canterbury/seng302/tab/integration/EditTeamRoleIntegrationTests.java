package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.controller.EditTeamRoleController;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.enums.Role;
import nz.ac.canterbury.seng302.tab.mail.EmailService;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class EditTeamRoleIntegrationTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    WebApplicationContext webApplicationContext;

    HttpServletRequest request;

    @SpyBean
    TeamService teamService;

    @SpyBean
    UserService userService;

    private UserRepository userRepository;

    private TeamRepository teamRepository;

    Team team;

    User user;
    User testUser1;
    User testUser2;
    User testUser3;

    static long TEAM_ID = 1;

    @Before
    public void setupUser() throws IOException {
        // get the application context (thanks to @CucumberContextConfiguration in the Configurations class)
        userRepository = applicationContext.getBean(UserRepository.class);
        teamRepository = applicationContext.getBean(TeamRepository.class);
        // get all the necessary beans
        TaskScheduler taskScheduler = applicationContext.getBean(TaskScheduler.class);
        EmailService emailService = applicationContext.getBean(EmailService.class);
        PasswordEncoder passwordEncoder = applicationContext.getBean(PasswordEncoder.class);
        // create the spy with the required beans
        this.userService = Mockito.spy(new UserService(userRepository, taskScheduler, emailService, passwordEncoder));
        // create a custom MockMvc setup with the required controllers and inject the UserService Spy
        this.teamService = Mockito.spy(new TeamService(teamRepository));
        this.mockMvc = MockMvcBuilders.standaloneSetup(new EditTeamRoleController(userService, teamService)).build();

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);


        userRepository.deleteAll();
        teamRepository.deleteAll();
        Location testLocation1 = new Location(null, null, null, "CHCH", null, "NZ");
        Location testLocation2 = new Location(null, null, null, "Nelson", null, "NZ");
        Location testLocation3 = new Location(null, null, null, "Dunners", null, "NZ");
        Location testLocation4 = new Location(null, null, null, "Auckland", null, "NZ");
        user = new User("John", "Doe",
                new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@test.com",
                "Password123!", testLocation1);
        testUser1 = new User("Jane", "Doe",
                new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "team900@tab.gmail.com",
                "Password123!", testLocation2);
        testUser2 = new User("Test", "Acount",
                new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "team901@tab.gmail.com",
                "Password123!", testLocation3);
        testUser3 = new User("testing", "Acount",
                new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "team91@tab.gmail.com",
                "Password123!", testLocation4);
        userRepository.save(user);
        userRepository.save(testUser1);
        userRepository.save(testUser2);
        userRepository.save(testUser3);

        team = new Team("Test Team", "Hockey");
        team.setManager(userRepository.findById(user.getUserId()).get());
        team.setMember(userRepository.findById(testUser1.getUserId()).get());
        team.setMember(userRepository.findById(testUser2.getUserId()).get());
        team.setMember(userRepository.findById(testUser3.getUserId()).get());
//        team.setManager(user);
//        team = teamRepository.save(team);
//        team.setMember(testUser1);
//        team = teamRepository.save(team);
        team = Mockito.spy(team);
        Mockito.when(team.getTeamId()).thenReturn(TEAM_ID);
        Mockito.when(teamService.getTeam(TEAM_ID)).thenReturn(team);
        Mockito.when(userService.getCurrentUser()).thenReturn(Optional.of(user));
        Mockito.when(teamService.userRolesAreValid(List.of(Role.MEMBER.toString(), Role.COACH.toString(),
                Role.MANAGER.toString(), Role.MEMBER.toString()))).thenReturn(false);
    }

    @Given("I have created a team")
    public void iHaveCreatedATeam() {
        assertNotNull(teamService.getTeam(team.getTeamId()));
    }


    @And("I am a manager of the team")
    public void iAmAManagerOfTheTeam() {
        assertTrue(team.isManager(user));
    }


    @When("I click on the edit team role button")
    public void iClickOnTheEditTeamRoleButton() throws Exception {
        mockMvc.perform(get("/editTeamRole", 42L)
                        .with(csrf())
                .param("edit", String.valueOf(TEAM_ID))).andExpect(status().isOk());
    }

    @Then("I am taken to the edit team members role page")
    public void iAmTakenToTheEditTeamMembersRolePage() throws Exception {
        //Mockito.doReturn(Optional.of(user)).when(mockUserService).getCurrentUser();
        //Mockito.when(userService.getCurrentUser()).thenReturn(Optional.of(user));
        mockMvc.perform(get("/editTeamRole", 42L)
                .param("edit", String.valueOf(team.getTeamId()))).andExpect(status().isOk())
                .andExpect(view().name("editTeamRoleForm"));
    }

    @Given("I am on the edit team members role page")
    public void iAmOnTheEditTeamMembersRolePage() throws Exception {
        mockMvc.perform(get("/editTeamRole", 42L)
                        .param("edit", String.valueOf(team.getTeamId()))).andExpect(status().isOk())
                .andExpect(view().name("editTeamRoleForm"));
    }

    /**
     * Checks that the expected team member's ID appear in the user ID list returned
     * @throws Exception for mockMVC if it can't perform request
     */
    @When("I see all members of the team")
    public void iSeeAllMembersOfTheTeam() throws Exception {
        mockMvc.perform(get("/editTeamRole", 42L)
                        .param("edit", String.valueOf(team.getTeamId()))).andExpect(status().isOk())
                .andExpect(view().name("editTeamRoleForm"))
                .andExpect(model().attributeExists("userIds"));
    }

    /**
     * Checks that the possibleRoles are returned by the request
     * (In the page these are options of a select drop-down)
     * @throws Exception for mockMVC if it can't perform request
     */
    @Then("I can select their role to be any of the options")
    public void iCanSelectTheirRoleToBeAnyOfTheOptions() throws Exception {
        mockMvc.perform(get("/editTeamRole", 42L)
                        .param("edit", String.valueOf(team.getTeamId()))).andExpect(status().isOk())
                .andExpect(view().name("editTeamRoleForm"))
                .andExpect(model().attribute("possibleRoles", Role.values()));
    }


    @When("I change one team members role")
    public void iChangeOneTeamMembersRole() {
        //Change is made in JS
        assertNotNull(team.getTeamRoles());
    }

    @And("I save the change in team roles")
    public void iSaveTheChangeInTeamRoles() throws Exception {
        mockMvc.perform(post("/editTeamRole", 42L)
                .param("teamID", String.valueOf(team.getTeamId()))
                .param("userIds", String.valueOf(List.of(String.valueOf(user.getUserId()),
                        String.valueOf(testUser1.getUserId()), String.valueOf(testUser2.getUserId()),
                        String.valueOf(testUser3.getUserId()))))
                .param("userRoles", String.valueOf(List.of(Role.MANAGER, Role.COACH, Role.MANAGER, Role.MEMBER))))
                .andExpect(status().isOk());
    }

    @When("I select no managers and save the change")
    public void iSelectNoManagersAndSaveTheChange() throws Exception {
        mockMvc.perform(post("/editTeamRole", 42L)
                        .param("teamID", String.valueOf(team.getTeamId()))
                        .param("userIds", String.valueOf(List.of(String.valueOf(user.getUserId()),
                                String.valueOf(testUser1.getUserId()), String.valueOf(testUser2.getUserId()),
                                String.valueOf(testUser3.getUserId()))))
                        .param("userRoles", String.valueOf(List.of(Role.MEMBER, Role.COACH, Role.MANAGER, Role.MEMBER))));
    }

    @Then("The page returns back to the edit team page with an error")
    public void thePageReturnsBackToTheEditTeamPageWithAnError() throws Exception {
        mockMvc.perform(post("/editTeamRole", 42L)
                        .param("teamID", String.valueOf(team.getTeamId()))
                        .param("userIds", String.valueOf(List.of(String.valueOf(user.getUserId()),
                                String.valueOf(testUser1.getUserId()), String.valueOf(testUser2.getUserId()),
                                String.valueOf(testUser3.getUserId()))))
                        .param("userRoles", String.valueOf(List.of(Role.MANAGER, Role.COACH, Role.MANAGER, Role.MEMBER))))
                .andExpect(status().isFound()).andExpect(model().attributeExists("managerError"));
    }

    @When("I select {int} managers and save the change")
    public void iSelectManagersAndSaveTheChange(int arg0) throws Exception {
        mockMvc.perform(post("/editTeamRole", 42L)
                        .param("teamID", String.valueOf(team.getTeamId()))
                        .param("userIds", String.valueOf(List.of(String.valueOf(user.getUserId()),
                                String.valueOf(testUser1.getUserId()), String.valueOf(testUser2.getUserId()),
                                String.valueOf(testUser3.getUserId()))))
                        .param("userRoles", String.valueOf(List.of(Role.MANAGER, Role.MANAGER, Role.MANAGER, Role.MANAGER))))
                .andExpect(status().isFound()).andExpect(model().attributeExists("managerError"));
    }
}
