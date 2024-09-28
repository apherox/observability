package com.apherox.kafka.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author apherox
 */
@Data
@RequiredArgsConstructor
public class Song {

    private Metadata metadata;

    private final String id;

    @NotBlank(message = "performer is required parameter")
    private final String performer;

    @NotBlank(message = "name is required parameter")
    private final String name;

    @NotBlank(message = "album is required parameter")
    private final String album;

    @NotBlank(message = "genre is required parameter")
    private final String genre;

    @NotBlank(message = "year is required parameter")
    private final String year;

    @Data
    @Builder
    public static final class Metadata {
        private String spanContext;
    }
}
