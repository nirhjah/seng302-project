package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Formation;
import nz.ac.canterbury.seng302.tab.repository.FormationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FormationService {

    @Autowired
    private FormationRepository formationRepository;

    public Optional<Formation> findFormationById(long id) {
        return formationRepository.findById(id);
    }

    public Optional<Formation> findTeamById(long teamId) {
        return formationRepository.findByTeamTeamId(teamId);
    }

    public Formation addOrUpdateFormation(Formation formation) {
        return formationRepository.save(formation);
    }

    public Optional<Formation> getFormation(long formationID) {
        return formationRepository.findById(formationID);
    }

    public List<Formation> getTeamsFormations(Long teamID) {
        return formationRepository.findAllTeamsFormations(teamID);
    }
}
