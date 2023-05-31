package nz.ac.canterbury.seng302.tab.service;


import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.ActivityStatistics;
import nz.ac.canterbury.seng302.tab.repository.ActivityStatisticRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityStatisticService {

    @Autowired
    ActivityStatisticRepository activityStatisticRepository;

    public List<ActivityStatistics> findAll() {return activityStatisticRepository.findAll();}


    /**
     * Gets activity statistic by given activity statistic ID
     * @param activityStatisticsId the ID of the activity statistic entity
     * @return activity statistic
     */
    public ActivityStatistics getActivityStatistics(long activityStatisticsId) {
        return activityStatisticRepository.findById(activityStatisticsId).orElse(null);
    }

    /**
     * Gets activity statistic by the activity
     * @param activity activity get activity statistic from
     * @return activity statistic
     */
    public ActivityStatistics getActivityStatisticByActivity(Activity activity) {
        return activityStatisticRepository.findActivityStatisticsByActivity(activity);
    }


}

