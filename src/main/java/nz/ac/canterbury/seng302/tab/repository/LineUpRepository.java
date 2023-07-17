package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUp;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LineUpRepository {

    Optional<LineUp> findByTeamTeamId(Long teamId);
}
