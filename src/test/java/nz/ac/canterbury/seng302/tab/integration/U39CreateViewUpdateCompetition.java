package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.controller.FederationManagerInviteController;
import nz.ac.canterbury.seng302.tab.controller.InviteToFederationManagerController;
import nz.ac.canterbury.seng302.tab.entity.FederationManagerInvite;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.enums.AuthorityType;
import nz.ac.canterbury.seng302.tab.mail.EmailService;
import nz.ac.canterbury.seng302.tab.service.*;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class U39CreateViewUpdateCompetition {


    @Autowired
    private ApplicationContext applicationContext;

    private MockMvc mockMvc;

    private UserService userService;

    private EmailService emailService;

    private FederationService federationService;

    private User user;

    private User generalUser;

    private void setupMocking() {
        // get all the necessary beans
        userService = Mockito.spy(applicationContext.getBean(UserService.class));
        emailService = Mockito.spy(applicationContext.getBean(EmailService.class));
        federationService = Mockito.spy(applicationContext.getBean(FederationService.class));
        // create mockMvc manually with spied services
        InviteToFederationManagerController inviteController = new InviteToFederationManagerController(
                userService, emailService, federationService
        );

        FederationManagerInviteController fedManInvite = new FederationManagerInviteController(userService, federationService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(inviteController, fedManInvite).build();
    }

    @Before("@create_view_update_competition")
    public void setup() throws Exception {
        setupMocking();
        Location location = new Location("abcd", null, null, "chch", null, "nz");
        user = new User("Test", "User", "test@example.com", "insecure", location);
        Location loc = new Location("abcd", null, null, "chch", null, "nz");
        generalUser = new User("Test", "User", "test1@example.com", "insecure", loc);
        user.grantAuthority(AuthorityType.FEDERATION_MANAGER);
        user = userService.updateOrAddUser(user);
        generalUser = userService.updateOrAddUser(generalUser);
        generalUser = userService.findUserById(generalUser.getUserId()).orElseThrow();
        user = userService.findUserById(user.getUserId()).orElseThrow();
        Mockito.doReturn(Optional.of(user)).when(userService).getCurrentUser();
        Page<User> page = Page.empty();
        Mockito.doReturn(page).when(userService).getPaginatedUsers(any());
    }

    @Given("I am a user of account type federation administrator")
    public void iAmAUserOfAccountTypeFederationAdministrator() {
    }


    @And("I am on the create or update competition page")
    public void iAmOnTheCreateOrUpdateCompetitionPage() throws Exception {
        mockMvc.perform(get("/createCompetition"))
                .andExpect(status().isOk()) // Accepted 200
                .andExpect(view().name("createCompetition"));
    }

    @Then("There are fields for name, sport and grade level")
    public void thereAreFieldsForNameSportAndGradeLevel() throws Exception {
        mockMvc.perform(get("/createCompetition"))
                .andExpect(status().isOk())
                .andExpect(view().name("competitionForm")) // Assuming "competitionForm" is the name of the view
                .andExpect(model().attributeExists("name")) // Assuming "name" is the name of the attribute for the name field
                .andExpect(model().attributeExists("sport")) // Assuming "sport" is the name of the attribute for the sport field
                .andExpect(model().attributeExists("gradeLevel")); // Assuming "gradeLevel" is the name of the attribute for the grade level field
    }

    @Given("I am a federation manager")
    public void givenIAmAFederationManager() {
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
}