package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUpPositions;
import nz.ac.canterbury.seng302.tab.repository.LineUpPositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LineUpPositionService {

    @Autowired
    private LineUpPositionRepository lineUpPositionRepository;

    public Optional<LineUpPositions> findLineUpPositionsByLineUp(long id) {
        return lineUpPositionRepository.findLineUpPositionsByLineUpId(id);
    }

}
