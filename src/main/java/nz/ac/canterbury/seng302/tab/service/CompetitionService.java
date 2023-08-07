
package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import nz.ac.canterbury.seng302.tab.entity.competition.TeamCompetition;
import nz.ac.canterbury.seng302.tab.entity.competition.UserCompetition;
import nz.ac.canterbury.seng302.tab.repository.CompetitionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

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
     * @param competition
     * @return the saved competition
     */
    public Competition updateOrAddCompetition(Competition competition) {
        return competitionRepository.save(competition);
    }

    /**
     * Finds past competitions by sport.
     * @param pageable The pageable detailing information about the query
     * @param filteredSports The list of sports to filter by
     * @return The Page of competitions
     */
    public Page<Competition> findPastCompetitionsBySports(Pageable pageable, List<String> filteredSports) {
        long now = Instant.EPOCH.getEpochSecond();
        if (filteredSports == null) {
            filteredSports = EMPTY_LIST;
        }
        return competitionRepository.findPastCompetitionsBySports(pageable, filteredSports, now);
    }

    /**
     * Finds upcoming competitions by sport.
     * @param pageable The pageable detailing information about the query
     * @param filteredSports The list of sports to filter by
     * @return The Page of competitions
     */
    public Page<Competition> findUpcomingCompetitionsBySports(Pageable pageable, List<String> filteredSports) {
        long now = Instant.EPOCH.getEpochSecond();
        if (filteredSports == null) {
            filteredSports = EMPTY_LIST;
        }
        return competitionRepository.findUpcomingCompetitionsBySports(pageable, filteredSports, now);
    }

    /**
     * Finds current competitions by sport.
     * @param pageable The pageable detailing information about the query
     * @param filteredSports The list of sports to filter by
     * @return The Page of competitions
     */
    public Page<Competition> findCurrentCompetitionsBySports(Pageable pageable, List<String> filteredSports) {
        long now = Instant.EPOCH.getEpochSecond();
        if (filteredSports == null) {
            filteredSports = EMPTY_LIST;
        }
        return competitionRepository.findCurrentCompetitionsBySports(pageable, filteredSports, now);
    }
}
