package com.CapStone.inu.taxi.domain.room.dto.kakao;

import com.google.gson.annotations.Expose;
import lombok.Getter;

@Getter
public class Summary {
    @Expose(deserialize = false)
    private Origin origin;
    @Expose(deserialize = false)
    private Destination destination;
    @Expose(deserialize = false)
    private Waypoint[] waypoints;
    @Expose(deserialize = false)
    private String priority;
    @Expose(deserialize = false)
    private Bound bound;
    private Fare fare;
    private Integer distance;
    private Integer duration;
}
