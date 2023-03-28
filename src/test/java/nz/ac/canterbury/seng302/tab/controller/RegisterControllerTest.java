package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.forms.RegisterForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class RegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;


    private RegisterForm registerForm;

    private void setRegisterFormLocation(RegisterForm registerForm) {
        registerForm.setAddressLine1("49 Mays Road");
        registerForm.setAddressLine2("St Albans");
        registerForm.setSuburb("Papanui");
        registerForm.setPostcode("8052");
        registerForm.setCity("Christchurch");
        registerForm.setCountry("New Zealand");
    }

    @BeforeEach
    void beforeEach() {
        var password = "LocalH0st/epic-password92";
        var rf = new RegisterForm();
        rf.setCity("Christchurch");
        rf.setFirstName("John");
        rf.setLastName("Garrett");
        rf.setEmail("jga14@uclive.ac.nz");
        rf.setPassword(password);
        rf.setConfirmPassword(password);
        rf.setDateOfBirth(Date.from(Instant.EPOCH)); // born in 1970
        setRegisterFormLocation(rf);
        registerForm = rf;
    }

    @Test
    void whenRegisterIsValid_return200() throws Exception {
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("registerForm", registerForm))
            .andExpect(status().isFound())
            .andExpect(view().name("redirect:/user-info?name=1"));
    }

    @Test
    void whenLocationIsInvalid_return4XX() throws Exception {
        registerForm.setCity(null);
        registerForm.setCountry(null);
        mockMvc.perform(post("/register")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .flashAttr("registerForm", registerForm))
            .andExpect(view().name("redirect:/user-info?name=1"));
    }
}
