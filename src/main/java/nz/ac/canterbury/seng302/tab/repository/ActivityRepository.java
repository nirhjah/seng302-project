package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
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

    @Query("SELECT distinct u FROM Activity u " +
            "LEFT JOIN Team t on u.team = t " +
            "WHERE u.activityOwner = (:user) " +
            "OR (:user) in t.teamMembers " +
            "ORDER BY " +
            "u.activityOwner.firstName DESC NULLS LAST, " +
            "lower(t.name) ASC NULLS LAST, " +
            "u.activityStart ASC")
            public Page<Activity> findActivitiesByUser(@Param("user") User user, Pageable pageable);
}
