package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ClubService {

    @Autowired
    ClubRepository clubRepository;

    /**
     * Gets a page of all clubs.
     *
     * @param pageable A page object showing how the page should be shown
     *                 (Page size, page count, and [optional] sorting)
     * @return A slice of clubs returned from pagination
     */
    public Page<Club> getPaginatedClubs(Pageable pageable) {
        return clubRepository.findAll(pageable);
    }

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
    public Club findClubById(Long id) {
        Optional<Club> club = clubRepository.findById(id);
        if (club.isPresent()) {
            return club.get();
        } else {
            return null;
        }
    }

    /**
     * Update or add a club
     * @param club club to be updated or added
     * @return saved club
     */
    public Club updateOrAddClub(Club club) {
        return clubRepository.save(club);
    }





}
