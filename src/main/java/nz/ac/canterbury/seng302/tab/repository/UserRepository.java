package nz.ac.canterbury.seng302.tab.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.User;

/**
 * FormResult repository accessor using Spring's @link{CrudRepository}.
 * These (basic) methods are provided for us without the need to write our own implementations
 */
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findById(long id);
    Optional<User> findByEmail(String email);
    List<User> findAll();

    boolean existsByEmail(String email);

    @Query("SELECT u FROM UserEntity u WHERE u.email = :email and u.hashedPassword = :password")
    User getUserByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    List<User> findAll(Pageable pageable);

    /*
     * Query Logic:
     * - The `:? is null or ...` means the filter isn't applied if you give it a null. 
     * - U17 AC2 specifies that provided a list of sports,
     *      get all users who have at least one of these sports.
     *      This is achieved via the join, and the `s in :sports` check.
     * - U7 AC4 states that a search succeeds if "The search string is contained in the first or last name".
     *      However, this would mean that User("John", "Capenter") would never be found by searching
     *      "John Capenter", as "John Capenter" isn't in "John", nor is it in "Capenter".
     *      So, we concatenate it and then check.
     */
    @Query("SELECT distinct u FROM UserEntity u JOIN u.favouriteSports s"
            +"WHERE (:sports is null or s in :sports)"
            +"AND (:name is null or lower(:name) like lower(CONCAT(u.firstName, ' ', u.lastName)))")
    List<User> findAllFiltered(Pageable pageable, @Param("sports") List<Sport> sports, @Param("name") String name);
}
