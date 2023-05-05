package nz.ac.canterbury.seng302.tab.unit.controller;

import nz.ac.canterbury.seng302.tab.controller.ProfileFormController;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.SportService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class MyTeamsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TeamService teamService;

    @MockBean
    private UserService mockUserService;

    @Autowired
    private TeamRepository teamRepository;

    private Team team;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void beforeAll() throws IOException {
        teamRepository.deleteAll();
        userRepository.deleteAll();
        Location location = new Location("1 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand");
        team = new Team("test", "Hockey", location);
        teamRepository.save(team);
        ProfileFormController.teamId = team.getTeamId();

        Location testLocation = new Location("23 test street", "24 test street", "surburb", "city", "8782",
                "New Zealand");
        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(),
                "johndoe@example.com", "Password123!", testLocation);
        userRepository.save(user);

        Mockito.when(mockUserService.getCurrentUser()).thenReturn(Optional.of(user));
    }


    /**
     * Test getting the my teams page
     * @throws Exception thrown if Mocking fails
     */
    @Test
    public void testMyTeamsPage() throws Exception {
        mockMvc.perform(get("/my-teams")
        ).andExpect(status().isOk()).andExpect(view().name("myTeams"));
    }


   /* @Test
    public void whenJoiningATeamWithInvalidToken_return() throws Exception {
        mockMvc.perform(post("/my-teams")
                .param("token", "abcdefg")).andExpect(status().isBadRequest())
                .andExpect(view().name("redirect:/my-teams?page=1"));
    }*/

}
