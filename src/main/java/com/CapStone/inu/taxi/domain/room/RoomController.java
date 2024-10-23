package com.CapStone.inu.taxi.domain.room;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @GetMapping(value = "/test/{memberId}")
    public void test(@PathVariable Long memberId) {
        roomService.matchUser(memberId);
    }
}
