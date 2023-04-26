package nz.ac.canterbury.seng302.tab.helper.tokens;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;

public class JoinTeamToken extends Token {

    private TeamRepository teamRepository;

    public JoinTeamToken(Team team, TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
        this.generateNewUniqueTeamTokenValue();
    }

    public void generateNewUniqueTeamTokenValue() {
        this.generateToken();
        while (teamRepository.findByToken(this).isPresent()) {
            this.generateToken();
        }
    }

}
