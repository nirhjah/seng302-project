package nz.ac.canterbury.seng302.tab.service;


import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.ActivityStatistics;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.ActivityStatisticRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityStatisticService {

    @Autowired
    ActivityStatisticRepository activityStatisticRepository;

    public List<ActivityStatistics> findAll() {return activityStatisticRepository.findAll();}

    
    public ActivityStatistics getActivityStatistics(long activityStatisticsId) {
        return activityStatisticRepository.findById(activityStatisticsId).orElse(null);
    }


}

