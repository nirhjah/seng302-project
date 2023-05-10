package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@ExtendWith(MockitoExtension.class) // SpringExtension.class
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class ResetPasswordIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    // @SpyBean was used previously, broke other tests since the controller uses a different object.
    @Autowired
    private UserService userService = mock(UserService.class);

    private User user;

    private HttpServletRequest request;

    private String token;

    private String EMAIL = "johndoe@example.com";

    public void setupUser() throws IOException {
        if (user == null) {
            var opt = userRepository.findByEmail(EMAIL);
            if (opt.isPresent()) {
                user = opt.get();
            } else {
                Location location = new Location(null, null, null, "Christchurch", null, "New Zealand");
                var usr = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", location);
                user = userRepository.save(usr);
            }
        }
    }

    public void setupToken() {
        user.generateToken(userService, 1);
        token = user.getToken();
        userService.updateOrAddUser(user);
    }

    @Before
    public void before() {
        MockitoAnnotations.openMocks(this);
    }

    @Given("I am on the login page")
    public void i_am_on_the_login_page() throws Exception {
        mockMvc.perform(get("/login")).andExpect(status().isOk());
    }


    @When("I hit the lost password button")
    public void i_hit_the_lost_password_button() throws Exception {
        mockMvc.perform(get("/forgot-password"));
    }

    @Then("I see a form asking me for my email address")
    public void i_see_a_form_asking_me_for_my_email_address() throws Exception {
        mockMvc.perform(get("/forgot-password")).andExpect(status().isOk());
    }

    @Given("I am on the lost password form")
    public void i_am_on_the_lost_password_form() throws Exception {
        setupUser();
        mockMvc.perform(get("/forgot-password"));
    }

    @When("I enter an email with invalid format")
    public void i_enter_an_email_with_invalid_format() throws Exception {
        mockMvc.perform(post("/forgot-password", 42L)
                .with(csrf())
                .param("email", "test@"));
    }

    @Then("An error message tells me the email address is invalid")
    public void an_error_message_tells_me_the_email_address_is_invalid() throws Exception {
        mockMvc.perform(post("/forgot-password", 42L)
                .with(csrf())
                .param("email", "test@")).andExpect(status().isBadRequest());
    }

    @When("I enter a valid email that is not known to the system")
    public void i_enter_a_valid_email_that_is_not_known_to_the_system() throws Exception {
        mockMvc.perform(post("/forgot-password", 42L)
                .with(csrf())
                .param("email", "test@gmail.com"));
    }

    @Then("A confirmation message tells me that an email was sent to the address if it was recognised")
    public void a_confirmation_message_tells_me_that_an_email_was_sent_to_the_address_if_it_was_recognised() throws Exception {
        mockMvc.perform(post("/forgot-password", 42L)
                .with(csrf())
                .param("email", "test@gmail.com")).andExpect(status().isOk());;
    }


    @When("I enter a email known to the system")
    public void i_enter_a_email_known_to_the_system() throws Exception {
        mockMvc.perform(post("/forgot-password", 42L)
                .with(csrf())
                .param("email", "johndoe@example.com")).andExpect(status().isOk());
    }


    @Then("An email is sent with a unique link to update the password of the associated email")
    public void an_email_is_sent_with_a_unique_link_to_update_the_password_of_the_associated_email() {
        // TODO: find a way to test this.
        // verify(userService, times(1)).resetPasswordEmail(any(), any());
    }

    @Given("I received a reset password email")
    public void i_received_a_reset_password_email() throws IOException {
        setupUser();
        setupToken();
    }

    @When("I go to the URL in the link")
    @WithMockUser
    public void i_go_to_the_url_in_the_link() throws Exception {
        mockMvc.perform(get("/reset-password?token=" + user.getToken())).andExpect(view().name("resetPassword"));
      //  mockMvc.perform(get("/reset-password").param("token", user.getToken())).andExpect(status().isOk());
      //  mockMvc.perform(get("/reset-password?token=").param("token", user.getToken())).andExpect(status().isOk());
      //  mockMvc.perform(get("/reset-password").param("token", user.getToken())).andExpect(view().name("resetPassword"));

    }

    @Then("I see the reset password page")
    @WithMockUser
    public void i_see_the_reset_password_page() throws Exception {
        System.out.println(token);
        Optional<User> optUser = userService.findByToken(token);
        mockMvc.perform(get("/reset-password?token=" + token)).andExpect(status().isOk()).andExpect(view().name("resetPassword"));
    }

}
