package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.controller.CreateClubController;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.form.CreateAndEditClubForm;
import nz.ac.canterbury.seng302.tab.mail.EmailService;
import nz.ac.canterbury.seng302.tab.repository.ClubRepository;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.ClubService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@ContextConfiguration(classes = IntegrationTestConfigurations.class)
public class CreateViewUpdateClubIntegrationTests {


    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private UserService userService;

    @SpyBean
    private ClubService clubService;

    @Autowired
    private ApplicationContext applicationContext;


    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamService teamService;

    private ClubRepository clubRepository;



    private User user;

    private Team team;

    private Team team2;

    private Team team3;


    @Before("@create_club")
    public void setup() throws IOException {

        clubRepository = applicationContext.getBean(ClubRepository.class);
        teamRepository = applicationContext.getBean(TeamRepository.class);
        userRepository = applicationContext.getBean(UserRepository.class);

        TaskScheduler taskScheduler = applicationContext.getBean(TaskScheduler.class);
        EmailService emailService = applicationContext.getBean(EmailService.class);
        PasswordEncoder passwordEncoder = applicationContext.getBean(PasswordEncoder.class);

        userService = Mockito.spy(new UserService(userRepository, taskScheduler, emailService, passwordEncoder));
        clubService = Mockito.spy(new ClubService(clubRepository));

        this.mockMvc = MockMvcBuilders.standaloneSetup(new CreateClubController(clubService, userService, teamService)).build();

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        teamRepository.deleteAll();
        userRepository.deleteAll();
        Location testLocation = new Location(null, null, null, "CHCH", null, "NZ");
        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", testLocation);
        team = new Team("test1", "Hockey", new Location(null, null, null, "chch", null, "nz"));
        team2 = new Team("test2", "Hockey", new Location("2 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand"));
        team3 = new Team("test3", "Rugby", new Location("3 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand"));

        teamRepository.save(team);
        teamRepository.save(team2);
        teamRepository.save(team3);


        userRepository.save(user);

        when(userService.getCurrentUser()).thenReturn(Optional.of(user));

    }

    private CreateAndEditClubForm createAndEditClubForm() {
        var form =  new CreateAndEditClubForm();
        List<Team> teams = new ArrayList<>();
        teams.add(team);
        teams.add(team2);
        form.setCity("Chch");
        form.setAddressLine1("1");
        form.setAddressLine2("test");
        form.setPostcode("8042");
        form.setCountry("nz");
        form.setName("Hockey club");
        form.setSuburb("test");
        form.setSelectedTeams(teams);
        return form;
    }


    private ResultActions postCreateAndEditClubForm(CreateAndEditClubForm form) throws Exception {

        return mockMvc.perform(post("/createClub")
                .param("clubId", "1")
                .param("name", form.getName())
                .param("addressLine1", form.getAddressLine1())
                .param("addressLine2", form.getAddressLine2())
                .param("suburb", form.getSuburb())
                .param("city", form.getCity())
                .param("country", form.getCountry())
                .param("postcode", form.getPostcode())
                .param("selectedTeams", form.getSelectedTeams().toString()));
    }



    @Given("I am anywhere on the system")
    @WithMockUser()
    public void i_am_anywhere_on_the_system() throws Exception {
        mockMvc.perform(get("/home"));
    }

    @When("I click on a UI element to create a club")
    @WithMockUser()
    public void i_click_on_a_UI_element_to_create_a_club() throws Exception {
        mockMvc.perform(get("/createClub"));
    }

    @Then("I will see a form to create a club")
    @WithMockUser()
    public void i_will_see_a_form_to_create_a_club() throws Exception {
        mockMvc.perform(get("/createClub")).andExpect(status().isOk());
    }

    @Given("I am on the create club page")
    public void i_am_on_the_create_club_page() throws Exception {
        mockMvc.perform(get("/createClub"));
    }

    @When("I enter valid values for the name, address line, postcode, city and country  and optionally a logo")
    @WithMockUser()
    public void i_enter_valid_values_for_name_address_line_postcode_city_and_country_and_optionally_a_logo() throws Exception {
        /*List<Team> teamsToAdd = new ArrayList<>();
        teamsToAdd.add(team);
        teamsToAdd.add(team2);

*/
     //   Mockito.when(clubService.validateTeamSportsinClub(teamsToAdd)).thenReturn(true);

       /* mockMvc.perform(post("/createClub", 42L)
                        .with(csrf())
                .param("clubId", "1")
                .param("name", "new club")
                .param("addressLine1", "addressline1")
                .param("addressLine2", "addressline2")
                .param("suburb", "Ilam")
                .param("city", "Christchurch")
                .param("country", "New Zealand")
                .param("postcode", "1111")
                .param("selectedTeams", team.getTeamId().toString()))
                .andExpect(status().isFound());*/

        CreateAndEditClubForm form = createAndEditClubForm();
        postCreateAndEditClubForm(form)
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/home"));
    }

    @Then("The club is created into the system")
    public void the_club_is_created_into_the_system() {
        verify(clubService, times(1)).updateOrAddClub(any());
        System.out.println(clubService.findAll());

    }



}
