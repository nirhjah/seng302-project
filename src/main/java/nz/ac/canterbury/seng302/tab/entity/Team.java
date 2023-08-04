package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;

import nz.ac.canterbury.seng302.tab.enums.Role;
import nz.ac.canterbury.seng302.tab.helper.ImageType;
import nz.ac.canterbury.seng302.tab.helper.interfaces.HasImage;
import nz.ac.canterbury.seng302.tab.helper.interfaces.Identifiable;
import nz.ac.canterbury.seng302.tab.helper.exceptions.UnmatchedSportException;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.hibernate.Hibernate;

import java.io.IOException;

import java.time.LocalDateTime;

import java.util.*;

/**
 * Class for Team object which is annotated as a JPA entity.
 */
@Entity
public class Team implements Identifiable, HasImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamId;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Location location;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String sport;

    @Column
    private String token;

    @Column()
    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<TeamRole> teamRoles = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "team_members",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> teamMembers = new HashSet<User>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="clubId")
    private Club teamClub;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private Grade grade;

    @Enumerated(value = EnumType.STRING)
    private ImageType imageType;

    protected Team() {
    }

    public Team(String name, String sport, Location location) throws IOException {
        this.name = name;
        this.location = location;
        this.sport = sport;
        this.token = generateToken();
        this.creationDate = LocalDateTime.now();
        this.grade = new Grade(Grade.Age.ADULT, Grade.Sex.OTHER);
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
        // set the manager
        this.setManager(manager);
        this.creationDate = LocalDateTime.now();
        this.grade = new Grade(Grade.Age.ADULT, Grade.Sex.OTHER);
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
        this.creationDate = LocalDateTime.now();
    }

    public ImageType getImageType() {
        return imageType;
    }
    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public Long getTeamId() {
        return this.teamId;
    }

    public long getId() {
        return teamId;
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

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public LocalDateTime getCreationDate() {return creationDate;}

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

    public Set<User> getTeamManagers() {
        Set<User> managers = new HashSet<>();
        Hibernate.initialize(teamRoles);
        for (var tRole : teamRoles) {
            if (tRole.getRole() == Role.MANAGER) {
                managers.add(tRole.getUser());
            }
        }
        return managers;
    }

    public Set<User> getTeamCoaches() {
        Set<User> coaches = new HashSet<>();
        for (var tRole: teamRoles) {
            if (tRole.getRole() == Role.COACH) {
                coaches.add(tRole.getUser());
            }
        }
        return coaches;
    }

    /**
     * Returns true is user is a manager, false otherwise
     * @param user The user in question
     * @return true if user manages team, false otherwise
     */
    public boolean isManager(User user) {
        var userId = user.getUserId();
        return getTeamManagers().stream().anyMatch((u) -> u.getUserId() == userId);
    }

    public boolean isCoach(User user) {
        var userId = user.getUserId();
        return getTeamCoaches().stream().anyMatch((u) -> u.getUserId() == userId);
    }


    /**
     * Remove all team roles for this user.
     * We should call this function if we are updating a user's role.
     * @param user The user to remove the team roles for
     *
     */
    private void removeTeamRoleForUser(User user) {
        var id = user.getUserId();
        teamRoles.removeIf(tRole -> tRole.getUser().getUserId() == id);
    }

    /**
     * @param user, the User we are changing
     * @param role the role we are changing to user to
     */
    public void setRole(User user, Role role) {
        removeTeamRoleForUser(user);
        TeamRole teamRole = new TeamRole();
        teamRole.setUser(user);
        teamRole.setRole(role);
        teamRole.setTeam(this);
        teamRoles.add(teamRole);
        teamMembers.add(user);
    }

    public Set<TeamRole> getTeamRoles() {
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

    public Club getTeamClub() {
        return teamClub;
    }

    /**
     * Sets the club of a team.
     * If the club is invalid (i.e. Club sport doesn't match team sport)
     * an exception is thrown.
     * @param teamClub
     */
    public void setTeamClub(Club teamClub) {
        // TODO: We shouldn't be comparing string here, we should ideally have these
        //   columns referencing actual sport entities.
        if (!teamClub.getSport().equals(getSport())) {
            throw new UnmatchedSportException(teamClub.getSport(), getSport());
        }
        this.teamClub = teamClub;
    }

    public void clearTeamClub() {
        this.teamClub = null;
    }

    @Override
    public String toString() {
        return name;
    }


}
