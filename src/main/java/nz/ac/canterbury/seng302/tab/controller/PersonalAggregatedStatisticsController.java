package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import nz.ac.canterbury.seng302.tab.service.ActivityService;
import nz.ac.canterbury.seng302.tab.service.FactService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PersonalAggregatedStatisticsController {

    @Autowired
    UserService userService;

    @Autowired
    TeamService teamService;

    @Autowired
    ActivityService activityService;

    @Autowired
    FactService factService;

    /**
     * Prefills the model with required values
     * @param model the model to be filled
     * @param httpServletRequest the request
     * @throws MalformedURLException can be thrown by getting the path if invalid
     */
    public void prefillModel(Model model, HttpServletRequest httpServletRequest) throws MalformedURLException {
        User user = userService.getCurrentUser().orElseThrow();
        List<Team> allUserTeams = teamService.findTeamsWithUser(user);
        List<Team> teamList = new ArrayList<>();
        for (Team team : allUserTeams) {
            if (team.isManager(user) || team.isCoach(user)) {
                teamList.add(team);
            }
        }
        model.addAttribute("teamList", teamList);
        model.addAttribute("activityTypes", ActivityType.values());
        URL url = new URL(httpServletRequest.getRequestURL().toString());
        String path = (url.getPath() + "/..");
        model.addAttribute("path", path);
    }

    @GetMapping("/myStats")
    public String personalStatistics(Model model, HttpServletRequest httpServletRequest) throws MalformedURLException {
        prefillModel(model, httpServletRequest);
        model.addAttribute("httpServletRequest", httpServletRequest);
        User user = userService.getCurrentUser().orElseThrow();
        List<Team> teams = teamService.findTeamsWithUser(user);
        List<UserTeamStatistic> userStats = new ArrayList<>();
        for (Team team : teams) {
            userStats.add(new UserTeamStatistic(team,
                    factService.getTotalGoalsForTeamPerUser(user, team),
                    activityService.getTotalTimeAUserHasPlayedForATeam(user, team),
                    activityService.getTotalUserMatches(team)));
        }
        model.addAttribute("teamStats", userStats);
        return "userTeamSummaryStats";
    }

    @GetMapping("/myActivityStats")
    public String activityPersonalStats(Model model, HttpServletRequest httpServletRequest,
                                        @RequestParam(value = "teamID") Long teamID) throws MalformedURLException {
        prefillModel(model, httpServletRequest);
        model.addAttribute("httpServletRequest", httpServletRequest);
        Team team = teamService.getTeam(teamID);
        User user = userService.getCurrentUser().orElseThrow();

        List<Activity> activities = activityService.getAllTeamActivities(team);
        List<UserTeamActivityStatistic> userTeamActStats = new ArrayList<>();
        for (Activity act : activities) {
            userTeamActStats.add(new UserTeamActivityStatistic(
                    factService.getGoalsForActivityForPlayer(act, user),
                    factService.getTimePlayed(act),
                    factService.getUserSubOffForActivity(user, act),
                    factService.getUserSubOnsForActivity(user, act),
                    act
            ));
        }
        model.addAttribute("team", team);
        model.addAttribute("actStats", userTeamActStats);
        return "userActivityTeamStats";
    }


}
