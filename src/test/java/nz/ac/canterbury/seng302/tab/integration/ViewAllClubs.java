package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.controller.CreateClubController;
import nz.ac.canterbury.seng302.tab.controller.ViewAllClubsController;
import nz.ac.canterbury.seng302.tab.controller.ViewAllTeamsController;
import nz.ac.canterbury.seng302.tab.controller.ViewTeamController;
import nz.ac.canterbury.seng302.tab.controller.ViewClubController;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.enums.Role;
import nz.ac.canterbury.seng302.tab.mail.EmailService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import nz.ac.canterbury.seng302.tab.repository.*;
import nz.ac.canterbury.seng302.tab.service.*;
import nz.ac.canterbury.seng302.tab.service.image.ClubImageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
/**
 * 
 */
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@ContextConfiguration(classes = IntegrationTestConfigurations.class)
public class ViewAllClubs {
    @Autowired
    private ApplicationContext applicationContext;

    private UserService userService;
    private TeamService teamService;
    private ClubService clubService;
    private SportService sportService;
    private LocationService locationService;
    private UserRepository userRepository;

    private ClubRepository clubRepository;

    private TeamRepository teamRepository;

    private MockMvc mockMvc;

    private Set<String> selectedSports = new HashSet<>();
    private Set<String> selectedCities = new HashSet<>();

    private static final String TEST_SPORT = "TEST_SPORT";

    Team team;

    User user;

    Club club;
    Club club2;
    Club club3;

    private void setupMorganMocking() throws IOException {

        userService = applicationContext.getBean(UserService.class);
        teamService = applicationContext.getBean(TeamService.class);
        locationService = applicationContext.getBean(LocationService.class);
        sportService = applicationContext.getBean(SportService.class);
        clubService = applicationContext.getBean(ClubService.class);
        userRepository = applicationContext.getBean(UserRepository.class);
        clubRepository = applicationContext.getBean(ClubRepository.class);
        teamRepository = applicationContext.getBean(TeamRepository.class);

        // Mock the authentication
        Mockito.doReturn(Optional.of(User.defaultDummyUser())).when(userService).getCurrentUser();

        this.mockMvc = MockMvcBuilders.standaloneSetup(new ViewAllClubsController(clubService)).build();
    }

    /**
     * Performs the get request to the <code>/view-teams</code> page
     * @return The result of the request, so you can chain <code>.andExpect(...)</code>
     */
    private ResultActions performGet() {
        // Build the request
        MockHttpServletRequestBuilder request = get("/view-all-clubs")
                .with(csrf())       // Required as the post is a form
                .with(anonymous())  // Pretend we're logged in
                .param("page", "1");
        // Populate the dropdowns
        for (String sportName : selectedSports)
            request = request.param("sports", sportName);
        for (String cityName : selectedCities)
            request = request.param("cities", cityName);
        try {
            return mockMvc.perform(request);
        } catch (Exception e) {
            fail(e);
            return null;
        }
    }

    @Before("@view_all_clubs")
    public void setup() throws IOException {
        setupMorganMocking();

        team = new Team("test3", "Rugby", new Location("3 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand"));

        Location testLocation = new Location("23 test street", "24 test street", "surburb", "city", "8782",
                "New Zealand");
        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(),
                "johndoe@example.com", "Password123!", testLocation);
        userRepository.save(user);


        club = new Club("Rugby Club", new Location("5 Test Lane", "", "", "Christchurch", "8042", "New Zealand"), "Rugby",null);
        club2 = new Club("Hockey Club", new Location("34 Testing", "", "", "Nelson", "2020", "New Zealand"), "Hockey", null);
        club3 = new Club("Football Club", new Location("Testing", "", "", "Auckland", "2020", "New Zealand"), "Football", null);
        team.setTeamClub(club);

        clubRepository.save(club);
        teamRepository.save(team);
        Mockito.when(userService.getCurrentUser()).thenReturn(Optional.of(user));
    }

    @Given("there are no other clubs")
    public void there_are_no_other_clubs() {
        clubRepository.deleteAll();
    }

    @Given("there is a club called {string} located in {string}")
    public void there_is_a_club_called_located_in(String clubName, String locationName) throws IOException {
        Location location = new Location("", "", "", locationName, "", "Test Country");
        Club club = new Club(clubName, location, "Test", null);
        clubService.updateOrAddClub(club);
    }

    @When("When I select the city {string}")
    public void when_i_select_the_city(String cityName) {
        selectedCities.add(cityName);
    }



    @Then("only these clubs are selected:")
    @SuppressWarnings("unchecked")
    public void only_these_teams_are_selected(List<String> expectedClubs) throws Exception {
        // We want the expectedTeams to exactly match the resulting teams
        // but we don't care about order
        Set<String> expectedClubsSet = Set.copyOf(expectedClubs);
        performGet()
                .andExpect(status().isOk()) // Accepted 200
                .andExpect(view().name("viewAllClubs")) // Rendering the right page
                .andDo(result -> {
                    // Check that all the teams are present
                    List<Club> listOfClubs = (List<Club>) result.getModelAndView().getModel().get("listOfClubs");
                    assertNotNull("The webpage's model does not contain a 'listOfTeams'", listOfClubs);
                    Set<String> setOfClubNames = listOfClubs.stream().map(Club::getName).collect(Collectors.toSet());
                    assertEquals(expectedClubsSet, setOfClubNames);
                });
    }


}
