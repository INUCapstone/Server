package com.CapStone.inu.taxi.domain.room.dto.kakao;

import com.google.gson.annotations.Expose;
import lombok.Getter;

@Getter
public class Road {
    @Expose(deserialize = false)
    private String name;
    @Expose(deserialize = false)
    private Integer distance;
    @Expose(deserialize = false)
    private Integer duration;
    @Expose(deserialize = false)
    private Double traffic_speed;
    @Expose(deserialize = false)
    private Integer traffic_state;
    private Double[] vertexes;
}
