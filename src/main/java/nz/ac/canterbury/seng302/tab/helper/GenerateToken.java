package nz.ac.canterbury.seng302.tab.helper;

import nz.ac.canterbury.seng302.tab.service.TeamService;

import java.util.UUID;

public class GenerateToken {

    public static String generateToken(int length){
        assert length > 0;
        return UUID.randomUUID().toString().replaceAll("\\-*", "").substring(0, length);
    }

    private static final int TEAM_TOKEN_SIZE = 12;

    public static String generateTokenForTeam(TeamService teamService) {
        String token = generateToken(TEAM_TOKEN_SIZE);
        while (teamService.findByToken(token).isPresent()) {
            token = generateToken(TEAM_TOKEN_SIZE);
        }
        return token;
    }
}
