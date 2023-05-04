package nz.ac.canterbury.seng302.tab.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.form.RegisterForm;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class GenerateEmailTokenFeature {

    @Autowired
    private UserRepository userRepository;

    private static final String URL = "/register";

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void beforeAll() throws IOException {
        userRepository.deleteAll();
    }
    @Given("I submit a fully valid registration form")
    public void i_submit_a_fully_valid_registration_form() throws Exception {
        RegisterForm registerForm = new RegisterForm();
        registerForm.setCity("chch");
        registerForm.setCountry("new Zealand");
        registerForm.setEmail("test@gmail.com");
        registerForm.setFirstName("bob");
        registerForm.setLastName("johnson");
        registerForm.setPassword("Hello123$");
        registerForm.setConfirmPassword("Hello123$");
        Date d = new Date(2002-1900, Calendar.JULY, 5);
        registerForm.setDateOfBirth(d);
        mockMvc.perform(post("/register"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(registerForm))
                .andExpect(status().isOk());
    }

    @When("I click on register")
    public void i_click_on_register() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("a confirmation email is sent to my email address")
    public void a_confirmation_email_is_sent_to_my_email_address() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("a unique registration token is attached to the email in the form of a confirmation link.")
    public void a_unique_registration_token_is_attached_to_the_email_in_the_form_of_a_confirmation_link() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

}
