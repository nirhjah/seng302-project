package nz.ac.canterbury.seng302.tab.unit.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
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
