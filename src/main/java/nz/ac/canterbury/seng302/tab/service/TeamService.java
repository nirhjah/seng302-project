package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService {
    @Autowired
    private TeamRepository teamRepository;

    public List<Team> getTeamList() {
        return teamRepository.findAll();
    }
    public Team addTeam(Team team) {
        return teamRepository.save(team);
    }

    /**
     * Method which uses the team id to set the profile picture's filename
     * @param id Team entity's primary key
     * @param fileName Name of file for profile picture
     */
    public void updateTeamPhoto(long id, String fileName){
        Team team = teamRepository.findById(id).get();
        team.setPhoto(fileName);
        teamRepository.save(team);
    }
}
