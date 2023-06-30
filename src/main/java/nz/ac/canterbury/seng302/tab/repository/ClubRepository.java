package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Club;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;



import java.util.List;
import java.util.Optional;

@Repository
public interface ClubRepository extends CrudRepository<Club, Long> {
    Optional<Club> findById(long id);

    List<Club> findAll();

}



