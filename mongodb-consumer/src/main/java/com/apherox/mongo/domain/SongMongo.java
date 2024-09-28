package com.apherox.mongo.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;

@NoArgsConstructor
@ToString
@Getter
@Setter
public class SongMongo {

    private String id;
    private String performer;
    private String name;
    private String album;
    @Indexed
    private String genre;
    @Indexed
    private String year;
}
