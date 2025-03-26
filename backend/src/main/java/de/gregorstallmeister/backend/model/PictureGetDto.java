package de.gregorstallmeister.backend.model;

import java.time.Instant;

public record PictureGetDto(
        String id,
        String imagePath,
        String location,
        Instant instant
) {
    }
