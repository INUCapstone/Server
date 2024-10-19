package com.CapStone.inu.taxi.domain.room;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @GetMapping(value = "/test")
    public void test() {
        roomService.matchUser(1L);
    }
}
