package nz.ac.canterbury.seng302.tab.unit.controller;

import nz.ac.canterbury.seng302.tab.api.response.ClubTeamInfo;
import nz.ac.canterbury.seng302.tab.entity.Club;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.ClubRepository;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.ClubService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
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

    @Autowired
    private ClubRepository clubRepository;


    private Team team;

    private Team team2;
    private Team team3;

    private User user;

    private Club club;

    private final long DEFAULT_CLUB_ID=0;

    private MockMultipartFile mockMultipartFile;



    @AfterEach
    void afterEach() {
        teamRepository.deleteAll();
        clubRepository.deleteAll();
        userRepository.deleteAll();
    }

    @BeforeEach
    void beforeEach() throws IOException {
        Location location = new Location("1 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand");
        team = new Team("test", "Hockey", location);
        team2 = new Team("test2", "Hockey", new Location("2 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand"));
        team3 = new Team("test3", "Rugby", new Location("3 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand"));

        Location testLocation = new Location("23 test street", "24 test street", "surburb", "city", "8782",
                "New Zealand");
        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(),
                "johndoe@example.com", "Password123!", testLocation);
        userRepository.save(user);


        club = new Club("Rugby Club", new Location("5 Test Lane", "", "", "Christchurch", "8042", "New Zealand"), "Rugby",null);
        team3.setTeamClub(club);

        clubRepository.save(club);
        teamRepository.save(team);
        teamRepository.save(team2);
        teamRepository.save(team3);
        Mockito.when(mockUserService.getCurrentUser()).thenReturn(Optional.of(user));

        Mockito.when(mockClubService.findClubById(club.getClubId())).thenReturn(Optional.of(club));

        Resource resource = new ClassPathResource("/static/image/default-profile.png");
        File file = resource.getFile();
        FileInputStream input = new FileInputStream(file);
        this.mockMultipartFile = new MockMultipartFile("file", "clublogo.png", "image/png", input.readAllBytes());
    }

    @Test
    public void testDisplayCreateClubForm_return200() throws Exception {
        mockMvc.perform(get("/createClub"))
                .andExpect(status().isOk())
                .andExpect(view().name("createClubForm"));
    }


    @Test
        public void whenAllFieldsValid_return302() throws Exception {

            mockMvc.perform(multipart("/createClub", 42L)
                            .file(this.mockMultipartFile)
                            .param("clubId", "-1")
                            .param("name", "new club")
                            .param("sport", "Hockey")
                            .param("addressLine1", "addressline1")
                            .param("addressLine2", "addressline2")
                            .param("suburb", "Ilam")
                            .param("city", "Christchurch")
                            .param("country", "New Zealand")
                            .param("postcode", "1111")
                    .param("selectedTeams", team.getTeamId().toString(), team2.getTeamId().toString()))
                    .andExpect(status().isFound())
                    .andExpect(view().name("redirect:/view-club?clubID="+DEFAULT_CLUB_ID));

            verify(mockClubService, times(1)).updateOrAddClub(any());
        }


     @Test
        public void whenSelectedTeamsIsEmpty_return302() throws Exception {
            mockMvc.perform(multipart("/createClub", 42L)
                            .file(this.mockMultipartFile)
                            .param("clubId", "-1")
                            .param("name", "new club")
                            .param("sport", "Hockey")
                            .param("addressLine1", "addressline1")
                            .param("addressLine2", "addressline2")
                            .param("suburb", "Ilam")
                            .param("city", "Christchurch")
                            .param("country", "New Zealand")
                            .param("postcode", "1111")
                            .param("selectedTeams", ""))
                    .andExpect(status().isFound())
                    .andExpect(view().name("redirect:/view-club?clubID="+DEFAULT_CLUB_ID));

            verify(mockClubService, times(1)).updateOrAddClub(any());
        }


    @Test
    public void whenOptionalFieldsEmpty_return302() throws Exception {
        mockMvc.perform(multipart("/createClub", 42L)
                        .file(this.mockMultipartFile)
                        .param("clubId", "-1")
                        .param("name", "new club")
                        .param("sport", "Hockey")
                        .param("addressLine1", "addressline1")
                        .param("addressLine2", "")
                        .param("suburb", "")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "1111")
                        .param("selectedTeams", ""))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/view-club?clubID="+DEFAULT_CLUB_ID));

        verify(mockClubService, times(1)).updateOrAddClub(any());
    }

    @Test
    public void whenSelectedTeamsHaveDifferentSports_return400() throws Exception {
        mockMvc.perform(multipart("/createClub", 42L)
                        .file(this.mockMultipartFile)
                        .param("clubId", "-1")
                        .param("name", "new club")
                        .param("sport", "Hockey")
                        .param("addressLine1", "addressline1")
                        .param("addressLine2", "addressline2")
                        .param("suburb", "Ilam")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "1111")
                        .param("selectedTeams", team.getTeamId().toString(), team3.getTeamId().toString()))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("createClubForm"));

        verify(mockClubService, times(0)).updateOrAddClub(any());
    }


    @Test
    public void whenAllRequiredFieldsEmpty_return400() throws Exception {
        mockMvc.perform(multipart("/createClub", 42L)
                        .file(this.mockMultipartFile)
                        .param("clubId", "-1")
                        .param("name", "")
                        .param("sport", "Hockey")
                        .param("addressLine1", "")
                        .param("addressLine2", "addressline2")
                        .param("suburb", "Ilam")
                        .param("city", "")
                        .param("country", "")
                        .param("postcode", "1111"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("createClubForm"));

        verify(mockClubService, times(0)).updateOrAddClub(any());
    }

    @Test
    public void whenClubNameInvalid_return400() throws Exception {
        mockMvc.perform(multipart("/createClub", 42L)
                        .file(this.mockMultipartFile)
                        .param("clubId", "-1")
                        .param("name", "!@#$%^")
                        .param("sport", "Hockey")
                        .param("addressLine1", "2")
                        .param("addressLine2", "addressline2")
                        .param("suburb", "Ilam")
                        .param("city", "Christchurch")
                        .param("country", "NZ")
                        .param("postcode", "1111"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("createClubForm"));

        verify(mockClubService, times(0)).updateOrAddClub(any());
    }

    @Test
    public void whenTeamAlreadyPartOfAnotherClub_return400() throws Exception {
        mockMvc.perform(multipart("/createClub", 42L)
                        .file(this.mockMultipartFile)
                        .param("clubId", "-1")
                        .param("name", "new club")
                        .param("sport", "Rugby")
                        .param("addressLine1", "addressline1")
                        .param("addressLine2", "addressline2")
                        .param("suburb", "Ilam")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "1111")
                        .param("selectedTeams", team3.getTeamId().toString()))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("createClubForm"));
    }

    @Test
    public void testDisplayingEditClub_returns200() throws Exception {
        mockMvc.perform(get("/createClub?edit={id}", club.getClubId()))
                .andExpect(status().isOk())
                .andExpect(view().name("createClubForm"));
    }

    @Test
    public void testWhenInvalidFieldEntered_ClubNameNotUpdated() throws Exception {
        mockMvc.perform(multipart("/createClub?edit={id}", club.getClubId())
                        .file(this.mockMultipartFile)
                        .param("clubId", String.valueOf(club.getClubId()))
                        .param("name", "!@#$%^")
                        .param("sport", "Rugby")
                        .param("addressLine1", "5 Test Lane")
                        .param("addressLine2", "")
                        .param("suburb", "")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "8042"));

        Assertions.assertEquals("Rugby Club", club.getName());
    }

    @Test
    public void testWhenSportInvalid_ClubNotUpdated() throws Exception {
        mockMvc.perform(multipart("/createClub?edit={id}", club.getClubId())
                .file(this.mockMultipartFile)
                .param("clubId", String.valueOf(club.getClubId()))
                .param("name", "Club")
                .param("sport", "@#@#")
                .param("addressLine1", "5 Test Lane")
                .param("addressLine2", "")
                .param("suburb", "")
                .param("city", "Christchurch")
                .param("country", "New Zealand")
                .param("postcode", "8042")).andExpect(status().isBadRequest());

        verify(mockClubService, times(0)).updateOrAddClub(any());
    }


    @Test
    public void testGettingTeamsBySportNoClub() throws  Exception {
        String sport = "YourSport";
        Team teamTest = new Team("NewTeam", sport);
        Team teamTestNotMatchingSport = new Team("NewTeam", "Soccer");
        teamRepository.save(teamTest);
        teamRepository.save(teamTestNotMatchingSport);

        mockMvc.perform(MockMvcRequestBuilders.get("/teams_by_sport_not_in_club")
                        .param("sport", sport)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("[{\"teamId\":4,\"name\":\"NewTeam\"}]"));
    }

    @Test
    public void testGettingTeamsBySport() throws  Exception {
        Team teamInClub = new Team("NewTeam", "Rugby");
        Team teamNotInClubButSameSport = new Team("OtherTeam", "Rugby");
        Team teamNotInClubAndNotMatchingSport = new Team("RandomTeam", "Cricket");

        teamRepository.save(teamInClub);
        teamRepository.save(teamNotInClubButSameSport);
        teamRepository.save(teamNotInClubAndNotMatchingSport);
        teamInClub.setTeamClub(club);

        mockMvc.perform(MockMvcRequestBuilders.get("/teams_by_sport")
                        .param("sport", "Rugby")
                        .param("clubId", String.valueOf(club.getClubId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("[{\"teamId\":3,\"name\":\"test3\"},{\"teamId\":4,\"name\":\"NewTeam\"},{\"teamId\":5,\"name\":\"OtherTeam\"}]"));
    }

}

