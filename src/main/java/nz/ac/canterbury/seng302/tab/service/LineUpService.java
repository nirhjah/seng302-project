package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUp;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUpPositions;
import nz.ac.canterbury.seng302.tab.repository.LineUpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LineUpService {

    @Autowired
    LineUpRepository lineUpRepository;

    public Optional<LineUp> findLineUpByTeam(long id) {
        return lineUpRepository.findLineUpByTeamId(id);
    }

}
