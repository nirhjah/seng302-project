package nz.ac.canterbury.seng302.tab.service;


import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.ActivityStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityStatisticService {

    @Autowired
    ActivityStatisticService activityStatisticRepository;

    public List<Activity> findAll() {return activityStatisticRepository.findAll();}


    public ActivityStatistics findActivityStatisticById(long activityStatisticID) {
        return activityStatisticRepository.findActivityStatisticById(activityStatisticID);
    }

}

