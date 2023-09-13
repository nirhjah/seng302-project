package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Formation;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUp;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUpPosition;
import nz.ac.canterbury.seng302.tab.repository.FormationRepository;
import nz.ac.canterbury.seng302.tab.repository.LineUpPositionRepository;
import nz.ac.canterbury.seng302.tab.repository.LineUpRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.sound.sampled.Line;
import java.util.*;

@Service
public class LineUpService {

    private Logger logger = LoggerFactory.getLogger(LineUpService.class);


    @Autowired
    private LineUpRepository lineUpRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FormationRepository formationRepository;

    @Autowired
    private LineUpPositionRepository lineUpPositionRepository;

    @Autowired
    public LineUpService(LineUpRepository lineUpRepository, FormationRepository formationRepository, LineUpPositionRepository lineUpPositionRepository, UserRepository userRepository) {
        this.lineUpRepository = lineUpRepository;
        this.formationRepository = formationRepository;
        this.lineUpPositionRepository = lineUpPositionRepository;
        this.userRepository = userRepository;
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

    /**
     * Gets a map of the formation and lineup for an activity
     * @param activity activity to get lineups with formation of
     * @return map of formation and lineups
     */
    public Map<Long, List<List<Object>>> getFormationAndPlayersAndPosition(Activity activity) {

        Map<Long, List<List<Object>>> formationAndPlayersAndPosition = new HashMap<>();
        for (Map.Entry<Formation, LineUp> entry : getLineUpsForTeam(activity.getTeam(), activity).entrySet()) {
            Formation formation = entry.getKey();
            LineUp lineUp = entry.getValue();

            List<List<Object>> playersAndPosition = new ArrayList<>();

            Optional<List<LineUpPosition>> lineupPosition = (lineUpPositionRepository.findLineUpPositionsByLineUpLineUpId(lineUp.getLineUpId()));

            if (lineupPosition.isPresent()) {
                for (LineUpPosition position : lineupPosition.get()) {
                    int positionId = position.getPosition();

                    User player = position.getPlayer();

                    List<Object> playerInfo = Arrays.asList((long) positionId, player.getId(), player.getFirstName());
                    playersAndPosition.add(playerInfo);

                }
            }

            List<Object> subsInfo = new ArrayList<>();
            for (User sub : lineUp.getSubs()) {
                List<Object> specificPlayerSubInfo = new ArrayList<>();

                specificPlayerSubInfo.add(sub.getUserId());
                specificPlayerSubInfo.add(sub.getFirstName());

                subsInfo.add(specificPlayerSubInfo);
            }
            playersAndPosition.add(subsInfo);

            formationAndPlayersAndPosition.put(formation.getFormationId(), playersAndPosition);
        }
        return formationAndPlayersAndPosition;
    }


    /**
     * Takes list of positions and players fron the selected line up then creates LineUpPositions for each and saves them with the lineup
     * @param positionsAndPlayers list of positions and players
     * @param bindingResult binding result for errors
     * @param activityLineUp lineup for the activity
     */
    public void saveLineUp(List<String> positionsAndPlayers, BindingResult bindingResult, LineUp activityLineUp) {
        boolean error = false;

        for (String positionPlayer : positionsAndPlayers) {
            if (Objects.equals(Arrays.stream(positionPlayer.split(" ")).toList().get(1), "X")) {
                logger.info("No player was set at the position {} ", Arrays.stream(positionPlayer.split(" ")).toList().get(0));
                error = true;
                break;
            }
        }

        if (!error) {
            for (String positionPlayer : positionsAndPlayers) {
                logger.info("Valid player so creating line up position object now..");

                Optional<User> optUser = userRepository.findById(Long.parseLong(Arrays.stream(positionPlayer.split(" ")).toList().get(1)));
                if (optUser.isPresent()) {
                    User player = optUser.get();
                    int position = Integer.parseInt(Arrays.stream(positionPlayer.split(" ")).toList().get(0));
                    LineUpPosition lineUpPosition = new LineUpPosition(activityLineUp, player, position);
                    lineUpPositionRepository.save(lineUpPosition);
                }
            }
        }

        if (error) {
            bindingResult.addError(new FieldError("createActivityForm", "lineup", "The line-up is not complete"));
        }
    }


    /**
     * Saves subs to activity lineup
     * @param subs subs to save
     * @param activityLineUp lineup for the activity
     */
    public void saveSubs(String subs, LineUp activityLineUp) {
        if (subs != null && !subs.isEmpty()) {
            List<String> lineUpSubs = Arrays.stream(subs.split(", ")).toList();
            for (String playerId : lineUpSubs) {
                Optional<User> optSub = userRepository.findById(Long.parseLong(playerId));
                if (optSub.isPresent()) {
                    User subPlayer = optSub.get();
                    activityLineUp.getSubs().add(subPlayer);
                }
            }
        }
    }
}
