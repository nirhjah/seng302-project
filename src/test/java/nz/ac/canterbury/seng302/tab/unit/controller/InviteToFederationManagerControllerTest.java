package nz.ac.canterbury.seng302.tab.unit.controller;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.mail.EmailService;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.FederationService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class InviteToFederationManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private FederationService federationService;

    @MockBean
    private UserService userService;

    @MockBean
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @WithMockUser()
    void testGettingPage() throws Exception {
        mockMvc.perform(get("/inviteToFederationManager")).andExpect(status().isOk()).andExpect(view().name("inviteToFederationManager"));
    }

    @Test
    @WithMockUser()
    void testSendingInvite() throws Exception {
        User u = new User("Test", "Account", "email@gmail.com", "password", new Location(null, null, null, "chch", null, "nz"));
        userRepository.save(u);
        when(userService.findUserById(u.getUserId())).thenReturn(Optional.of(u));
        when(federationService.findFederationManagerByUser(u)).thenReturn(Optional.empty());
        mockMvc.perform(post("/inviteToFederationManager")
                .param("userId", String.valueOf(u.getUserId()))).andExpect(status().isFound())
                .andExpect(view().name("redirect:/inviteToFederationManager"));
        verify(emailService).federationManagerInvite(any(), any(), any());
    }

}
