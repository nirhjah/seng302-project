package nz.ac.canterbury.seng302.tab.response;

import java.util.List;

/**
 * Class used to define data for json query for displaying formation
 */
public class FormationInfo {
    private long formationID;
    private String formation;
    private String customPlayerPositions;
    private Boolean custom;
    private List<PlayerFormationInfo> players;

    // Default constructor (required by Jackson)
    public FormationInfo() {
    }

    public FormationInfo(long formationId, String formation, String customPlayerPositions, Boolean custom, List<PlayerFormationInfo> players) {
        this.formationID = formationId;
        this.formation = formation;
        this.customPlayerPositions = customPlayerPositions;
        this.custom = custom;
        this.players = players;
    }

    public List<PlayerFormationInfo> getPlayers() {
        return players;
    }

    public String getCustomPlayerPositions() {
        return customPlayerPositions;
    }

    public String getFormation() {
        return formation;
    }

    public long getFormationID() {
        return formationID;
    }

    public Boolean getCustom() {
        return custom;
    }
}

