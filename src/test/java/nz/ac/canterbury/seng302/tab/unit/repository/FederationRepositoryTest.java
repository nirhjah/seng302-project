package nz.ac.canterbury.seng302.tab.unit.repository;

import nz.ac.canterbury.seng302.tab.entity.FederationManagerInvite;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.FederationRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
public class FederationRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    FederationRepository federationRepository;

    private User userOne;
    private User userTwo;
    private FederationManagerInvite federationManagerInviteOne;
    private FederationManagerInvite federationManagerInviteTwo;
    @BeforeEach
    void beforeEach() throws Exception {
        Location location = new Location(null, null, null, "Christchurch", null, "New Zealand");
        userOne = new User("TestOnw", "AccountOne", "test1@test.com", "Password1!", location);
        userTwo = new User("TestTwo", "AccountTwo", "test2@test.com", "Password2!", location);
        userRepository.save(userOne);
        userRepository.save(userTwo);
        federationManagerInviteOne= new FederationManagerInvite(userOne);
        federationManagerInviteTwo = new FederationManagerInvite(userTwo);
        federationRepository.save(federationManagerInviteOne);
        federationRepository.save(federationManagerInviteTwo);
    }

    @Test
    void testFindByUser(){
        federationRepository.findByUser(userOne);
        Optional<FederationManagerInvite> userOneInvite = federationRepository.findByUser(userOne);
        Assertions.assertNotNull(userOneInvite);
        Assertions.assertEquals(userOneInvite.get().getUser().getFirstName(), userOne.getFirstName());

        Optional<FederationManagerInvite> userTwoInvite = federationRepository.findByUser(userTwo);
        Assertions.assertNotNull(userTwoInvite);
        Assertions.assertEquals(userTwoInvite.get().getUser().getFirstName(), userTwo.getFirstName());

    }

    @Test
    void testGettingFederationManagerInviteByToken(){
        String tokenOne = federationManagerInviteOne.getToken();
        String tokenTwo = federationManagerInviteTwo.getToken();

        FederationManagerInvite testOne = federationRepository.getFederationManagerInviteByToken(tokenOne);
        FederationManagerInvite testTwo = federationRepository.getFederationManagerInviteByToken(tokenTwo);

        Assertions.assertNotNull(testOne);
        Assertions.assertNotNull(testTwo);

        Assertions.assertEquals(federationManagerInviteOne.getUser().getFirstName(), testOne.getUser().getFirstName());
        Assertions.assertEquals(federationManagerInviteTwo.getUser().getFirstName(), testTwo.getUser().getFirstName());

    }
}
