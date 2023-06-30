package nz.ac.canterbury.seng302.tab.unit.service;

import nz.ac.canterbury.seng302.tab.entity.Club;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.service.ClubService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@Import(ClubService.class)
public class ClubServiceTest {

    @Autowired
    private ClubService clubService;

    Location location = new Location(null, null, null, "Christchurch", null,
            "New Zealand");

    @Test
    public void testFindClubById() throws IOException {
        Club club = new Club("Rugby Club", location);
        clubService.updateOrAddClub(club);
        Assertions.assertEquals(clubService.findClubById(1L), club);
    }

    @Test
    public void testUpdatingOrAddingClub() throws IOException {
        Club club = new Club("Rugby Club", location);
        clubService.updateOrAddClub(club);
        Assertions.assertEquals(clubService.findAll().size(), 1);
    }

    @Test
    public void ifTeamsHaveDifferentSports_returnFalse() throws IOException {

        Team team1 = new Team("team1", "Rugby");
        Team team2 = new Team("Team2", "Rugby");
        Team team3 = new Team("Team3", "Soccer");
        Team team4 = new Team("Team4", "Tennis");
        Team team5 = new Team("Team5", "Hockey");

        List<Team> teamsToAdd = new ArrayList<>();
        teamsToAdd.add(team1);
        teamsToAdd.add(team2);
        teamsToAdd.add(team3);
        teamsToAdd.add(team4);
        teamsToAdd.add(team5);

        Assertions.assertFalse(clubService.validateTeamSportsinClub(teamsToAdd));
    }

    @Test
    public void ifTeamsHaveSameSports_returnTrue() throws IOException {

        Team team1 = new Team("team1", "Rugby");
        Team team2 = new Team("Team2", "Rugby");
        Team team3 = new Team("Team3", "Rugby");

        List<Team> teamsToAdd = new ArrayList<>();
        teamsToAdd.add(team1);
        teamsToAdd.add(team2);
        teamsToAdd.add(team3);

        Assertions.assertTrue(clubService.validateTeamSportsinClub(teamsToAdd));
    }



}
