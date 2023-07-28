
package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
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

    public List<Competition> getAllTeamCompetitions() {
      return competitionRepository.findByCompetitionType("TEAM");
    }

    public List<Competition> getAllUserCompetitions() {
      return competitionRepository.findByCompetitionType("USER");
    }
}
