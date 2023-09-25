package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.WhiteBoardRecording;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface WhiteBoardRecordingRepository extends CrudRepository<WhiteBoardRecording, Long> {
    Optional<WhiteBoardRecording> findById(long id);
}
