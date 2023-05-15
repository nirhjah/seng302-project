package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.controller.ForgotPasswordController;
import nz.ac.canterbury.seng302.tab.controller.LoginController;
import nz.ac.canterbury.seng302.tab.controller.ResetPasswordController;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.mail.EmailService;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class ResetPasswordIntegrationTests {
    private UserRepository userRepository;

    @SpyBean
    private UserService userService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MockMvc mockMvc;

    private User user;

    private HttpServletRequest request;

    private String token;

    @Before
    public void setup() throws IOException {
        // get the application context (thanks to @CucumberContextConfiguration in the Configurations class)
        userRepository = applicationContext.getBean(UserRepository.class);
        // get all the necessary beans
        TaskScheduler taskScheduler = applicationContext.getBean(TaskScheduler.class);
        EmailService emailService = applicationContext.getBean(EmailService.class);
        PasswordEncoder passwordEncoder = applicationContext.getBean(PasswordEncoder.class);
        // create the spy with the required beans
        userService = Mockito.spy(new UserService(userRepository, taskScheduler, emailService, passwordEncoder));
        // create a custom MockMvc setup with the required controllers and inject the UserService Spy
        this.mockMvc = MockMvcBuilders.standaloneSetup(new LoginController(), new ForgotPasswordController(userService), new ResetPasswordController(userService, passwordEncoder)).build();

        userRepository.deleteAll();
        Location testLocation = new Location(null, null, null, "CHCH", null, "NZ");
        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", testLocation);
        userRepository.save(user);
        user.generateToken(userService, 1);
        token = user.getToken();
        userRepository.save(user);
    }


    @Given("I am on the login page")
    public void i_am_on_the_login_page() throws Exception {
        // todo: this fails for some reason "Circular view path [login]:" may be easiest to just ignore it, I'm sure you check /login returns a 200 somewhere else
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

    @When("I enter a email known to the system")
    public void i_enter_a_email_known_to_the_system() throws Exception {
        mockMvc.perform(post("/forgot-password")
                .with(csrf())
                .param("email", "johndoe@example.com")).andExpect(status().isOk()).andExpect(view().name("forgotPassword"));

    }

    @Then("A confirmation message tells me that an email was sent to the address if it was recognised")
    public void a_confirmation_message_tells_me_that_an_email_was_sent_to_the_address_if_it_was_recognised() throws Exception {
        mockMvc.perform(post("/forgot-password", 42L)
                .with(csrf())
                .param("email", "test@gmail.com")).andExpect(status().isOk());
        ;
    }


    @Then("An email is sent with a unique link to update the password of the associated email")
    public void an_email_is_sent_with_a_unique_link_to_update_the_password_of_the_associated_email() {
        verify(userService, times(1)).resetPasswordEmail(any(User.class), any(HttpServletRequest.class));
    }

    @Given("I received a reset password email")
    public void i_received_a_reset_password_email() throws IOException {
        Assertions.assertTrue(user.getToken() != null);
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
