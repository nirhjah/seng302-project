package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class TeamServiceTest {

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamRepository teamRepository;

    Location location;

    @BeforeEach
    public void beforeEach() {
        location = new Location("1 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand");
        teamRepository.deleteAll();
    }


    @Test
    public void testGettingTeamList() throws IOException {
        List<Team> teamList = teamService.getTeamList();
        assertTrue(teamList.isEmpty());
        Location location1 = new Location("add1", "add2", "sub" , "Christchurch", "8081", "NZ");
        Location location2 = new Location("another", "test", "location" , "Christchurch", "8081", "NZ");
        Location location3 = new Location("test", "location", "again" , "Christchurch", "8081", "NZ");
        Team team = new Team("test", "Hockey", location1);
        Team team2 = new Team("test2", "Netball", location2);
        Team team3 = new Team("test3", "Cricket", location3);

        teamService.addTeam(team);
        teamService.addTeam(team2);
        teamService.addTeam(team3);

        List<Team> list = Arrays.asList(team, team2, team3);

        List<Team> result = teamService.getTeamList();

        assertEquals(list.toString(), result.toString());
    }

    @Test
    public void testAddingTeam() throws IOException {
        Team team = new Team("test", "Hockey", location);
        teamService.addTeam(team);
        assertEquals(team.getName(), teamRepository.findById(team.getTeamId()).get().getName());
//        assertEquals(team.getLocation(), teamRepository.findById(team.getTeamId()).get().getLocation());
        assertTrue(team.getLocation().equals(teamRepository.findById(team.getTeamId()).get().getLocation()));
        assertEquals(team.getSport(), teamRepository.findById(team.getTeamId()).get().getSport());
    }

    @Test
    public void testUpdatingPicture() throws IOException {
        Team team = new Team("test", "Hockey", location);
        teamRepository.save(team);

        Resource resource = new ClassPathResource("/static/image/default-profile.png");
        File file = resource.getFile();
        String pictureString = Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
        try (FileInputStream input = new FileInputStream(file)) {
            MultipartFile multipartFile = new MockMultipartFile("file",
                    file.getName(), "image/png", input.readAllBytes());
            teamService.updatePicture(multipartFile, team.getTeamId());
            assertEquals(pictureString, Base64.getEncoder().encodeToString(multipartFile.getBytes()));
        }
    }

}
