package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUp;
import nz.ac.canterbury.seng302.tab.repository.LineUpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LineUpService {

    @Autowired
    LineUpRepository lineUpRepository;

    public Optional<List<LineUp>> findLineUpsByTeam(long id) {
        return lineUpRepository.findLineUpByTeamTeamId(id);
    }

    public void updateOrAddLineUp(LineUp lineUp) {
        lineUpRepository.save(lineUp);
    }

    public Optional<List<LineUp>> findLineUpByActivity(long actId) {
        return lineUpRepository.findLineUpByActivityId(actId);
    }
}
