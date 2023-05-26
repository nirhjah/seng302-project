package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.ActivityStatistics;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityStatisticRepository extends CrudRepository<ActivityStatistics, Long> {

    Optional<ActivityStatistics> findById(long id);

    List<ActivityStatistics> findAll();


}
