package nz.ac.canterbury.seng302.tab.unit.controller;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = true)
@SpringBootTest
public class HomeFormControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService mockUserService;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();
        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", new Location(null, null, null, "CHCH", null, "NZ"));
        userRepository.save(user);
        when(mockUserService.getCurrentUser()).thenReturn(Optional.of(user));

    }


    @Test
    @WithMockUser
    void getHomePage_LoggedIn_GoToHomeForm() throws Exception {

        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("homeForm"))
                .andExpect(model().attribute("userTeams", user.getJoinedTeams()))
                .andExpect(model().attribute("userPersonalActivities", List.of()))
                .andExpect(model().attribute("userTeamActivities", List.of()));

    }

    @Test
    void getHomePage_NotLoggedIn_GoToLogin() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isFound())
                .andExpect(status().is3xxRedirection());
    }
}
