package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Fact.Fact;
import nz.ac.canterbury.seng302.tab.entity.Team;
import org.springframework.data.jpa.repository.Query;
import nz.ac.canterbury.seng302.tab.entity.User;
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

    /**
     * Error of type mismatch is intellji not knowing that Goal is a sub type of fact so this DOES work
     * Return Multiple Entities Code <a href="https://www.baeldung.com/jpa-return-multiple-entities#:~:text=In%20order%20to%20create%20a,primary%20and%20corresponding%20foreign%20keys">...</a>.
     * @param team the team for which top scorers are to be found
     * @return an Object List which contains the scorer and their total scores for the team
     */
    @Query("SELECT f.scorer, sum(f.goalValue) FROM Activity a JOIN Goal f WHERE f.factType=1 AND f MEMBER of a.activityFacts AND a.team=:team AND (a.activityType = 0 OR a.activityType = 1) GROUP BY f.scorer ORDER BY sum(f.goalValue) desc limit 5")
    List<Object[]> getListOfTopScorersAndTheirScores(Team team);

}
