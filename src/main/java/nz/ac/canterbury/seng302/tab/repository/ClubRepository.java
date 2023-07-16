package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Club;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;



import java.util.List;
import java.util.Optional;

/**
 * Spring Boot Repository class for ClubRepository which extends Spring Data
 * interface for generic CRUD operations.
 */
@Repository
public interface ClubRepository extends CrudRepository<Club, Long> {
    Optional<Club> findById(long id);

    List<Club> findAll();

}



