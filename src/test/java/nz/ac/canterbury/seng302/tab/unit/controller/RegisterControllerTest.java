package nz.ac.canterbury.seng302.tab.unit.controller;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.form.RegisterForm;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class RegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;

    private Team team;

    @Autowired
    private UserRepository userRepository;

    private Optional<User> optionalUser;

    @BeforeEach
    public void beforeAll() throws IOException {
        userRepository.deleteAll();
    }

    private static final String EMAIL = "myemail@gmail.com";
    private static final String PASSWORD = "Hello123$";

    private RegisterForm getDummyRegisterForm() {
        var form =  new RegisterForm();
        form.setCity("Christchurch");
        form.setCountry("New Zealand");
        form.setEmail(EMAIL);
        form.setFirstName("Bobby");
        form.setLastName("Johnson");
        form.setPassword(PASSWORD);
        form.setConfirmPassword(PASSWORD);
        var d = new Date(2002-1900, Calendar.JULY, 5);
        form.setDateOfBirth(d);
        return form;
    }

    private void ensureUserConfirmed(boolean isConfirmed) {
        assertTrue(optionalUser.isPresent());
        assertEquals(isConfirmed, optionalUser.get().getEmailConfirmed());
    }

    private ResultActions postRegisterForm(RegisterForm form) throws Exception {
        var dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        var dateString = dateFormat.format(form.getDateOfBirth());
        
        return mockMvc.perform(post("/register")
            .param("firstName", form.getFirstName())
            .param("lastName", form.getLastName())
            .param("email", form.getEmail())
            .param("country", form.getCountry())
            .param("city", form.getCity())
            .param("password", form.getPassword())
            .param("confirmPassword", form.getConfirmPassword())
            .param("dateOfBirth", dateString)

            .param("addressLine1", form.getAddressLine1())
            .param("addressLine2", form.getAddressLine2())
            .param("postcode", form.getPostcode())
            .param("suburb", form.getSuburb())
        );
    }

    @Test
    public void whenRegister_expectUnconfirmedUserInDb() throws Exception {
        var form = getDummyRegisterForm();
        postRegisterForm(form).andExpect(
                MockMvcResultMatchers.redirectedUrl("/login")
        );

        optionalUser = userRepository.findByEmail(EMAIL);
        ensureUserConfirmed(false);
    }

    private static final String CONFIRM_URL = "/confirm";

    @Test
    public void whenRegisterAndConfirmToken_expectConfirmedUserInDb() throws Exception {
        var form = getDummyRegisterForm();
        postRegisterForm(form).andExpect(
                MockMvcResultMatchers.redirectedUrl("/login")
        );

        optionalUser = userRepository.findByEmail(EMAIL);
        ensureUserConfirmed(false);

        mockMvc.perform(get(CONFIRM_URL)
                .param("token", optionalUser.get().getToken()))
                .andExpect(
                        MockMvcResultMatchers.redirectedUrl("/login")
                );

        optionalUser = userRepository.findByEmail(EMAIL);
        ensureUserConfirmed(true);
    }

    @Test
    public void whenConfirmUnknownURL_expect404() throws Exception {
        var form = getDummyRegisterForm();
        postRegisterForm(form).andExpect(
                MockMvcResultMatchers.redirectedUrl("/login")
        );

        var BAD_TOKEN = "abcdefg12345";
        optionalUser = userRepository.findByEmail(EMAIL);
        mockMvc.perform(get(CONFIRM_URL)
                .param("token", BAD_TOKEN))
                .andExpect(status().isNotFound());
    }
}
