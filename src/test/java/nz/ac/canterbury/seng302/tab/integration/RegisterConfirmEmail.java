package nz.ac.canterbury.seng302.tab.integration;


import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.form.RegisterForm;
import nz.ac.canterbury.seng302.tab.mail.EmailService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class RegisterConfirmEmail {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private MockMvc mockMvc;

    private static final String CONFIRM_URL = "/confirm";

    private String registrationLink;
    private String registrationToken;

    private User user;

    private String EMAIL = "myemail@gmail.com";
    private String PASSWORD = "secure_@hello9994";

    private RegisterForm getRegisterForm() {
        var form = RegisterForm.getDummyRegisterForm();
        form.setEmail(EMAIL);
        form.setPassword(PASSWORD);
        return form;
    }

    private void assertUser() {
        user = new User();
        userService.updateOrAddUser(user);
    }

    @Given("there is a valid registration link")
    public void thereIsAValidRegistrationLink() {
        assertUser();
        user.generateToken(userService, 10);
    }

    @Given("I am on the register page")
    public void iAmOnTheRegisterPage() {
    }

    @When("I submit a valid form on the register page")
    public void iSubmitAValidFormOnTheRegisterPage() throws Exception {
        mockMvc.perform(get(CONFIRM_URL)).andExpect(status().isOk());
    }


    @When("I click on the registration link")
    public void iClickOnTheURL() {

    }

    @Then("I am logged into the system and the account is activated")
    public void iAmLoggedIntoTheSystemAndTheAccountIsActivated() {

    }

    @Given("there is not a valid registration link")
    public void thereIsNotAValidRegistrationLink() {
    }

    @Then("I receive an email containing a valid registration link")
    public void iReceiveAnEmailContainingAValidRegistrationLink() {
    }

    @Then("I am redirected to NOT FOUND page")
    public void iAmRedirectedToNOTFOUNDPage() {
    }
}
