package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.WhiteboardScreenshot;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface WhiteboardScreenshotRepository extends CrudRepository<WhiteboardScreenshot, Long> {
    Optional<WhiteboardScreenshot> findById(long id);
}

