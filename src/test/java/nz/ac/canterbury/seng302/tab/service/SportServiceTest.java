package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.repository.SportRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.util.AssertionErrors.assertEquals;

@DataJpaTest
@Import(SportService.class)
public class SportServiceTest {

    @Autowired
    SportService sportService;

    @Autowired
    SportRepository sportRepository;

    @Test
    public void getAllSportsTest() {
        Sport hockey = new Sport("Hockey");
        Sport football = new Sport("Football");
        sportRepository.save(hockey);
        sportRepository.save(football);
        List<Sport> sports = Arrays.asList(hockey, football);
        assertEquals("", sports.toString(), sportService.getAllSports().toString());
    }

    @Test
    public void getAllSportNamesTest() {
        Sport hockey = new Sport("Hockey");
        Sport football = new Sport("Football");
        sportRepository.save(hockey);
        sportRepository.save(football);
        List<String> sportNames = Arrays.asList(hockey.getName(), football.getName());
        assertEquals("", sportNames, sportService.getAllSportNames());
    }

    @Test
    public void addSportTest() {
        Sport netball = new Sport("Netball");
        sportService.addSport(netball);
        assertEquals("", netball, sportService.findSportByName(netball.getName()).get());
    }

    @Test
    public void addMultipleSportTest() {
        Sport rugby = new Sport("Rugby");
        Sport running = new Sport("Running");
        Sport cricket = new Sport("Cricket");
        List<Sport> sports = Arrays.asList(rugby, running, cricket);
        sportService.addAllSports(sports);
        assertEquals("", sports, sportService.getAllSports());
    }

}
