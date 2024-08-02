package com.CapStone.inu.taxi.domain.waitingroom.dto.request;

import com.CapStone.inu.taxi.domain.waitingroom.WaitingRoom;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateWatingRoomReq {

    @NotBlank(message = "제한시간은 필수 입력 값입니다.")
    private String title;

    @NotBlank(message = "제한시간은 필수 입력 값입니다.")
    private String departure;

    @NotBlank(message = "제한시간은 필수 입력 값입니다.")
    private String arrival;

    public WaitingRoom toEntity()
}
