package nz.ac.canterbury.seng302.tab.unit.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Generic Get Request Test
     * @throws Exception - Thrown when mockMVC can't perform the request
     */
    @Test
    public void getControllerTest() throws Exception {
        mockMvc.perform(get("/login", 42L)).andExpect(status().isOk())
                .andExpect(view().name("login"));
    }
}
