// package nz.ac.canterbury.seng302.tab.integration;

// import static org.junit.jupiter.api.Assertions.fail;
// import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
// import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
// import static org.springframework.test.util.AssertionErrors.assertEquals;
// import static org.springframework.test.util.AssertionErrors.assertNotNull;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// import java.io.IOException;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Optional;
// import java.util.Set;

// import org.mockito.Mockito;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.context.ApplicationContext;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.ResultActions;
// import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders;
// import org.springframework.ui.ExtendedModelMap;
// import org.springframework.ui.Model;

// import io.cucumber.java.Before;
// import io.cucumber.java.en.Given;
// import io.cucumber.java.en.Then;
// import io.cucumber.java.en.When;
// import nz.ac.canterbury.seng302.tab.controller.ViewAllTeamsController;
// import nz.ac.canterbury.seng302.tab.entity.Team;
// import nz.ac.canterbury.seng302.tab.entity.User;
// import nz.ac.canterbury.seng302.tab.service.LocationService;
// import nz.ac.canterbury.seng302.tab.service.SportService;
// import nz.ac.canterbury.seng302.tab.service.TeamService;
// import nz.ac.canterbury.seng302.tab.service.UserService;


// @AutoConfigureMockMvc(addFilters = false)
// @SpringBootTest
// public class U19FilterTeamsBySport {

//     @Autowired
//     private ApplicationContext applicationContext;

//     private UserService userService;
//     private TeamService teamService;
//     private SportService sportService;
//     private LocationService locationService;


//     private MockMvc mockMvc;

//     private ResultActions result;

//     private Set<String> selectedSports = new HashSet<>();


//     private void setupMorganMocking() throws IOException {

//         userService = Mockito.spy(applicationContext.getBean(UserService.class));
//         teamService = applicationContext.getBean(TeamService.class);
//         locationService = applicationContext.getBean(LocationService.class);
//         sportService = applicationContext.getBean(SportService.class);

//         // Mock the authentication
//         Mockito.doReturn(Optional.of(User.defaultDummyUser())).when(userService).getCurrentUser();

//         this.mockMvc = MockMvcBuilders.standaloneSetup(new ViewAllTeamsController(
//                 teamService, userService, locationService, sportService
//         )).build();
//     }

//     @Before("@filter_teams_by_sport")
//     public void setup() throws IOException {
//         setupMorganMocking();

//         teamService.deleteAllTeams();

//         result = null;
//         selectedSports.clear();

//         int ct = 0;
//         for (var sport: List.of("soccer", "hockey", "cricket")) {
//             teamService.addTeam(new Team("team" + ct, sport));
//             ct++;
//         }
//     }

//     private void performGet() {
//         Model model = new ExtendedModelMap();
//         model.addAttribute("sports", List.copyOf(selectedSports));

//         try {
//             result = mockMvc.perform(get("/view-teams")
//                     .with(csrf())
//                     .with(anonymous())
//                     .flashAttr("model", model)
//                     .param("page", "1"));
//         } catch (Exception e) {
//             fail(e);
//         }
//     }

//     @Given("I am on the search teams page")
//     public void i_am_on_the_search_teams_page() {
//         // No-op, this only exists so the feature file looks pretty
//     }

//     @When("I select a sport {string}")
//     public void iSelectASport(String sport) {
//         selectedSports.add(sport);
//         performGet();
//     }

//     @Then("only teams with sports are selected:")
//     public void onlyTeamsWithSportsAreSelected(List<String> sports) throws Exception {
//         performGet();
//         result.andDo(MockMvcResultHandlers.print());
//         result.andDo(result -> {
//             var model = result.getModelAndView().getModel();
//             List<Team> teams = (List<Team>) model.get("listOfTeams");
//             assertNotNull("The result did not contain 'listOfTeams'", teams);
//             for (var team: teams) {
//                 if (!sports.contains(team.getSport())) {
//                     String msg = String.format("Model %s didn't contain sport '%s'", sports, team.getSport());
//                     fail(msg);
//                 }
//             }
//         });
//     }

//     @When("I deselect a sport {string}")
//     public void iDeselectASport(String sport) {
//         selectedSports.remove(sport);
//         performGet();
//     }

//     @When("no teams are selected")
//     public void noTeamsAreSelected() throws Exception {
//         selectedSports.clear();
//     }

//     @Then("all teams are shown")
//     public void allTeamsAreShown() throws Exception {
//         performGet();
//         result.andExpect(status().isOk());
//         result.andDo(result -> {
//             var model = result.getModelAndView().getModel();
//             List<Team> teams = (List<Team>) model.get("listOfTeams");
//             int allTeamsSize = teamService.getAllTeamNames().size();
//             assertEquals("All teams not shown", teams.size(), allTeamsSize);
//         });
//     }
// }
