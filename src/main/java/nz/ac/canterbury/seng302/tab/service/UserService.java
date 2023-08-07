package nz.ac.canterbury.seng302.tab.service;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.authentication.EmailVerification;
import nz.ac.canterbury.seng302.tab.authentication.TokenVerification;
import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.mail.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;

/**
 * Service class for User database entries, defined by the @link{Service}
 * annotation.
 * This class links automatically with @link{UserRepository}, see
 * the @link{Autowired} annotation below
 */
@Service
@Configuration
@ComponentScan("nz.ac.canterbury.seng302.tab.service")
public class UserService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UserRepository userRepository;

    private final TaskScheduler taskScheduler;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, TaskScheduler taskScheduler, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.taskScheduler = taskScheduler;
        this.passwordEncoder = passwordEncoder;
    }


    public static final Sort SORT_BY_LAST_AND_FIRST_NAME = Sort.by(
        Order.asc("lastName").ignoreCase(),
        Order.asc("firstName").ignoreCase()
    );



    /**
     * Gets a page of users.
     * 
     * @param pageable A page object showing how the page should be shown
     *                 (Page size, page count, and [optional] sorting)
     * @return A slice of users returned from pagination
     */
    public Page<User> getPaginatedUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Optional<User> findByToken(String token) {
        return userRepository.findByToken(token);
    }


    /**
     * Gets a page of users, filtered down by their name and sports interest
     * 
     * @param pageable        A page object showing how the page should be shown
     *                        (Page size, page count, and [optional] sorting)
     * @param favouriteSports Includes all users who have <em>at least one</em>
     *                        of these as their favourite sport
     * @param favouriteCities Includes all users who have <em>at least one</em>
     *                        of these as their filtered city
     * @param nameSearch      Includes all users where
     *                        <code>nameSearch</code> is a substring of
     *                        <code>firstName+' '+lastName</code>
     * @return A slice of users with the applied filters
     */
    public Page<User> findUsersByNameOrSportOrCity(Pageable pageable,
            @Nullable List<String> favouriteSports,
            @Nullable List<String> favouriteCities,
            @Nullable String nameSearch) {

        logger.info("fav cities = {}", favouriteCities);
        logger.info("fav sports = {}", favouriteSports);
        logger.info("nameSearch = {}", nameSearch);
        if (favouriteSports == null) {
            favouriteSports = List.of();
        }
        if (favouriteCities == null) {
            favouriteCities = List.of();
        }
        if (nameSearch != null && nameSearch.isEmpty()) {
            nameSearch = null;
        }
        logger.info("...nameSearch = {}", nameSearch);
        logger.info("...fav sports = {}", favouriteSports);
        logger.info("...fav city = {}", favouriteCities);
        return userRepository.findUserByFilteredLocationsAndSports(pageable, favouriteCities, favouriteSports,
                nameSearch);
    }

    /**
     * returns a list of the locations that are relevant to the current search, this
     * means that we can populate the filter buttons with locations that only appear
     * in the results table.
     * if the search is empty, all the users will be displayed and so we will return
     * all locations
     * 
     * @param name The current query, this is the current search in the search bar
     * @return a list of the locations that is relevant to the users that were
     *         returned from the search
     **/
    public List<Location> findLocationBysearch(String name) {
        if (name == null || name.isEmpty()) {
            return userRepository.findAllUserLocations();
        }
        List<Location> listOfLocations = userRepository.findLocationByUser(name);
        return listOfLocations;
    }

    /**
     * returns a list of the sports that are relevant to the current search, this
     * means that we can populate the filter buttons with sports that only appear
     * in the results table.
     * if the search is empty, all the users will be displayed and so we will return
     * all sports
     *
     * @param name The current query, this is the current search in the search bar
     * @return a list of the sports that is relevant to the users that were
     *         returned from the search
     **/
    public List<Sport> findSportBysearch(String name) {
        if (name == null || name.isEmpty()) {
            return  userRepository.findAllUserSports(); //find ALL sports if search is blank
        }
        List<Sport> listOfUserSports = userRepository.findSportByUser(name);
        return listOfUserSports;

    }

    /**
     * Finds a user by their ID.
     * 
     * @param id The user's database ID
     * @return An optional object, containing either the user if they exist,
     *         otherwise it's empty.
     */
    public Optional<User> findUserById(long id) {
        return userRepository.findById(id);
    }

    /**
     * Find a user by their email. Most likely used for signing in.
     * 
     * @param email the email that's being checked to see if it already has an associated account
     * @return An optional object, containing either the user if they exist,
     *         otherwise it's empty.
     */
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * <h4>For Editing</h4>
     * <p>
     * Checks whether the given email is in use <strong>by someone else</strong>,
     * i.e. not the current user.
     * <br/>
     * This is so the user can keep their email while updating other details,
     * without failing its "unique" constraint.
     * </p>
     *
     * If you simply want to see if the email is already used, see
     * {@link UserService#emailIsInUse}
     * 
     * @param currentUser The user who's email we're excluding
     * @param email       The email that we're checking is unique
     * @return <code>true</code> if another user has this email,
     *         <code>false</code> if it's the <code>currentUser</code>'s email, or
     *         if the email is unique
     */
    public boolean emailIsUsedByAnother(User currentUser, String email) {
        // If the user isn't changing their email, no 'unique' email constraints are
        // broken
        if (currentUser.getEmail().equals(email)) {
            return false;
        }
        // If the email's changed, we must see that it's not in use
        return emailIsInUse(email);
    }

    /**
     * <h4>For Registration</h4>
     * <p>
     * Checks whether the given email is already in the repository
     * </p>
     *
     * If you want to see if <strong>another</strong> user has that email, see
     * {@link UserService#emailIsUsedByAnother}
     * 
     * @param email The email that we're checking is unique
     * @return <code>true</code> if another user has this email
     */
    public boolean emailIsInUse(String email) {
        return userRepository.existsByEmail(email);
    }

    private static final Duration TOKEN_EXPIRY_TIME = Duration.ofHours(2);

    /**
     * Saves a user to persistence. Starts a timer for two hours whereupon the user will be
     * deleted if they have not verified their email
     * 
     * @param user User to save to persistence
     * @return the saved user object
     */
    public User updateOrAddUser(User user) {
        if (!user.getEmailConfirmed()) {
            Instant executionTime = Instant.now().plus(TOKEN_EXPIRY_TIME);
            taskScheduler.schedule(new EmailVerification(user, userRepository), executionTime);
        }
        return userRepository.save(user);
    }

    /**
     * Checks who the logged in user of this session is.
     * <p>
     * Note: If the current user is authenticated, but their username isn't in the
     * database (e.g. it got deleted), it'll also return empty
     * </p>
     * 
     * @return The currently logged in user, otherwise empty.
     */
    public Optional<User> getCurrentUser() {

        // getContext() only returns null if we're inside a test
        if (SecurityContextHolder.getContext() == null) {
            return Optional.empty();
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Issue: The security context chain gives you "Anonymous Authentication"
        // if you're not logged in, so `isAuthenticated()` can be true.
        // To get around this, check if you're anonymous.
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        String email = auth.getName();
        return userRepository.findByEmail(email);
    }

    /**
     * Method which updates the picture by taking the MultipartFile type and
     * updating the picture
     * stored in the team with id primary key.
     *
     * @param file MultipartFile file upload
     * @param id   Team's unique id
     */
    public void updatePicture(MultipartFile file, long id) {
        User user = userRepository.findById(id).get();

        // Gets the original file name as a string for validation
        String pictureString = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (pictureString.contains("..")) {
            logger.info("not a a valid file");
        }
        try {
            // Encodes the file to a byte array and then convert it to string, then set it
            // as the pictureString variable.
            user.setPictureString(Base64.getEncoder().encodeToString(file.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Saved the updated picture string in the database.
        userRepository.save(user);
    }

    /**
     * Updates the user's password then creates and sends email informing the user that their password has been updated.
     * @param user the user whose password was updated
     * @param password the password to update the user with
     */
    public void updatePassword(User user, String password) throws MessagingException {
        user.setPassword(passwordEncoder.encode(password));
        updateOrAddUser(user);
    }


    /**
     * Creates a reset password link with unique token for the user and sends it to their email
     * @param user      user to send reset password link to
     * @param request   to get current url to create the link
     */
    public void resetPasswordEmail(User user, HttpServletRequest request) throws MessagingException {

        user.generateToken(this, 1);
        updateOrAddUser(user);

        taskScheduler.schedule(new TokenVerification(user, this), Instant.now().plus(Duration.ofHours(1)));
    }



    /**
     * Adds user to team and updates user
     * @param user user to join team
     * @param team team for user to join
     */
    public void userJoinTeam(User user, Team team) {
        user.joinTeam(team);
        updateOrAddUser(user);
    }

    /**
     * gets all users who arent federation managers
     * @param pageable
     * @return all the users who arent federation managers
    */
    public Page<User> getAllUsersNotFedMans(Pageable pageable) {
        return userRepository.findUsersThatArentFedMans(pageable);
    }

    /**
     * searches all users who arent a fedman by name
     *
     * @param pageable
     * @param name a string to search the name by
     * @return
    */
    public Page<User> getAllUsersNotFedMansByName(Pageable pageable, String name) {
        return userRepository.findUsersThatArentFedMansByName(pageable, name);
    }

    /**
     * searches all users who arent a fedman by email
     *
     * @param pageable
     * @param email a string to search the name by
     * @return
    */
    public Page<User> getAllUsersNotFedMansByEmail(Pageable pageable, String email) {
        return userRepository.findUsersThatArentFedMansByEmail(pageable, email);
    }
}
