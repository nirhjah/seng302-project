package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
     * @return list of all stored activities
     */
    public List<Activity> findAll() {return activityRepository.findAll();}

    /**
     * Finds activity based on its id
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
     * @param activity - to be stored/updated
     * @return The stored activity entity
     */
    public Activity updateOrAddActivity(Activity activity) {return activityRepository.save(activity); }

    /**
     * Checks that the activity is scheduled for after a team's creation.
     * @param activity the activity
     * @return true if activity is scheduled after team creation
     */
    public boolean validateActivityDateTime(LocalDateTime teamCreation, LocalDateTime startActivity, LocalDateTime endActivity) {
        return teamCreation.isBefore(startActivity) && teamCreation.isBefore(endActivity);
    }

    /**
     * Checks that the start of activity is before the end of the activity
     * @param activity - the activity
     * @return true if the end of activity is after the start
     */
    public boolean validateStartAndEnd(LocalDateTime startActivity, LocalDateTime endActivity) {
        return startActivity.isBefore(endActivity);
    }

    /**
     * Checks that the team selection based on what activity type is selected
     * @param type the type of activity
     * @param team the team selected
     * @return true if the type is game or friendly and there is a team, or if type is anything but game and friendly
     */
    public boolean validateTeamSelection(Activity.ActivityType type, Team team) {
        if ((type == Activity.ActivityType.Game || type== Activity.ActivityType.Friendly) && team==null) {
            return false;
        } else {
            return true;
        }
    }
}
