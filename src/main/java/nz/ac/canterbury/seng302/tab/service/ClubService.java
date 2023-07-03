package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for Club
 */
@Service
public class ClubService {

    @Autowired
    ClubRepository clubRepository;

    /**
     * Gets list of all clubs
     * @return list of all clubs
     */
    public List<Club> findAll() {
        return clubRepository.findAll();
    }

    /**
     * Finds club by id
     *
     * @param id id of club to find
     * @return club or null
     */
    public Optional<Club> findClubById(Long id) {
        return clubRepository.findById(id);

    }

    /**
     * Update or add a club
     * @param club club to be updated or added
     * @return saved club
     */
    public Club updateOrAddClub(Club club) {
        return clubRepository.save(club);
    }


    /**
     * Checks if all teams to be added have the same sport
     * @param teamsToAdd list of teams to add to club
     * @return true if all teams are valid or false if not
     */
    public boolean validateTeamSportsinClub(List<Team> teamsToAdd) {

        for (Team team : teamsToAdd) {
            if (!team.getSport().equals(teamsToAdd.get(0).getSport()))
                return false;
        }
        return true;
    }



}
