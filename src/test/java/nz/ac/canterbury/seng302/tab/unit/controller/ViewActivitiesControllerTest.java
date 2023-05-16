package nz.ac.canterbury.seng302.tab.unit.controller;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.helper.GenerateRandomUsers;
import nz.ac.canterbury.seng302.tab.repository.ActivityRepository;
import nz.ac.canterbury.seng302.tab.repository.SportRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ViewActivitiesControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService mockUserService;

    @MockBean
    ActivityService activityService;

    private User user;

    private static long USER_ID = 1;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityRepository activityRepository;
    private static final String USER_ADDRESS_LINE_1 = "1 Street Road";
    private static final String USER_ADDRESS_LINE_2 = "A";
    private static final String USER_SUBURB = "Riccarton";
    private static final String USER_POSTCODE = "8000";
    private static final String USER_CITY = "Christchurch";
    private static final String USER_COUNTRY = "New Zealand";

    private Activity activity;

    @BeforeEach
    public void beforeAll() throws IOException {
        userRepository.deleteAll();
        activityRepository.deleteAll();
        Location testLocation = new Location(USER_ADDRESS_LINE_1, USER_ADDRESS_LINE_2, USER_SUBURB, USER_CITY, USER_POSTCODE, USER_COUNTRY);
        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", testLocation);
        //userRepository.save(user);

        activity = new Activity(Activity.ActivityType.Other, null, "A workout session",
                LocalDateTime.of(2020, Month.JANUARY,1, 6, 30),
                LocalDateTime.of(2020, Month.JANUARY,1, 8, 30),
                user);
        activityRepository.save(activity);
        userRepository.save(user);
    }

    @Test
    @WithMockUser()
    public void getMyActivitiesTest() throws Exception {
        Mockito.when(mockUserService.getCurrentUser()).thenReturn(Optional.of(user));
        Page<Activity> page = new PageImpl<>(List.of(activity));
        Pageable pageable = PageRequest.of(0, 10);
        Mockito.when(activityService.getPaginatedActivities(pageable, user)).thenReturn(page);
        mockMvc.perform(get("/view-activities")).andExpect(view().name("redirect:/view-activities?page=1"));
    }
}
