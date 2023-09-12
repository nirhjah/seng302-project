package nz.ac.canterbury.seng302.tab.unit.repository;

import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.repository.SportRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SportRepositoryTest {
    @Autowired
    SportRepository sportRepository;
    private List<String> sportsList;

    @BeforeEach
    public void beforeEach() {
        sportRepository.deleteAll();
        sportsList = Arrays.asList("American Football", "Basketball", "Hockey", "Netball", "Rugby", "Soccer", "Tennis", "Volleyball");
        for (String sport : sportsList) {
            sportRepository.save(new Sport(sport));
        }
    }
    @Test
    @Order(1)
    public void testFindingById() {
        for (int i=0; i<sportsList.size(); i++){
            Optional<Sport> sport = sportRepository.findById(i+1);
            System.out.println(sport.get().getName());
            Assertions.assertEquals(sportsList.get(i), sport.get().getName());
        }
    }
    @Test
    @Order(2)
    public void testFindingAll(){
        List<Sport> actualSportList = sportRepository.findAll();
        int index=0;
        for (Sport sport : actualSportList){
            Assertions.assertEquals(sportsList.get(index), sport.getName());
            index++;
        }
    }
    @Test
    @Order(3)
    public void testFindingSportsByName(){
        for (String sport: sportsList) {
            Optional<Sport> actualSport = sportRepository.findSportByName(sport);
            Assertions.assertEquals(sport,actualSport.get().getName());
        }
    }

}
