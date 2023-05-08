package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.enums.Role;
import nz.ac.canterbury.seng302.tab.form.EditTeamRolesForm;
import nz.ac.canterbury.seng302.tab.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.TeamRole;
import nz.ac.canterbury.seng302.tab.service.TeamService;

import java.util.Optional;
import java.util.Set;

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
    model.addAttribute("user", user.get());

    Team team = teamService.getTeam(teamID);
    if (team == null) {
      logger.error("Team ID does not exist!");
      return "redirect:/home";
    }

    model.addAttribute("httpServletRequest", request);
    populateListsInModel(team, model);
    return "editTeamRoleForm";
  }


  /**
   * TODO: We need to think of how best to pass in the new team roles.
   * I have created a rolesForm
   */
  @PostMapping("/editTeamRole")
  public String editTeamRoles(
          @RequestParam(name = "teamID", required = true) String teamID,
          @RequestParam("tags") List<String> tags,
          @RequestParam("userIds") List<String> userIds,
          Model model,
          HttpServletRequest request)
          throws Exception {
    logger.info("GET /EditTeamRole");
    logger.info(tags.toString());

    Team team = teamService.getTeam(Long.parseLong(teamID));
    if (team == null) {
      logger.error("Team ID does not exist!");
      return "redirect:/home";
    }

    model.addAttribute("httpServletRequest", request);
    populateListsInModel(team, model);
    return "editTeamRoleForm";
  }

  public void populateListsInModel(Team team, Model model) {
    List<TeamRole> teamRoles = team.getTeamRoleList();
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
