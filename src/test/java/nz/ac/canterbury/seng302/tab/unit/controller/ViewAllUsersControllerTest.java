package nz.ac.canterbury.seng302.tab.unit.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.SportRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ViewAllUsersControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private SportRepository sportRepository;


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

    @AfterEach
    public void afterEach() throws IOException {
        userRepository.deleteAll();
        sportRepository.deleteAll();
    }

    @WithMockUser(username = "johndoe@example.com", password = "Password123!", roles = "USER")
    @Test
    public void testViewAllUsersReturns200() throws Exception {
        mockMvc.perform(get("/view-users"))
                .andExpect(status().isOk())
                .andExpect(view().name("viewAllUsers"));
    }
}
