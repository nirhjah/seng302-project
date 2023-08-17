package nz.ac.canterbury.seng302.tab.unit.service;

import nz.ac.canterbury.seng302.tab.entity.Club;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.helper.FileDataSaver;
import nz.ac.canterbury.seng302.tab.helper.GenerateRandomUsers;
import nz.ac.canterbury.seng302.tab.helper.ImageType;
import nz.ac.canterbury.seng302.tab.service.ClubImageService;
import nz.ac.canterbury.seng302.tab.service.ClubService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ClubImageServiceTest {

    @Autowired
    private ClubImageService clubImageService;

    @Autowired
    private ClubService clubService;

    @SpyBean
    private UserService userService;

    private GenerateRandomUsers generateRandomUsers = new GenerateRandomUsers();

    Location location;
    Club club;
    User manager;

    byte[] bytes = new byte[] {1,2,4,76,8,56,34,22,76,99,12,11,8,75};
    MultipartFile mockedFileJpg = new MockMultipartFile("my_image.jpg", bytes);
    MultipartFile mockedFileJpeg = new MockMultipartFile("my_image.jpeg", bytes);
    MultipartFile mockedFilePng = new MockMultipartFile("my_image.png", bytes);
    MultipartFile mockedFilePngCapital = new MockMultipartFile("my_image.PNG", bytes);

    MultipartFile mockedFileSvg = new MockMultipartFile("my_image.svg", bytes);
    MultipartFile mockedFileSvgCapital = new MockMultipartFile("my_image.SVG", bytes);

    public ClubImageServiceTest() {
    }

    @BeforeEach
    void beforeEach() throws IOException {
        location = new Location(null, null, null, "Christchurch", null, "New Zealand");
        manager = generateRandomUsers.createRandomUser();
        userService.updateOrAddUser(manager);
        club = new Club("Rugby Club", location, "soccer", manager);
        clubService.updateOrAddClub(club);

        FileDataSaver.clearTestFolder();
    }

    @Test
    void testSaveWithNoErrors() {
        clubImageService.saveImage(club, mockedFileJpg);
        clubImageService.saveImage(club, mockedFilePng);
        clubImageService.saveImage(club, mockedFileJpeg);
        clubImageService.saveImage(club, mockedFileSvg);
    }


    private void testSaveThenReadImage(MultipartFile mockMultipartFile) throws IOException {
        clubImageService.saveImage(club, mockMultipartFile);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "image/png");

        ResponseEntity<byte[]> expected = ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .headers(headers)
                .body(mockMultipartFile.getBytes());

        var got = clubImageService.getImageResponse(club);

        assertEquals(expected, got);
        assertEquals(ImageType.PNG_OR_JPEG, club.getImageType());
    }


    private void testSaveThenReadSvg(MultipartFile mockMultipartFile) throws IOException {
        clubImageService.saveImage(club, mockMultipartFile);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "image/svg+xml");

        ResponseEntity<byte[]> expected = ResponseEntity.ok()
                .headers(headers)
                .body(mockMultipartFile.getBytes());

        var got = clubImageService.getImageResponse(club);

        assertEquals(expected, got);
        assertEquals(ImageType.SVG, club.getImageType());
    }

    @Test
    void testSaveThenReadForPngsAndJpegs() {
        assertDoesNotThrow(() -> {
            testSaveThenReadImage(mockedFilePng);
            testSaveThenReadImage(mockedFilePngCapital);
            testSaveThenReadImage(mockedFileJpeg);
            testSaveThenReadImage(mockedFileJpg);
        });
    }

    @Test
    void testSaveThenReadForSvgs() throws IOException {
        testSaveThenReadSvg(mockedFileSvg);
        testSaveThenReadSvg(mockedFileSvgCapital);
    }

    @Test
    void testDefaultImageTypeOk() {
        assertEquals(ImageType.SVG, clubImageService.getDefaultImageType());
    }

    @Test
    void testDefaultImageDataOk() throws IOException {
        Resource resource = new ClassPathResource("/static/image/icons/club-logo.svg");
        InputStream is = resource.getInputStream();
        var bytes = is.readAllBytes();

        assertArrayEquals(bytes, clubImageService.getDefaultBytes());
    }

    private final String FOLDER_NAME = "CLUB_LOGOS";

    @Test
    public void testFolderOk() {
        assertEquals(clubImageService.getFolderName(), FOLDER_NAME);
    }

    private void testSaveThenReadThroughClubSavePng(MultipartFile mockMultipartFile) throws IOException {
        Mockito.when(userService.getCurrentUser()).thenReturn(Optional.of(manager));

        clubImageService.updateClubLogo(club, mockMultipartFile);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "image/png");

        ResponseEntity<byte[]> expected = ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .headers(headers)
                .body(mockMultipartFile.getBytes());

        var got = clubImageService.getImageResponse(club);

        assertEquals(expected, got);
        assertEquals(ImageType.PNG_OR_JPEG, club.getImageType());
    }

    @Test
    void testSavingNormally() throws IOException {
        testSaveThenReadThroughClubSavePng(mockedFileJpg);
        testSaveThenReadThroughClubSavePng(mockedFilePng);
        testSaveThenReadThroughClubSavePng(mockedFilePngCapital);
        testSaveThenReadThroughClubSavePng(mockedFileJpeg);
    }
}
