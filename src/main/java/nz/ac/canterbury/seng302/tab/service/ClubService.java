package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Club
 */
@Service
public class ClubService {


    private final ClubRepository clubRepository;

    @Autowired
    public ClubService(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
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
}
