package com.CapStone.inu.taxi.domain.requirement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class UpdateRequirementReq {

    @NotBlank(message = "제한시간은 필수 입력 값입니다.")
    private LocalDateTime limitTime;

    @NotBlank(message = "제한금액은 필수 입력 값입니다.")
    private Long limitPrice;

    @NotBlank(message = "출발지는 필수 입력 값입니다.")
    private String departure;

    @NotBlank(message = "도착지는 필수 입력 값입니다.")
    private String arrival;
}
