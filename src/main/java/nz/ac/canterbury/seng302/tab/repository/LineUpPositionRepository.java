package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUp;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUpPosition;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LineUpPositionRepository extends CrudRepository<LineUpPosition, Long> {

    Optional<List<LineUpPosition>> findLineUpPositionsByLineUpId(Long lineUpId);

}
