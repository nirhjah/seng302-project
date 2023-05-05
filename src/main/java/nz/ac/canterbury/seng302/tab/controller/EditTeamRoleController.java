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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.TeamRole;
import nz.ac.canterbury.seng302.tab.service.TeamService;

import java.util.Optional;
import java.util.Set;

/**
 * Spring Boot Controller class for the Home Form class.
 */
@Controller
public class EditTeamRoleController {
  Logger logger = LoggerFactory.getLogger(HomeFormController.class);

  @Autowired
  private TeamService teamService;

  /**
   * Redirects GET default url '/' to '/home'
   *
   * @return redirect to /home
   */
  @GetMapping("/editTeamRole")
  public String home(@RequestParam(name = "edit", required = true) Long teamID, Model model, HttpServletRequest request)
      throws Exception {
    logger.info("GET /EditTeamRole");

    Team team = teamService.getTeam(teamID);

    if (team == null) {
      logger.error("Team ID does not exist!");
      return "redirect:/home";
    }

    List<TeamRole> teamRoles = team.getTeamRoleList();

    model.addAttribute("possibleRoles", Role.values());
    logger.info("POSSIBLE ROLES =" + Role.values().toString());
    model.addAttribute("roleList", teamRoles);
    logger.info("ROLES LIST =" + teamRoles);
    model.addAttribute("httpServletRequest", request);
    return "editTeamRoleForm";
  }
}
