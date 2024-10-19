package com.CapStone.inu.taxi.domain.room.dto.kakao;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class pathInfo {
    private Double x;
    private Double y;

    //테스트용
    @Override
    public String toString() {
        return "{" + x + "," + y + "} -> ";
    }
}
