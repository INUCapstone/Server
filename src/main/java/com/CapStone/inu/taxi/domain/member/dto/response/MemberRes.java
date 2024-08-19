package com.CapStone.inu.taxi.domain.member.dto.response;

import com.CapStone.inu.taxi.domain.member.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberRes {
    private final Long memberId;
    private final String email;
    private final String nickname;
    private final String phoneNumber;
    private final Integer point;

    @Builder
    private MemberRes(Long memberId,String email, String nickname, String phoneNumber, Integer point) {
        this.memberId=memberId;
        this.email = email;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.point = point;
    }

    public static MemberRes from(Member member){
        return MemberRes.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .phoneNumber(member.getPhoneNumber())
                .point(member.getPoint())
                .build();
    }
}
