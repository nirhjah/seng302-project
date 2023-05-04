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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    /*
      Taken from:
      https://stackoverflow.com/questions/36568518/testing-form-posts-through-mockmvc
     */
    private String buildUrlEncodedFormEntity(String[] params) {
        if( (params.length % 2) > 0 ) {
            throw new IllegalArgumentException("Need to give an even number of parameters");
        }
        StringBuilder result = new StringBuilder();
        for (int i=0; i<params.length; i+=2) {
            if( i > 0 ) {
                result.append('&');
            }
            result.append(URLEncoder.encode(params[i], StandardCharsets.UTF_8))
                    .append('=')
                    .append(URLEncoder.encode(params[i+1], StandardCharsets.UTF_8));
        }
        return result.toString();
    }

    private static final String REGISTER_URL = "/register";

    private void postRegisterForm(RegisterForm form) throws Exception {
        var dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        var dateString = dateFormat.format(form.getDateOfBirth());

        List<String> params = new ArrayList<>(List.of(
            "firstName", form.getFirstName(),
            "lastName", form.getLastName(),
            "email", form.getEmail(),
            "country", form.getCountry(),
            "city", form.getCity(),
            "password", form.getPassword(),
            "confirmPassword", form.getConfirmPassword(),
            "dateOfBirth", dateString
        ));

        if (form.getAddressLine1() != null) {
            params.addAll(List.of("addressLine1", form.getAddressLine1()));
        }
        if (form.getAddressLine2() != null) {
            params.addAll(List.of("addressLine2", form.getAddressLine2()));
        }
        if (form.getPostcode() != null) {
            params.addAll(List.of("postcode", form.getPostcode()));
        }
        if (form.getSuburb() != null) {
            params.addAll(List.of("suburb", form.getSuburb()));
        }

        mockMvc.perform(post(REGISTER_URL)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .content(buildUrlEncodedFormEntity(
                    params.toArray(String[]::new)
            )));
    }

    @Test
    public void whenRegister_expectUnconfirmedUserInDb() throws Exception {
        var form = getDummyRegisterForm();
        postRegisterForm(form);

        optionalUser = userRepository.findByEmail(EMAIL);
        ensureUserConfirmed(false);
    }

    private static final String CONFIRM_URL = "/confirm";

    @Test
    public void whenRegisterAndConfirmToken_expectConfirmedUserInDb() throws Exception {
        var form = getDummyRegisterForm();
        postRegisterForm(form);
        optionalUser = userRepository.findByEmail(EMAIL);
        ensureUserConfirmed(false);

        mockMvc.perform(get(CONFIRM_URL)
                .requestAttr("token", optionalUser.get().getToken()));

        ensureUserConfirmed(true);
    }

    @Test
    public void whenConfirmUnknownURL_expect404() throws Exception {
        var form = getDummyRegisterForm();
        postRegisterForm(form);

        optionalUser = userRepository.findByEmail(EMAIL);
        mockMvc.perform(get(CONFIRM_URL)
                .requestAttr("token", optionalUser.get().getToken()))
                .andExpect(status().isNotFound());
    }
}