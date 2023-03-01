package nz.ac.canterbury.seng302.tab.service;

import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

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

    public void  updatePicture(MultipartFile file,long id)
    {
        Team team = teamRepository.findById(id).get();
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        if(fileName.contains(".."))
        {
            System.out.println("not a a valid file");
        }
        try {
            team.setPicturePath(Base64.getEncoder().encodeToString(file.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        teamRepository.save(team);
    }
}
