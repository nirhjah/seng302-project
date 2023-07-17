package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUp;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUpPositions;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LineUpPositionRepository extends CrudRepository<LineUpPositions, Long> {

    Optional<LineUpPositions> findLineUpPositionsByLineUpId(Long lineUpId);

}
