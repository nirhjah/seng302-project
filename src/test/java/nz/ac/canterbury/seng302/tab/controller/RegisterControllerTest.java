package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.forms.RegisterForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.sql.Date;
import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class RegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private RegisterForm registerForm;

    private static final String NAME = "John";
    private static final String SURNAME = "Garrett";
    private static final String EMAIL =  "jga14@uclive.ac.nz";

    private void resetRegisterForm() {
        var password = "LocalH0st/epic-password92";
        var rf = new RegisterForm();
        rf.setFirstName(NAME);
        rf.setLastName(SURNAME);
        rf.setEmail(EMAIL);
        rf.setPassword(password);
        rf.setConfirmPassword(password);
        rf.setDateOfBirth(Date.from(Instant.EPOCH)); // born in 1970
        setRegisterFormLocation(rf);
        registerForm = rf;
    }

    private void setRegisterFormLocation(RegisterForm registerForm) {
        registerForm.setAddressLine1("49 Mays Road");
        registerForm.setAddressLine2("St Albans");
        registerForm.setSuburb("Papanui");
        registerForm.setPostcode("8052");
        registerForm.setCity("Christchurch");
        registerForm.setCountry("New Zealand");
    }

    private ResultActions performRegister() throws Exception {
        return mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("registerForm", registerForm));
    }

    private void assertHasErrors() throws Exception {
        performRegister().andExpect(model().hasErrors());
    }

    @BeforeEach
    void beforeEach() {
        resetRegisterForm();
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
    void whenCityIsInvalid_hasErrors() throws Exception {
        registerForm.setCity(null);
        assertHasErrors();
    }

    @Test
    void whenSuburbIsInvalid_hasErrors() throws Exception {
        registerForm.setSuburb(null);
        assertHasErrors();
    }

    @Test
    void whenCountryIsInvalid_hasErrors() throws Exception {
        registerForm.setCountry(null);
        assertHasErrors();
    }

    @Test
    void whenPostcodeIsInvalid_hasErrors() throws Exception {
        String[] invalidPostcodes = new String[] {"800", "80000", "4abc", null, "324-0"};
        for (var postcode: invalidPostcodes) {
            registerForm.setPostcode(postcode);
            assertHasErrors();
        }
    }

    @Test
    void whenAddressesAreInvalid_hasErrors() throws Exception  {
        var invalidAddresses = new String[] {"", null};
        for (var address: invalidAddresses) {
            registerForm.setAddressLine1(address);
            assertHasErrors();
            resetRegisterForm();
            registerForm.setAddressLine2(address);
            assertHasErrors();
        }
    }

    @Test
    void whenLocationIsInvalid_hasErrors() throws Exception {
        registerForm.setCity(null);
        assertHasErrors();
    }

    @Test
    void whenPasswordsAreDifferent_hasPasswordsDontMatchError() throws Exception {
        registerForm.setPassword(registerForm.getPassword() + "x");
        performRegister().andExpect(model().attributeHasFieldErrors("registerForm", "password"));
    }

    @Test
    void whenPasswordsAreInsecure_hasPasswordInsecureError() throws Exception {
        var invalidPasswords = new String[] {"123", "abc", "ABc123", "////", "ABC!///abcert"};
        for (var password: invalidPasswords) {
            registerForm.setPassword(password);
            registerForm.setConfirmPassword(password);
            performRegister().andExpect(model().attributeHasFieldErrors("registerForm", "password"));
        }
    }

    @Test
    void whenPasswordsAreInsecure_hasPasswordContainsOtherFieldError() throws Exception {
        for (var field: new String[] {NAME, SURNAME, EMAIL}) {
            var password = "._x_yz" + field + "12/3XYYZYZYZ";
            registerForm.setPassword(password);
            registerForm.setConfirmPassword(password);
            performRegister().andExpect(model().attributeHasFieldErrors("registerForm", "password"));
        }
    }
}
