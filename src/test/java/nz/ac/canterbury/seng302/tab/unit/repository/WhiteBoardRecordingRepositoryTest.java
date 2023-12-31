package nz.ac.canterbury.seng302.tab.unit.repository;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.WhiteBoardRecording;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.WhiteBoardRecordingRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Set;

@DataJpaTest
class WhiteBoardRecordingRepositoryTest {

    @Autowired
    private WhiteBoardRecordingRepository repository;

    @Autowired
    private TeamRepository teamRepository;

    @Test
    void getAllRecordings() throws Exception {
        Team team = new Team("Test", "Soccer");
        team = teamRepository.save(team);

        WhiteBoardRecording wb1 = new WhiteBoardRecording("TestOne", team);
        WhiteBoardRecording wb2 = new WhiteBoardRecording("TestTwo", team);
        WhiteBoardRecording wb3 = new WhiteBoardRecording("TestThree", team);
        wb1.setPublic(true);
        wb2.setPublic(true);
        wb3.setPublic(true);
        wb1 = repository.save(wb1);
        wb2 = repository.save(wb2);
        wb3 = repository.save(wb3);

        Pageable page = PageRequest.of(0, 10);
        Set<WhiteBoardRecording> allWBRs = repository.findPublicWhiteboardsByNameAndSport(page, null, List.of()).toSet();
        
        Assertions.assertEquals(3, allWBRs.size(), "The wrong number of whiteboards were returned");
        Assertions.assertEquals(Set.of(wb1, wb2, wb3), allWBRs);
    }

    @Test
    void findRecordingsByName() throws Exception {
        Team team = new Team("Test", "Soccer");
        team = teamRepository.save(team);
        WhiteBoardRecording wb1 = new WhiteBoardRecording("Yes One", team);
        WhiteBoardRecording wb2 = new WhiteBoardRecording("Yes Two", team);
        WhiteBoardRecording wb3 = new WhiteBoardRecording("Nope Three", team);
        wb1.setPublic(true);
        wb2.setPublic(true);
        wb3.setPublic(true);
        wb1 = repository.save(wb1);
        wb2 = repository.save(wb2);
        wb3 = repository.save(wb3);

        Pageable page = PageRequest.of(0, 10);
        Set<WhiteBoardRecording> allWBRs = repository.findPublicWhiteboardsByNameAndSport(page, "yes", List.of()).toSet();
        
        Assertions.assertEquals(2, allWBRs.size(), "The wrong number of whiteboards were returned");
        Assertions.assertEquals(Set.of(wb1, wb2), allWBRs);
    }

    @Test
    void findRecordingsByName_caseInsensitive() throws Exception {
        Team team = new Team("Test", "Soccer");
        team = teamRepository.save(team);
        WhiteBoardRecording wb1 = new WhiteBoardRecording("yes One", team);
        WhiteBoardRecording wb2 = new WhiteBoardRecording("YeS Two", team);
        WhiteBoardRecording wb3 = new WhiteBoardRecording("Nope Three", team);
        wb1.setPublic(true);
        wb2.setPublic(true);
        wb3.setPublic(true);
        wb1 = repository.save(wb1);
        wb2 = repository.save(wb2);
        wb3 = repository.save(wb3);

        Pageable page = PageRequest.of(0, 10);
        Set<WhiteBoardRecording> allWBRs = repository.findPublicWhiteboardsByNameAndSport(page, "YES", List.of()).toSet();
        
        Assertions.assertEquals(2, allWBRs.size(), "The wrong number of whiteboards were returned");
        Assertions.assertEquals(Set.of(wb1, wb2), allWBRs);
    }

    @Test
    void findRecordingsByTeamName() throws Exception {
        Team team1 = new Team("Oranges", "Hockey");
        Team team2 = new Team("Bananas", "Soccer");
        team1 = teamRepository.save(team1);
        team2 = teamRepository.save(team2);

        WhiteBoardRecording wb1 = new WhiteBoardRecording("Yes One", team1);
        WhiteBoardRecording wb2 = new WhiteBoardRecording("Yes Two", team1);
        WhiteBoardRecording wb3 = new WhiteBoardRecording("Nope Three", team2);
        wb1.setPublic(true);
        wb2.setPublic(true);
        wb3.setPublic(true);
        wb1 = repository.save(wb1);
        wb2 = repository.save(wb2);
        wb3 = repository.save(wb3);

        Pageable page = PageRequest.of(0, 10);
        Set<WhiteBoardRecording> allWBRs = repository.findPublicWhiteboardsByNameAndSport(page, "oranges", List.of()).toSet();
        
        Assertions.assertEquals(2, allWBRs.size(), "The wrong number of whiteboards were returned");
        Assertions.assertEquals(Set.of(wb1, wb2), allWBRs);
    }

    @Test
    void findRecordingsByTeamName_multipleTeams() throws Exception {
        Team team1 = new Team("The Apples", "Hockey");
        Team team2 = new Team("The Oranges", "Rugby");
        Team team3 = new Team("Bananas", "Soccer");
        team1 = teamRepository.save(team1);
        team2 = teamRepository.save(team2);
        team3 = teamRepository.save(team3);

        WhiteBoardRecording wb1 = new WhiteBoardRecording("Yes One", team1);
        WhiteBoardRecording wb2 = new WhiteBoardRecording("Yes Two", team2);
        WhiteBoardRecording wb3 = new WhiteBoardRecording("Nope Three", team3);
        wb1.setPublic(true);
        wb2.setPublic(true);
        wb3.setPublic(true);
        wb1 = repository.save(wb1);
        wb2 = repository.save(wb2);
        wb3 = repository.save(wb3);

        Pageable page = PageRequest.of(0, 10);
        Set<WhiteBoardRecording> allWBRs = repository.findPublicWhiteboardsByNameAndSport(page, "The", List.of()).toSet();
        
        Assertions.assertEquals(2, allWBRs.size(), "The wrong number of whiteboards were returned");
        Assertions.assertEquals(Set.of(wb1, wb2), allWBRs);
    }

    @Test
    void findRecordingsBySport() throws Exception {
        Team team1 = new Team("Test1", "Hockey");
        Team team2 = new Team("Test2", "Soccer");
        team1 = teamRepository.save(team1);
        team2 = teamRepository.save(team2);

        WhiteBoardRecording wb1 = new WhiteBoardRecording("Yes One", team1);
        WhiteBoardRecording wb2 = new WhiteBoardRecording("Yes Two", team1);
        WhiteBoardRecording wb3 = new WhiteBoardRecording("Nope Three", team2);
        wb1.setPublic(true);
        wb2.setPublic(true);
        wb3.setPublic(true);
        wb1 = repository.save(wb1);
        wb2 = repository.save(wb2);
        wb3 = repository.save(wb3);

        Pageable page = PageRequest.of(0, 10);
        List<String> sports = List.of("hockey");    // We make it lowercase here because we'd normally do this in the service.
        Set<WhiteBoardRecording> allWBRs = repository.findPublicWhiteboardsByNameAndSport(page, null, sports).toSet();
        
        Assertions.assertEquals(2, allWBRs.size(), "The wrong number of whiteboards were returned");
        Assertions.assertEquals(Set.of(wb1, wb2), allWBRs);
    }

    @Test
    void findRecordingsBySport_multipleSports() throws Exception {
        Team team1 = new Team("Test1", "Hockey");
        Team team2 = new Team("Test2", "Soccer");
        Team team3 = new Team("Test3", "Rugby");
        team1 = teamRepository.save(team1);
        team2 = teamRepository.save(team2);
        team3 = teamRepository.save(team3);

        WhiteBoardRecording wb1 = new WhiteBoardRecording("Yes One", team1);
        WhiteBoardRecording wb2 = new WhiteBoardRecording("Yes Two", team2);
        WhiteBoardRecording wb3 = new WhiteBoardRecording("Nope Three", team3);
        wb1.setPublic(true);
        wb2.setPublic(true);
        wb3.setPublic(true);
        wb1 = repository.save(wb1);
        wb2 = repository.save(wb2);
        wb3 = repository.save(wb3);

        Pageable page = PageRequest.of(0, 10);
        List<String> sports = List.of("hockey", "soccer");    // We make it lowercase here because we'd normally do this in the service.
        Set<WhiteBoardRecording> allWBRs = repository.findPublicWhiteboardsByNameAndSport(page, null, sports).toSet();
        
        Assertions.assertEquals(2, allWBRs.size(), "The wrong number of whiteboards were returned");
        Assertions.assertEquals(Set.of(wb1, wb2), allWBRs);
    }

    @Test
    void findRecordingsBySport_noMatchingSports_returnsNothing() throws Exception {
        Team team1 = new Team("Test1", "Hockey");
        Team team2 = new Team("Test2", "Soccer");
        Team team3 = new Team("Test3", "Rugby");
        team1 = teamRepository.save(team1);
        team2 = teamRepository.save(team2);
        team3 = teamRepository.save(team3);

        WhiteBoardRecording wb1 = new WhiteBoardRecording("Yes One", team1);
        WhiteBoardRecording wb2 = new WhiteBoardRecording("Yes Two", team2);
        WhiteBoardRecording wb3 = new WhiteBoardRecording("Nope Three", team3);
        wb1.setPublic(true);
        wb2.setPublic(true);
        wb3.setPublic(true);
        wb1 = repository.save(wb1);
        wb2 = repository.save(wb2);
        wb3 = repository.save(wb3);

        Pageable page = PageRequest.of(0, 10);
        List<String> sports = List.of("swimming");    // We make it lowercase here because we'd normally do this in the service.
        Set<WhiteBoardRecording> allWBRs = repository.findPublicWhiteboardsByNameAndSport(page, null, sports).toSet();
        
        Assertions.assertEquals(0, allWBRs.size(), "The wrong number of whiteboards were returned");
        Assertions.assertEquals(Set.of(), allWBRs);
    }

    @Test
    void findRecordingsByNameAndSport() throws Exception {
        Team team1 = new Team("Test1", "Hockey");
        Team team2 = new Team("Test2", "Soccer");
        team1 = teamRepository.save(team1);
        team2 = teamRepository.save(team2);

        WhiteBoardRecording wb1 = new WhiteBoardRecording("Yes One", team1);
        WhiteBoardRecording wb2 = new WhiteBoardRecording("No Two", team1);
        WhiteBoardRecording wb3 = new WhiteBoardRecording("Yes but actually no Three", team2);
        WhiteBoardRecording wb4 = new WhiteBoardRecording("No Four", team2);
        wb1.setPublic(true);
        wb2.setPublic(true);
        wb3.setPublic(true);
        wb4.setPublic(true);
        wb1 = repository.save(wb1);
        wb2 = repository.save(wb2);
        wb3 = repository.save(wb3);
        wb4 = repository.save(wb4);

        Pageable page = PageRequest.of(0, 10);
        List<String> sports = List.of("hockey");    // We make it lowercase here because we'd normally do this in the service.
        List<WhiteBoardRecording> allWBRs = repository.findPublicWhiteboardsByNameAndSport(page, "Yes", sports).toList();
        
        Assertions.assertEquals(1, allWBRs.size(), "The wrong number of whiteboards were returned");
        Assertions.assertEquals(List.of(wb1), allWBRs);
    }

    @Test
    void findRecordingsByNameAndSport_multipleSports_returnsAnyWithSportAndName() throws Exception {
        Team team1 = new Team("Test1", "Hockey");
        Team team2 = new Team("Test2", "Soccer");
        Team team3 = new Team("Test3", "Rugby");
        team1 = teamRepository.save(team1);
        team2 = teamRepository.save(team2);
        team3 = teamRepository.save(team3);

        WhiteBoardRecording wb1 = new WhiteBoardRecording("Yes One", team1);
        WhiteBoardRecording wb2 = new WhiteBoardRecording("Yes but actually no Two", team2);
        WhiteBoardRecording wb3 = new WhiteBoardRecording("Yes actually Three", team3);
        WhiteBoardRecording wb4 = new WhiteBoardRecording("No Four", team1);
        wb1.setPublic(true);
        wb2.setPublic(true);
        wb3.setPublic(true);
        wb4.setPublic(true);
        wb1 = repository.save(wb1);
        wb2 = repository.save(wb2);
        wb3 = repository.save(wb3);
        wb4 = repository.save(wb4);

        Pageable page = PageRequest.of(0, 10);
        List<String> sports = List.of("hockey", "rugby");    // We make it lowercase here because we'd normally do this in the service.
        List<WhiteBoardRecording> allWBRs = repository.findPublicWhiteboardsByNameAndSport(page, "yes", sports).toList();
        
        Assertions.assertEquals(2, allWBRs.size(), "The wrong number of whiteboards were returned");
        Assertions.assertEquals(List.of(wb1, wb3), allWBRs);
    }

}
