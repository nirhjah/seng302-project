package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Team;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface TeamRepository extends CrudRepository<Team, Long> {
    Optional<Team> findById(long id);
    List<Team> findAll();

    @Query("SELECT t FROM Team t WHERE t.name = :name")
    public List<Team> findTeamByName(@Param("name") String name);
}
