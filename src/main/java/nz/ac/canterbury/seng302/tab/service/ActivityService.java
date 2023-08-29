package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Fact.Goal;
import nz.ac.canterbury.seng302.tab.entity.Fact.Substitution;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUp;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUpPosition;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import nz.ac.canterbury.seng302.tab.repository.ActivityRepository;
import nz.ac.canterbury.seng302.tab.repository.LineUpPositionRepository;
import nz.ac.canterbury.seng302.tab.repository.LineUpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.PageRequest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for Activity
 */
@Service
public class ActivityService {

   /* @Autowired
    LineUpService lineUpService;*/

    /*@Autowired
    LineUpPositionService lineUpPositionService;
*/

    private final LineUpRepository lineUpRepository;

    private final LineUpPositionRepository lineUpPositionRepository;
    private final ActivityRepository activityRepository;

    @Autowired
    public ActivityService(ActivityRepository activityRepository, LineUpRepository lineUpRepository, LineUpPositionRepository lineUpPositionRepository) {
        this.activityRepository = activityRepository;
        this.lineUpRepository = lineUpRepository;
        this.lineUpPositionRepository = lineUpPositionRepository;
    }

    public static final String activityScoreHyphenRegex = "^(\\p{N}+-(\\p{N}+))+$";

    public static final String activityScoreNumberOnlyRegex = "^[0-9]+$";

    /**
     * Returns all activities
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
        if (team == null && (type == ActivityType.Game || type == ActivityType.Friendly)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Gets page of all team activities
     * @param team team to get activities from
     * @param pageNo page number
     * @param pageSize page size
     * @return  page of all team activities
     */
    public Page<Activity> getAllTeamActivitiesPage(Team team, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return activityRepository.findActivityByTeam(team, pageable);
    }

    /**
     * Get list of all team activities
     * @param team team to get activities from
     * @return list of all team activities
     */
    public List<Activity> getAllTeamActivities(Team team) {
        return activityRepository.findActivityByTeam(team);
    }

    /**
     * Checks that the scores provided for both teams are of the same format
     * First checks if both scores are empty, then true, or if one is empty and the other is not, it is false
     * Then checks if the first team's score is of appropriate hyphen format, if
     * true will check if second team's score is of same hyphen format
     * Otherwise checks if first team's score is of only number format, if true it
     * will check if second team's score is of same only number format
     *
     * @param activityTeamScore score for the team associated with the activity
     * @param otherTeamScore    score for the other team
     * @return 0 (true) if scores are both blank, both have hyphens or both are plain numbers.
     * 1 (false) if mismatch of hyphen score and plain number score, and
     * 2 (false) if one score is left empty while the other is not
     */
    public int validateActivityScore(String activityTeamScore, String otherTeamScore) {
        if (Objects.equals(activityTeamScore, "") && Objects.equals(otherTeamScore, "")) {
            return 0; //return 0
        }
        if (Objects.equals(activityTeamScore, "") || Objects.equals(otherTeamScore, "")) {
            return 2;
        }
        if (activityTeamScore.matches(activityScoreHyphenRegex)) {
            if (otherTeamScore.matches(activityScoreHyphenRegex)) {
                return 0;
            } else {
                return 1;
            }
        } else {
            if (activityTeamScore.matches(activityScoreNumberOnlyRegex)) {
                if (otherTeamScore.matches(activityScoreNumberOnlyRegex)) {
                    return 0;
                } else {
                    return 1;
                }
            }
            return 1;
        }
    }

    /**
     * Returns a list of the last 5 activities that have an outcome
     * @param team the team whose statistics are being returned
     * @return a list of the last 5 activities that are being looked for.
     */
    public List<Activity> getLast5GamesOrFriendliesForTeamWithOutcome(Team team) {
        return activityRepository.getLast5GameOrFriendly(team);
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

    /**
     * Returns a list of all games and friendlies for a team
     * @param team the team whose games and friendlies are being returned
     * @return a list of only the games and friendlies a team has
     */
    public List<Activity> getAllGamesAndFriendliesForTeam(Team team) {return activityRepository.getAllGamesAndFriendliesForTeam(team);}

    /**
     * Get A players total time played for a team across all activities
     * @param user the user whose total time is being requested
     * @param team the team the user is playing for
     * @return total time a player has played for a team
     */
    public long getTotalTimeAUserHasPlayedForATeam(User user, Team team) {
        List<Activity> games = findActivitiesByTeamAndActivityType(team, ActivityType.Game);
        List<Activity> friendlies = findActivitiesByTeamAndActivityType(team, ActivityType.Friendly);
        games.addAll(friendlies);
        long totalTime = 0;
        for (Activity act : games) {
            totalTime += Duration.between(act.getActivityStart(), act.getActivityEnd()).toMinutes();;
        }
        return totalTime;
    }


    public List<User> playersInLineUp(Activity activity) {
        List<LineUp> activityLineups = lineUpRepository.findLineUpByActivityId(activity.getId()).get();

        LineUp lineup = activityLineups.get(0);
        Optional<List<LineUpPosition>> optionaLineupPositions = lineUpPositionRepository.findLineUpPositionsByLineUpLineUpId(lineup.getLineUpId());
        if (optionaLineupPositions.isEmpty()) {
            return List.of();
        }
        List<User> playersInLineUp = optionaLineupPositions.get().stream().map(x -> x.getPlayer()).collect(Collectors.toList());

        return playersInLineUp;
    }

    /**
     * Gets all substitution facts that the given user is a part of (either player on or player off)
     * @param act activity to get substitution facts of
     * @param user user to check if in sub facts
     * @return list of sub facts user is in
     */
    public List<Substitution> subFactsUserIsIn(Activity act, User user) {
        List<Substitution> activitySubstitutions = new ArrayList<>();

        for (Object fact : act.getFactList()) {
            if(fact instanceof Substitution) {
                if (((Substitution) fact).getPlayerOff() == user || ((Substitution) fact).getPlayerOn() == user) {
                    activitySubstitutions.add((Substitution) fact);
                }
            }
        }
        return activitySubstitutions;
    }

    public long getOverallPlayTimeForUserBasedOnSubs(User user, Team team) {
        List<Activity> games = findActivitiesByTeamAndActivityType(team, ActivityType.Game);
        List<Activity> friendlies = findActivitiesByTeamAndActivityType(team, ActivityType.Friendly);
        games.addAll(friendlies);
        long totalTime = 0;
        for (Activity act : games) {
            int listSize = subFactsUserIsIn(act, user).size();
            if (playersInLineUp(act).contains(user)) {
                //user in lineup
                totalTime += Integer.parseInt( subFactsUserIsIn(act, user).get(0).getTimeOfEvent());
                //added time here because user was already player on
                for (int i = 1; i < subFactsUserIsIn(act, user).size() - 1; i += 2) {
                    Substitution sub1 = subFactsUserIsIn(act, user).get(i);
                    Substitution sub2 = subFactsUserIsIn(act, user).get(i + 1);
                    System.out.println("user off   " + "at: " + sub1.getTimeOfEvent() + sub1.getPlayerOff().getFirstName() + " other player ff (user on): " + "at: " + sub2.getTimeOfEvent() + sub2.getPlayerOff().getFirstName());
                    int timeBetweenPlayerOnAndOff = Integer.parseInt(sub2.getTimeOfEvent()) - Integer.parseInt(sub1.getTimeOfEvent());
                    totalTime += timeBetweenPlayerOnAndOff;
                        System.out.println("TIME after next increment " + totalTime);
                }
        } else {
                //not in lineup starting so check for player on
                for (int i = 0; i < subFactsUserIsIn(act, user).size() - 1; i += 2) {
                    Substitution sub1 = subFactsUserIsIn(act, user).get(i);
                    Substitution sub2 = subFactsUserIsIn(act, user).get(i + 1);
                    System.out.println("user on   " + "at: " + sub1.getTimeOfEvent() + sub1.getPlayerOn().getFirstName() + " other player on (user off): " + "at: " + sub2.getTimeOfEvent() + sub2.getPlayerOn().getFirstName());
                    int timeBetweenPlayerOnAndOff = Integer.parseInt(sub2.getTimeOfEvent()) - Integer.parseInt(sub1.getTimeOfEvent());
                    totalTime += timeBetweenPlayerOnAndOff;
                }
            }

            Substitution lastSubFact = subFactsUserIsIn(act, user).get(listSize-1);
            if (lastSubFact.getPlayerOn() == user) {
                System.out.println("Last sub fact is player on so calculating time from subbed on to end of game.");
                totalTime += Duration.between(act.getActivityStart(), act.getActivityEnd()).toMinutes() - Integer.parseInt(subFactsUserIsIn(act, user).get(listSize - 1).getTimeOfEvent());
            }
            System.out.println("act total time:" + totalTime);

        }
        System.out.println("Total time overall for user " + totalTime);
        return totalTime;


    }

    /**
     * Get total matches
     * TODO Make this dependant on the played matches once formation is complete
     * @param team the teams matches wanted
     * @return the total number of played matches.
     */
    public long getTotalUserMatches(Team team) {
        return getAllTeamActivities(team).size();
    }


    public List<Activity> findActivitiesByTeamAndActivityType(Team team, ActivityType activityType) {
        return activityRepository.findActivitiesByTeamAndActivityType(team, activityType);
    }


    /**
     * Sorts given list of goals by time
     * @param goalsList list of goals
     * @return list of goals sorted in ascending time order
     */
    public List<Goal> sortGoalTimesAscending(List<Goal> goalsList) {
        return goalsList.stream()
                .sorted(Comparator.comparingInt(goal -> Integer.parseInt(goal.getTimeOfEvent())))
                .toList();
    }

    /**
     * Checks if given time of fact is within the duration of an activity
     * @param activity activity to get duration
     * @param timeOfFact time of fact to be checked
     * @return true if time of fact is within activity duration, false otherwise
     */
    public boolean checkTimeOfFactWithinActivity(Activity activity, int timeOfFact) {
        return timeOfFact <= Duration.between(activity.getActivityStart(), activity.getActivityEnd()).toMinutes();
    }

}
