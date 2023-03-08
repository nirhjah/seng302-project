package nz.ac.canterbury.seng302.tab.service;

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

    @BeforeEach
    public void beforeEach() {
        teamRepository.deleteAll();
    }

    @Test
    public void testGettingTeamList() {
        List<Team> teamList = teamService.getTeamList();
        assertTrue(teamList.isEmpty());
        Team team = new Team("test", "Christchurch", "Hockey");
        Team team2 = new Team("test2", "Auckland", "Netball");
        Team team3 = new Team("test3", "Dunedin", "Basketball");
        List<Team> list = Arrays.asList(team, team2, team3);
        teamRepository.save(team);
        teamRepository.save(team2);
        teamRepository.save(team3);
        teamService.getTeamList();

        assertEquals(list.toString(), teamService.getTeamList().toString());
    }

    @Test
    public void testAddingTeam(){
        Team team = new Team("test", "Christchurch", "Hockey");
        teamService.addTeam(team);
        assertEquals(team.getName(), teamRepository.findById(team.getTeamId()).get().getName());
        assertEquals(team.getLocation(), teamRepository.findById(team.getTeamId()).get().getLocation());
        assertEquals(team.getSport(), teamRepository.findById(team.getTeamId()).get().getSport());
    }

    @Test
    public void testUpdatingPicture() throws IOException {
        Team team = new Team("test", "Christchurch", "Hockey");
        teamRepository.save(team);


        Resource resource = new ClassPathResource("/static/image/default-profile.png");
        File file = resource.getFile();
        String pictureString = Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
                file.getName(), "image/png", input.readAllBytes());
        teamService.updatePicture(multipartFile, team.getTeamId());
        assertEquals(pictureString, Base64.getEncoder().encodeToString(multipartFile.getBytes()));
    }


}


