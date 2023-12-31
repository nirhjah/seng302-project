package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import nz.ac.canterbury.seng302.tab.enums.AuthorityType;
import nz.ac.canterbury.seng302.tab.enums.Role;
import nz.ac.canterbury.seng302.tab.helper.ImageType;
import nz.ac.canterbury.seng302.tab.helper.interfaces.HasImage;
import nz.ac.canterbury.seng302.tab.helper.interfaces.Identifiable;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;

import java.sql.Timestamp;


import java.util.*;

@Entity(name = "UserEntity")
public class User implements Identifiable, HasImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private long userId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private Date dateOfBirth;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "favSports")
    private List<Sport> favoriteSports;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_locationId", referencedColumnName = "locationId")
    private Location location;

    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", flags = Pattern.Flag.CASE_INSENSITIVE)
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String hashedPassword;

    @Column
    private boolean emailConfirmed;

    @Column
    private Date expiryDate;

    @Column
    private String token;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<TeamRole> teamRoles;

    @Enumerated
    private ImageType profilePictureType;

    public User() {

    }

    public static User defaultDummyUser() throws IOException {
        return new User(
                "test",
                "again",
                new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(),
                "test@gmail.com",
                "dfghjk",
                new ArrayList<>(),
                new Location(null, null, null, "Christchurch", null, "New Zealand"));

    }

    /**
     * <strong>WARNING:</strong> Passwords are NOT hashed here. You have to provide
     * a hashed password
     * (By Autowiring a <code>PasswordEncoder</code>)
     */
    public User(String firstName, String lastName, Date dateOfBirth, String email, String password,
            List<Sport> favoriteSports, Location location) throws IOException {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.hashedPassword = password;
        this.favoriteSports = favoriteSports;
        this.location = location;
    }

    /**
     * <strong>WARNING:</strong> Passwords are NOT hashed here. You have to provide
     * a hashed password
     * (By Autowiring a <code>PasswordEncoder</code>)
     */
    public User(String firstName, String lastName, Date dateOfBirth, String email, String password, Location location) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.hashedPassword = password;
        this.favoriteSports = new ArrayList<>();
        this.location = location;
    }

    /**
     * <strong>WARNING:</strong> Passwords are NOT hashed here. You have to provide
     * a hashed password
     * (By Autowiring a <code>PasswordEncoder</code>)
     */
    public User(String firstName, String lastName, String email, String password, Location location) throws Exception {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dateOfBirth = new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime();
        this.hashedPassword = password;
        this.location = location;
    }

    @ManyToMany(mappedBy = "teamMembers")
    private Set<Team> joinedTeams = new HashSet<Team>();

    @Override
    public ImageType getImageType() {
        return profilePictureType;
    }

    @Override
    public void setImageType(ImageType imageType) {
        this.profilePictureType = imageType;
    }


    public long getUserId() {
        return userId;
    }

    @Override
    public long getId() {
        return getUserId();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setFullName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {return hashedPassword; }

    /**
     * <p>Sets the user's password</p>
     * <strong>THIS DOES NOT HASH THE PASSWORD!</strong>, you have to do it.
     * 
     * @param password The password string to be set.
     */
    public void setPassword(String password) {
        this.hashedPassword = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Date getExpiryDate() {
        return this.expiryDate;
    }

    public boolean getEmailConfirmed() {
        return emailConfirmed;
    }

    /**
     * Confirms the user's email
     */
    public void confirmEmail() {
        this.emailConfirmed = true;
    }

    public boolean getConfirmEmail(){
        return this.emailConfirmed;
    }

    @Column()
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "userId")
    private List<Authority> userRoles;

    /**
     * Assigns this user the provided role.
     * <p><em>
     *  Note: There are no duplicate role checks. If that's a problem, roll your own check.
     * </em></p>
     * @param authority The authority/role you're providing.
     */
    public void grantAuthority(AuthorityType authority) {
        if (userRoles == null) {
            userRoles = new ArrayList<>();
        }
        userRoles.add(new Authority(authority.role()));
    }

    public List<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (userRoles != null) {
            this.userRoles.forEach(authority -> authorities.add(new SimpleGrantedAuthority(authority.getRole())));
        }
        return authorities;
    }

    public List<Sport> getFavoriteSports() {
        return favoriteSports;
    }

    public void setFavoriteSports(List<Sport> favoriteSports) {
        this.favoriteSports = favoriteSports;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        User user = (User) o;

        if (getFirstName() != null ? !getFirstName().equals(user.getFirstName()) : user.getFirstName() != null)
            return false;
        if (getLastName() != null ? !getLastName().equals(user.getLastName()) : user.getLastName() != null)
            return false;
        if (getDateOfBirth() != null ? !getDateOfBirth().equals(user.getDateOfBirth()) : user.getDateOfBirth() != null)
            return false;
        if (getEmail() != null ? !getEmail().equals(user.getEmail()) : user.getEmail() != null)
            return false;
        if (!Objects.equals(hashedPassword, user.hashedPassword))
            return false;
        return Objects.equals(userRoles, user.userRoles);
    }

    @Override
    public int hashCode() {
        int result = getFirstName() != null ? getFirstName().hashCode() : 0;
        result = 31 * result + (getLastName() != null ? getLastName().hashCode() : 0);
        result = 31 * result + (getDateOfBirth() != null ? getDateOfBirth().hashCode() : 0);
        result = 31 * result + (getEmail() != null ? getEmail().hashCode() : 0);
        result = 31 * result + (hashedPassword != null ? hashedPassword.hashCode() : 0);
        result = 31 * result + (userRoles != null ? userRoles.hashCode() : 0);
        return result;
    }

    public List<String> getFavouriteSportNames() {
        List<String> sport = new ArrayList<>();
        for (Sport s : favoriteSports) {
            sport.add(s.getName());
        }
        return sport;
    }

    /**
     * Calculates the expiry date of the verification token based on the current
     * time and the specified expiry time in hours.
     *
     * @param expiryTimeInHours the expiry time in hours
     *                          set the expiry date of the verification token
     */
    private void calculateExpiryDate(int expiryTimeInHours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Timestamp(calendar.getTime().getTime()));
        calendar.add(Calendar.HOUR, expiryTimeInHours);
        setExpiryDate(new Date(calendar.getTime().getTime()));
    }

    /**
     * Generates a random string of characters to be used as a verification token.
     *
     * @return a randomly generated verification token
     */

    private static String generateToken() {
        final int USER_TOKEN_SIZE = 12;
        return UUID.randomUUID().toString().replaceAll("\\-*", "").substring(0, USER_TOKEN_SIZE);
    }

    /**
     * Generates a unique verification token and set the token and expiryDate
     * columns
     *
     * @param userService the service is used to check if the token is already in
     *                    use
     * @param expiryHour  an integer which is the hours till the token is expired
     *
     */

    public void generateToken(UserService userService, int expiryHour) {
        String token = generateToken(); // generate random token
        while (userService.findByToken(token).isPresent()) {
            // if this token is already taken, generate another one.  (Code will likely never get here)
            token = generateToken();
        }
        setToken(token);
        calculateExpiryDate(expiryHour);
    }

    /**
     * Adds user to given team and sets their role as a Member.
     *
     * @param team team to add user to
     */
    public void joinTeam(Team team) {
        this.joinedTeams.add(team);
        team.setRole(this, Role.MEMBER);
    }

    public Set<Team> getJoinedTeams() {
        return this.joinedTeams;
    }

    public void setJoinedTeams(Set<Team> teams) {
        this.joinedTeams = teams;
    }
}




