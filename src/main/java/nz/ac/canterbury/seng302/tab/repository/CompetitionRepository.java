package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.competition.Competition;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Boot Repository class for CompetitionRepository which extends Spring Data
 * interface for generic CRUD operations.
 */
@Repository
public interface CompetitionRepository extends CrudRepository<Competition, Long> {

    Optional<Competition> findById(long id);

    List<Competition> findAll();
}
