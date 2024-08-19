package com.CapStone.inu.taxi.domain.member;

import com.CapStone.inu.taxi.domain.member.dto.request.LoginMemberReq;
import com.CapStone.inu.taxi.domain.member.dto.request.SignUpMemberReq;
import com.CapStone.inu.taxi.domain.member.dto.request.UpdateMemberReq;
import com.CapStone.inu.taxi.domain.member.dto.response.LoginMemberRes;
import com.CapStone.inu.taxi.domain.member.dto.response.MemberRes;
import com.CapStone.inu.taxi.global.exception.CustomException;
import com.CapStone.inu.taxi.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.CapStone.inu.taxi.global.common.StatusCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public MemberRes getMember(Long memberId){
        Member member=findByMemberId(memberId);
        return MemberRes.from(member);
    }

    @Transactional
    public void signup(SignUpMemberReq reqDto){
        if(!reqDto.getConfirmPassword().equals(reqDto.getPassword()))
            throw new CustomException(PASSWORD_INCORRECT);
        checkEmailDuplicated(reqDto.getEmail());
        checkNicknameDuplicated(reqDto.getNickname());
        Member member=reqDto.toEntity(passwordEncoder);
        memberRepository.save(member);
    }


    public LoginMemberRes login(LoginMemberReq loginMemberReq){
        Member member= memberRepository.findByEmail(loginMemberReq.getEmail())
                .orElseThrow(()->new CustomException(LOGIN_ID_INVALID));
        if(!passwordEncoder.matches(loginMemberReq.getPassword(),member.getPassword())){
            throw new CustomException(PASSWORD_INVALID);
        }

        return new LoginMemberRes(jwtTokenProvider.createAccessToken(member.getId(), member.getRole().name(),member.getNickname()));
    }

    @Transactional
    public MemberRes updateMember(Long memberId, UpdateMemberReq updateMemberReq){
        Member member=findByMemberId(memberId);
        member.changePassword(passwordEncoder.encode(updateMemberReq.getPassword()));
        member.changeNickname(updateMemberReq.getNickname());
        return MemberRes.from(member);
    }

    @Transactional
    public void deleteMember(Long memberId){
        Member member=findByMemberId(memberId);
        memberRepository.deleteById(member.getId());
    }


    private void checkEmailDuplicated(String email){
        if(memberRepository.existsByEmail(email))
            throw new CustomException(EMAIL_DUPLICATED);
    }

    private void checkNicknameDuplicated(String nickname){
        if(memberRepository.existsByNickname(nickname))
            throw new CustomException(NICKNAME_DUPLICATED);
    }

    private Member findByMemberId(Long memberId){
        return memberRepository.findById(memberId)
                .orElseThrow(()->new CustomException(MEMBER_NOT_EXIST));
    }
}
