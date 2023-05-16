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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class U26EditTeamRoleFeature {

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
        userService = Mockito.spy(new UserService(userRepository, taskScheduler, emailService, passwordEncoder));
        // create a custom MockMvc setup with the required controllers and inject the UserService Spy
        teamService = Mockito.spy(new TeamService(teamRepository));
        this.mockMvc = MockMvcBuilders.standaloneSetup(new EditTeamRoleController(userService, teamService)).build();

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);


        userRepository.deleteAll();
        teamRepository.deleteAll();
        Location testLocation = new Location(null, null, null, "CHCH", null, "NZ");
        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@test.com", "Password123!", testLocation);
        team = new Team("Test Team", "Hockey");
        team.setManager(user);
        team = teamRepository.save(team);
        team = Mockito.spy(team);
        Mockito.when(team.getTeamId()).thenReturn(TEAM_ID);
        Mockito.when(teamService.getTeam(TEAM_ID)).thenReturn(team);
        Mockito.when(userService.getCurrentUser()).thenReturn(Optional.of(user));

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
                .param("edit", String.valueOf(TEAM_ID))).andDo(print()).andExpect(status().isOk());
    }

    @Then("I am taken to the edit team members role page")
    public void iAmTakenToTheEditTeamMembersRolePage() throws Exception {
        //Mockito.doReturn(Optional.of(user)).when(mockUserService).getCurrentUser();
        //Mockito.when(userService.getCurrentUser()).thenReturn(Optional.of(user));
        mockMvc.perform(get("/editTeamRole", 42L)
                .param("edit", String.valueOf(team.getTeamId()))).andDo(print()).andExpect(status().isOk())
                .andExpect(view().name("editTeamRoleForm"));
    }
}
