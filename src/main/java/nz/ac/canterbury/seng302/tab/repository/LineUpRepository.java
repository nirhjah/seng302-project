package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Formation;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUp;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.sound.sampled.Line;
import java.util.List;
import java.util.Optional;

@Repository
public interface LineUpRepository extends CrudRepository<LineUp, Long> {

    Optional<List<LineUp>> findLineUpByTeamTeamId(Long teamId);

    Optional<List<LineUp>> findLineUpByActivityId(Long activityId);

    List<LineUp> findLineUpsByActivityId(Long activityId);

    @Query("SELECT l.formation FROM LineUp l WHERE l.lineUpId = :lineUpId")
    Optional<Formation> findFormationByLineUpId(Long lineUpId);

    LineUp findLineUpByActivityIdAndFormation(Long activityId, Formation formation);

}
