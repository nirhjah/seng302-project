package nz.ac.canterbury.seng302.tab.unit.controller;

import nz.ac.canterbury.seng302.tab.entity.FederationManagerInvite;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.FederationRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.FederationService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class FederationManagerInviteControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    FederationService federationService;

    @MockBean
    UserService userService;

    @Autowired
    FederationRepository federationRepository;

    @Autowired
    UserRepository userRepository;

    private FederationManagerInvite invite;

    private User user1;

    private User user2;

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        federationRepository.deleteAll();
    }

    @BeforeEach
    void beforeEach() throws Exception {
        Location location1 = new Location("1 Test Lane", "", "Ilam", "Christchurch",
                "8041", "New Zealand");
        user1 = new User("Test", "Account", "test@test.com", "password1", location1);
        userRepository.save(user1);
        Location location2 = new Location("1 Test Lane", "", "Ilam", "Christchurch",
                "8041", "New Zealand");
        user2 = new User("Test", "Account", "test1@test.com", "password2", location2);
        userRepository.save(user2);
        Mockito.when(userService.getCurrentUser()).thenReturn(Optional.of(user1));
        invite = new FederationManagerInvite(user1);
        Mockito.when(federationService.getByToken(invite.getToken())).thenReturn(invite);
    }

    @Test
    void userAccessesTheirTokenInvite() throws Exception {
        String url = "/federationManager?token=" + invite.getToken();
        mockMvc.perform(get(url)).andExpect(status().isOk()).andExpect(view().name("federationManagerInvite"));
    }

    @Test
    void userAccessesAnotherUsersInvite() throws Exception {
        Mockito.when(userService.getCurrentUser()).thenReturn(Optional.of(user2));
        String url = "/federationManager?token=" + invite.getToken();
        mockMvc.perform(get(url)).andExpect(status().isFound()).andExpect(view().name("redirect:user-info/self"));
    }

    @Test
    void userAccessesInvalidToken() throws Exception {
        String url = "/federationManager?token=" + invite.getToken() + "abc";
        mockMvc.perform(get(url)).andExpect(status().isFound()).andExpect(view().name("redirect:user-info/self"));
    }

    @Test
    void acceptingToken() throws Exception {
        mockMvc.perform(post("/federationManager").param("decision", "true"))
                .andExpect(status().isFound()).andExpect(view().name("redirect:user-info/self"))
                .andExpect(flash().attribute("fedmanTokenMessage", "Success! You are now a federation manager"));
    }

    @Test
    void decliningToken() throws Exception {
        mockMvc.perform(post("/federationManager").param("decision", "false"))
                .andExpect(status().isFound()).andExpect(view().name("redirect:user-info/self"));
    }


}
