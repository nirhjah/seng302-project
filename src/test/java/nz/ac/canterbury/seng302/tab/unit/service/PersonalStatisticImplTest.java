package nz.ac.canterbury.seng302.tab.unit.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.entity.Fact.Goal;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import nz.ac.canterbury.seng302.tab.repository.ActivityRepository;
import nz.ac.canterbury.seng302.tab.repository.FactRepository;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.FactService;
import nz.ac.canterbury.seng302.tab.service.PersonalStatisticImpl;
import nz.ac.canterbury.seng302.tab.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import({PersonalStatisticImpl.class, UserService.class})
public class PersonalStatisticImplTest {

    private static final String USER_FNAME = "Test";
    private static final String USER_LNAME = "User";
    private static final String USER_EMAIL = "test@email.org";
    private static final String USER_DOB = "2000-01-01";
    private static final String USER_PWORD = "super_insecure";
    private static final String USER_ADDRESS_LINE_1 = "1 Street Road";
    private static final String USER_ADDRESS_LINE_2 = "A";
    private static final String USER_SUBURB = "Riccarton";
    private static final String USER_POSTCODE = "8000";
    private static final String USER_CITY = "Christchurch";
    private static final String USER_COUNTRY = "New Zealand";
    


    @Autowired
    ActivityService activityService;
    
    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    FactRepository factRepository;

    @Autowired
    TeamRepository teamRepository;
    
    @Autowired
    FactService factService;

    @Autowired
    PersonalStatisticImpl personalStatisticImpl;


    @MockBean
    private UserService mockUserService;


    private Team team;

    private User testUser;

    @BeforeEach
    public void beforeAll() throws IOException {
        teamRepository.deleteAll();
        userRepository.deleteAll();
        Location location = new Location("1 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand");
        team = new Team("test", "Hockey", location);
        teamRepository.save(team);

        Location testLocation = new Location("23 test street", "24 test street", "surburb", "city", "8782",
                "New Zealand");
        testUser = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(),
                "johndoe@example.com", "Password123!", testLocation);
        userRepository.save(testUser);

        Mockito.when(mockUserService.getCurrentUser()).thenReturn(Optional.of(testUser));
    }


    @Test
    public void GivenIHaveScoredOneGoal_WhenICheckMyGoalsScored_ThenISee1GoalScored(){

        userRepository.save(testUser);

        Activity game = new Activity(ActivityType.Game, team, "A Test Game",
                LocalDateTime.of(2025, 1,1,6,30),
                LocalDateTime.of(2025, 1,1,8,30), testUser,
                new Location("Test", "Test", "Test", "test", "Tst", "test"));
        activityRepository.save(game);

        Goal goal = new Goal("test", "85", game, testUser, 1);
        factRepository.save(goal);

        int goals = personalStatisticImpl.getGoalsScored(testUser, team);
        assertEquals(1, goals);
    }

}
