package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Formation;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUp;
import nz.ac.canterbury.seng302.tab.repository.FormationRepository;
import nz.ac.canterbury.seng302.tab.repository.LineUpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LineUpService {

    @Autowired
    private LineUpRepository lineUpRepository;

    @Autowired
    private FormationRepository formationRepository;

    @Autowired
    public LineUpService(LineUpRepository lineUpRepository, FormationRepository formationRepository) {
        this.lineUpRepository = lineUpRepository;
        this.formationRepository = formationRepository;
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

    /**
     * Gets lineup for an activity and given formation
     * @param activity activity to get lineup of
     * @param formation formation that matches both lineup and activity
     * @return lineup for activity and given activity formation
     */
    public LineUp findLineUpByActivityAndFormation(Long activity, Formation formation) {
        return lineUpRepository.findLineUpByActivityIdAndFormation(activity, formation);
    }

    public Optional<Formation> findFormationByLineUpId(long id){
        return lineUpRepository.findFormationByLineUpId(id);
    }

    public void updateOrAddLineUp(LineUp lineUp) {
        lineUpRepository.save(lineUp);
    }


    /**
     * Gets lineup for each formation for a team's activity
     * @param team team to get lineup and formation of
     * @param activity activity to get formations of
     * @return lineup for each formation for a team's activity as a map
     */
    public Map<Formation, LineUp> getLineUpsForTeam(Team team, Activity activity) {
        List<Formation> teamFormations = formationRepository.findByTeamTeamId(team.getTeamId());
        List<LineUp> allLineUps = (List<LineUp>) lineUpRepository.findAll();

        Map<Formation, LineUp> formationLineUpMap = new HashMap<>();
        for (Formation formation : teamFormations) {

            for (LineUp lineUp : allLineUps) {

                if (lineUp.getTeam() == team && lineUp.getFormation().getFormationId() == formation.getFormationId() && lineUp.getActivity() == activity) {
                    formationLineUpMap.put(formation, lineUp);
                }
            }
        }

        return formationLineUpMap;
    }



    public Optional<List<LineUp>> findLineUpByActivity(long actId) {
        return lineUpRepository.findLineUpByActivityId(actId);
    }

}
