package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.FormResult;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * FormResult repository accessor using Spring's @link{CrudRepository}.
 * These (basic) methods are provided for us without the need to write our own implementations
 */
public interface FormRepository extends CrudRepository<FormResult, Long> {
    Optional<FormResult> findById(long id);

    List<FormResult> findAll();
}
