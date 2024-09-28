package com.apherox.redis.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash
@NoArgsConstructor
@ToString
@Getter
@Setter
public class SongRedis {

    private String id;
    private String performer;
    private String name;
    private String album;
    @Indexed
    private String genre;
    @Indexed
    private String year;
}
