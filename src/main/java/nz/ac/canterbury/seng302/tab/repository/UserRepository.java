package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * FormResult repository accessor using Spring's @link{CrudRepository}.
 * These (basic) methods are provided for us without the need to write our own implementations
 */
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findById(long id);
    Optional<User> findByEmail(String email);
    List<User> findAll();

    @Query("SELECT u FROM UserEntity u WHERE u.email = :email and u.hashedPassword = :password")
    User getUserByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    List<User> findAll(Pageable pageable);

    List<User> findAllByFirstNameIgnoreCaseContainingAndLastNameIgnoreCaseContaining(Pageable pageable, String firstName, String lastName);
    List<User> findAllByFirstNameIgnoreCaseContainingOrLastNameIgnoreCaseContaining(Pageable pageable, String firstName, String lastName);
}
