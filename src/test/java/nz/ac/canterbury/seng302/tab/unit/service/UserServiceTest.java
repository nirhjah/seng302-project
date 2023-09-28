package nz.ac.canterbury.seng302.tab.unit.service;

import nz.ac.canterbury.seng302.tab.authentication.UserPasswordEncoder;
import nz.ac.canterbury.seng302.tab.config.ThreadPoolTaskSchedulerConfig;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.enums.AuthorityType;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({UserService.class, ThreadPoolTaskSchedulerConfig.class, UserPasswordEncoder.class})
public class UserServiceTest {

    Logger logger = LoggerFactory.getLogger(UserServiceTest.class);

    @Autowired
    private UserService userService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;



    Location location = new Location("1 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand");


    @BeforeEach
    public void beforeAll() {
        userRepository.deleteAll();
    }
    
    @Test
    void testGettingAllUsersWhoArentAFedMan() throws Exception {
        User user = new User("Hee", "Account", "tab@gmail.com", "password", location);
        userRepository.save(user);
        User user2 = new User("test", "test", "test@gmail.com", "1", location);
        userRepository.save(user2);

        user.grantAuthority(AuthorityType.FEDERATION_MANAGER);
        userService.updateOrAddUser(user);
        
        Pageable pageable = PageRequest.of(0, 10);
        List<User> actualUsers = userService.getAllUsersNotFedMans(pageable).getContent();
        
        assertFalse(actualUsers.stream().anyMatch(u -> u == user));
    }

    @Test 
    void testSearchingUsersWhoArentFedManByName() throws Exception {
        User user = new User("Hee", "Account", "tab@gmail.com", "password", location);
        userRepository.save(user);
        User user2 = new User("test", "test", "test@gmail.com", "password", location);
        userRepository.save(user2);
        User user3 = new User("Heeman", "test", "test2@gmail.com", "password", location);
        userRepository.save(user3);
        
        user3.grantAuthority(AuthorityType.FEDERATION_MANAGER);
        userService.updateOrAddUser(user3);
        
        Pageable pageable = PageRequest.of(0, 10);
        List<User> actualUsers = userService.getAllUsersNotFedMansByName(pageable, "Hee").getContent();
        
        assertTrue(actualUsers.stream().anyMatch(u -> u == user));
    }

    @Test
    void testSearchingUsersWhoArentFedManByEmail() throws Exception {
        User user = new User("Hee", "Account", "tab@gmail.com", "password", location);
        userRepository.save(user);
        User user2 = new User("test", "test", "test@gmail.com", "password", location);
        userRepository.save(user2);
        User user3 = new User("Heeman", "test", "test2@gmail.com", "password", location);
        userRepository.save(user3);
        
        user3.grantAuthority(AuthorityType.FEDERATION_MANAGER);
        userService.updateOrAddUser(user3);
        
        Pageable pageable = PageRequest.of(0, 10);
        List<User> actualUsers = userService.getAllUsersNotFedMansByEmail(pageable, "tab@gmail.com").getContent();
        
        assertTrue(actualUsers.stream().anyMatch(u -> u == user));
    }

    
    @Test
    void testSearchingUsersWhoArentFedManByNameAndEmail() throws Exception {
        User user = new User("Hee", "Account", "tab@gmail.com", "password", location);
        userRepository.save(user);
        User user2 = new User("tab@gmail.com", "test", "test@gmail.com", "password", location);
        userRepository.save(user2);
        User user3 = new User("Heeman", "test", "test2@gmail.com", "password", location);
        userRepository.save(user3);
        
        user3.grantAuthority(AuthorityType.FEDERATION_MANAGER);
        userService.updateOrAddUser(user3);
        
        Pageable pageable = PageRequest.of(0, 10);
        List<User> actualUsers = userService.getAllUsersNotFedMansByNameAndEmail(pageable, "tab@gmail.com").getContent();
        
        assertTrue(actualUsers.stream().anyMatch(u -> u == user));
        assertTrue(actualUsers.stream().anyMatch(u -> u == user2));
        
    }

    @Test
    void testFindUsersEmptyFilters() throws Exception {
        User user = new User("Hee", "Account", "tab@gmail.com", "password", location);
        userRepository.save(user);
        User user2 = new User("tab@gmail.com", "test", "test@gmail.com", "password", location);
        userRepository.save(user2);
        User user3 = new User("Heeman", "test", "test2@gmail.com", "password", location);
        userRepository.save(user3);

        Pageable pageable = PageRequest.of(0, 10, UserService.SORT_BY_LAST_AND_FIRST_NAME);

        assertTrue(List.of(user, user2, user3).containsAll(userService.findUsersByNameOrSportOrCity(pageable, List.of(), List.of(), "").toList()));
    }

    @Test
    void testEmailIsInUse() throws Exception {
        User user = new User("Hee", "Account", "tab@gmail.com", "password", location);
        userRepository.save(user);
        userService.emailIsInUse(user.getEmail());
    }

    @Test
    void testEmailIsInUse_EmailDoesntExist() throws Exception {
        User user = new User("Hee", "Account", "tab@gmail.com", "password", location);
        userRepository.save(user);
        userService.emailIsInUse("test@test.com");
    }

    @Test
    void testUpdatePassword() throws Exception {
        User user = new User("Hee", "Account", "tab@gmail.com", "password", location);
        userRepository.save(user);
        String pw = user.getPassword();
        userService.updatePassword(user, "hello!H999");
        assertNotEquals(pw, user.getPassword());
    }

    @Test
    void testUserJoinTeam() throws Exception {
        Team team = new Team("test", "sports", new Location(null, null, null, "chc", null, "nz"));
        teamRepository.save(team);
        User user = new User("Hee", "Account", "tab@gmail.com", "password", location);
        userRepository.save(user);

        userService.userJoinTeam(user, team);

        assertTrue(teamService.findTeamsWithUser(user).contains(team));

    }



}
