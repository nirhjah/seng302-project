package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.WhiteBoardRecording;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WhiteBoardRecordingRepository extends CrudRepository<WhiteBoardRecording, Long> {
    Optional<WhiteBoardRecording> findById(long id);

    @Query("""
        SELECT w from WhiteBoardRecording w
        WHERE (:#{#sports.size} = 0 OR lower(w.sport) in (:sports))
        AND (w.isPublic)
        AND (:currentSearch is null
            OR lower(w.name) LIKE lower(concat('%', :currentSearch, '%'))
        )
    """)    // TODO: Figure out how "public" is gonna work
    Page<WhiteBoardRecording> findWhiteboardsByNameAndSport(
            Pageable pageable,
            @Param("currentSearch") String currentSearch,
            @Param("sports") List<String> sports);
}
