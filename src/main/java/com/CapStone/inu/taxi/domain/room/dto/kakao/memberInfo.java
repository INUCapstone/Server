package com.CapStone.inu.taxi.domain.room.dto.kakao;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class memberInfo {
    private String nickname;
    private Long memberId;
    private Boolean isReady;
}
