package nz.ac.canterbury.seng302.tab.integration;


import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.form.RegisterForm;
import nz.ac.canterbury.seng302.tab.mail.EmailService;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.utility.RegisterTestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;

import static org.springframework.test.util.AssertionErrors.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class RegisterConfirmEmail {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private MockMvc mockMvc;

    private ResultActions latestResult;

    private String registrationToken;

    private User user;

    private static final String EMAIL = "myemail@gmail.com";
    private static final String PASSWORD = "secure_@hello9994";

    private static final String CONFIRM_URL = "/confirm";

    private RegisterForm getRegisterForm() {
        var form = RegisterForm.getDummyRegisterForm();
        form.setEmail(EMAIL);
        form.setPassword(PASSWORD);
        return form;
    }

    private void assertUser() throws IOException {
        user = User.defaultDummyUser();
        userService.updateOrAddUser(user);
    }

    @Before
    public void beforeTest() {
        userRepository.deleteAll();
    }

    @Given("there is a valid registration link")
    public void thereIsAValidRegistrationLink() throws IOException {
        assertUser();
        user.generateToken(userService, 10);
    }

    @Given("I am on the register page")
    public void iAmOnTheRegisterPage() {

    }

    @When("I submit a valid form on the register page")
    public void iSubmitAValidFormOnTheRegisterPage() throws Exception {
        var form = RegisterTestUtil.getDummyRegisterForm();
        latestResult = RegisterTestUtil.postRegisterForm(mockMvc, form)
                .andExpect(redirectedUrl("/login"));
    }

    @When("I click on the registration link")
    public void iClickOnTheURL() throws Exception {
        latestResult = mockMvc.perform(get(CONFIRM_URL).param(
                "token", registrationToken
        ));
    }

    @Then("I am redirected to the login page and the account is activated")
    public void iAmLoggedIntoTheSystemAndTheAccountIsActivated() throws Exception {
        latestResult.andExpect(redirectedUrl("/login"));
        var userOpt = userService.findUserByEmail(EMAIL);
        assertTrue("No account", userOpt.isPresent());
        assertTrue("Not verified", userOpt.get().getEmailConfirmed());
    }

    @Given("there is not a valid registration link")
    public void thereIsNotAValidRegistrationLink() {
        // this token is an invalid token.
        registrationToken = "abcdefg123456";
    }

    @Then("I receive an email containing a valid registration link")
    public void iReceiveAnEmailContainingAValidRegistrationLink() throws Exception {
        User user = User.defaultDummyUser();
        user.setEmail(EMAIL);
        user.generateToken(userService, 1);
        registrationToken = user.getToken();
    }

    @Then("I am redirected to NOT FOUND page")
    public void iAmRedirectedToNOTFOUNDPage() throws Exception {
        latestResult.andExpect(status().isNotFound());
    }
}
