package nz.ac.canterbury.seng302.tab.unit.service;

import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import nz.ac.canterbury.seng302.tab.entity.competition.TeamCompetition;
import nz.ac.canterbury.seng302.tab.service.CompetitionService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import java.util.*;
import org.junit.jupiter.api.Assertions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootTest
@Import(CompetitionService.class)
public class CompetitionServiceTest {
  Logger logger = LoggerFactory.getLogger(CompetitionService.class);

  @Autowired
  private CompetitionService competitionService;

  @Test 
  public void testGettingAllCompetitions() throws Exception {
	  Competition competition1 = new TeamCompetition("Test1", "U10", "football");
	  Competition competition2 = new TeamCompetition("Test2", "U10", "football");
    competitionService.updateOrAddCompetition(competition1);
    competitionService.updateOrAddCompetition(competition2);

    List<Competition> allCompetitions = competitionService.findAll();

    Assertions.assertEquals(3, allCompetitions.size());
    Assertions.assertTrue(allCompetitions.contains(competition1));
    Assertions.assertTrue(allCompetitions.contains(competition2));
  }
  
  @Test
  public void testFindingCompetitionById() throws Exception {
    Competition competition = new TeamCompetition("Test", "U10", "football");
    competition = competitionService.updateOrAddCompetition(competition);

    Optional<Competition> foundCompetition = competitionService.findCompetitionById(competition.getCompetitionId());

    Assertions.assertTrue(foundCompetition.isPresent());
    Assertions.assertEquals(competition.getCompetitionId(), foundCompetition.get().getCompetitionId());
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
