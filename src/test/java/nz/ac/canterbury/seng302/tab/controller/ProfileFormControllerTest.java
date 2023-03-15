package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class ProfileFormControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamRepository teamRepository;

    private Team team;

    @BeforeEach
    public void beforeEach() throws IOException {
        teamRepository.deleteAll();
        team = new Team("test", "Christchurch", "Hockey");
        teamRepository.save(team);
    }

    @Test
    public void testGettingTeamList() throws Exception {
        Team team = new Team("test", "Christchurch", "Hockey");
        teamRepository.save(team);
        mockMvc.perform(get("/profile?teamID={id}", team.getTeamId())
                        .requestAttr("teamID", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("profileForm"))
                .andExpect(MockMvcResultMatchers.model().attribute("teamID", team.getTeamId()))
                .andExpect(MockMvcResultMatchers.model().attribute("displayName", team.getName()))
                .andExpect(MockMvcResultMatchers.model().attribute("displaySport", team.getSport()))
                .andExpect(MockMvcResultMatchers.model().attribute("displayPicture", team.getPictureString()));
    }

    @Test
    public void testUploadValidProfilePicture() throws Exception {
        Resource resource = new ClassPathResource("/static/image/default-profile.png");
        File file = resource.getFile();
        FileInputStream input = new FileInputStream(file);
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                file.getName(), "image/png", input.readAllBytes());
        mockMvc.perform(multipart("/profile").file(multipartFile))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?teamID=1"));

    }
}
