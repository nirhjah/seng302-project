package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.OptionalInt;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ViewUserControllerTest {

    private final String name = "Cam";
    private final String password = "Password42$";
    private final String email = "abc123@uclive.ac.nz";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TeamService teamService;

    @MockBean
    private UserService mockUserService;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void beforeAll() throws IOException {
        userRepository.deleteAll();
        user = new User(name, name, email, password);
        userRepository.save(user);

        Mockito.when(mockUserService.getCurrentUser()).thenReturn(Optional.of(user));
    }

    @Test
    @WithMockUser
    public void testHome() throws Exception {
        mockMvc.perform(get("/home")
        ).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void whenLoggedIn_redirectToViewUserInfo() throws Exception {
        mockMvc.perform(get("/user-info/self"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/user-info?name=" + user.getUserId()));
    }

    @Test
    @WithMockUser
    public void whenLoggedIn_checkUserFieldsAreCorrect() throws Exception {
        mockMvc.perform(get("/user-info/self"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/user-info?name=" + user.getUserId()))
                .andExpect(MockMvcResultMatchers.model().attribute("pictureString", user.getPictureString()));
    }
}
