package nz.ac.canterbury.seng302.tab.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.entity.Fact.Fact;
import nz.ac.canterbury.seng302.tab.entity.Fact.Goal;

@Service
public class PersonalStatisticImpl implements PersonalStatistics {

    @Autowired
     ActivityService activityService;

    @Autowired
    FactService factService;

    @Override
    public int getMinutesPlayed(User player, Team team) {
        return 0;
    }

    @Override
    public int getGoalsScored(User player, Team team) {
        List<Activity> teamActivities = activityService.getAllTeamActivities(team);
        
        List<Fact> goals = teamActivities.stream()
        .flatMap(activity -> factService.getAllFactsOfGivenTypeForActivity(1, activity).stream())
        .filter(fact -> fact instanceof Goal && ((Goal) fact).getScorer().equals(player))
        .toList();

        return goals.size();
    }

    @Override
    public int getMatchesAppearedIn(User player, Team team){
        return 0;
    }
    
}

