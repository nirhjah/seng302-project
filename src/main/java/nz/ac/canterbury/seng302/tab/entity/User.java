package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.text.SimpleDateFormat;
import java.util.*;

@Entity(name = "UserEntity")
public class User {

    public User() {
    }

    public static User defaultDummyUser() {
        return new User(
                "test",
                "again",
                new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(),
                "test@gmail.com",
                "dfghjk",
                new ArrayList<>());
    }

    /**
     * TODO: Implement password hashing, probably via Bcrypt
     */
    public User(String firstName, String lastName, Date dateOfBirth, String email, String password, List<Sport> favoriteSports) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.hashedPassword = password;
        this.favoriteSports = favoriteSports;
    }

    public User(String firstName, String lastName, Date dateOfBirth, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.hashedPassword = password;
        this.favoriteSports = new ArrayList<>();
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
    private Date dateOfBirth;

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

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
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

        if (getFirstName() != null ? !getFirstName().equals(user.getFirstName()) : user.getFirstName() != null)
            return false;
        if (getLastName() != null ? !getLastName().equals(user.getLastName()) : user.getLastName() != null)
            return false;
        if (getDateOfBirth() != null ? !getDateOfBirth().equals(user.getDateOfBirth()) : user.getDateOfBirth() != null)
            return false;
        if (getEmail() != null ? !getEmail().equals(user.getEmail()) : user.getEmail() != null) return false;
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
}
