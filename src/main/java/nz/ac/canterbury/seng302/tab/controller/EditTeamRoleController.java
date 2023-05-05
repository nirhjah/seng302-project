package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.enums.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import nz.ac.canterbury.seng302.tab.entity.Location;
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

  /**
   * Takes the user to the edit ream roles page
   * @return the edit team role page
   */
  @GetMapping("/editTeamRole")
  public String home(@RequestParam(name = "edit", required = true) Long teamID, Model model, HttpServletRequest request)
      throws Exception {
    logger.info("GET /EditTeamRole");

    /**
     * This is dummy data for the front end for testing
     * TODO remove when the backend is implemented
     */
    User user1 = new User("john", "doe", "1test@test.com", "Password1",
        new Location(null, null, null, "chch", null, "nz"));
    User user2 = new User("dave", "doe", "2test@test.com", "Password1",
        new Location(null, null, null, "chch", null, "nz"));
    User user3 = new User("seve", "doe", "3test@test.com", "Password1",
        new Location(null, null, null, "chch", null, "nz"));
    User user4 = new User("john", "doe", "4test@test.com", "Password1",
            new Location(null, null, null, "chch", null, "nz"));
    User user5 = new User("dave", "doe", "5test@test.com", "Password1",
            new Location(null, null, null, "chch", null, "nz"));
    User user6 = new User("seve", "doe", "6test@test.com", "Password1",
            new Location(null, null, null, "chch", null, "nz"));
    Team team = new Team("test team", "football", new Location(null, null, null, "chch", null, "nz"), user1);

    team.setMember(user2);
    team.setCoach(user3);
    team.setMember(user4);
    team.setManager(user5);
    team.setMember(user6);

    List<TeamRole> teamRoles = team.getTeamRoleList();

    model.addAttribute("possibleRoles", Role.values());
    model.addAttribute("roleList", teamRoles);
    model.addAttribute("httpServletRequest", request);
    return "editTeamRoleForm";
  }

}
