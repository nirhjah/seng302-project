package nz.ac.canterbury.seng302.tab.unit.controller;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.form.RegisterForm;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class RegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TeamService teamService;

    @MockBean
    private UserService mockUserService;


    private Team team;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void beforeAll() throws IOException {
        userRepository.deleteAll();
    }

    private static final String EMAIL = "myemail@gmail.com";
    private static final String PASSWORD = "Hello123$";

    private RegisterForm getDummyRegisterForm() {
        var form =  new RegisterForm();
        form.setCity("chch");
        form.setCountry("new Zealand");
        form.setEmail(EMAIL);
        form.setFirstName("bob");
        form.setLastName("johnson");
        form.setPassword(PASSWORD);
        form.setConfirmPassword(PASSWORD);
        var d = new Date(2002-1900, Calendar.JULY, 5);
        form.setDateOfBirth(d);
        return form;
    }

    private void ensureUserConfirmed(boolean isConfirmed) {
        assertNotNull(user);
        assertEquals(isConfirmed, user.getEmailConfirmed());
    }

//    @Disabled
//    @Test
//    public void whenRegister_expectUnconfirmedUserInDb() throws Exception {
//        var form = getDummyRegisterForm();
//
//        // TODO: This isn't working
//        mockMvc.perform(MockMvcRequestBuilders.post("/example")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//
//        // TODO: This isn't working
//        mockMvc.perform(MockMvcRequestBuilders.post("/example").param("registerForm", form))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//
//        // TODO: This isn't working
//        mockMvc.perform(post("/register?")
//                .requestAttr("registerForm", form));
//
//        user = userRepository.getUserByEmailAndPassword(EMAIL, PASSWORD);
//        ensureUserConfirmed(false);
//    }

    @Test
    public void whenRegisterAndConfirmToken_expectConfirmedUserInDb() throws Exception {
        var form = getDummyRegisterForm();
        mockMvc.perform(post("/register?")
                .requestAttr("registerForm", form));

        user = userRepository.getUserByEmailAndPassword(EMAIL, PASSWORD);
        ensureUserConfirmed(false);

        mockMvc.perform(post("/confirm?")
                .requestAttr("token", user.getToken()));
    }
}