package com.CapStone.inu.taxi.domain.waitingmember;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class WaitingMemberController {
    private final WaitingMemberService waitingMemberService;

    @GetMapping(value = "/test")
    public void test() {
        waitingMemberService.matchUser();
    }

}
