package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import nz.ac.canterbury.seng302.tab.TabApplication;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = TabApplication.class)
@AutoConfigureMockMvc
@CucumberContextConfiguration
public class GenerateEmailTokenFeature {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void beforeAll() throws IOException {
        userRepository.deleteAll();
    }
    @Given("I submit a fully valid registration form")
    public void i_submit_a_fully_valid_registration_form() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
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
