package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Formation;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUp;
import nz.ac.canterbury.seng302.tab.repository.LineUpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Optional<LineUp> findLineUpsByActivity(long id){
        return lineUpRepository.findLineUpsByActivityId(id);
    }

    public Optional<Formation> findFormationByLineUpId(long id){
        return lineUpRepository.findFormationByLineUpId(id);
    }

    public void updateOrAddLineUp(LineUp lineUp) {
        lineUpRepository.save(lineUp);
    }
}
