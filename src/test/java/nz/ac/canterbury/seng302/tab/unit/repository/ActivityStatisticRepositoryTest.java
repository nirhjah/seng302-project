package nz.ac.canterbury.seng302.tab.unit.repository;

import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.entity.Fact.Fact;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import nz.ac.canterbury.seng302.tab.repository.ActivityRepository;
import nz.ac.canterbury.seng302.tab.repository.ActivityStatisticRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@DataJpaTest
public class ActivityStatisticRepositoryTest {


    @Autowired
    ActivityStatisticRepository activityStatisticRepository;

    @Autowired
    ActivityRepository activityRepository;

    private Location location;

    @BeforeEach
    void beforeEach() {
        location = new Location(null, null, null, "Christchurch", null, "New Zealand");

    }

    @Test
    public void getActivityStatisticByActivity() throws Exception {
        User creator = new User("Test", "Account", "test@test.com", "Password1!", location);
        Team team = new Team("TeamName", "Sport");
        Activity activity = new Activity(ActivityType.Game, team, "Test Activity Game",
                LocalDateTime.of(2023, 5,29,10,00),
                LocalDateTime.of(2023, 5,30,10,00), creator, location);


        List<String> activityScore = new ArrayList<>();
        activityScore.add("141");
        activityScore.add("94");
        activity.setActivityScore(activityScore);

        List<Fact> factList = new ArrayList<>();
        factList.add(new Fact("Test fact", "20m", activity));

        activityRepository.save(activity);

        ActivityStatistics activityStatistics = new ActivityStatistics(creator, activity);
        activityStatisticRepository.save(activityStatistics);

        Assertions.assertEquals(activityStatisticRepository.findActivityStatisticsByActivity(activity), activityStatistics);

    }
}
