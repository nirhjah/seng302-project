package nz.ac.canterbury.seng302.tab.integration;


import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.validation.constraints.Email;
import nz.ac.canterbury.seng302.tab.controller.RegisterController;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.form.RegisterForm;
import nz.ac.canterbury.seng302.tab.mail.EmailDetails;
import nz.ac.canterbury.seng302.tab.mail.EmailService;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.utility.RegisterTestUtil;
import org.junit.jupiter.api.Disabled;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.util.AssertionErrors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class RegisterConfirmEmailIntegrationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationContext applicationContext;

    private EmailService emailService;

    private MockMvc mockMvc;

    private ResultActions latestResult;

    private String registrationToken;

    private User user;

    private static final String EMAIL = "myemail@gmail.com";
    private static final String PASSWORD = "SEcure_@hello9994";

    private static final String CONFIRM_URL = "/confirm";

    private EmailDetails sentMailContent;

    private RegisterForm getRegisterForm() {
        var form = RegisterForm.getDummyRegisterForm();
        form.setEmail(EMAIL);
        form.setPassword(PASSWORD);
        form.setConfirmPassword(PASSWORD);
        return form;
    }

    private void assertUser() throws IOException {
        user = User.defaultDummyUser();
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        userService.updateOrAddUser(user);
    }

    private EmailService emailServiceMock() {
        // We don't want to spam emails, as we have a limited number that we can
        // send with our API key.  So in tests, we should mock.
        var javaMailSender = applicationContext.getBean(JavaMailSender.class);
        var emailServ = Mockito.spy(new EmailService(javaMailSender));

        Mockito.when(emailServ.sendSimpleMail(any())).then(invocation -> {
            sentMailContent = invocation.getArgument(0, EmailDetails.class);
            return "Mocked Success";
        });

        return emailServ;
    }

    private void setupMorganMocking() {
        // get all the necessary beans
        userRepository = applicationContext.getBean(UserRepository.class);
        userService = applicationContext.getBean(UserService.class);
        var passwordEncoder = applicationContext.getBean(PasswordEncoder.class);
        var authenticationManager = applicationContext.getBean(AuthenticationManager.class);

        // create email spy with manual DI
        emailService = emailServiceMock();

        // create mockMvc manually with spied services
        var controller = new RegisterController(emailService, userService, passwordEncoder, authenticationManager);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Before("@register_confirm_email")
    public void beforeTest() {
        sentMailContent = null;
        userRepository.deleteAll();
        setupMorganMocking();
    }

    @Given("there is a valid registration link")
    public void thereIsAValidRegistrationLink() throws IOException {
        assertUser();
        user.generateToken(userService, 10);
        registrationToken = user.getToken();
        assertNotNull("registration token was null", registrationToken);
        user.setToken(registrationToken);
        userService.updateOrAddUser(user);
    }

    @When("I submit a valid form on the register page")
    public void iSubmitAValidFormOnTheRegisterPage() throws Exception {
        var form = getRegisterForm();
        latestResult = RegisterTestUtil.postRegisterForm(mockMvc, form)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @When("I click on the registration link")
    public void iClickOnTheURL() throws Exception {
        latestResult = mockMvc.perform(get(CONFIRM_URL).param(
                "token", registrationToken
        ));
    }

    @Then("The account is activated")
    public void iAmLoggedIntoTheSystemAndTheAccountIsActivated() throws Exception {
        var userOpt = userService.findUserByEmail(EMAIL);
        latestResult.andDo(print());
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
        assertNotNull("expected sent email", sentMailContent);
        var body = sentMailContent.getMsgBody();
        assertNotNull("Email body was null", body);
        if (!Objects.isNull(registrationToken)) {
            assertTrue("not correct reg token", body.contains(registrationToken));
        }
    }

    @Then("I am redirected to NOT FOUND page")
    public void iAmRedirectedToNOTFOUNDPage() throws Exception {
        latestResult
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
