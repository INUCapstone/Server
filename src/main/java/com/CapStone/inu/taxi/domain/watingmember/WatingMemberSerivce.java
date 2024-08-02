package com.CapStone.inu.taxi.domain.watingmember;

import com.CapStone.inu.taxi.domain.member.MemberRepository;
import com.CapStone.inu.taxi.global.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WatingMemberSerivce {
    private final MemberRepository memberRepository;
    private final RedisRepository redisRepository;


}
