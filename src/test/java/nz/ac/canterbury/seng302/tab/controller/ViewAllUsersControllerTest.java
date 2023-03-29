package nz.ac.canterbury.seng302.tab.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.helper.GenerateRandomUsers;
import nz.ac.canterbury.seng302.tab.repository.SportRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ViewAllUsersControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SportRepository sportRepository;

    @Autowired
    private GenerateRandomUsers generateRandomUsers;

    private User user;

    @AfterEach
    public void afterEach() throws IOException {
        userRepository.deleteAll();
        sportRepository.deleteAll();
    }
    
    @Test
    @WithMockUser
    public void basicGet_returns200() throws Exception {
        user = generateRandomUsers.createRandomUser();
        userRepository.save(user);
        mockMvc.perform(get("/view-users"))
                .andExpect(status().isOk())
                .andExpect(view().name("viewAllUsers"));
    }

}
