package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


/**
 * Spring Boot Repository class for TeamRespository which extends Spring Data interface for generic CRUD operations.
 */
@Repository
public interface TeamRepository extends CrudRepository<Team, Long>, PagingAndSortingRepository<Team, Long> {
    Optional<Team> findById(long id);

    List<Team> findAll();

    Page<Team> findAll(Pageable pageable);

    @Query("SELECT t FROM Team t " +
            "WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(t.location) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "ORDER BY LOWER(t.name) ASC, LOWER(t.location) ASC ")
    public Page<Team> findTeamByName(@Param("name") String name, Pageable pageable);


}
