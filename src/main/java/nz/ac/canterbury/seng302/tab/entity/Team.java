package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;
import nz.ac.canterbury.seng302.tab.enums.Role;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.*;
import java.nio.file.Files;
import java.util.*;

/**
 * Class for Team object which is annotated as a JPA entity.
 */
@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamId;

    @ManyToOne(cascade = CascadeType.ALL)
    private Location location;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String sport;

    @Column(columnDefinition = "MEDIUMBLOB")
    private String pictureString;

    @Column
    private String token;

    @Column()
    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<TeamRole> teamRoles;

    @ManyToMany
    @JoinTable(
            name = "team_members",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> teamMembers = new HashSet<User>();

    protected Team() {
    }

    public Team(String name, String sport, Location location) throws IOException {
        this.name = name;
        this.location = location;
        this.sport = sport;
        Resource resource = new ClassPathResource("/static/image/default-profile.png");
        InputStream is = resource.getInputStream();
        this.pictureString = Base64.getEncoder().encodeToString(is.readAllBytes());
        this.teamRoles = new ArrayList<>();
        this.creationDate = LocalDateTime.now();
        this.token = generateToken();
    }

    /**
     * constructor that sets the manager
     *
     * Should be used for testing ONLY!
     * TODO: Remove this constructor, use builder pattern. same for user
     *
     * @param name
     * @param sport
     * @param location
     * @param manager
     * @throws IOException
     */
    public Team(String name, String sport, Location location, User manager) throws IOException {
        this.name = name;
        this.location = location;
        this.sport = sport;
        Resource resource = new ClassPathResource("/static/image/default-profile.png");
        InputStream is = resource.getInputStream();
        this.pictureString = Base64.getEncoder().encodeToString(is.readAllBytes());
        // set the manager
        this.teamRoles = new ArrayList<>();
        this.setManager(manager);
        this.creationDate = LocalDateTime.now();
    }

    /**
     * Should be used for testing ONLY!
     * TODO: Remove this constructor, use builder pattern. same for user
     *
     * @param name  - team name
     * @param sport - sport name
     */
    public Team(String name, String sport) throws IOException {
        this.name = name;
        // create a dummy location
        this.location = new Location("address1", "address2", "suburb", "chch", "8052", "new zealand");
        this.sport = sport;
        Resource resource = new ClassPathResource("/static/image/default-profile.png");
        InputStream is = resource.getInputStream();
        this.pictureString = Base64.getEncoder().encodeToString(is.readAllBytes());
        this.creationDate = LocalDateTime.now();
        this.teamRoles = new ArrayList<>();
    }

    /**
     * Returns true is user is a manager, false otherwise
     * @param user The user in question
     * @return true if user manages team, false otherwise
     */
    public boolean isManager(User user) {
        // TODO: this is a facade and needs to be implemented
        return true;
    }

    public Long getTeamId() {
        return this.teamId;
    }

    public String getName() {
        return this.name;
    }

    public String getSport() {
        return this.sport;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String getPictureString() {
        return this.pictureString;
    }

    public void setPictureString(String pictureString) {
        this.pictureString = pictureString;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private static String generateToken(){
        final int TEAM_TOKEN_SIZE = 12;
        return UUID.randomUUID().toString().replaceAll("\\-*", "").substring(0, TEAM_TOKEN_SIZE);
    }

    /**
     * U24/AC1 states that the token must consist of letters and numbers, the UUID
     * method will generate '-'s aswell so we replace all occurances with the empty
     * string
     *
     * @return new random token only containing characters and numbers
     */
    public void generateToken(TeamService teamService) {
        String token = generateToken();
        while (teamService.findByToken(token).isPresent()) {
            token = generateToken();
        }
        setToken(token);
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * @param user, the
     * @param role
     */
    public void setRole(User user, Role role) {

        TeamRole teamRole = new TeamRole();
        teamRole.setUser(user);
        teamRole.setRole(role);
        teamRole.setTeam(this);
        this.teamRoles.add(teamRole);
    }

    public List<TeamRole> getTeamRoleList() {
        return this.teamRoles;
    }

    public void setMember(User user) {
        this.setRole(user, Role.MEMBER);
    }

    public void setCoach(User user) {
        this.setRole(user, Role.COACH);
    }

    public void setManager(User user) {
        this.setRole(user, Role.MANAGER);
    }

    public Set<User> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(Set<User> teamMembers) {
        this.teamMembers = teamMembers;
    }

}
