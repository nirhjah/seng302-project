package nz.ac.canterbury.seng302.tab.helper;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.SportRepository;
import nz.ac.canterbury.seng302.tab.service.LocationService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class GenerateRandomTeams implements ApplicationRunner {

    @Autowired
    SportRepository sportRepository;

    @Autowired
    TeamService teamService;

    @Autowired
    LocationService locationService;

    private final Random random = ThreadLocalRandom.current();

    // Sourced from https://gist.github.com/ruanbekker/a1506f06aa1df06c5a9501cb393626ea
    private static final String[] RANDOM_NAMES = {
            "Liverpool", "Manutd", "Nomads", "Harewood", "Burnley",
            "Canterbury", "Otago", "Highlanders", "Crusaders", "rovers",
            "Hello1", "xyzd", "qwertyuiop", "Celtics", "Springboks"
    };
    private static final String[] RANDOM_SPORTS = {
            "Hockey", "Rugby", "E-Sports", "Football", "Water polo"
    };

    private static final String[] RANDOM_CITIES = {
        "Christchurch", "Auckland", "Sydney", "Dunedin", "Gore"
    };

    private Location getRandomLocation() {
        var city = RANDOM_CITIES[random.nextInt(0, RANDOM_CITIES.length)];
        return new Location("49 Mays Road", "", "St Albans", city, "8052", "New Zealand");
    }

    /**
     * Generates a team with random values.
     * <p><strong>NOTE:</strong> Isn't saved to the database!
     * @return A randomly generated team.
     */
    public Team createRandomTeam() throws IOException {
        var name = RANDOM_NAMES[random.nextInt(0, RANDOM_NAMES.length)];
        var sport = RANDOM_SPORTS[random.nextInt(0, RANDOM_SPORTS.length)];
        var location = getRandomLocation();
        return new Team(name, sport, location);
    }

    public void createAndSaveRandomTeams(int count) throws IOException {
        for (int i=0; i<count; i++) {
            var team = createRandomTeam();
            team.generateToken(teamService);
            teamService.addTeam(team);
        }
    }

    /**
     * Used for initialization, to register the random
     * sports that we are using.
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        for (var sport: RANDOM_SPORTS) {
            sportRepository.save(new Sport(sport));
        }
    }
}
