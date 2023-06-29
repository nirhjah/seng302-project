package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Club;
import nz.ac.canterbury.seng302.tab.entity.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ClubRepository extends CrudRepository<Club, Long> {

    Optional<Club> findById(long id);

    List<Club> findAll();

    Page<Club> findAll(Pageable pageable);

    Page<Club> findClubByTeam(Team team, Pageable pageable);

}
