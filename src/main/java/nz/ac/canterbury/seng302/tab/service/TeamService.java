package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

/**
 *Spring Boot Service class for Team Service
 */
@Service
public class TeamService {
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
    
    public Page<Team> findPaginatedTeamsByCity(Pageable pageable, List<String> searchedLocations) {

        if (searchedLocations == null) {
            searchedLocations = List.of();
        }
        return teamRepository.findTeamByFilteredLocations(searchedLocations, pageable);
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
}
