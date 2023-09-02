package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Formation;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUp;
import nz.ac.canterbury.seng302.tab.repository.LineUpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class LineUpService {

    @Autowired
    private LineUpRepository lineUpRepository;
    @Autowired
    public LineUpService(LineUpRepository lineUpRepository) {
        this.lineUpRepository = lineUpRepository;
    }

    public Optional<List<LineUp>> findLineUpsByTeam(long id) {
        return lineUpRepository.findLineUpByTeamTeamId(id);
    }

    /**
     * Finds the LineUp by using the activity id, return null if none is found or return the most current LineUp
     * if there is more than one LineUp with the same activity id
     * @param id takes in the activity id of type long
     * @return the LineUp variable which has the activity id
     */
    public LineUp findLineUpsByActivity(long id){
        List<LineUp> lineup= lineUpRepository.findLineUpsByActivityId(id);
        if (lineup.isEmpty()){
            return null;
        }
        else if (lineup.size()>1){
            lineup.sort(Comparator.comparingLong(LineUp::getLineUpId).reversed());
        }
        return lineup.get(0);
    }

    public Optional<Formation> findFormationByLineUpId(long id){
        return lineUpRepository.findFormationByLineUpId(id);
    }

    public void updateOrAddLineUp(LineUp lineUp) {
        lineUpRepository.save(lineUp);
    }

    public Optional<List<LineUp>> findLineUpByActivity(long actId) {
        return lineUpRepository.findLineUpByActivityId(actId);
    }
}
