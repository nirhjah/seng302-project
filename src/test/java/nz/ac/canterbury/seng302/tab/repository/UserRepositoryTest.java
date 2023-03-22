package nz.ac.canterbury.seng302.tab.repository;

import nz.ac.canterbury.seng302.tab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;
}
