package com.CapStone.inu.taxi.domain.member.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginMemberRes {
    private final String accessToken;
}
