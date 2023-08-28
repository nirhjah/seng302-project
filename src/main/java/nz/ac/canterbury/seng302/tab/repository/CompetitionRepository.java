package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    @Query("""
        SELECT DISTINCT c
        FROM Competition c
        WHERE c.competitionEnd < :now
        AND (:#{#filteredSports.size()} = 0 OR c.sport IN (:filteredSports))
        """)
    Page<Competition> findPastCompetitionsBySports(Pageable pageable,
                                                   @Param("filteredSports") List<String> filteredSports,
                                                   @Param("now") LocalDateTime now);

    @Query("""
            SELECT DISTINCT c
            FROM Competition c
              WHERE ((:#{#filteredSports.size}=0 OR (c.sport in :filteredSports))
              AND ((:now >= c.competitionStart) AND (:now <= c.competitionStart)))
              """)
    Page<Competition> findCurrentCompetitionsBySports(Pageable pageable,
                                                      @Param("filteredSports") List<String> filteredSports,
                                                      @Param("now") LocalDateTime now);


    @Query("""
            SELECT DISTINCT c
            FROM Competition c
              WHERE ((:#{#filteredSports.size}=0 OR (c.sport in :filteredSports)))
              """)
    Page<Competition> findAllCompetitionsBySports(Pageable pageable, List<String> filteredSports);

}