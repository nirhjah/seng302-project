package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import nz.ac.canterbury.seng302.tab.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Activity
 */
@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    @Autowired
    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public static final String activityScoreHyphenRegex = "^(\\p{N}+-(\\p{N}+))+$";

    public static final String activityScoreNumberOnlyRegex = "^[0-9]+$";

    private static String winString = "Won";
    private static String loseString = "Lost";
    private static String drawString = "Tied";

    /**
     * Returns all activities
     *
     * @return list of all stored activities
     */
    public List<Activity> findAll() {
        return activityRepository.findAll();
    }

    /**
     * Finds activity based on its id
     *
     * @param id id of entity to find
     * @return either the activity or none
     */
    public Activity findActivityById(Long id) {
        Optional<Activity> act = activityRepository.findById(id);
        if (act.isPresent()) {
            return act.get();
        } else {
            return null;
        }
    }

    /**
     * Gets a page of activities.
     *
     * @param pageable A page object showing how the page should be shown
     *                 (Page size, page count, and [optional] sorting)
     * @param user     User for which the activities belong to
     * @return A slice of activities returned from pagination
     */
    public Page<Activity> getPaginatedActivities(Pageable pageable, User user) {
        return activityRepository.findActivitiesByUserSorted(pageable, user);
    }

    /**
     * Updates or saves the activity to the database
     *
     * @param activity - to be stored/updated
     * @return The stored activity entity
     */
    public Activity updateOrAddActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    /**
     * Checks that the activity is scheduled for after a team's creation.
     *
     * @param teamCreation  - the date and time that the team was created
     * @param startActivity - the date and time of the start of the activity
     * @param endActivity   - the date and time of the end of the activity
     * @return true if activity is scheduled after team creation
     */
    public boolean validateActivityDateTime(LocalDateTime teamCreation, LocalDateTime startActivity,
            LocalDateTime endActivity) {
        return teamCreation.isBefore(startActivity) && teamCreation.isBefore(endActivity);
    }

    /**
     * Checks that the start of activity is before the end of the activity
     *
     * @param startActivity - the date and time of the start of the activity
     * @param endActivity   - the date and time of the end of the activity
     * @return true if the end of activity is after the start
     */
    public boolean validateStartAndEnd(LocalDateTime startActivity, LocalDateTime endActivity) {
        return (startActivity != null && endActivity != null) && startActivity.isBefore(endActivity);
    }

    /**
     * Checks that the team selection based on what activity type is selected
     *
     * @param type the type of activity
     * @param team the team selected
     * @return true if the type is game or friendly and there is a team, or if type
     *         is anything but game and friendly
     */
    public boolean validateTeamSelection(ActivityType type, Team team) {
        if ((type == ActivityType.Game || type == ActivityType.Friendly) && team == null) {
            return false;
        } else {
            return true;
        }
    }

    public Page<Activity> getAllTeamActivitiesPage(Team team, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return activityRepository.findActivityByTeam(team, pageable);
    }

    /**
     * Checks that the scores provided for both teams are of the same format
     * First checks if the first team's score is of appropriate hyphen format, if
     * true will check if second team's score is of same hyphen format
     * Otherwise checks if first team's score is of only number format, if true it
     * will check if second team's score is of same only number format
     *
     * @param activityTeamScore score for the team associated with the activity
     * @param otherTeamScore    score for the other team
     * @return true if the scores are both of same format, false otherwise
     */
    public boolean validateActivityScore(String activityTeamScore, String otherTeamScore) {
        if (activityTeamScore.matches(activityScoreHyphenRegex)) {
            if (otherTeamScore.matches(activityScoreHyphenRegex)) {
                return true;
            } else {
                return false;
            }
        } else {
            if (activityTeamScore.matches(activityScoreNumberOnlyRegex)) {
                if (otherTeamScore.matches(activityScoreNumberOnlyRegex)) {
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }
    }

//    public List<String> getLast5ActivityResultsForTeam(Team team) {
//        if (team.getTeamId() != null) {
//            List<Activity> last5Activities = activityRepository.getLast5GameOrFriendly(team);
//            List<String> outcomes = new ArrayList<>();
//            for (Activity activity : last5Activities) {
//                String teamScore = activity.getActivityTeamScore();
//                String otherScore = activity.getOtherTeamScore();
//                if (teamScore.contains("-")) {
//                    teamScore = teamScore.split("-")[0];
//                    otherScore = otherScore.split("-")[0];
//                }
//                try {
//                    int teamScoreNum = Integer.parseInt(teamScore);
//                    long otherTeamScoreNum = Integer.parseInt(otherScore);
//                    if (teamScoreNum > otherTeamScoreNum) {
//                        outcomes.add(winString);
//                    } else if (otherTeamScoreNum == teamScoreNum) {
//                        outcomes.add(drawString);
//                    } else {
//                        outcomes.add(loseString);
//                    }
//                } catch (Exception e) {
//                    return null;
//                }
//            }
//            return outcomes;
//        }
//        return null;
//    }

    /**
     * Returns a list of the last 5 activities that have an outcome
     * @param team the team whose statistics are being returned
     * @return a list of the last 5 activities that are being looked for.
     */
    public List<Activity> getLast5GamesOrFriendliesForTeamWithOutcome(Team team) {
        return activityRepository.getLast5ActivityGameOrFriendly(team);
    }

    /**
     * Returns total number of games and friendlies played
     * @param team team whose games and friendlies are being returned
     * @return the number of games and friendlies played by the given team
     */
    public int numberOfTotalGamesAndFriendlies(Team team) {
        return activityRepository.getAllGamesAndFriendlies(team);
    }

    /**
     * Returns the total number of wins a team has
     * @param team team which the number of wins is wanted
     * @return the total number of wins (friendly and games) a team has
     */
    public int getNumberOfWins(Team team) {
        return activityRepository.getNumberOfWinsForATeam(team);
    }

    /**
     * Returns the total number of loses a team has
     * @param team team which the number of loses is wanted
     * @return the total number of loses (friendly and games) a team has
     */
    public int getNumberOfLoses(Team team) {
        return activityRepository.getNumberOfLosesForATeam(team);
    }

    /**
     * Returns the total number of draws a team has
     * @param team team which the number of draws is wanted
     * @return the total number of draws (friendly and games) a team has
     */
    public int getNumberOfDraws(Team team) {
        return activityRepository.getNumberOfDrawsForATeam(team);
    }

    public List<Activity> getAllGamesAndFriendliesForTeam(Team team) {return activityRepository.getAllGamesAndFriendliesForTeam(team);}
}
