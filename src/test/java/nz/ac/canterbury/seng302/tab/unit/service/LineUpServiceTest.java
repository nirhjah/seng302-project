package nz.ac.canterbury.seng302.tab.unit.service;

import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUp;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUpPosition;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import nz.ac.canterbury.seng302.tab.repository.ActivityRepository;
import nz.ac.canterbury.seng302.tab.repository.LineUpPositionRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.LineUpService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.*;

@DataJpaTest
@Import(LineUpService.class)
public class LineUpServiceTest {

    @Autowired
    private LineUpService lineUpService;

    private LineUp lineUp;

    private LineUp lineUp2;


    private Formation formation;

    private Formation formation2;

    @Autowired
    private LineUpPositionRepository lineUpPositionRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private ActivityRepository activityRepository;

    @Test
    void testFindLineUpByActivityAndFormation() throws Exception {
        Team team = new Team("adfds", "hello");
        formation = new Formation("4-5-6", team);

        User u = new User("Test", "Account", "tab.team900@gmail.com", "password",
                new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));
        Activity game = new Activity(ActivityType.Game, team, "A Test Game",
                LocalDateTime.of(2026, 1,1,6,30),
                LocalDateTime.of(2026, 1,1,8,30), u,
                new Location("Jack Erskine", null, "Ilam", "Chch", "Test", "NZ"));
        game.setFormation(formation);
        activityRepository.save(game);
        lineUp = new LineUp(game.getFormation().get(), team, game);
        lineUpService.updateOrAddLineUp(lineUp);
        Assertions.assertEquals(lineUp, lineUpService.findLineUpByActivityAndFormation(game.getId(), formation));
    }


    @Test
    void testGettingLineupsForTeamPerFormation() throws Exception {
        Team team = new Team("adfds", "hello");
        formation = new Formation("4-5-6", team);
        formation2 = new Formation("1-2-3", team);

        User u = new User("Test", "Account", "tab.team900@gmail.com", "password",
                new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));
        Activity game = new Activity(ActivityType.Game, team, "A Test Game",
                LocalDateTime.of(2026, 1,1,6,30),
                LocalDateTime.of(2026, 1,1,8,30), u,
                new Location("Jack Erskine", null, "Ilam", "Chch", "Test", "NZ"));
        game.setFormation(formation);
        activityRepository.save(game);
        lineUp = new LineUp(game.getFormation().get(), team, game);
        lineUpService.updateOrAddLineUp(lineUp);
        game.setFormation(formation2);
        lineUp2 = new LineUp(game.getFormation().get(), team, game);
        lineUpService.updateOrAddLineUp(lineUp2);

        Map<Formation, LineUp> expectedFormationLineUpMap = new HashMap<>();

        expectedFormationLineUpMap.put(formation, lineUp);
        expectedFormationLineUpMap.put(formation2, lineUp2);


        Assertions.assertEquals(expectedFormationLineUpMap, lineUpService.getLineUpsForTeam(team, game));

    }

    @Test
    void testGetFormationAndPlayersAndPosition_ValidFormationAndLineupAndSubs() throws Exception {

        Team team = new Team("adfds", "hello");
        formation = new Formation("4-5-6", team);

        User u = new User("Test", "Account", "tab.team900@gmail.com", "password",
                new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));
        User player1 = new User("bob", "Account", "player1@gmail.com", "password",
                new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));
        User player2 = new User("sally", "Account", "player2@gmail.com", "password",
                new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));
        User sub1 = new User("bill", "Account", "sub1@gmail.com", "password",
                new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));
        userRepository.save(player1);
        userRepository.save(player2);
        userRepository.save(sub1);

        player1.joinTeam(team);
        player2.joinTeam(team);
        sub1.joinTeam(team);

        Activity game = new Activity(ActivityType.Game, team, "A Test Game",
                LocalDateTime.of(2026, 1,1,6,30),
                LocalDateTime.of(2026, 1,1,8,30), u,
                new Location("Jack Erskine", null, "Ilam", "Chch", "Test", "NZ"));
        game.setFormation(formation);
        activityRepository.save(game);
        lineUp = new LineUp(game.getFormation().get(), team, game);
        lineUp.setLineUpName("lineup1");
        lineUpService.updateOrAddLineUp(lineUp);
        LineUpPosition lineUpPosition = new LineUpPosition(lineUp, player1, 1);
        LineUpPosition lineUpPosition2 = new LineUpPosition(lineUp, player2, 2);

        lineUpPositionRepository.save(lineUpPosition);
        lineUpPositionRepository.save(lineUpPosition2);
        lineUp.getSubs().add(sub1);

        Map<Long, List<List<Object>>> expectedformationAndPlayersAndPosition = new HashMap<>();
        List<List<Object>> expectedplayersAndPosition = new ArrayList<>();
        List<Object> expectedsubsInfo = new ArrayList<>();

        List<Object> playerInfo1 = Arrays.asList((long) lineUpPosition.getPosition(), player1.getId(), player1.getFirstName());
        List<Object> playerInfo2 = Arrays.asList((long) lineUpPosition2.getPosition(), player2.getId(), player2.getFirstName());
        expectedplayersAndPosition.add(playerInfo1);
        expectedplayersAndPosition.add(playerInfo2);
        List<Object> specificPlayerSubInfo = Arrays.asList(sub1.getId(), sub1.getFirstName());
        expectedsubsInfo.add(specificPlayerSubInfo);
        expectedplayersAndPosition.add(List.of("lineup1"));

        expectedplayersAndPosition.add(expectedsubsInfo);

        expectedformationAndPlayersAndPosition.put(formation.getFormationId(), expectedplayersAndPosition);

        Assertions.assertEquals(expectedformationAndPlayersAndPosition, lineUpService.getFormationAndPlayersAndPosition(game));


    }

    @Test
    void testGetFormationAndPlayersAndPosition_FormationNoLineup_ReturnEmptyMap() throws Exception {

        Team team = new Team("adfds", "hello");
        formation = new Formation("4-5-6", team);

        User u = new User("Test", "Account", "tab.team900@gmail.com", "password",
                new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));

        Activity game = new Activity(ActivityType.Game, team, "A Test Game",
                LocalDateTime.of(2026, 1,1,6,30),
                LocalDateTime.of(2026, 1,1,8,30), u,
                new Location("Jack Erskine", null, "Ilam", "Chch", "Test", "NZ"));
        game.setFormation(formation);
        activityRepository.save(game);

        Map<Long, List<List<Object>>> expectedformationAndPlayersAndPosition = new HashMap<>();

        Assertions.assertEquals(expectedformationAndPlayersAndPosition, lineUpService.getFormationAndPlayersAndPosition(game));

    }

    @Test
    void testSaveLineUp_ValidPlayersAndPositions() throws Exception {
        List<String> validPositionsAndPlayers = Arrays.asList("1 1", "2 2");
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "createActivityForm");

        Team team = new Team("adfds", "hello");
        Formation formation = new Formation("4-5-6", team);

        User u = new User("Test", "Account", "tab.team900@gmail.com", "password",
                new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));

        Activity game = new Activity(ActivityType.Game, team, "A Test Game",
                LocalDateTime.of(2026, 1,1,6,30),
                LocalDateTime.of(2026, 1,1,8,30), u,
                new Location("Jack Erskine", null, "Ilam", "Chch", "Test", "NZ"));
        game.setFormation(formation);
        activityRepository.save(game);

        LineUp lineUp = new LineUp(game.getFormation().get(), team, game);
        lineUpService.updateOrAddLineUp(lineUp);

        lineUpService.saveLineUp(validPositionsAndPlayers, bindingResult, lineUp);

        Assertions.assertFalse(bindingResult.hasErrors());
    }

    @Test
    void testSaveLineUp_InvalidPlayersAndPositions() throws Exception {
        List<String> invalidPositionsAndPlayers = Arrays.asList("1 X", "2 2");
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "createActivityForm");

        Team team = new Team("adfds", "hello");
        Formation formation = new Formation("4-5-6", team);

        User u = new User("Test", "Account", "tab.team900@gmail.com", "password",
                new Location("1 Place", "B", "Ilam", "CHCH", "808", "NZ"));

        Activity game = new Activity(ActivityType.Game, team, "A Test Game",
                LocalDateTime.of(2026, 1,1,6,30),
                LocalDateTime.of(2026, 1,1,8,30), u,
                new Location("Jack Erskine", null, "Ilam", "Chch", "Test", "NZ"));
        game.setFormation(formation);

        LineUp lineUp = new LineUp(game.getFormation().get(), team, game);
        activityRepository.save(game);
        lineUpService.updateOrAddLineUp(lineUp);

        lineUpService.saveLineUp(invalidPositionsAndPlayers, bindingResult, lineUp);

        Assertions.assertTrue(bindingResult.hasErrors());
    }


}
