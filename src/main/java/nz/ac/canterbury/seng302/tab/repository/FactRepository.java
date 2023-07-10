package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Fact.Fact;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FactRepository extends CrudRepository<Fact, Long> {
    List<Fact> getFactByActivity(Activity activity);
    List<Fact> getFactByFactTypeAndActivity(int factType, Activity activity);

}
