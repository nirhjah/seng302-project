package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Team;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.enums.ActivityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends CrudRepository<Activity, Long> {
    Optional<Activity> findById(long id);

    List<Activity> findAll();

    List<Activity> findActivityByTeam(Team team);

    Page<Activity> findActivityByTeam(Team team, Pageable pageable);

    List<Activity> findActivitiesByTeamAndActivityType(Team team, ActivityType activityType);

    @Query("SELECT a FROM Activity a " +
            "LEFT JOIN Team t ON a.team = t " +
            "WHERE a.activityOwner = :user " +
            "OR (a.team IS NOT NULL AND :user IN (SELECT tr.user FROM TeamRole tr WHERE t = tr.team)) " +
            "GROUP BY a, t.name " +
            "ORDER BY COALESCE(LOWER(t.name),''), a.activityStart")
    Page<Activity> findActivitiesByUserSorted(Pageable pageable, @Param("user") User user);

    @Query("""
        SELECT a FROM Activity a
        WHERE a.team = :team
            and (a.outcome = 0 OR a.outcome = 1 OR a.outcome = 2)
            and (a.activityType = 0 OR a.activityType = 1)
        ORDER BY a.activityEnd desc, a.activityStart desc, a.activityId desc
        LIMIT 5""")
    List<Activity> getLast5GameOrFriendly(@Param("team") Team team);

    @Query("SELECT COUNT(a) FROM Activity a WHERE (a.activityType = 0 OR a.activityType = 1) and a.team= :team")
    int getAllGamesAndFriendlies(@Param("team") Team team);

    @Query("SELECT count(a) FROM Activity a WHERE (a.activityType = 0 OR a.activityType = 1) and a.team= :team and a.outcome=0")
    int getNumberOfWinsForATeam(@Param("team") Team team);

    @Query("SELECT count(a) FROM Activity a WHERE (a.activityType = 0 OR a.activityType = 1) and a.team= :team and a.outcome=1")
    int getNumberOfLosesForATeam(@Param("team") Team team);

    @Query("SELECT count(a) FROM Activity a WHERE (a.activityType = 0 OR a.activityType = 1) and a.team= :team and a.outcome=2")
    int getNumberOfDrawsForATeam(@Param("team") Team team);

    @Query("SELECT (a) FROM Activity a WHERE (a.activityType = 0 OR a.activityType = 1) and a.team= :team")
    List<Activity> getAllGamesAndFriendliesForTeam(@Param("team") Team team);

    @Query("SELECT a FROM Activity a " +
            "LEFT JOIN Team t ON a.team = t " +
            "WHERE a.activityOwner = :user " +
            "OR (a.team IS NOT NULL AND :user IN (SELECT tr.user FROM TeamRole tr WHERE t = tr.team)) " +
            "GROUP BY a, t.name " +
            "ORDER BY COALESCE(LOWER(t.name),''), a.activityStart")
    List<Activity> findUserTeamAndPersonalActivities(@Param("user") User user);

    @Query("SELECT a FROM Activity a " +
            "WHERE a.activityOwner = :user and a.team is null " +
            "ORDER BY a.activityStart")
    List<Activity> getPersonalActivitiesForUser(@Param("user") User user);

    @Query("SELECT a FROM Activity a " +
            "LEFT JOIN Team t ON a.team = t " +
            "WHERE (a.team IS NOT NULL AND :user IN (SELECT tr.user FROM TeamRole tr WHERE t = tr.team)) " +
            "GROUP BY a, t.name " +
            "ORDER BY COALESCE(LOWER(t.name),''), a.activityStart")
    List<Activity> getTeamActivitiesForUser(@Param("user") User user);
}
