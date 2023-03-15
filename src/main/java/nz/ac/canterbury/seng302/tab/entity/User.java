package nz.ac.canterbury.seng302.tab.entity;

import java.util.*;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Entity(name = "UserEntity")
public class User {

    protected User() {
    }

    public static User defaultDummyUser() {
        return new User(
                "test",
                "again",
                new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(),
                "test@gmail.com",
                "dfghjk");
    }

    /**
     * TODO: Implement password hashing, probably via Bcrypt
     */
    public User(String firstName, String lastName, Date dateOfBirth, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.hashedPassword = password;
    }

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dateOfBirth = new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime();
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
    private Date dateOfBirth;

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

    public String getEmail() {
        return email;
    }

    public String getPassword() {return hashedPassword; }

    public void setHashedPassword(String password) {this.hashedPassword = password;}

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
