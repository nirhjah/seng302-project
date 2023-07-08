package nz.ac.canterbury.seng302.tab.unit.controller;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.ClubService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@WithMockUser
public class CreateClubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService mockUserService;
    @MockBean
    private ClubService mockClubService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;


    private Team team;

    private Team team2;
    private Team team3;

    private User user;


    @BeforeEach
    void beforeEach() throws IOException {
        userRepository.deleteAll();

        Location location = new Location("1 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand");
        team = new Team("test", "Hockey", location);
        team2 = new Team("test2", "Hockey", new Location("2 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand"));
        team3 = new Team("test3", "Rugby", new Location("3 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand"));

        teamRepository.save(team);
        teamRepository.save(team2);
        teamRepository.save(team3);

        Location testLocation = new Location("23 test street", "24 test street", "surburb", "city", "8782",
                "New Zealand");
        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(),
                "johndoe@example.com", "Password123!", testLocation);
        userRepository.save(user);

        Mockito.when(mockUserService.getCurrentUser()).thenReturn(Optional.of(user));

    }

    @Test
    public void testDisplayCreateClubForm_return200() throws Exception {
        mockMvc.perform(get("/createClub"))
                .andExpect(status().isOk())
                .andExpect(view().name("createClub"));
    }


    @Test
    public void whenAllFieldsValid_return302() throws Exception {
        Mockito.when(mockClubService.validateTeamSportsinClub(any())).thenReturn(true);
        mockMvc.perform(post("/createClub", 42L)
                        .param("clubId", "1")
                        .param("name", "new club")
                        .param("addressLine1", "addressline1")
                        .param("addressLine2", "addressline2")
                        .param("suburb", "Ilam")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "1111")
                .param("selectedTeams", team.getTeamId().toString(), team2.getTeamId().toString()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/home"));

        verify(mockClubService, times(1)).updateOrAddClub(any());
        verify(mockClubService, times(1)).validateTeamSportsinClub(any());
    }

    @Test
    public void whenSelectedTeamsIsEmpty_return302() throws Exception {
        Mockito.when(mockClubService.validateTeamSportsinClub(any())).thenReturn(true);
        mockMvc.perform(post("/createClub", 42L)
                        .param("clubId", "1")
                        .param("name", "new club")
                        .param("addressLine1", "addressline1")
                        .param("addressLine2", "addressline2")
                        .param("suburb", "Ilam")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "1111")
                        .param("selectedTeams", ""))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/home")); //TODO Change redirect to the view club page when created

        verify(mockClubService, times(1)).updateOrAddClub(any());
        verify(mockClubService, times(1)).validateTeamSportsinClub(any());
    }

    @Test
    public void whenOptionalFieldsEmpty_return302() throws Exception {
        Mockito.when(mockClubService.validateTeamSportsinClub(any())).thenReturn(true);
        mockMvc.perform(post("/createClub", 42L)
                        .param("clubId", "1")
                        .param("name", "new club")
                        .param("addressLine1", "addressline1")
                        .param("addressLine2", "")
                        .param("suburb", "")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "1111")
                        .param("selectedTeams", ""))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/home")); //TODO Change redirect to the view club page when created

        verify(mockClubService, times(1)).updateOrAddClub(any());
        verify(mockClubService, times(1)).validateTeamSportsinClub(any());
    }

    @Test
    public void whenSelectedTeamsHaveDifferentSports_return400() throws Exception {
        Mockito.when(mockClubService.validateTeamSportsinClub(any())).thenReturn(false);

        mockMvc.perform(post("/createClub", 42L)
                        .param("clubId", "1")
                        .param("name", "new club")
                        .param("addressLine1", "addressline1")
                        .param("addressLine2", "addressline2")
                        .param("suburb", "Ilam")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "1111")
                        .param("selectedTeams", team.getTeamId().toString(), team3.getTeamId().toString()))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("createClub"));

        verify(mockClubService, times(0)).updateOrAddClub(any());
        verify(mockClubService, times(1)).validateTeamSportsinClub(any());
    }


    @Test
    public void whenAllRequiredFieldsEmpty_return400() throws Exception {
        Mockito.when(mockClubService.validateTeamSportsinClub(any())).thenReturn(true);

        mockMvc.perform(post("/createClub", 42L)
                        .param("clubId", "1")
                        .param("name", "")
                        .param("addressLine1", "")
                        .param("addressLine2", "addressline2")
                        .param("suburb", "Ilam")
                        .param("city", "")
                        .param("country", "")
                        .param("postcode", "1111")
                        .param("selectedTeams", team.getTeamId().toString(), team3.getTeamId().toString()))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("createClub"));

        verify(mockClubService, times(0)).updateOrAddClub(any());
        verify(mockClubService, times(1)).validateTeamSportsinClub(any());
    }

    @Test
    public void whenClubNameInvalid_return400() throws Exception {
        Mockito.when(mockClubService.validateTeamSportsinClub(any())).thenReturn(true);

        mockMvc.perform(post("/createClub", 42L)
                        .param("clubId", "1")
                        .param("name", "!@#$%^")
                        .param("addressLine1", "2")
                        .param("addressLine2", "addressline2")
                        .param("suburb", "Ilam")
                        .param("city", "Christchurch")
                        .param("country", "NZ")
                        .param("postcode", "1111")
                        .param("selectedTeams", team.getTeamId().toString(), team3.getTeamId().toString()))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("createClub"));

        verify(mockClubService, times(0)).updateOrAddClub(any());
    }


}
