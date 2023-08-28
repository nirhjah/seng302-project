
package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import nz.ac.canterbury.seng302.tab.entity.competition.TeamCompetition;
import nz.ac.canterbury.seng302.tab.entity.competition.UserCompetition;
import nz.ac.canterbury.seng302.tab.repository.CompetitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Spring Boot Service class for Compettiion repository
 */
@Service
public class CompetitionService {
    Logger logger = LoggerFactory.getLogger(getClass());

    private final CompetitionRepository competitionRepository;

    private static final List<String> EMPTY_LIST = List.of();

    @Autowired
    public CompetitionService(CompetitionRepository competitionRepository) {
      this.competitionRepository = competitionRepository;
    }

    /**
     * Gets list of all competitions 
     * @return list of all competitons
     */
    public List<Competition> findAll() {
        return competitionRepository.findAll();
    }
    
    /**
     * Finds competitions by id
     *
     * @param id id of competition to find
     * @return competition or null
     */
    public Optional<Competition> findCompetitionById(Long id) {
        return competitionRepository.findById(id);
    }

    /**
     * Gets list of all team competitions
     * @return list of all team competitions 
    */
    public List<Competition> getAllTeamCompetitions() {
        return competitionRepository.findAll().stream().filter(
                x -> x.getClass() == TeamCompetition.class
        ).toList();
    }

    /**
     * Gets list of all user competitions
     * @return list of all user competitions
     */
    public List<Competition> getAllUserCompetitions() {
        return competitionRepository.findAll().stream().filter(
                x -> x.getClass() == UserCompetition.class
        ).toList();
    }

    /**
     * update or add a competition
     * @param competition The competition to add
     * @return the saved competition
     */
    public Competition updateOrAddCompetition(Competition competition) {
        return competitionRepository.save(competition);
    }

    /**
     * Returns a list of competitions the given team is apart of
     * @param team Team we want list of competitions of
     * @return list of competitions team is apart of
     */
    public List<Competition> getAllCompetitionsWithTeam(Team team) {
       List<Competition> competitionsTeamIsIn = new ArrayList<>();
        for (Competition competition : getAllTeamCompetitions()) {
            if (competition instanceof TeamCompetition) {
                if (((TeamCompetition) competition).getTeams().contains(team)) {
                    competitionsTeamIsIn.add(competition);
                }
            }
        }
        return competitionsTeamIsIn;

    }

    /**
     * Finds past competitions by sport.
     * @param pageable The pageable detailing information about the query
     * @param filteredSports The list of sports to filter by
     * @return The Page of competitions
     */
    public Page<Competition> findPastCompetitionsBySports(Pageable pageable, List<String> filteredSports) {
        if (filteredSports == null) {
            filteredSports = EMPTY_LIST;
        } else {
            filteredSports = filteredSports.stream().map(String::toLowerCase).toList();
        }
        Date now = Date.from(Instant.now());
        return competitionRepository.findPastCompetitionsBySports(pageable, filteredSports, now);
    }

    /**
     * Finds current competitions by sport.
     * @param pageable The pageable detailing information about the query
     * @param filteredSports The list of sports to filter by
     * @return The Page of competitions
     */
    public Page<Competition> findCurrentCompetitionsBySports(Pageable pageable, List<String> filteredSports) {
        if (filteredSports == null) {
            filteredSports = EMPTY_LIST;
        } else {
            filteredSports = filteredSports.stream().map(String::toLowerCase).toList();
        }
        Date now = Date.from(Instant.now());
        return competitionRepository.findCurrentCompetitionsBySports(pageable, filteredSports, now);
    }

    /**
     * Finds all competitions given a list of sports.
     * @param pageable The pageable detailing information about the query
     * @param filteredSports The list of sports to filter by
     * @return The Page of competitions
     */
    public Page<Competition> findAllCompetitionsBySports(Pageable pageable, List<String> filteredSports) {
        if (filteredSports == null) {
            filteredSports = EMPTY_LIST;
        } else {
            filteredSports = filteredSports.stream().map(String::toLowerCase).toList();
        }
        return competitionRepository.findAllCompetitionsBySports(pageable, filteredSports);
    }
}
