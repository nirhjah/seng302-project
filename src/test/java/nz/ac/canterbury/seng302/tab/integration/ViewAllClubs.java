package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.controller.CreateClubController;
import nz.ac.canterbury.seng302.tab.controller.ViewAllTeamsController;
import nz.ac.canterbury.seng302.tab.controller.ViewTeamController;
import nz.ac.canterbury.seng302.tab.controller.ViewClubController;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.enums.Role;
import nz.ac.canterbury.seng302.tab.mail.EmailService;
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

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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

    private MockMvc mockMvc;

    private Set<String> selectedSports = new HashSet<>();
    private Set<String> selectedCities = new HashSet<>();

    private static final String TEST_SPORT = "TEST_SPORT";

    private void setupMorganMocking() throws IOException {

        userService = applicationContext.getBean(UserService.class);
        teamService = applicationContext.getBean(TeamService.class);
        locationService = applicationContext.getBean(LocationService.class);
        sportService = applicationContext.getBean(SportService.class);
        clubService = applicationContext.getBean(ClubService.class);

        // Mock the authentication
        Mockito.doReturn(Optional.of(User.defaultDummyUser())).when(userService).getCurrentUser();

        this.mockMvc = MockMvcBuilders.standaloneSetup(new ViewAllTeamsController(
                teamService, clubservice, locationService, sportService
        )).build();
    }

    @Before("@view_teams_page_filtering")
    public void setup() throws IOException {
        setupMorganMocking();

        selectedSports.clear();
        selectedCities.clear();
    }

    /**
     * Performs the get request to the <code>/view-teams</code> page
     * @return The result of the request, so you can chain <code>.andExpect(...)</code>
     */
    private ResultActions performGet() {
        // Build the request
        MockHttpServletRequestBuilder request = get("/view-clubs")
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

}
