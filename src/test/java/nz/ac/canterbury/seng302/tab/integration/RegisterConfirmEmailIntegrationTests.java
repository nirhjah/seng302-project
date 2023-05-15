package nz.ac.canterbury.seng302.tab.integration;


import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.validation.constraints.Email;
import nz.ac.canterbury.seng302.tab.controller.RegisterController;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.form.RegisterForm;
import nz.ac.canterbury.seng302.tab.mail.EmailService;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.utility.RegisterTestUtil;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;

import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.util.AssertionErrors.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class RegisterConfirmEmailIntegrationTests {

    @SpyBean
    private UserService userService;

    @SpyBean
    private EmailService emailService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MockMvc mockMvc;

    private UserRepository userRepository;

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

    private void setupMorganMocking() {
        userRepository = applicationContext.getBean(UserRepository.class);
        // get all the necessary beans
        userService = applicationContext.getBean(UserService.class);
        TaskScheduler taskScheduler = applicationContext.getBean(TaskScheduler.class);
        PasswordEncoder passwordEncoder = applicationContext.getBean(PasswordEncoder.class);

        emailService = Mockito.spy(new EmailService());

        var controller = new RegisterController(emailService, userService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Before
    public void beforeTest() {
        setupMorganMocking();
        userRepository.deleteAll();
    }

    @Given("there is a valid registration link")
    public void thereIsAValidRegistrationLink() throws IOException {
        assertUser();
        user.generateToken(userService, 10);
        registrationToken = user.getToken();
        assertNotNull("registration token was null", registrationToken);
    }

    @When("I submit a valid form on the register page")
    public void iSubmitAValidFormOnTheRegisterPage() throws Exception {
        var form = getRegisterForm();
        latestResult = RegisterTestUtil.postRegisterForm(mockMvc, form);
    }

    @When("I click on the registration link")
    public void iClickOnTheURL() throws Exception {
        latestResult = mockMvc.perform(get(CONFIRM_URL).requestAttr(
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
        // TODO: This one's going to be difficult.
        // (Also currently blocked by the mocking issue.)
    }

    @Then("I am redirected to NOT FOUND page")
    public void iAmRedirectedToNOTFOUNDPage() throws Exception {
        latestResult
                .andDo(print())
                //.andExpect(view().name("error"))
                .andExpect(status().isNotFound());
    }
}
