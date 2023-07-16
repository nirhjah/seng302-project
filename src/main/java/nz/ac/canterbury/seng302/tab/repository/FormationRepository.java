package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Formation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FormationRepository extends CrudRepository<Formation, Long> {
    Optional<Formation> findById(long id);

    Optional<Formation> findByTeamTeamId(long teamId);

    @Query("SELECT distinct u FROM Formation u WHERE u.team.teamId = :teamId")
    public List<Formation> findAllTeamsFormations(@Param("teamId") long teamId);

}
