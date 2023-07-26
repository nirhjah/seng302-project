package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.helper.FileDataSaver;
import nz.ac.canterbury.seng302.tab.helper.GenerateRandomUsers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@Import({UserImageService.class})
/*
 TODO: This is terrible!!!
  A SpringBootTest should not be used here, as it
  loads the entire sprint context, which is completely
  overkill. I haven't been able to find a better way.
  (PLS change the annotations if you know of a way to get it working)
 */
@SpringBootTest
class UserImageServiceTest {

    @Autowired
    private UserImageService userImageService;

    @Autowired
    private UserService userService;

    @Autowired
    private GenerateRandomUsers generateRandomUsers;

    private static final byte[] fakeImageData = new byte[] {
            1,2,3,4,5,6,7,8
    };

    private byte[] defaultBytes;

    private final List<User> users = new ArrayList<>();

    private static final int NUM_USERS = 30;

    @BeforeEach
    public void beforeEach() throws IOException {
        // Check that we are on test.
        // If we aren't on test, we shouldn't run the test!!!
        // (This will mess up our filesystem on prod if it fails!!!)
        assertEquals(userImageService.getDeploymentType(), FileDataSaver.DeploymentType.TEST);

        // Set default bytes (think of this like a fallback value)
        defaultBytes = userImageService.getDefaultBytes();

        // clear test users
        users.clear();

        // Clear files for test
         UserImageService.clearTestFolder();

        // Generate our mock users
        for (int i=0; i<NUM_USERS; i++) {
            User user = generateRandomUsers.createRandomUser();
            userService.updateOrAddUser(user);
            users.add(user);
        }
    }

    @Test
    public void testSingularImageIsSaved() {
        // Take a user, check that the file is saved.
        User usr = users.get(0);
        long id = usr.getUserId();
        userImageService.updateProfilePicture(id, fakeImageData);

        byte[] result = userImageService.readFileOrDefault(id);

        assertArrayEquals(fakeImageData, result);
    }


    @Test
    public void testMultipleImagesAreSaved() {
        // Multiple users, check that they all end up with unique data.
        var profileData = List.of(
                new byte[] {1,2,3,4,5,6},
                new byte[] {5,6,7,67,43,45},
                new byte[] {9,8,6,5,8,6,5}
        );

        for (int i=0; i<profileData.size(); i++) {
            User usr = users.get(i);
            long id = usr.getUserId();
            byte[] data = profileData.get(i);
            userImageService.updateProfilePicture(id, data);
        }

        for (int i=0; i<profileData.size(); i++) {
            User usr = users.get(i);
            long id = usr.getUserId();
            byte[] data = profileData.get(i);
            byte[] result = userImageService.readFileOrDefault(id);
            assertArrayEquals(data, result);
        }
    }

    /*
    Check that default bytes are returned, given the user
    doesn't have a valid profile picture.
     */
    @Test
    public void testDefaultsAreConsistent() {
        User a, b;
        int size = users.size();
        a = users.get(size - 1);
        b = users.get(size - 2);

        byte[] dataA = userImageService.readFileOrDefault(a.getUserId());
        byte[] dataB = userImageService.readFileOrDefault(b.getUserId());

        assertArrayEquals(dataA, defaultBytes);
        assertArrayEquals(dataB, defaultBytes);
    }

    @Test
    public void testSaveDeniedOnCrash() {
        long invalidId = 3290;
        while (userService.findUserById(invalidId).isPresent()) {
            // Just in case!
            invalidId++;
        }

        // This file should not be saved, because the user ID doesn't exist.
        userImageService.updateProfilePicture(invalidId, fakeImageData);

        byte[] savedData = userImageService.readFileOrDefault(invalidId);
        assertArrayEquals(savedData, defaultBytes);
    }

    @AfterAll
    public static void afterAll() {
        // Clear files
        UserImageService.clearTestFolder();
    }
}
