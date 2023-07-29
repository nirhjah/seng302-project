package nz.ac.canterbury.seng302.tab.unit.service;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import nz.ac.canterbury.seng302.tab.entity.competition.TeamCompetition;
import nz.ac.canterbury.seng302.tab.repository.CompetitionRepository;
import nz.ac.canterbury.seng302.tab.repository.LocationRepository;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.CompetitionService;
import nz.ac.canterbury.seng302.tab.service.TeamService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import java.util.*;
import org.junit.jupiter.api.Assertions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(CompetitionService.class)
public class CompetitionServiceTest {
  Logger logger = LoggerFactory.getLogger(CompetitionService.class);

  @Autowired
  private CompetitionService competitionService;

  @Test 
  public void testGettingAllCompetitions() throws Exception {
	  Competition competition = new TeamCompetition("Test", "U10", "football");
    competitionService.updateOrAddCompetition(competition);

	  List<Competition> expectedCompetitions = new ArrayList<Competition>();
	  expectedCompetitions.add(competition);

    Assertions.assertEquals(competitionService.findCompetitionById(1L).get(), competition);
  }
  
  // @Test
  // public void testGettingAllTeamCompetitions() throws Exception {
  //   // given i have two 
  //   Location location = new Location("1 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand");
  //   locationRepository.save(location);
  //   Team team = new Team("test", "Hockey", location);
  //   teamRepository.save(team);
	 //  User user = new User("test", "test", "test@gmail.com", "1", location);
	 //  userRepository.save(user);
	 //  Set<User> users = new HashSet<User>();
	 //  users.add(user);
	 //  
	 //  Competition competition = new TeamCompetition("Test", users, "U10", "football", team);
  //   competitionService.updateOrAddCompetition(competition);
  //
	 //  List<Competition> expectedCompetitions = new ArrayList<Competition>();
	 //  expectedCompetitions.add(competition);
	 //  List<Competition> actualCompetitions = competitionService.getAllTeamCompetitions();
  //
	 //  assertEquals(expectedCompetitions, actualCompetitions);
  // }
}
