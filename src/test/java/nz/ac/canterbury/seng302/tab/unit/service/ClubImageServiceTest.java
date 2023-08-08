package nz.ac.canterbury.seng302.tab.unit.service;


import nz.ac.canterbury.seng302.tab.entity.Club;
import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.helper.FileDataSaver;
import nz.ac.canterbury.seng302.tab.helper.ImageType;
import nz.ac.canterbury.seng302.tab.service.ClubImageService;
import nz.ac.canterbury.seng302.tab.service.ClubService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ClubImageServiceTest {

    @Autowired
    private ClubImageService clubImageService;

    @Autowired
    private ClubService clubService;

    Location location;
    Club club;

    byte[] bytes = new byte[] {1,2,4,76,8,56,34,22,76,99,12,11,8,75};
    MultipartFile mockedFileJpg = new MockMultipartFile("my_image.jpg", bytes);
    MultipartFile mockedFileJpeg = new MockMultipartFile("my_image.jpeg", bytes);
    MultipartFile mockedFilePng = new MockMultipartFile("my_image.png", bytes);
    MultipartFile mockedFilePngCapital = new MockMultipartFile("my_image.PNG", bytes);

    MultipartFile mockedFileSvg = new MockMultipartFile("my_image.svg", bytes);
    MultipartFile mockedFileSvgCapital = new MockMultipartFile("my_image.SVG", bytes);

    @BeforeEach
    public void beforeEach() throws IOException {
        location = new Location(null, null, null, "Christchurch", null, "New Zealand");
        club = new Club("Rugby Club", location, "soccer",null);
        clubService.updateOrAddClub(club);

        FileDataSaver.clearTestFolder();
    }

    @Test
    public void testSaveWithNoErrors() {
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
        assertEquals(club.getImageType(), ImageType.PNG_OR_JPEG);
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
        assertEquals(club.getImageType(), ImageType.SVG);
    }

    @Test
    public void testSaveThenReadForPngsAndJpegs() throws IOException {
        testSaveThenReadImage(mockedFilePng);
        testSaveThenReadImage(mockedFilePngCapital);
        testSaveThenReadImage(mockedFileJpeg);
        testSaveThenReadImage(mockedFileJpg);
    }

    @Test
    public void testSaveThenReadForSvgs() throws IOException {
        testSaveThenReadSvg(mockedFileSvg);
        testSaveThenReadSvg(mockedFileSvgCapital);
    }
}
