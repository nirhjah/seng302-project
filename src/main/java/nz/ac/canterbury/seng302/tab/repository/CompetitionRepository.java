package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.competition.Competition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
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


    @Query("""
            SELECT DISTINCT c
            FROM Competition c
              WHERE (:#{#filteredSports.size}=0 OR c.sport in (:filteredSports))
              AND (:name is null OR
                lower(:name) like lower(concat('%', c.name, '%'))
              OR (lower(c.name) like lower(concat('%', :name, '%'))))""")
    Page<Competition> findPastCompetitionsBySports(Pageable pageable,
                                                    @Param("filteredSports") List<String> filteredSports,
                                                    @Param("date") long time,
                                                    @Param("name") String name);


    @Query("""
            SELECT DISTINCT c
            FROM Competition c
              WHERE ((:#{#filteredSports.size}=0 OR c.sport in (:filteredSports))
              AND (:now <= c.endDate) AND (:now >= c.startDate))
              """)
    Page<Competition> findCurrentCompetitionsBySports(Pageable pageable,
                                                          @Param("filteredSports") List<String> filteredSports,
                                                          @Param("now") long now);


    @Override
    Optional<Competition> findById(Long aLong);
}
