package nz.ac.canterbury.seng302.tab.repository;

import java.util.List;
import java.util.Optional;

import nz.ac.canterbury.seng302.tab.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import nz.ac.canterbury.seng302.tab.entity.User;

/**
 * FormResult repository accessor using Spring's @link{CrudRepository}.
 * These (basic) methods are provided for us without the need to write our own implementations
 */
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findById(long id);
    Optional<User> findByEmail(String email);
    Page<User> findAll();

    boolean existsByEmail(String email);

    @Query("SELECT u FROM UserEntity u WHERE u.email = :email and u.hashedPassword = :password")
    User getUserByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    Page<User> findAll(Pageable pageable);

    /*
     * Query Logic:
     * - The `:<name> is null OR ...` means the filter isn't applied if you don't provide that field.
     * - U17 AC2 specifies that provided a list of sports,
     *      get all users who have at least one of these sports.
     *      This is achieved via the join, and the `s in :sports` check.
     * - U7 AC4 states that a search succeeds if "The search string is contained in the first or last name".
     *      However, this would mean that User("John", "Carpenter") would never be found by searching
     *      "John Carpenter", as "John Carpenter" isn't in "John", nor is it in "Carpenter".
     *      So, we ALSO check if the first name or last name is in the search string.
     */
    @Query("SELECT distinct u "
            +"FROM UserEntity u LEFT JOIN u.favoriteSports s "
            +"WHERE (:#{#searchedSports.size}=0 OR s.name in (:searchedSports)) "
            +"AND (:name is null OR "
                    +"lower(:name) like lower(concat('%', u.firstName, '%')) OR lower(:name) like lower(concat('%', u.lastName, '%')) "
                    +"OR lower(u.firstName) like lower(concat('%', :name, '%')) OR lower(u.lastName) like lower(concat('%', :name, '%')) "
            +")"
    )
    Page<User> findAllFiltered(Pageable pageable, @Param("searchedSports") List<String> searchedSports, @Param("name") String name);

    @Query("SELECT u.location FROM UserEntity u " +
            "WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(u.location.country) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(u.location.city) LIKE LOWER(CONCAT('%', :name, '%')) ")
    public List<Location> findLocationsByEmail(@Param("name") String email);

}
