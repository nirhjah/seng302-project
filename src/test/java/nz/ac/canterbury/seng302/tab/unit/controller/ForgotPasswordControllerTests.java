package nz.ac.canterbury.seng302.tab.unit.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ForgotPasswordControllerTests {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Test getting the forgot password page
     * @throws Exception thrown if Mocking fails
     */
    @Test
    public void testForgotPasswordPage() throws Exception {
        mockMvc.perform(get("/forgot-password")
        ).andExpect(status().isOk());
    }


    /**
     * Tests when a valid email is inputted in the form
     * @throws Exception thrown if Mocking fails
     */
    @Test
    void whenTheEmailIsValid_return200() throws Exception {
        mockMvc.perform(post("/forgot-password")
                        .param("email", "test@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("forgotPassword"));
    }

    /**
     * Tests when an email of invalid format is inputted in the form
     * @throws Exception thrown if Mocking fails
     */
    @Test
    void whenTheEmailIsInvalid_return400() throws Exception {
        mockMvc.perform(post("/forgot-password")
                        .param("email", "test@"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("forgotPassword"));
    }

    /**
     * Tests when an empty email field is inputted in the form
     * @throws Exception thrown if Mocking fails
     */
    @Test
    void whenTheEmailIsBlank_return400() throws Exception {
        mockMvc.perform(post("/forgot-password")
                        .param("email", ""))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("forgotPassword"));
    }
    
}
