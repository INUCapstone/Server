package com.CapStone.inu.taxi.global.security;

import com.CapStone.inu.taxi.domain.member.MemberRepository;
import com.CapStone.inu.taxi.global.common.StatusCode;
import com.CapStone.inu.taxi.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String memberId){
        return memberRepository.findById(Long.parseLong(memberId))
                .orElseThrow(()->new CustomException(StatusCode.MEMBER_NOT_EXIST));
    }

}
