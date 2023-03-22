package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * FormResult repository accessor using Spring's @link{CrudRepository}.
 * These (basic) methods are provided for us without the need to write our own implementations
 */
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findById(long id);
    Optional<User> findByEmail(String email);
    Page<User> findAll();

    @Query("SELECT u FROM UserEntity u WHERE u.email = :email and u.hashedPassword = :password")
    User getUserByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    Page<User> findAll(Pageable pageable);

    Page<User> findAllByFirstNameIgnoreCaseContainingAndLastNameIgnoreCaseContaining(Pageable pageable, String firstName, String lastName);
    Page<User> findAllByFirstNameIgnoreCaseContainingOrLastNameIgnoreCaseContaining(Pageable pageable, String firstName, String lastName);
}
