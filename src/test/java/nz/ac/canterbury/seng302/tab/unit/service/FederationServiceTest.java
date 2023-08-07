package nz.ac.canterbury.seng302.tab.unit.service;

import nz.ac.canterbury.seng302.tab.entity.FederationManagerInvite;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.FederationRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.FederationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(FederationService.class)
class FederationServiceTest {

    @Autowired
    FederationRepository federationRepository;

    @Autowired
    FederationService federationService;

    @Autowired
    UserRepository userRepository;

    @Test
    void saveAndGetInviteTestTest() throws Exception {
        User u = new User("Test", "First", "email@gmail.com", "password",
                    new Location(null, null, null, "chch", null, "nz"));
        userRepository.save(u);
        FederationManagerInvite invite = new FederationManagerInvite(u);
        federationService.updateOrSave(invite);

        Assertions.assertEquals(invite, federationService.getByToken(invite.getToken()));
    }

    @Test
    void deletionTest() throws Exception {
        User u = new User("Test", "First", "email@gmail.com", "password",
                new Location(null, null, null, "chch", null, "nz"));
        userRepository.save(u);
        FederationManagerInvite invite = new FederationManagerInvite(u);
        federationService.updateOrSave(invite);
        Assertions.assertEquals(invite, federationService.getByToken(invite.getToken()));
        federationService.delete(invite);
        Assertions.assertNull(federationService.getByToken(invite.getToken()));
    }
}
