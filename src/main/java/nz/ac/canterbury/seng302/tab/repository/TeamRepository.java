package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.enums.Role;
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

    @Query("SELECT t FROM Team t " +
            "WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR (t.location.city) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "ORDER BY LOWER(t.name) ASC, (t.location) ASC ")
    public Page<Team> findTeamByName(@Param("name") String name, Pageable pageable);


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

    @Query("SELECT t.location FROM Team t " +
            "WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(t.location.country) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(t.location.city) LIKE LOWER(CONCAT('%', :name, '%')) ")
    List<Location> findLocationsByName(@Param("name") String name);

    @Query("SELECT distinct(t.sport) FROM Team t " +
            "WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(t.location.country) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(t.location.city) LIKE LOWER(CONCAT('%', :name, '%')) ")
    List<String> findSportsByName(@Param("name") String name);

    @Query("SELECT t FROM Team t " +
            "WHERE (:#{#filteredLocations.size} = 0 OR t.location.city in (:filteredLocations)) " +
            "AND (:name IS NOT NULL " +
            "AND (lower(t.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "OR (lower(t.location.city) like lower(concat('%', :name, '%')))) " +
            "ORDER BY LOWER(t.name) ASC, LOWER(t.location.city) ASC ")
    Page<Team> findTeamByFilteredLocations(@Param("filteredLocations") List<String> filteredLocations,
            Pageable pageable, @Param("name") String name);

    @Query("SELECT t FROM Team t " +
            "WHERE (:#{#filteredLocations.size} = 0 OR lower(t.location.city) in (:filteredLocations)) " +
            "AND (:#{#filteredSports.size} = 0 OR lower(t.sport) in (:filteredSports)) " +
            "AND (:name IS NOT NULL " +
            "AND (lower(t.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "OR (lower(t.location.city) like lower(concat('%', :name, '%')))" +
            "OR (lower(t.teamClub.name) LIKE LOWER(CONCAT('%', :name, '%')))) " +
            "ORDER BY LOWER(t.name) ASC, LOWER(t.location.city) ASC ")
    Page<Team> findTeamByFilteredLocationsAndSports(
            Pageable pageable,
            @Param("filteredLocations") List<String> filteredLocations,
            @Param("filteredSports") List<String> filteredSports,
            @Param("name") String name);

    @Query("SELECT tr FROM TeamRole tr WHERE tr.team.teamId = :teamId AND tr.role = :role")
    TeamRole findTeamManager(@Param("teamId") Long teamId, @Param("role") Role role);

    @Query("SELECT t.name FROM Team t")
    List<String> getAllTeamNames();

    List<Team> findTeamsByTeamClubClubId(long clubId);
}
