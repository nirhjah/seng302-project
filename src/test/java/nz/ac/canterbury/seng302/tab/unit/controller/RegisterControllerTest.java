package nz.ac.canterbury.seng302.tab.unit.controller;

import static nz.ac.canterbury.seng302.tab.form.RegisterForm.getDummyRegisterForm;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.utility.RegisterTestUtil;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class RegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private Optional<User> optionalUser;

    @BeforeEach
    public void beforeAll() {
        userRepository.deleteAll();
    }

    private void ensureUserConfirmed(boolean isConfirmed) {
        assertTrue(optionalUser.isPresent());
        assertEquals(isConfirmed, optionalUser.get().getEmailConfirmed());
    }

    @Test
    public void whenRegister_expectUnconfirmedUserInDb() throws Exception {
        var form = getDummyRegisterForm();
        RegisterTestUtil.postRegisterForm(mockMvc, form).andExpect(
                redirectedUrl("/login")
        );

        optionalUser = userRepository.findByEmail(form.getEmail());
        ensureUserConfirmed(false);
    }

    private static final String CONFIRM_URL = "/confirm";

    @Test
    public void whenRegisterAndConfirmToken_expectConfirmedUserInDb() throws Exception {
        var form = getDummyRegisterForm();
        RegisterTestUtil.postRegisterForm(mockMvc, form).andExpect(
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
        RegisterTestUtil.postRegisterForm(mockMvc, form).andExpect(
                redirectedUrl("/login")
        );

        var BAD_TOKEN = "abcdefg12345";
        optionalUser = userRepository.findByEmail(form.getEmail());
        mockMvc.perform(get(CONFIRM_URL)
                .param("token", BAD_TOKEN))
                .andExpect(status().isNotFound());
    }
}
