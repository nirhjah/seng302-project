package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.repository.SportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *Spring Boot Service class for Sport Service
 */
@Service
public class SportService {

    @Autowired
    SportRepository sportRepository;

    @Autowired
    public SportService(  SportRepository sportRepository) {
        this.sportRepository = sportRepository;
    }


    public List<Sport> getAllSports() { return sportRepository.findAll(); }

    public Sport addSport(Sport sport) { return sportRepository.save(sport); }

    public Sport getTeam(long sportId) {
        return sportRepository.findById(sportId).orElse(null);
    }

    public Optional<Sport> findSportByName(String name) {
        return sportRepository.findSportByName(name);
    }

    public void deleteSport(Sport sport) { sportRepository.delete(sport); }

    public List<String> getAllSportNames() {
        List<Sport> allSports = getAllSports();
        List<String> sportNames = new ArrayList<>();
        for (Sport sport : allSports) {
            sportNames.add(sport.getName());
        }
        return sportNames;
    }

    public void addAllSports(List<Sport> sports) {
        for (Sport sport : sports) {
            addSport(sport);
        }
    }

    public void deleteSports(List<Sport> sports) {
        for (Sport sport : sports) {
            deleteSport(sport);
        }
    }

}
