package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;

/* Interface to get personal statistics for a player
 * this way we aren't tied to a specific implementation and program to the interface not the implementation
 **/
public interface PersonalStatistics {
  
    /**
     * @param team
     * @return the number of minutes the user has played for that particular team
     **/
    public int getMinutesPlayed(User player, Team team);
  
    /**
     * @param team
     * @return number of goals scored for that team
     **/
    public int getGoalsScored(User player, Team team);

    /**
     * an appearance is 1 min or more in a game 
     * @param team
     * @return the number of matches the user has appeard in for that team 
     **/
    public int getMatchesAppearedIn(User player, Team team);

}
