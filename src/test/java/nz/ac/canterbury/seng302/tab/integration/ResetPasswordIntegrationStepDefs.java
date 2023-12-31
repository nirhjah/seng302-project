package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.controller.LostPasswordController;
import nz.ac.canterbury.seng302.tab.controller.ResetPasswordController;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.mail.EmailService;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class ResetPasswordIntegrationStepDefs {

    private UserRepository userRepository;

    private UserService userService;

    @Autowired
    private ApplicationContext applicationContext;

    private MockMvc mockMvc;

    private User user;

    private String token;

    @Before("@reset_password")
    public void setup() throws Exception {
        userRepository = applicationContext.getBean(UserRepository.class);

        TaskScheduler taskScheduler = applicationContext.getBean(TaskScheduler.class);
        EmailService emailService = Mockito.spy(applicationContext.getBean(EmailService.class));
        PasswordEncoder passwordEncoder = applicationContext.getBean(PasswordEncoder.class);

        userService = Mockito.spy(new UserService(userRepository, taskScheduler, passwordEncoder));
        doNothing().when(emailService).sendHtmlMessage(any());
        this.mockMvc = MockMvcBuilders.standaloneSetup(new LostPasswordController(userService, emailService), new ResetPasswordController(userService, passwordEncoder, emailService)).build();

        userRepository.deleteAll();
        Location testLocation = new Location(null, null, null, "CHCH", null, "NZ");
        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", testLocation);
        user.generateToken(userService, 1);
        token = user.getToken();
        userService.updateOrAddUser(user);
    }

    
    @Given("I see the forgot password button on the login page")
    public void i_see_the_forgot_password_button_on_the_login_page() throws Exception {
        mockMvc.perform(get("/lost-password"));
    }


    @When("I hit the lost password button")
    public void i_hit_the_lost_password_button() throws Exception {
        mockMvc.perform(get("/lost-password"));
    }

    @Then("I see a form asking me for my email address")
    public void i_see_a_form_asking_me_for_my_email_address() throws Exception {
        mockMvc.perform(get("/lost-password")).andExpect(status().isOk());
    }

    @Given("I am on the lost password form")
    public void i_am_on_the_lost_password_form() throws Exception {
        mockMvc.perform(get("/lost-password"));
    }

    @When("I enter an email with invalid format")
    public void i_enter_an_email_with_invalid_format() throws Exception {
        mockMvc.perform(post("/lost-password", 42L)
                .with(csrf())
                .param("email", "test@"));
    }

    @Then("An error message tells me the email address is invalid")
    public void an_error_message_tells_me_the_email_address_is_invalid() throws Exception {
        mockMvc.perform(post("/lost-password", 42L)
                .with(csrf())
                .param("email", "test@")).andExpect(status().isBadRequest());
    }

    @When("I enter a valid email that is not known to the system")
    public void i_enter_a_valid_email_that_is_not_known_to_the_system() throws Exception {
        mockMvc.perform(post("/lost-password", 42L)
                .with(csrf())
                .param("email", "test@gmail.com"));
    }

    @Then("A confirmation message tells me that an email was sent to the address if it was recognised")
    public void a_confirmation_message_tells_me_that_an_email_was_sent_to_the_address_if_it_was_recognised() throws Exception {
        mockMvc.perform(post("/lost-password", 42L)
                .with(csrf())
                .param("email", "test@gmail.com")).andExpect(status().isOk());
    }


    @When("I enter a email known to the system")
    public void i_enter_a_email_known_to_the_system() throws Exception {
        mockMvc.perform(post("/lost-password")
                .with(csrf())
                .param("email", "johndoe@example.com")).andExpect(status().isOk()).andExpect(view().name("lostPassword"));
    }

    @Then("An email is sent with a unique link to update the password of the associated email")
    public void an_email_is_sent_with_a_unique_link_to_update_the_password_of_the_associated_email() throws MessagingException {
        verify(userService, times(1)).resetPasswordEmail(any(User.class), any(HttpServletRequest.class));
    }

    @Given("I received a reset password email")
    public void i_received_a_reset_password_email() {
        Assertions.assertNotNull(user.getToken());
    }

    @When("I go to the URL in the link")
    @WithMockUser
    public void i_go_to_the_url_in_the_link() throws Exception {
        mockMvc.perform(get("/reset-password?token=" + user.getToken()))
                .andDo(print())
                .andExpect(view().name("resetPassword"));
    }

    @Then("I see the reset password page")
    @WithMockUser
    public void i_see_the_reset_password_page() throws Exception {
        mockMvc.perform(get("/reset-password?token=" + token)).andExpect(status().isOk()).andExpect(view().name("resetPassword"));
    }


    @Given("I am on the reset password page")
    public void i_am_on_the_reset_password_page() throws Exception {
        mockMvc.perform(get("/reset-password?token=" + token));

    }

    @When("I enter two different passwords in password and confirm password and click save")
    public void i_enter_two_different_passwords_in_password_and_confirm_password_and_click_save() throws Exception {
        mockMvc.perform(post("/reset-password?token=" + token)
                        .param("password", "Password123")
                        .param("confirmPassword", "!!Password123!!"));
    }

    @Then("An error message tells me the passwords do not match")
    public void an_error_message_tells_me_the_passwords_do_not_match() throws Exception {
        mockMvc.perform(post("/reset-password?token=" + token)
                        .param("password", "Password123")
                        .param("confirmPassword", "Password123!"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("resetPassword"));
    }

}
