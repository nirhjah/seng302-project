package nz.ac.canterbury.seng302.tab.helper;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.SportRepository;

@Service
public class GenerateRandomUsers {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    SportRepository sportRepository;

    @Autowired
    TeamRepository teamRepository;

    private final Random random = ThreadLocalRandom.current();

    // Sourced from https://gist.github.com/ruanbekker/a1506f06aa1df06c5a9501cb393626ea
    public static final String[] RANDOM_NAMES = {
            "Arved", "Brehme", "Chrismedi", "Dre", "Edwin", "Farren", "Georgy", "Harris", "Ishwar", "Justinas", "Kit",
            "Lael", "Mackie", "Nick", "Orrick", "Paul", "Qasim", "Raphael", "Stevie", "Tyler", "Ubayd", "Vincenzo",
            "Wiktor", "Xander", "Yahya", "Zaine"
    };
    public static final String[] RANDOM_SPORTS = {
            "Hockey", "Rugby", "E-Sports", "Cheese eating (personal fave)", "Boot throwing", "Driving with screaming kids in the back"
    };

    /**
     * Generates a user with a random name, email, and DOB (Without any sports)
     * <p><strong>NOTE:</strong> This user isn't saved to the repository, you need to do that.</p>
     * @return A randomly generated user.
     */
    public User createRandomUser() throws IOException {

        String firstName = RANDOM_NAMES[random.nextInt(0, RANDOM_NAMES.length)];
        
        if (random.nextBoolean()) {
            firstName = firstName.toLowerCase();
        }
        String lastName = RANDOM_NAMES[random.nextInt(0, RANDOM_NAMES.length)];
        if (random.nextBoolean()) {
            lastName = lastName.toLowerCase();
        }
        String email = UUID.randomUUID().toString() + "@email.com";
        long startDate = new GregorianCalendar(1980, Calendar.JANUARY, 1).getTime().getTime();
        long endDate = new GregorianCalendar(2005, Calendar.DECEMBER, 31).getTime().getTime();
        Date dob = new Date(random.nextLong(startDate, endDate));
        return new User(firstName, lastName, dob, email, "abc123", new Location(null, null, null, "Christchurch", null, "New Zealand"));

    }

    /**
     * Generates a user with a random name, email, DOB, and between [0, all] favourite sports.
     * <p>If no sports exist, it'll populate the database with the in <code>RANDOM_SPORTS</code></p>
     * <p><strong>NOTE</strong>: This user isn't saved to the repository, you need to do that.</p>
     * @return A randomly generated user.
     */
    public User createRandomUserWithSports() throws IOException {
        Team teamToJoin = teamRepository.findAll().get(0);
        System.out.println(teamToJoin);
        User user = createRandomUser();
        // Generate random sports
        if (sportRepository.count() == 0) {
            for (String sportName: RANDOM_SPORTS) {
                Sport sport = new Sport(sportName);
                sportRepository.save(sport);
            }
        }
        
        // Add random sports
        List<Sport> allSports = sportRepository.findAll();
        Collections.shuffle(allSports, random);
        List<Sport> ourSports = allSports.subList(0, random.nextInt(allSports.size()));

        user.setFavoriteSports(ourSports);
        user.joinTeam(teamToJoin);

        return user;

    }
}
