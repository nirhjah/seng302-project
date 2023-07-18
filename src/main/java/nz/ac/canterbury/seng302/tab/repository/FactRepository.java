package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Fact.Fact;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FactRepository extends CrudRepository<Fact, Long> {
    List<Fact> getFactByActivity(Activity activity);
    List<Fact> getFactByFactTypeAndActivity(int factType, Activity activity);

    @Query("SELECT count(f.scorer) FROM Activity a JOIN Goal f WHERE f.factType=1 AND f MEMBER of a.activityFacts AND f.activity=a AND a.team=:team AND f.scorer=:user AND (a.activityType = 0 OR a.activityType = 1)")
    int getTotalGoalsScoredPerTeam(User user, Team team);

    @Query("SELECT f.timeOfEvent FROM Activity a JOIN Substitution f WHERE f.factType=2 AND f MEMBER of a.activityFacts AND f.activity=:activity AND f.playerOff=:user AND (a.activityType = 0 OR a.activityType = 1)")
    List<String> getUserSubOffForActivity(User user, Activity activity);

    @Query("SELECT f.timeOfEvent FROM Activity a JOIN Substitution f WHERE f.factType=2 AND f MEMBER of a.activityFacts AND f.activity=:activity AND f.playerOn=:user AND (a.activityType = 0 OR a.activityType = 1)")
    List<String> getUserSubOnsForActivity(User user, Activity activity);
}
