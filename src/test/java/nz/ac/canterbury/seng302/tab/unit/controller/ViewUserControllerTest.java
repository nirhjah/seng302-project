package nz.ac.canterbury.seng302.tab.unit.controller;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.image.UserImageService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ViewUserControllerTest {

    @Autowired
    private UserImageService userImageService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TeamService teamService;

    @MockBean
    private UserService mockUserService;

    @Autowired
    private UserRepository userRepository;

    private User user;

    private static final String USER_ADDRESS_LINE_1 = "1 Street Road";
    private static final String USER_ADDRESS_LINE_2 = "A";
    private static final String USER_SUBURB = "Riccarton";
    private static final String USER_POSTCODE = "8000";
    private static final String USER_CITY = "Christchurch";
    private static final String USER_COUNTRY = "New Zealand";

    @BeforeEach
    public void beforeAll() throws IOException {
        userRepository.deleteAll();
        Location testLocation = new Location(USER_ADDRESS_LINE_1, USER_ADDRESS_LINE_2, USER_SUBURB, USER_CITY, USER_POSTCODE, USER_COUNTRY);
        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", testLocation);
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
                .andExpect(view().name("redirect:/user-info?name=" + user.getUserId()));
    }


    @Test
    @WithMockUser
    public void whenLoggedIn_whenUploadTooBigFile_expectTypeError() throws Exception {
        var URL = "/user-info/upload-pfp";
        Resource resource = new ClassPathResource("/testingfiles/maxFileSize.png");
        File file = resource.getFile();
        FileInputStream input= new FileInputStream(file);
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        MockMultipartFile tooBigImage = new MockMultipartFile("file", file.getName(),"image/png", input.readAllBytes());
        mockMvc.perform(multipart(URL).file(tooBigImage))
                .andExpect(status().is3xxRedirection());

        assertNotEquals(fileBytes, userImageService.readFileOrDefault(user.getUserId()));
    }

    @Test
    @WithMockUser
    public void whenLoggedIn_whenUploadGoodFile_expectNoErrors() throws Exception {
        var URL = "/user-info/upload-pfp";
        Resource resource = new ClassPathResource("/testingfiles/pfp.png");
        File file = resource.getFile();
        FileInputStream input= new FileInputStream(file);
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        MockMultipartFile okImg = new MockMultipartFile("file", file.getName(),"image/png", input.readAllBytes());
        mockMvc.perform(multipart(URL).file(okImg))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/user-info?name=%s", user.getUserId())));
    }
}
