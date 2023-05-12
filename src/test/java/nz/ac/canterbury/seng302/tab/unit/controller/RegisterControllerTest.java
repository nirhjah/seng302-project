package nz.ac.canterbury.seng302.tab.unit.controller;

import static nz.ac.canterbury.seng302.tab.form.RegisterForm.getDummyRegisterForm;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.form.RegisterForm;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;


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

    private void ensureUserConfirmed(boolean isConfirmed) {
        assertTrue(optionalUser.isPresent());
        assertEquals(isConfirmed, optionalUser.get().getEmailConfirmed());
    }

    /**
     * Helper function to post the contents of the register form, as Spring
     * provides no ability to do this in tests.
     * 
     * @param form The form object being posted
     * @return The mockMvc's return value, so you can chain <code>.andExpect(...)</code>
     * @throws Exception
     */
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
                redirectedUrl("/login")
        );

        optionalUser = userRepository.findByEmail(form.getEmail());
        ensureUserConfirmed(false);
    }

    private static final String CONFIRM_URL = "/confirm";

    @Test
    public void whenRegisterAndConfirmToken_expectConfirmedUserInDb() throws Exception {
        var form = getDummyRegisterForm();
        postRegisterForm(form).andExpect(
                redirectedUrl("/login")
        );

        optionalUser = userRepository.findByEmail(form.getEmail());
        ensureUserConfirmed(false);

        mockMvc.perform(get(CONFIRM_URL)
                .param("token", optionalUser.get().getToken()))
                .andExpect(
                        redirectedUrl("/login")
                );

        optionalUser = userRepository.findByEmail(form.getEmail());
        ensureUserConfirmed(true);
    }

    @Test
    public void whenConfirmUnknownURL_expect404() throws Exception {
        var form = getDummyRegisterForm();
        postRegisterForm(form).andExpect(
                redirectedUrl("/login")
        );

        var BAD_TOKEN = "abcdefg12345";
        optionalUser = userRepository.findByEmail(form.getEmail());
        mockMvc.perform(get(CONFIRM_URL)
                .param("token", BAD_TOKEN))
                .andExpect(status().isNotFound());
    }
}
