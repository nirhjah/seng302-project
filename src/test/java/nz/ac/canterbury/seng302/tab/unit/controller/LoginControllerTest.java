package nz.ac.canterbury.seng302.tab.unit.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = true)
@SpringBootTest
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Generic Get Request Test
     * @throws Exception - Thrown when mockMVC can't perform the request
     */
    @Test
    void getControllerTest() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void redirectIfNotAuthenticated() throws Exception {
        mockMvc.perform(get("/this-url-does-not-exist"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login?continue=/this-url-does-not-exist"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"http://evil.com", "//evil.com"})
    void discardRedirectsContainingOpenRedirectVulnerability(String url) throws Exception {
        mockMvc.perform(get("/login?continue="+url))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
