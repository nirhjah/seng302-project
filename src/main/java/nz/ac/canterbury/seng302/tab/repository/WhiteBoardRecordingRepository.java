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
        WHERE w.isPublic = TRUE
        AND (:#{#sports.size} = 0 OR lower(w.sport) in (:sports))
        AND (:currentSearch is null
            OR lower(w.name) LIKE lower(concat('%', :currentSearch, '%'))
        )
    """)
    Page<WhiteBoardRecording> findPublicWhiteboardsByNameAndSport(
            Pageable pageable,
            @Param("currentSearch") String currentSearch,
            @Param("sports") List<String> sports);


    @Query("""
        SELECT distinct(w.team.sport) from WhiteBoardRecording w
        WHERE w.isPublic = TRUE
    """)
    List<String> getAllDistinctPublicSports();
}
