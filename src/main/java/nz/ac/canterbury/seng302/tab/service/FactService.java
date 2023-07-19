package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Fact.Fact;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.FactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FactService {

    private final FactRepository factRepository;

    @Autowired
    public FactService(FactRepository factRepository) {
        this.factRepository=factRepository;
    }

    /**
     * Returns a list of all facts related to an activity
     * @param activity the activity whose facts are wanted to be returned
     * @return the list of facts associated with an activity
     */
    public List<Fact> getAllFactsForActivity(Activity activity) {return factRepository.getFactByActivity(activity);}

    /**
     * Returns a list of all facts of a certain type.
     * @param factType the ordinal value of the FactType enum representing the type of facts to be found
     *                 (0 - Fact, 1 - Goal, 2 - Substitution)
     * @param activity the activity whose facts are being retrieved.
     * @return list of activities with the wanted fact type.
     */
    public List<Fact> getAllFactsOfGivenTypeForActivity(int factType, Activity activity) {
        return factRepository.getFactByFactTypeAndActivity(factType, activity);
    }

    /**
     * Code for handling return of multiple entities adapted from
     * https://www.baeldung.com/jpa-return-multiple-entities#:~:text=In%20order%20to%20create%20a,primary%20and%20corresponding%20foreign%20keys.
     * @param team team that top scorers are to be found from
     * @return a List of mapping of top scorer by name to their number of goals
     */
    public List<Map<User, Long>> getTop5Scorers(Team team) {
        List<Object[]> scorers =  factRepository.getListOfTopScorersAndTheirScores(team);
        List<Map<User, Long>> scoreInformation = new ArrayList<>();
        for (Object[] scorerInfo : scorers) {
            User u = (User) scorerInfo[0];
            Long i = (Long) scorerInfo[1];
            scoreInformation.add(Map.of(u, i));
        }
        return scoreInformation;
    }


}
