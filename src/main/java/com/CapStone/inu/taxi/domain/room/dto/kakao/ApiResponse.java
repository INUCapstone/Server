package com.CapStone.inu.taxi.domain.room.dto.kakao;

import com.google.gson.annotations.Expose;
import lombok.Getter;

@Getter
public class ApiResponse {
    @Expose(deserialize = false)
    private String trans_id;
    private Route[] routes;
}

class Origin {
    private String name;
    private Double x;
    private Double y;
}

class Destination {
    private String name;
    private Double x;
    private Double y;
}

class Waypoint {
    private String name;
    private Double x;
    private Double y;
}

class Bound {
    private Double min_x;
    private Double min_y;
    private Double max_x;
    private Double max_y;
}

class Guide {
    private String name;
    private Double x;
    private Double y;
    private Integer distance;
    private Integer duration;
    private Integer type;
    private String guidance;
    private Integer road_index;
}

