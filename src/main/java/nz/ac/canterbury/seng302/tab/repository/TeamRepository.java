package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Location;
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
            "OR LOWER(t.location.country) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(t.location.city) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "ORDER BY LOWER(t.name) ASC, LOWER(t.location.country) ASC, LOWER(t.location.city) ASC ")
    public Page<Team> findTeamByName(@Param("name") String name, Pageable pageable);

    @Query("SELECT t.location FROM Team t " +
            "WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(t.location.country) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(t.location.city) LIKE LOWER(CONCAT('%', :name, '%')) ")
    public List<Location> findLocationsByName(@Param("name") String name);

//This was my idea for a potential way to solve the query?
//@Query("SELECT distinct t "
//        +"FROM Team t JOIN Location l on t.location = l "
//        +"WHERE (:#{#filteredLocations.size}=0 OR t.location.city in (:filteredLocations)) "
//        +"AND (:name is null OR "
//        +"lower(:name) like lower(concat('%', t.name, '%')) OR lower(:name) like lower(concat('%', t.sport, '%')) "
//        +")")

    @Query("SELECT t FROM Team t " +
            "WHERE (:#{#filteredLocations.size} = 0 OR (t.location.city) in (:filteredLocations)) " +
            "AND (:name IS NOT NULL " +
            "AND (lower(t.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "OR (lower(t.sport) like lower(concat('%', :name, '%')))) " +
            "ORDER BY LOWER(t.name) ASC, LOWER(t.location.city) ASC ")
    public Page<Team> findTeamByFilteredLocations(@Param("filteredLocations") List<String> filteredLocations, Pageable pageable, @Param("name") String name);

}
