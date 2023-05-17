package nz.ac.canterbury.seng302.tab.utility;

import nz.ac.canterbury.seng302.tab.form.RegisterForm;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class RegisterTestUtil {

    private static final String EMAIL = "myemail@gmail.com";
    private static final String PASSWORD = "Hello123$";

    /**
     * Generates a dummy register form.
     * For testing purposes only!!!
     * @return the dummy form to use in testing
     */
    public static RegisterForm getDummyRegisterForm() {
        var form =  new RegisterForm();
        form.setCity("Christchurch");
        form.setCountry("New Zealand");
        form.setEmail(EMAIL);
        form.setFirstName("Bobby");
        form.setLastName("Johnson");
        form.setPassword(PASSWORD);
        form.setConfirmPassword(PASSWORD);
        var d = new Date(2002-1900, Calendar.JULY, 5);
        form.setPostcode("8052");
        form.setSuburb("St Albans");
        form.setAddressLine1("56 Mays Road");
        form.setDateOfBirth(d);
        return form;
    }

    /**
     * Helper function to post the contents of the register form, as Spring
     * provides no ability to do this in tests.
     *
     * @param form The form object being posted
     * @return The mockMvc's return value, so you can chain <code>.andExpect(...)</code>
     * @throws Exception
     */
    public static ResultActions postRegisterForm(MockMvc mockMvc, RegisterForm form) throws Exception {
        var dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        var dateString = dateFormat.format(form.getDateOfBirth());

        return mockMvc.perform(post("/register")
                .with(csrf())
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
}
