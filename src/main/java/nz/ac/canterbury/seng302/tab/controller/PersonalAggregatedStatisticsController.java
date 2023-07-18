package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.entity.Fact.Goal;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        Optional<User> user = userService.getCurrentUser();
        model.addAttribute("firstName", user.get().getFirstName());
        model.addAttribute("lastName", user.get().getLastName());
        model.addAttribute("displayPicture", user.get().getPictureString());
        model.addAttribute("navTeams", teamService.getTeamList());
        List<Team> allUserTeams = teamService.findTeamsWithUser(user.get());
        List<Team> teamList = new ArrayList<>();
        for (Team team : allUserTeams) {
            if (team.isManager(user.get()) || team.isCoach(user.get())) {
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
        Optional<User> user = userService.getCurrentUser();
        List<Team> teams = teamService.findTeamsWithUser(user.get());
        List<UserTeamStatistic> userStats = new ArrayList<>();
        for (Team team : teams) {
            userStats.add(new UserTeamStatistic(team,
                    factService.getTotalGoalsForTeamPerUser(user.get(), team),
                    activityService.getTotalTimeAUserHasPlayedForATeam(user.get(), team),
                    activityService.getTotalUserMatches(team)));
        }
        model.addAttribute("teamStats", userStats);
        return "userTeamSummaryStats";
    }

    @GetMapping("/myActivityStats")
    public String activityPersonalStats(Model model, HttpServletRequest httpServletRequest, @RequestParam(value = "teamID") Long teamID) throws MalformedURLException {
        prefillModel(model, httpServletRequest);
        model.addAttribute("httpServletRequest", httpServletRequest);
        Team team = teamService.getTeam(teamID);
        User user = userService.getCurrentUser().get();

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
