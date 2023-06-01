package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import nz.ac.canterbury.seng302.tab.controller.ViewAllTeamsController;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.LocationService;
import nz.ac.canterbury.seng302.tab.service.SportService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.assertj.core.api.Fail.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class U19FilterTeamsBySport {

    @Autowired
    private ApplicationContext applicationContext;

    private UserRepository userRepository;

    private UserService userService;
    private TeamService teamService;
    private SportService sportService;
    private LocationService locationService;


    private MockMvc mockMvc;

    private ResultActions result;

    private Set<String> selectedSports = new HashSet<>();


    private void setupMorganMocking() {
        userRepository = applicationContext.getBean(UserRepository.class);

        teamService = applicationContext.getBean(TeamService.class);
        userService = applicationContext.getBean(UserService.class);
        teamService = applicationContext.getBean(TeamService.class);
        sportService = applicationContext.getBean(SportService.class);

        this.mockMvc = MockMvcBuilders.standaloneSetup(new ViewAllTeamsController(
                teamService, userService, locationService, sportService
        )).build();
    }

    @Before("@reset_password")
    public void setup() throws IOException {
        setupMorganMocking();

        teamService.deleteAllTeams();

        result = null;
        selectedSports.clear();

        int ct = 0;
        for (var sport: List.of("soccer", "hockey", "cricket")) {
            teamService.addTeam(new Team("team" + ct, sport));
            ct++;
        }
    }

    private void performGet() {
        Model model = new ExtendedModelMap();
        model.addAttribute("sports", List.copyOf(selectedSports));

        try {
            result = mockMvc.perform(get("post")
                    .flashAttr("model", model)
                    .param("page", "1"));
        } catch (Exception e) {
            fail("Nah!");
        }
    }

    @Before("filter_teams_by_sport")
    public void beforeTest() {

    }

    @Given("I select a sport {string}")
    public void iSelectASport(String sport) {
        selectedSports.add(sport);
        performGet();
    }

    @Then("only teams with sports are selected:")
    public void onlyTeamsWithSportsAreSelected() {
    }

    @Given("I deselect a sport {string}")
    public void iDeselectASport(String sport) {
        selectedSports.remove(sport);
        performGet();
    }

    @Given("no teams are selected")
    public void noTeamsAreSelected() {

    }

    @Then("all teams are shown")
    public void allTeamsAreShown() {
    }
}
