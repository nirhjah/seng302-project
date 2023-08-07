package nz.ac.canterbury.seng302.tab.unit.service;

import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.helper.FileDataSaver;
import nz.ac.canterbury.seng302.tab.helper.GenerateRandomUsers;
import nz.ac.canterbury.seng302.tab.service.UserImageService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;

@Import({UserImageService.class})
@SpringBootTest
class UserImageServiceTest {

    @Autowired
    private UserImageService userImageService;

    @SpyBean
    private UserService userService;

    @Autowired
    private GenerateRandomUsers generateRandomUsers;

    private static final MultipartFile fakeImageFile = new MockMultipartFile(
            "hello.jpg", new byte[] {1,2,3,4,5,6,7,8}
    );

    private byte[] defaultBytes;

    private final List<User> users = new ArrayList<>();

    private static final int NUM_USERS = 30;

    @BeforeEach
    void beforeEach() {
        // Check that we are on test.
        // If we aren't on test, we shouldn't run the test!!!
        // (This will mess up our filesystem on prod if it fails!!!)
        assertEquals(FileDataSaver.DeploymentType.TEST, userImageService.getDeploymentType());

        // Set default bytes (think of this like a fallback value)
        defaultBytes = userImageService.getDefaultBytes();

        // clear test users
        users.clear();

        // Clear files for test
        UserImageService.clearTestFolder();

        // Generate our mock users
        for (int i=0; i<NUM_USERS; i++) {
            User user = generateRandomUsers.createRandomUser();
            user = userService.updateOrAddUser(user);
            users.add(user);
        }
    }

    private MockMultipartFile getMockedFile(byte[] data) {
        return new MockMultipartFile("/my_file.jpg", data);
    }

    @Test
    void testSingularImageIsSaved() throws IOException {
        // Take a user, check that the file is saved.
        User usr = users.get(0);
        long id = usr.getUserId();
        userImageService.saveImage(usr, fakeImageFile);

        byte[] result = userImageService.readFileOrDefault(id);

        assertArrayEquals(fakeImageFile.getBytes(), result);
    }


    @Test
    void givenExistingUsers_givenDataIsSaved_testDataCanBeRetrieved() throws IOException {
        // Multiple users, check that they all end up with unique data.
        var files = List.of(
                getMockedFile(new byte[] {1,2,3,4,5,6}),
                getMockedFile(new byte[] {5,6,7,67,43,45,99}),
                getMockedFile(new byte[] {9,8,6,5,8,6,5,1,1,1})
        );

        for (int i=0; i<files.size(); i++) {
            User usr = users.get(i);
            var file = files.get(i);
            userImageService.saveImage(usr, file);
        }

        for (int i=0; i<files.size(); i++) {
            User usr = users.get(i);
            long id = usr.getUserId();
            byte[] data = files.get(i).getBytes();
            byte[] result = userImageService.readFileOrDefault(id);
            assertArrayEquals(data, result);
        }
    }

    /*
    Check that default bytes are returned, given the user
    doesn't have a valid profile picture.
     */
    @Test
    void givenNoProfilePicture_testDefaultsAreGiven() {
        User a, b;
        int size = users.size();
        a = users.get(size - 1);
        b = users.get(size - 2);

        byte[] dataA = userImageService.readFileOrDefault(a.getUserId());
        byte[] dataB = userImageService.readFileOrDefault(b.getUserId());

        assertArrayEquals(dataA, defaultBytes);
        assertArrayEquals(dataB, defaultBytes);
    }

    @AfterAll
    static void afterAll() {
        // Clear files
        UserImageService.clearTestFolder();
    }
}
