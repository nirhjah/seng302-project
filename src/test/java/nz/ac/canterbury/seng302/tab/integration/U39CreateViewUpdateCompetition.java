//package nz.ac.canterbury.seng302.tab.integration;
//
//import io.cucumber.java.Before;
//import io.cucumber.java.en.And;
//import io.cucumber.java.en.Given;
//import io.cucumber.java.en.Then;
//import nz.ac.canterbury.seng302.tab.controller.CreateActivityController;
//import nz.ac.canterbury.seng302.tab.entity.Location;
//import nz.ac.canterbury.seng302.tab.entity.Team;
//import nz.ac.canterbury.seng302.tab.entity.User;
//import nz.ac.canterbury.seng302.tab.service.*;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.ApplicationContext;
//import org.springframework.test.web.servlet.MockMvc;
//
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.util.Optional;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@AutoConfigureMockMvc(addFilters = false)
//@SpringBootTest
//public class U39CreateViewUpdateCompetition {
//
//
//    @Autowired
//    private ApplicationContext applicationContext;
//
//    private MockMvc mockMvc;
//
//    private UserService userService;
//
//    private User user;
//
//    private void setupMocking() {
//        // get all the necessary beans
//        userService = Mockito.spy(applicationContext.getBean(UserService.class));
//
//        // create mockMvc manually with spied services
////        var controller = new CreateCompetitionController(
////                userService, competitionService
////        );
//        this.mockMvc = MockMvcBuilders.standaloneSetup().build();
//    }
//
//    @Before("@edit_lineup_for_game")
//    public void setup() throws Exception {
//        setupMocking();
//        Location location = new Location("abcd", null, null, "chch", null, "nz");
//        user = new User("Test", "User", "test@example.com", "insecure", location);
//        user = userService.updateOrAddUser(user);
//        user = userService.findUserById(user.getUserId()).orElseThrow();
//        Mockito.doReturn(Optional.of(user)).when(userService).getCurrentUser();
//    }
//
//    @Given("I am a user of account type federation administrator")
//    public void iAmAUserOfAccountTypeFederationAdministrator() {
//    }
//
//
//    @And("I am on the create or update competition page")
//    public void iAmOnTheCreateOrUpdateCompetitionPage() throws Exception {
//        mockMvc.perform(get("/createCompetition"))
//                .andExpect(status().isOk()) // Accepted 200
//                .andExpect(view().name("createCompetition"));
//    }
//
//    @Then("There are fields for name, sport and grade level")
//    public void thereAreFieldsForNameSportAndGradeLevel() throws Exception {
//        mockMvc.perform(get("/createCompetition"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("competitionForm")) // Assuming "competitionForm" is the name of the view
//                .andExpect(model().attributeExists("name")) // Assuming "name" is the name of the attribute for the name field
//                .andExpect(model().attributeExists("sport")) // Assuming "sport" is the name of the attribute for the sport field
//                .andExpect(model().attributeExists("gradeLevel")); // Assuming "gradeLevel" is the name of the attribute for the grade level field
//    }
//}
