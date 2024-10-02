package com.CapStone.inu.taxi.domain.room.dto.kakao;

import com.google.gson.annotations.Expose;
import lombok.Getter;

@Getter
public class Section {
    @Expose(deserialize = false)
    private Integer distance;
    @Expose(deserialize = false)
    private Integer duration;
    @Expose(deserialize = false)
    private Bound bound;
    private Road[] roads;
    @Expose(deserialize = false)
    private Guide[] guides;
}
