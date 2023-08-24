package nz.ac.canterbury.seng302.tab.unit.service;

import nz.ac.canterbury.seng302.tab.entity.Formation;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.FormationRepository;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.service.FormationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@Import(FormationService.class)
public class FormationServiceTest {

    @Autowired
    private FormationService formationService;

    @Autowired
    private FormationRepository formationRepository;


    @Autowired
    private TeamRepository teamRepository;

    private Team team;

    private Formation formation;

    @BeforeEach
    public void setUp() throws IOException {
        teamRepository.deleteAll();
        formationRepository.deleteAll();
        Location location = new Location("1 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand");
        team = new Team("test", "Hockey", location);
        teamRepository.save(team);

        formation = new Formation ("4-2-2",team);
        formationService.addOrUpdateFormation(formation);


    }

    @Test
    public void testFindFormationById(){
        Optional<Formation> expectedFormation= formationService.findFormationById(formation.getFormationId());
        Assertions.assertEquals(formation.getFormation(),expectedFormation.get().getFormation() );
        Assertions.assertEquals(formation.getFormationId(),expectedFormation.get().getFormationId());
    }

    @Test
    public void testFindFormationTeamById(){
        List<Formation> expectedFormation=formationService.findTeamById(team.getTeamId());
        Assertions.assertEquals(formation.getFormation(),expectedFormation.get(expectedFormation.size()-1).getFormation());
        Assertions.assertEquals(formation.getFormationId(),expectedFormation.get(expectedFormation.size()-1).getFormationId());
    }

    @Test
    public void testAddAndUpdateFormation(){
        Formation formation2 = new Formation ("1-2-2",team);
        formationService.addOrUpdateFormation(formation2);
        Optional<Formation> expectedFormation= formationService.findFormationById(formation2.getFormationId());
        Assertions.assertEquals(formation2.getFormationId(),expectedFormation.get().getFormationId());
        Assertions.assertEquals(formation2.getFormation(),expectedFormation.get().getFormation());
    }


}
