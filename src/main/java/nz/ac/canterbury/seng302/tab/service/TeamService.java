package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 *Spring Boot Service class for Team Service
 */
@Service
public class TeamService {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private TeamRepository teamRepository;

    public List<Team> getTeamList() {
        return teamRepository.findAll();
    }

    public Team addTeam(Team team) {
        return teamRepository.save(team);
    }

    public Team getTeam(long teamID) {
        return teamRepository.findById(teamID).orElse(null);
    }

    public Team updateTeam(Team team) {
        return teamRepository.save(team);
    }

    public Page<Team> findPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return teamRepository.findAll(pageable);
    }

    /**
     * Method which updates the picture by taking the MultipartFile type and updating the picture
     * stored in the team with id primary key.
     *
     * @param file MultipartFile file upload
     * @param id   Team's unique id
     */
    public void updatePicture(MultipartFile file, long id) {
        Team team = teamRepository.findById(id).get();

        //Gets the original file name as a string for validation
        String pictureString = StringUtils.cleanPath(file.getOriginalFilename());
        if (pictureString.contains("..")) {
            System.out.println("not a valid file");
        }
        try {
            //Encodes the file to a byte array and then convert it to string, then set it as the pictureString variable.
            team.setPictureString(Base64.getEncoder().encodeToString(file.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Saved the updated picture string in the database.
        teamRepository.save(team);
    }

    /**
     * Method that finds paginated teams by city, using a list of cities to filter by selected by the user
     *
     * @param pageable page object
     * @param searchedLocations list of locations to filter by selected by the user
     * @param name the team name query inputted by the use
     * @return Page(s) of teams filtered by city/cities
     */
    public Page<Team> findPaginatedTeamsByCity(Pageable pageable, List<String> searchedLocations, String name) {

        if (searchedLocations == null) {
            searchedLocations = List.of();
        }
        return teamRepository.findTeamByFilteredLocations(searchedLocations, pageable, name);
    }

    /**
     * Method that finds paginated teams by city <strong>AND</strong> sports, using a list of both to filter by selected by the user
     *
     * @param pageable page object
     * @param searchedLocations list of locations to filter by selected by the user
     * @param searchedSports list of sports to filter by selected by the user
     * @param name the team name query inputted by the use
     * @return Page(s) of teams filtered by city/cities and sport/sports
     */
    public Page<Team> findPaginatedTeamsByCityAndSports(Pageable pageable, List<String> searchedLocations, List<String> searchedSports, String name) {

        if (searchedLocations == null) {
            searchedLocations = List.of();
        } else {
            searchedLocations = searchedLocations.stream().map(String::toLowerCase).toList();
        }

        if (searchedSports == null) {
            searchedSports = List.of();
        } else {
            searchedSports = searchedSports.stream().map(String::toLowerCase).toList();
        }
        return teamRepository.findTeamByFilteredLocationsAndSports(pageable, searchedLocations, searchedSports, name);
    }

    /**
     * gets a page of teams filtered by their name and sport
     *
     * @param pageable a page object showing how the page should be shown
     * @param filterSports List of sports to be filtered by
     * @param nameSearch the search query
     * @return a slice of teams that meet the name conditions and filter conditions
     */
    public Page<Team> findTeamsByNameOrSport(Pageable pageable, List<String> filterSports, String nameSearch)
    {
        if (filterSports == null) {
            filterSports = List.of();
        }
        if (nameSearch != null && nameSearch.isEmpty()) {
            nameSearch = null;
        }
        return teamRepository.findTeamByNameAndSportIn(pageable, filterSports, nameSearch);
    }
}
