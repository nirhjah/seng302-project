package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Spring Boot Repository class for TeamRespository which extends Spring Data
 * interface for generic CRUD operations.
 */
public interface TeamRepository extends CrudRepository<Team, Long>, PagingAndSortingRepository<Team, Long> {
    Optional<Team> findById(long id);

    Optional<Team> findByToken(String token);

    List<Team> findAll();

    Page<Team> findAll(Pageable pageable);

    @Query("SELECT t FROM Team t LEFT JOIN t.teamMembers tm " +
            "WHERE (:user) in (tm) " +
            "ORDER BY LOWER(t.name) ASC, (t.location) ASC")
    Page<Team> findTeamsWithUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT t FROM Team t LEFT JOIN t.teamMembers tm " +
            "WHERE (:user) in (tm) " +
            "ORDER BY LOWER(t.name) ASC, (t.location) ASC")
    List<Team> findTeamsWithUser_List(@Param("user") User user);


    @Query("SELECT t FROM Team t " +
            "WHERE (:#{#searchedSports.size}=0 or t.sport in (:searchedSports))" +
            "AND (:name IS NOT NULL " +
            "AND (LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR (t.location.city) LIKE LOWER(CONCAT('%', :name, '%')))) " +
            "ORDER BY LOWER(t.name) ASC, (t.location) ASC ")
    Page<Team> findTeamByNameAndSportIn(Pageable pageable, @Param("searchedSports") List<String> searchedSports,
            @Param("name") String name);

    @Query("SELECT t FROM Team t " +
            "WHERE (:#{#filteredLocations.size} = 0 OR t.location.city in (:filteredLocations)) " +
            "AND (:name IS NOT NULL " +
            "AND (lower(t.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "OR (lower(t.location.city) like lower(concat('%', :name, '%')))) " +
            "ORDER BY LOWER(t.name) ASC, LOWER(t.location.city) ASC ")
    Page<Team> findTeamByFilteredLocations(@Param("filteredLocations") List<String> filteredLocations,
            Pageable pageable, @Param("name") String name);

    @Query("""
            SELECT t FROM Team t
            LEFT JOIN Club c ON (t.teamClub.clubId = c.clubId)
            WHERE (:#{#filteredLocations.size} = 0 OR lower(t.location.city) in (:filteredLocations))
            AND (:#{#filteredSports.size} = 0 OR lower(t.sport) in (:filteredSports))
            AND (:name IS NULL OR :name = ''
            OR (lower(t.name) LIKE LOWER(CONCAT('%', :name, '%')))
            OR (lower(t.location.city) like lower(concat('%', :name, '%')))
            OR (lower(c.name) like lower(concat('%', :name, '%'))))
            """)
    Page<Team> findTeamByFilteredLocationsAndSports(
            Pageable pageable,
            @Param("filteredLocations") List<String> filteredLocations,
            @Param("filteredSports") List<String> filteredSports,
            @Param("name") String name);

    @Query("SELECT t.name FROM Team t")
    List<String> getAllTeamNames();

    List<Team> findTeamsByTeamClubClubId(long clubId);

    @Query("""
        SELECT distinct(t.sport) FROM Team t
          ORDER BY lower(t.sport)""")
    List<String> getAllDistinctSports();

    @Query("""
        SELECT distinct(t.location.city) FROM Team t
          ORDER BY lower(t.location.city)""")
    List<String> getAllDistinctCities();

    @Query("SELECT t FROM Team t " +
            "WHERE (t.sport = :sport) AND (t.teamClub = :club OR t.teamClub IS NULL)")
    List<Team> findTeamsBySportAndClubOrNotInClub(String sport, Club club);


    @Query("SELECT t FROM Team t " +
            "WHERE t.sport = :sport AND t.teamClub IS NULL")
    List<Team> findTeamsBySportAndNotInClub(String sport);

}
