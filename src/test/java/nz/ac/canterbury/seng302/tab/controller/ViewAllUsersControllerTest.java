package nz.ac.canterbury.seng302.tab.controller;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.UserService;

import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

@SpringBootTest
@AutoConfigureMockMvc
public class ViewAllUsersControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

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
    }

    @WithMockUser
    @Test
    public void testViewAllUsersReturns200() throws Exception {
        mockMvc.perform(get("/view-users"))
                .andExpect(status().isOk())
                .andExpect(view().name("viewAllUsers"));
    }
}
