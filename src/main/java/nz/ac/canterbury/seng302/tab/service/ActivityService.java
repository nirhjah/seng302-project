package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service class for Activity
 */
@Service
public class ActivityService {

    @Autowired
    ActivityRepository activityRepository;

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
     * @param activity the activity
     * @return true if activity is scheduled after team creation
     */
    public boolean validateActivityDateTime(Activity activity) {
        return activity.getTeam().getCreationDate().isBefore(activity.getActivityStart()) &&
                activity.getTeam().getCreationDate().isBefore(activity.getActivityEnd());
    }

    /**
     * Checks that the start of activity is before the end of the activity
     * 
     * @param activity - the activity
     * @return true if the end of activity is after the start
     */
    public boolean validateStartAndEnd(Activity activity) {
        return activity.getActivityStart().isBefore(activity.getActivityEnd());
    }

    // /**
    // *
    // * @param team - the team
    // * @return list of the teams activities
    // **/
    // public List<Activity> getAllTeamActivities(Team team) {
    // return activityRepository.findByTeam(team);
    // }

    public Page<Activity> getAllTeamActivitiesPage(Team team, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return activityRepository.findActivityByTeam(team, pageable);

    }
}
