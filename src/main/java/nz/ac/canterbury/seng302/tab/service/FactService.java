package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Activity;
import nz.ac.canterbury.seng302.tab.entity.Fact.Fact;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.helper.TimeOfFactComparator;
import nz.ac.canterbury.seng302.tab.repository.FactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FactService {

    private final FactRepository factRepository;

    @Autowired
    public FactService( FactRepository factRepository) {
        this.factRepository = factRepository;
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
        return sortFactTimesAscending(factRepository.getFactByFactTypeAndActivity(factType, activity));
    }

    /**
     * Sorts given list of goals by time
     * @param factList list of goals
     * @return list of goals sorted in ascending time order
     */
    public List<Fact> sortFactTimesAscending(List<Fact> factList) {
        return factList.stream()
                .sorted(new TimeOfFactComparator())
                .toList();
    }

    /**
     * Get list of substitution (off) times for a user for a specific activity
     * @param user the user whose sub offs are being requested
     * @param activity the activity which the user participated in
     * @return list of the times of substitution
     */
    public List<String> getUserSubOffForActivity(User user, Activity activity) {
        return factRepository.getUserSubOffForActivity(user, activity);
    }

    /**
     * Get list of substitution (on) times for a user for a specific activity
     * @param user the user whose sub ons are being requested
     * @param activity the activity which the user participated in
     * @return list of the times of substitution
     */
    public List<String> getUserSubOnsForActivity(User user, Activity activity) {
        return factRepository.getUserSubOnsForActivity(user, activity);
    }

    /**
     * Get the total time a player participated in an activity
     * @param activity the activity the player took part in
     * @return the length of the activity in minutes
     */
    public long getTimePlayed(Activity activity) {
        return Duration.between(activity.getActivityStart(), activity.getActivityEnd()).toMinutes();
    }
    
    /**
     * Code for handling return of multiple entities adapted from
     * https://www.baeldung.com/jpa-return-multiple-entities#:~:text=In%20order%20to%20create%20a,primary%20and%20corresponding%20foreign%20keys.
     * @param team team that top scorers are to be found from
     * @return a List of mapping of top scorer by name to their number of goals
     */
    public Map<User, Long> getTop5Scorers(Team team) {
        List<Object[]> scorers = factRepository.getListOfTopScorersAndTheirScores(team);
        Map<User, Long> scoreInformation = new HashMap<>();
        for (Object[] scorerInfo : scorers) {
            User u = (User) scorerInfo[0];
            Long i = (Long) scorerInfo[1];
            scoreInformation.put(u, i);
        }


        return scoreInformation.entrySet()
                .stream()
                .sorted(Map.Entry.<User, Long>comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (score1, score2) -> score1,
                        LinkedHashMap::new
                ));
    }

    public void addOrUpdate(Fact fact) {factRepository.save(fact);}
}
