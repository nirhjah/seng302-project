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
            "JOIN t.location l "+
            "WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(l.addressLine1) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(l.addressLine2) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(l.suburb) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(l.postcode) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(l.city) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(l.country) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "ORDER BY LOWER(t.name) ASC, LOWER(l.addressLine1) ASC, LOWER(l.addressLine2) ASC, LOWER(l.suburb) ASC, LOWER(l.postcode) ASC, LOWER(l.city) ASC, LOWER(l.country) ASC" )
    public Page<Team> findTeamByName(@Param("name") String name, Pageable pageable);

}
