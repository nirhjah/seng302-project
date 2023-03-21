package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Service class for User database entries, defined by the @link{Service} annotation.
 * This class links automatically with @link{UserRepository}, see the @link{Autowired} annotation below
 */
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    /**
     * Gets a page of users.
     * 
     * @param pageSize How many users are in a "page"
     * @param pageNumber The page number (page 0 is the first page)
     * @return A slice of users returned from pagination
     */
    public Page<User> getPaginatedUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<User> findUsersByName(Pageable pageable, String name) {
        return userRepository.findAllByFirstNameIgnoreCaseContainingOrLastNameIgnoreCaseContaining(pageable, name, name);
    }

    public Page<User> findUsersByName(Pageable pageable, String firstName, String lastName) {
        return userRepository.findAllByFirstNameIgnoreCaseContainingAndLastNameIgnoreCaseContaining(pageable, firstName, lastName);
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
            System.out.println("not a a valid file");
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
