package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Fact.Fact;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.FactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class FactService {

    @Autowired
    FactRepository factRepository;

    @Autowired
    ActivityService activityService;

    /**
     * Returns a list of all facts related to an activity
     * @param activity the activity whose facts are wanted to be returned
     * @return the list of facts associated with an activity
     */
    public List<Fact> getAllFactsForActivity(Activity activity) {return factRepository.getFactByActivity(activity);}

    /**
     * Returns a list of all facts of a certain type.
     * @param factType the ordinal value of the FactType enum representing the type of facts to be found
     *                 (0 - Fact, 1 - Goal, 2 - Substitution)
     * @param activity the activity whose facts are being retrieved.
     * @return list of activities with the wanted fact type.
     */
    public List<Fact> getAllFactsOfGivenTypeForActivity(int factType, Activity activity) {
        return factRepository.getFactByFactTypeAndActivity(factType, activity);
    }

    /**
     * Gets a user's total goals for a team they are apart of
     * @param user the user's whose goals are being found
     * @param team the team the user is apart of
     * @return the total number of goals they've scored for a team
     */
    public int getTotalGoalsForTeamPerUser(User user, Team team) {
        return factRepository.getTotalGoalsScoredPerTeam(user, team);
    }

    /**
     * Get list of substitution (off) times for a user for a specific activity
     * @param user the user whose sub offs are being requested
     * @param activity the activity which the user participated in
     * @return list of the times of substitution
     */
    public List<String> getUserSubOffForActivity(User user, Activity activity) {
        return factRepository.getUserSubOffForActivity(user, activity);
    }

    /**
     * Get list of substitution (on) times for a user for a specific activity
     * @param user the user whose sub ons are being requested
     * @param activity the activity which the user participated in
     * @return list of the times of substitution
     */
    public List<String> getUserSubOnsForActivity(User user, Activity activity) {
        return factRepository.getUserSubOnsForActivity(user, activity);
    }

    /**
     * Get the total time a player participated in an activity
     * TODO implement in combination with subbing and starting line up
     * @param activity the activity the player took part in
     * @return the length of the activity in minutes
     */
    public long getTimePlayed(Activity activity) {
        return Duration.between(activity.getActivityStart(), activity.getActivityEnd()).toMinutes();
    }

    /**
     * Get A players total time played for a team across all activities
     * @param user the user whose total time is being requested
     * @param team the team the user is playing for
     * @return total time a player has played for a team
     */
    public long getTotalTimeAUserHasPlayedForATeam(User user, Team team) {
        List<Activity> activities = activityService.getAllTeamActivities(team);
        long totalTime = 0;
        for (Activity act : activities) {
            totalTime += getTimePlayed(act);
        }
        return totalTime;
    }

    /**
     * Get total matches
     * TODO Make this dependant on the played matches once formation is complete
     * @param team the teams matches wanted
     * @return the total number of played matches.
     */
    public long getTotalUserMatches(Team team) {
        return activityService.getAllTeamActivities(team).size();
    }

}
