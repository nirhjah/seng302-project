package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email; import jakarta.validation.constraints.Pattern; import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Entity(name = "UserEntity")
public class User {

    public User() {
    }

    public User(String firstName, String lastName, LocalDate dateOfBirth, String email, String password, List<Sport> favoriteSports, String location) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.hashedPassword = password;
        this.favoriteSports = favoriteSports;
        this.location = location;
        this.userRoles = Collections.emptyList();
    }

    public static User defaultDummyUser() throws IOException{
        return new User(
                "test",
                "again",
                LocalDate.EPOCH,
                "test@gmail.com",
                "dfghjk",
                new ArrayList<>());
    }

    /**
     * TODO: Implement password hashing, probably via Bcrypt
     */
    public User(String firstName, String lastName, LocalDate dateOfBirth, String email, String password, List<Sport> favoriteSports) throws IOException {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.hashedPassword = password;
        this.favoriteSports = favoriteSports;
        Resource resource = new ClassPathResource("/static/image/default-profile.png");
        File file = resource.getFile();
        this.pictureString = Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
    }

    public User(String firstName, String lastName, LocalDate dateOfBirth, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.hashedPassword = password;

        this.favoriteSports = new ArrayList<>();
    }
    
    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dateOfBirth = LocalDate.EPOCH;
        this.hashedPassword = password;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private long userId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(columnDefinition = "MEDIUMBLOB")
    private String pictureString;

    @Column()
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "Id")
    private List<Sport> favoriteSports;


    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
            flags = Pattern.Flag.CASE_INSENSITIVE)
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String hashedPassword;

    @Column(nullable = true)
    private String location;

    public long getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * Returns the date as a string in a 'yyyy-MM-dd format, such that it can be directly parsed in an
     * HTML date object
     *
     * @return date string
     */
    public String getDateOfBirthFormatted() {
        SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String isoDate = isoDateFormat.format(dateOfBirth);
        return isoDate;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {return hashedPassword; }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPictureString() {
        return this.pictureString;
    }

    public void setPictureString(String pictureString) {
        this.pictureString = pictureString;
    }

    @Column()
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "Id")
    private List<Authority> userRoles;

    public void grantAuthority(String authority)
    {
        if (userRoles == null) {
            userRoles = new ArrayList<Authority>();
        }
        userRoles.add(new Authority(authority));
    }

    public List<GrantedAuthority> getAuthorities()
    {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        this.userRoles.forEach(authority -> authorities.add(new SimpleGrantedAuthority(authority.getRole())));
        return authorities;
    }

    public List<Sport> getFavoriteSports() {
        return favoriteSports;
    }

    public void setFavoriteSports(List<Sport> favoriteSports) {
        this.favoriteSports = favoriteSports;
    }

    /**
     * TODO: IMPLEMENT. There shouldn't be a way to see the password, only to check
     * if it's right.
     * 
     * @param password The password provided by the user that we're checking
     * @return true/false if the provided password is the same one we've stored
     */
    public boolean checkPassword(String password) {
        throw new RuntimeException("NOT IMPLEMENTED!!!");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(dateOfBirth, user.dateOfBirth) && Objects.equals(List.copyOf(favoriteSports), List.copyOf(user.favoriteSports)) && Objects.equals(email, user.email) && Objects.equals(hashedPassword, user.hashedPassword) && Objects.equals(location, user.location) && Objects.equals(List.copyOf(userRoles), List.copyOf(user.userRoles));
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, dateOfBirth, favoriteSports, email, hashedPassword, location, userRoles);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", favoriteSports=" + favoriteSports +
                ", email='" + email + '\'' +
                ", hashedPassword='" + hashedPassword + '\'' +
                ", location='" + location + '\'' +
                ", userRoles=" + userRoles +
                '}';
    }
}
