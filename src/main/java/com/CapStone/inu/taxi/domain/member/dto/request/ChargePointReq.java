package com.CapStone.inu.taxi.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChargePointReq {

    @NotBlank(message = "point는 필수 입력 값입니다.")
    private Integer point;

}
