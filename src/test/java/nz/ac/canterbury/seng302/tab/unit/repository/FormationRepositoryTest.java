package nz.ac.canterbury.seng302.tab.unit.repository;

import nz.ac.canterbury.seng302.tab.entity.Formation;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.FormationRepository;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@DataJpaTest
public class FormationRepositoryTest {
    @Autowired
    private FormationRepository formationRepository;

    @Autowired
    private TeamRepository teamRepository;

    private Location location;

    private Team team;

    List<String> formationList = new ArrayList<>(Arrays.asList("4-2-2", "4-2-2-2","4-2-3-1"));

    @BeforeEach
    void beforeAll() throws Exception {
        this.location = new Location(null, null, null, "Christchurch", null, "New Zealand");
        this.team = new Team("testName","sport",this.location);
        this.teamRepository.save(this.team);
        formationRepository.deleteAll();

        for (int i=0; i<formationList.size(); i++){
            Formation formation = new Formation (formationList.get(i), this.team);
            formationRepository.save(formation);
        }

    }
    @Test
    public void getFormationById(){
        Formation formation = new Formation ("4-4-3", this.team);
        formationRepository.save(formation);
        Formation retrievedFormation = formationRepository.findById(formation.getFormationId()).get();
        Assertions.assertEquals(formation,retrievedFormation);
        Assertions.assertEquals(formation.getFormation(),retrievedFormation.getFormation());
    }

    @Test
    public void getFormationByTeam() throws IOException {
        Team uniqueTeam = new Team ("unique","sport", this.location);
        teamRepository.save(team);
        Formation formation = new Formation("4-4-3",uniqueTeam);
        formationRepository.save(formation);
        Formation retrievedFormation = formationRepository.findByTeamTeamId(uniqueTeam.getTeamId()).get();
        Assertions.assertEquals(formation,retrievedFormation);
        Assertions.assertEquals(formation.getTeam().getName(),retrievedFormation.getTeam().getName());
    }

}
