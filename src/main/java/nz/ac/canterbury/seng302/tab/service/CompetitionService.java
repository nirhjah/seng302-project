
package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import nz.ac.canterbury.seng302.tab.entity.competition.TeamCompetition;
import nz.ac.canterbury.seng302.tab.entity.competition.UserCompetition;
import nz.ac.canterbury.seng302.tab.repository.CompetitionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Spring Boot Service class for Compettiion repository
 */
@Service
public class CompetitionService {
    Logger logger = LoggerFactory.getLogger(getClass());

    private final CompetitionRepository competitionRepository;

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
                (x) -> x.getClass() == TeamCompetition.class
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
}
