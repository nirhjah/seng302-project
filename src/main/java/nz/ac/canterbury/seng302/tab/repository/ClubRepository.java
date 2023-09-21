package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Club;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



import java.util.List;
import java.util.Optional;

/**
 * Spring Boot Repository class for ClubRepository which extends Spring Data
 * interface for generic CRUD operations.
 */
@Repository
public interface ClubRepository extends CrudRepository<Club, Long> {
    Optional<Club> findById(long id);

    List<Club> findAll();

    @Query("SELECT c FROM Club c " +
           "Where c.sport = :sport " +
           "AND :name IS NOT NULL " +
           "AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "ORDER By LOWER(c.name) ASC")
    List<Club> findClubByNameAndSport(@Param("name") String name, @Param("sport") String sport);

    @Query("SELECT c FROM Club c " +
           "WHERE (:{#searchedSports.size}=0 or c.sport in (:searchedSports)) " +
           "AND (:name IS NOT NULL) " +
           "AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) "+
           "OR (c.location.city) LIKE LOWER(CONCAT('%', :name, '%')))) "+
           "ORDER BY LOWER(c.name) ASC, (c.location) ASC ")
    Page<Club> findClubByNameAndSportIn(Pageable pageable, @Param("searchedSports") List<String> searchedSports,
            @Param("name") String name);
            
    @Query("SELECT c FROM Club c "+
           "WHERE (:#{#filteredLocations.size} = 0 OR c.location.city in (:filteredLocations)) "+
           "AND (:name IS NOT NULL "+
           "AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) "+
           "OR (LOWER(c.location.city) LIKE LOWER(CONCAT('%', :name, '%')))) "+
           "ORDER BY LOWER(c.name) ASC, LOWER(c.location.city) ASC ")
    Page<Club> findClubByFilteredLocations(@Param("filteredLocations") List<String> filteredLocations,
            Pageable pageable, @Param("name") String name); 


    @Query("SELECT c FROM Club c "+
           "WHERE (:#{#filteredLocations.size} = 0 OR LOWER)")
    Page<Club> findClubByFilteredLocationsAndSports(
            Pageable pageable,
            @Param("filteredLocations") List<String> filteredLocations,
            @Param("filteredSports") List<String> filteredSports,
            @Param("name") String name);
}



