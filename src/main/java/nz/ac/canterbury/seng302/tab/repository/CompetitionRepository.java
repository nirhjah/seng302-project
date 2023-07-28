package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Boot Repository class for CompetitionRepository which extends Spring Data
 * interface for generic CRUD operations.
 */
public interface CompetitionRepository extends CrudRepository<Competition, Long> {

    Optional<Competition> findById();

    List<Competition> findAll();

    List<Competition> findByCompetitionType(String competitionType);
}
