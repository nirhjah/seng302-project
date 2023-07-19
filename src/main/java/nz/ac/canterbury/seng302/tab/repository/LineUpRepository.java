package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUp;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LineUpRepository extends CrudRepository<LineUp, Long> {

    Optional<List<LineUp>> findLineUpByTeamTeamId(Long teamId);

}
