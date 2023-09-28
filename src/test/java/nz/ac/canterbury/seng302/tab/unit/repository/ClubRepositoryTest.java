package nz.ac.canterbury.seng302.tab.unit.repository;

import nz.ac.canterbury.seng302.tab.entity.Club;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.ClubRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

@DataJpaTest
public class ClubRepositoryTest {
    @Autowired
    ClubRepository clubRepository;

    @Autowired
    UserRepository userRepository;
    private final int CLUB_DB_SIZE = 10;

    @BeforeEach
    void beforeEach() throws Exception {
        clubRepository.deleteAll();
        userRepository.deleteAll();
        Location location = new Location(null, null, null, "Christchurch", null, "New Zealand");
        User manager = new User("Test", "Account", "test@test.com", "Password1!", location);
        userRepository.save(manager);
        for (int i = 0; i < CLUB_DB_SIZE; i++) {
            clubRepository.save(new Club("Club " + i, location, "football", manager));
        }
    }

    @Test
    void testFindClubById() {
        for (int i = 0; i < CLUB_DB_SIZE; i++) {
            Optional<Club> club = clubRepository.findById(i + 1);
            Assertions.assertEquals("Club " + i, club.get().getName());
        }
    }

    @Test
    void testFindAll() {
        List<Club> clubList = clubRepository.findAll();
        Assertions.assertEquals(CLUB_DB_SIZE, clubList.size());
        int counter = 0;
        for (Club club : clubList) {
            Assertions.assertEquals("Club " + counter, club.getName());
            counter++;
        }
    }
}
