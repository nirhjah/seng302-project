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

    @Query("SELECT c FROM Club c "+
           "WHERE (:#{#filteredLocations.size} = 0 OR LOWER(c.location.city) in (:filteredLocations)) "+
           "AND (:#{#filteredLocations.size} = 0 OR LOWER(c.sport) in (:filteredSports)) "+
           "AND (:name IS NULL OR :name = '' "+
           "OR (LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) "+
           "OR (LOWER(c.location.city) LIKE LOWER(CONCAT('%', :name, '%'))) "+
           "OR (LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))))")
    Page<Club> findClubByFilteredLocationsAndSports(
            Pageable pageable,
            @Param("filteredLocations") List<String> filteredLocations,
            @Param("filteredSports") List<String> filteredSports,
            @Param("name") String name);
}



