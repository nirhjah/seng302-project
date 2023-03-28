package nz.ac.canterbury.seng302.tab.service;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;

/**
 * Service class for User database entries, defined by the @link{Service} annotation.
 * This class links automatically with @link{UserRepository}, see the @link{Autowired} annotation below
 */
@Service
public class UserService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserRepository userRepository;

    /**
     * Gets a page of users.
     * 
     * @param pageable A page object showing how the page should be shown
     *                  (Page size, page count, and [optional] sorting)
     * @return A slice of users returned from pagination
     */
    public Page<User> getPaginatedUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * Gets a page of users, filtered down by their name and sports interest
     * 
     * @param pageable A page object showing how the page should be shown
     *                  (Page size, page count, and [optional] sorting)
     * @param favouriteSports Includes all users who have <em>at least one</em>
     *                          of these as their favourite sport
     * @param nameSearch Includes all users where
     *              <code>nameSearch</code> is a substring of <code>firstName+' '+lastName</code>
     * @return A slice of users with the applied filters
     */
    public Page<User> findUsersByNameOrSport(Pageable pageable, @Nullable List<String> favouriteSports, @Nullable String nameSearch) {
        logger.info("fav sports = {}", favouriteSports);
        logger.info("nameSearch = {}", nameSearch);
        if (favouriteSports == null) {
            favouriteSports = List.of();
        }
        if (nameSearch != null && nameSearch.isEmpty()) {
            nameSearch = null;
        }
        logger.info("...nameSearch = {}", nameSearch);
        logger.info("...fav sports = {}", favouriteSports);
        return userRepository.findAllFiltered(pageable, favouriteSports, nameSearch);
    }

    /**
     * Finds a user by their ID.
     * @param id The user's database ID
     * @return An optional object, containing either the user if they exist, otherwise it's empty.
     */
    public Optional<User> findUserById(long id) {
        return userRepository.findById(id);
    }

    /**
     * Find a user by their email. Most likely used for signing in.
     * @param email
     * @return An optional object, containing either the user if they exist, otherwise it's empty.
     */
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * <h4>For Editing</h4>
     * <p>
     *  Checks whether the given email is in use <strong>by someone else</strong>,
     *  i.e. not the current user.
     * <br/>
     * This is so the user can keep their email while updating other details, without failing its "unique" constraint.
     * </p>
     *
     * If you simply want to see if the email is already used, see {@link UserService#emailIsInUse}
     * @param currentUser The user who's email we're excluding
     * @param email The email that we're checking is unique
     * @return <code>true</code> if another user has this email,
     *          <code>false</code> if it's the <code>currentUser</code>'s email, or if the email is unique
     */
    public boolean emailIsUsedByAnother(User currentUser, String email) {
        // If the user isn't changing their email, no 'unique' email constraints are broken
        if (currentUser.getEmail().equals(email)) {
            return false;
        }
        // If the email's changed, we must see that it's not in use
        return emailIsInUse(email);
    }

    /**
     * <h4>For Registration</h4>
     * <p>Checks whether the given email is already in the repository</p>
     *
     * If you want to see if <strong>another</strong> user has that email, see {@link UserService#emailIsUsedByAnother}
     * @param email The email that we're checking is unique
     * @return <code>true</code> if another user has this email
     */
    public boolean emailIsInUse(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Saves a user to persistence
     * @param user User to save to persistence
     * @return the saved user object
     */
    public User updateOrAddUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Finds a user by their email and password
     * @param email the users email
     * @param password the users password
     * @return the user matching the parameters
     */
    public User getUserByEmailAndPassword(String email, String password) {
        return userRepository.getUserByEmailAndPassword(email, password);
    }

    public Optional<User> getCurrentUser() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Issue: The security context chain gives you "Anonymous Authentication"
        //          if you're not logged in, so `isAuthenticated()` can be true.
        //        To get around this, check if you're anonymous.
        if (!auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        String email = auth.getName();
        return userRepository.findByEmail(email);
    }

    /**
     * Method which updates the picture by taking the MultipartFile type and updating the picture
     * stored in the team with id primary key.
     *
     * @param file MultipartFile file upload
     * @param id   Team's unique id
     */
    public void updatePicture(MultipartFile file, long id) {
        User user = userRepository.findById(id).get();

        //Gets the original file name as a string for validation
        String pictureString = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (pictureString.contains("..")) {
            logger.info("not a a valid file");
        }
        try {
            //Encodes the file to a byte array and then convert it to string, then set it as the pictureString variable.
            user.setPictureString(Base64.getEncoder().encodeToString(file.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Saved the updated picture string in the database.
        userRepository.save(user);
    }
}
