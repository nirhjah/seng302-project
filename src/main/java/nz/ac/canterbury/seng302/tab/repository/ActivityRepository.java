package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Team;

// import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
// import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends CrudRepository<Activity, Long> {
    Optional<Activity> findById(long id);

    List<Activity> findAll();

    // List<Activity> findByTeam(Team team);

    Page<Activity> findActivityByTeam(Team team, Pageable pageable);
}
