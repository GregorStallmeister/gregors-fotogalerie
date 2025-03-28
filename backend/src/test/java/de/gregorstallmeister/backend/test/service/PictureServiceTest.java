package de.gregorstallmeister.backend.test.service;

import de.gregorstallmeister.backend.model.IdService;
import de.gregorstallmeister.backend.model.Picture;
import de.gregorstallmeister.backend.model.PictureInsertDto;
import de.gregorstallmeister.backend.repository.PictureRepository;
import de.gregorstallmeister.backend.service.PictureService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class PictureServiceTest {

    private PictureRepository pictureRepository;
    private PictureService pictureService;

    @BeforeEach
    void setup() {
        pictureRepository = mock(PictureRepository.class);
        pictureService = new PictureService(pictureRepository);
    }

    @Test
    void insertPicture() {
        // given
        IdService idService = new IdService();
        String id = idService.generateRandomId();
        String imagePath = "https://gregorstallmeister.de/fotogalerie/bilder/test123.jpg";
        String location = "Langeoog";
        Instant instant = Instant.now();
        Picture pictureToInsert = new Picture(id, imagePath, location, instant);
        PictureInsertDto pictureToInsertDto = new PictureInsertDto(imagePath, location, instant);
        when(pictureRepository.insert(pictureToInsert)).thenReturn(pictureToInsert);

        // when
        Picture pictureInserted = pictureService.insertPicture(pictureToInsertDto);

        // then
        verify(pictureRepository).insert(pictureInserted);
        assertNotNull(pictureInserted);
        assertEquals(imagePath, pictureInserted.imagePath());
        assertEquals(location, pictureInserted.location());
        assertEquals(instant, pictureInserted.instant());
        assertNotNull(pictureInserted.id());
    }

    @Test
    void getPictures() {
        // given
        IdService idService = new IdService();
        Picture picture1 = new Picture(
                idService.generateRandomId(),
                "https://gregorstallmeister.de/fotogalerie/bilder/test123.jpg",
                "Langeoog",
                Instant.parse("2025-03-26T09:17:30+01:00"));
        Picture picture2 = new Picture(
                idService.generateRandomId(),
                "https://gregorstallmeister.de/fotogalerie/bilder/test124.jpg",
                "Carolinensiel",
                Instant.parse("2025-08-26T09:17:30+02:00"));
        List<Picture> expected = List.of(picture1, picture2);
        when(pictureRepository.findAll()).thenReturn(List.of(picture1, picture2));

        // when
        List<Picture> actual = pictureService.getPictures();

        // then
        verify(pictureRepository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getPicturesWhenNoneIsPresent() {
        // given
        List<Picture> expected = List.of();

        // when
        List<Picture> actual = pictureService.getPictures();

        // then
        verify(pictureRepository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getPictureById() {
        // given
        IdService idService = new IdService();
        String id = idService.generateRandomId();
        String imagePath = "https://gregorstallmeister.de/fotogalerie/bilder/test123.jpg";
        String location = "Langeoog";
        Instant instant = Instant.now();
        Picture pictureToFind = new Picture(id, imagePath, location, instant);
        when(pictureRepository.findById(id)).thenReturn(Optional.of(pictureToFind));

        // when
        Optional<Picture> optionalPicture = pictureService.getPictureById(id);

        // then
        verify(pictureRepository).findById(id);
        assertNotNull(optionalPicture);
        assertTrue(optionalPicture.isPresent());
        assertEquals(pictureToFind, optionalPicture.get());
    }

    @Test
    void getPictureByIdWhenNotPresent() {
        // given
        String id = "test_not_present_1234";

        // when
        Optional<Picture> optionalPicture = pictureService.getPictureById(id);

        // then
        verify(pictureRepository).findById(id);
        assertNotNull(optionalPicture);
        assertFalse(optionalPicture.isPresent());
    }
}
