package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Sport;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Boot Repository class for SportRepository which extends Spring Data interface for generic CRUD operations.
 */
public interface SportRepository extends CrudRepository<Sport, Long>{

    Optional<Sport> findById(long id);

    List<Sport> findAll();

    Optional<Sport> findSportByName(String name);

}
