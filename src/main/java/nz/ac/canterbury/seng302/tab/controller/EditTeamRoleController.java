package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.enums.Role;
import nz.ac.canterbury.seng302.tab.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.TeamRole;
import nz.ac.canterbury.seng302.tab.service.TeamService;

/**
 * Spring Boot Controller class for the edit team role class
 */
@Controller
public class EditTeamRoleController {
  Logger logger = LoggerFactory.getLogger(HomeFormController.class);

  @Autowired
  private TeamService teamService;

  @Autowired
  private UserService userService;

  /**
   * Takes the user to the edit ream roles page
   * @return the edit team role page
   */
  @GetMapping("/editTeamRole")
  public String getTeamRoles(
          @RequestParam(name = "edit", required = true) Long teamID,
          Model model,
          HttpServletRequest request)
      throws Exception {
    logger.info("GET /getTeamRoles");
    Optional<User> user = userService.getCurrentUser();

    if (user.isEmpty()) {
      logger.error("No current user?");
      return "redirect:/home";
    }

    Team team = teamService.getTeam(teamID);
    if (team == null) {
      logger.error("Team ID does not exist!");
      return "redirect:/home";
    }

    model.addAttribute("user", user.get());
    model.addAttribute("httpServletRequest", request);
    populateListsInModel(team, model);
    return "editTeamRoleForm";
  }


  /**
   *  In this PostMapping, we pass in the userRoles and userIds.
   *  The userId in `userIds` maps DIRECTLY to the role in `userRoles`, per each index.
   *  ------------
   *  For example, if userId 5 exists at index 0 of userIds, then user-5 will
   *  be assigned to the role at index 0 of userRoles.
   */
  @PostMapping("/editTeamRole")
  public String editTeamRoles(
          @RequestParam(name = "teamID", required = true) String teamID,
          @RequestParam("userRoles") List<String> userRoles,
          @RequestParam("userIds") List<String> userIds,
          Model model,
          HttpServletRequest request)
          throws Exception {
    logger.info("GET /EditTeamRole");
    logger.info(userRoles.toString());
    logger.info(userIds.toString());

    Team team = teamService.getTeam(Long.parseLong(teamID));
    if (team == null) {
      logger.error("Team ID does not exist!");
      return "redirect:/home";
    }

    int len = Math.min(userRoles.size(), userIds.size());
    for (int i=0; i < len; i++) {
      // userIds list maps directly to userRoles list, per index.
      updateRole(team, userIds.get(i), userRoles.get(i));
    }
    teamService.updateTeam(team);

    model.addAttribute("httpServletRequest", request);
    populateListsInModel(team, model);
    return "editTeamRoleForm";
  }

  private void updateRole(Team team, String userId, String userRole) {
    long id;
    try {
      id = Long.parseLong(userId);
    } catch (NumberFormatException ex) {
      logger.error("unable to parse user id???");
      return;
    }

    if (Role.isValidRole(userRole)) {
      Role role = Role.stringToRole(userRole);
      Optional<User> user = userService.findUserById(id);
      if (user.isPresent()) {
        team.setRole(user.get(), role);
      } else {
        logger.error("Unknown user whilst changing roles: " + id);
      }
    }
  }

  public void populateListsInModel(Team team, Model model) {
    Set<TeamRole> teamRoles = team.getTeamRoles();
    List<Long> userIDList = new ArrayList<>();
    for (TeamRole role : teamRoles) {
      userIDList.add(role.getUser().getUserId());
    }

    model.addAttribute("roleList", teamRoles);
    model.addAttribute("userIds", userIDList);
    model.addAttribute("possibleRoles", Role.values());
    model.addAttribute("teamID", team.getTeamId().toString());
  }
}
