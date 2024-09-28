package com.apherox.elasticsearch.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@NoArgsConstructor
@ToString
@Getter
@Setter

@Document(indexName = "song")
public class SongElastic {

    @Id
    private String id;
    private String performer;
    private String name;
    private String album;
    private String genre;
    private String year;
}
