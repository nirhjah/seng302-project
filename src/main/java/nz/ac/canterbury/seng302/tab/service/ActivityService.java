package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
