package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Formation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FormationRepository extends CrudRepository<Formation, Long> {
    Optional<Formation> findById(long id);
    Optional<Formation> findByTeamTeamId(long teamId);
}
