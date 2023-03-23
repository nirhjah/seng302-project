package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.repository.SportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 *Spring Boot Service class for Sport Service
 */
@Service
public class SportService {

    @Autowired
    SportRepository sportRepository;

    public List<Sport> getAllSports() { return sportRepository.findAll(); }

    public Sport addSport(Sport sport) { return sportRepository.save(sport); }

    public Sport getTeam(long sportId) {
        return sportRepository.findById(sportId).orElse(null);
    }

    public Optional<Sport> findSportByName(String name) {
        return sportRepository.findSportByName(name);
    }

    public void deleteSport(Sport sport) { sportRepository.delete(sport); }

}
