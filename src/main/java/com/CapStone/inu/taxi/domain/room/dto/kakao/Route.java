package com.CapStone.inu.taxi.domain.room.dto.kakao;

import com.google.gson.annotations.Expose;
import lombok.Getter;

@Getter
public class Route {
    @Expose(deserialize = false)
    private Integer result_code;
    @Expose(deserialize = false)
    private String result_msg;
    private Summary summary;
    private Section[] sections;
}