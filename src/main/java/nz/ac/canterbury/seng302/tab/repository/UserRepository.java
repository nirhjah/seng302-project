package nz.ac.canterbury.seng302.tab.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.entity.User;

/**
 * FormResult repository accessor using Spring's @link{CrudRepository}.
 * These (basic) methods are provided for us without the need to write our own
 * implementations
 */
public interface UserRepository extends CrudRepository<User, Long> {
        Optional<User> findById(long id);

        Optional<User> findByEmail(String email);

        Optional<User> findByToken(String token);

        Page<User> findAll();

        boolean existsByEmail(String email);

        Page<User> findAll(Pageable pageable);

        /*
         * Query Logic:
         * - The `:<name> is null OR ...` means the filter isn't applied if you don't
         * provide that field.
         * - U17 AC2 specifies that provided a list of sports,
         * get all users who have at least one of these sports.
         * This is achieved via the join, and the `s in :sports` check.
         * - U7 AC4 states that a search succeeds if
         * "The search string is contained in the first or last name".
         * However, this would mean that User("John", "Carpenter") would never be found
         * by searching
         * "John Carpenter", as "John Carpenter" isn't in "John", nor is it in
         * "Carpenter".
         * So, we ALSO check if the first name or last name is in the search string.
         */
        @Query("""
            SELECT DISTINCT u
            FROM UserEntity u LEFT JOIN u.favoriteSports s
            WHERE (:#{#filteredLocations.size} = 0 OR (u.location.city) in (:filteredLocations))
              AND (:#{#filteredSports.size}=0 OR s.name in (:filteredSports))
              AND (:name is null OR
                lower(:name) like lower(concat('%', u.firstName, '%'))
              OR (lower(:name) like lower(concat('%', u.lastName, '%')))
              OR (lower(u.firstName) like lower(concat('%', :name, '%')))
              OR (lower(u.lastName) like lower(concat('%', :name, '%'))))""")
        Page<User> findUserByFilteredLocationsAndSports(Pageable pageable,
                        @Param("filteredLocations") List<String> filteredLocations,
                        @Param("filteredSports") List<String> filteredSports,
                        @Param("name") String name);


        @Query("SELECT u.favoriteSports FROM UserEntity u "
                + "WHERE lower(:name) like lower(concat('%', u.firstName, '%')) "
                + "OR lower(:name) like lower(concat('%', u.lastName, '%')) "
                + "OR lower(u.firstName) like lower(concat('%', :name, '%')) "
                + "OR lower(u.lastName) like LOWER(concat('%', :name, '%')) ")
        public List<Sport> findSportByUser(@Param("name") String name);


        @Query("SELECT u.location FROM UserEntity u "
                        + "WHERE lower(:name) like lower(concat('%', u.firstName, '%')) "
                        + "OR lower(:name) like lower(concat('%', u.lastName, '%')) "
                        + "OR lower(u.firstName) like lower(concat('%', :name, '%')) "
                        + "OR lower(u.lastName) like LOWER(concat('%', :name, '%')) ")
        public List<Location> findLocationByUser(@Param("name") String name);

        @Query("SELECT distinct u.location FROM UserEntity u")
        public List<Location> findAllUserLocations();


        @Query("SELECT distinct u.favoriteSports FROM UserEntity u")
        public List<Sport> findAllUserSports();

        @Query("""
            SELECT DISTINCT u
            FROM UserEntity u LEFT JOIN u.favoriteSports s
              WHERE s.name = :sport
              AND (:name is null OR
                lower(:name) like lower(concat('%', u.firstName, '%'))
              OR (lower(:name) like lower(concat('%', u.lastName, '%')))
              OR (lower(u.firstName) like lower(concat('%', :name, '%')))
              OR (lower(u.lastName) like lower(concat('%', :name, '%'))))""")
        List<User> findUserBySportAndName(@Param("sport") String sport, @Param("name") String name);

        @Query(nativeQuery = true, value =
            "SELECT u.* FROM USER_ENTITY u " +
            "WHERE u.ID NOT IN (" +
            "    SELECT DISTINCT a.user_Id FROM Authority a WHERE a.role = 'ROLE_FEDERATION_MANAGER'" +
            ")")
        Page<User> findUsersThatArentFedMans(Pageable pageable);


        @Query(nativeQuery = true, value =
            "SELECT u.* FROM USER_ENTITY u " +
            "WHERE u.ID NOT IN (" +
            "    SELECT DISTINCT a.user_Id FROM Authority a WHERE a.role = 'ROLE_FEDERATION_MANAGER'" +
            ") " +
            "AND ( " +
            "    lower(:name) like lower(concat('%', u.FIRST_NAME, '%')) " +
            "    OR lower(:name) like lower(concat('%', u.LAST_NAME, '%')) " +
            "    OR lower(u.FIRST_NAME) like lower(concat('%', :name, '%')) " +
            "    OR lower(u.LAST_NAME) like lower(concat('%', :name, '%')) " +
            ")")
        Page<User> findUsersThatArentFedMansByName(Pageable pageable, @Param("name") String name);


        @Query(nativeQuery = true, value =
            "SELECT u.* FROM USER_ENTITY u " +
            "WHERE u.ID NOT IN (" +
            "    SELECT DISTINCT a.user_Id FROM Authority a WHERE a.role = 'ROLE_FEDERATION_MANAGER'" +
            ") " +
            "AND ( " +
            "    lower(:email) = lower(u.EMAIL) " +
            ")")
        Page<User> findUsersThatArentFedMansByEmail(Pageable pageable, @Param("email") String email);


        @Query("SELECT u FROM UserEntity u " +
            "WHERE u.userId NOT IN (" +
            "    SELECT DISTINCT a.user FROM Authority a WHERE a.role = 'ROLE_FEDERATION_MANAGER'" +
            ") " +
            "AND ( " +
            "    lower(:search) like lower(concat('%', u.firstName, '%')) " +
            "    OR lower(:search) like lower(concat('%', u.lastName, '%')) " +
            "    OR lower(u.lastName) like lower(concat('%', :search, '%')) " +
            "    OR lower(u.lastName) like lower(concat('%', :search, '%')) " +
            "    OR lower(:search) = lower(u.email) " +
            ")")
        Page<User> findUsersThatArentFedMansByNameOrEmail(Pageable pageable, @Param("search") String search);

}
