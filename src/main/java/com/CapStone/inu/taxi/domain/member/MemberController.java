package com.CapStone.inu.taxi.domain.member;


import com.CapStone.inu.taxi.domain.member.dto.request.LoginMemberReq;
import com.CapStone.inu.taxi.domain.member.dto.request.SignUpMemberReq;
import com.CapStone.inu.taxi.domain.member.dto.request.UpdateMemberReq;
import com.CapStone.inu.taxi.domain.member.dto.response.MemberRes;
import com.CapStone.inu.taxi.global.common.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static com.CapStone.inu.taxi.global.common.StatusCode.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;


    @PostMapping
    public ResponseEntity<CommonResponse<Object>> signup(@RequestBody @Valid SignUpMemberReq reqDto){
        memberService.signup(reqDto);
        return ResponseEntity
                .status(MEMBER_CREATE.getStatus())
                .body(CommonResponse.from(MEMBER_CREATE.getMessage()));
    }


    @PostMapping(value = "/login")
    public ResponseEntity<CommonResponse<Object>> login(@RequestBody @Valid LoginMemberReq signInMemberReqDto){

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION,  memberService.login(signInMemberReqDto))
                .body(CommonResponse.from(MEMBER_LOGIN.getMessage()));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<MemberRes>> getMember(Principal principal){
        // 요청에서 인증 정보를 추출하고, 이 정보를 이용해 서비스 계층을 호출
        Long memberId=Long.parseLong(principal.getName());
        return ResponseEntity.ok()
                .body(CommonResponse.from(MEMBER_FOUND.getMessage(),memberService.getMember(memberId)));
    }

   @PatchMapping
    public ResponseEntity<CommonResponse<MemberRes>> updateMember(Principal principal, @RequestBody @Valid UpdateMemberReq updateMemberReq){
        Long memberId=Long.parseLong(principal.getName());
        return ResponseEntity.ok()
                .body(CommonResponse.from(MEMBER_UPDATE.getMessage(),memberService.updateMember(memberId, updateMemberReq)));
    }

    @DeleteMapping
    public ResponseEntity<CommonResponse<Object>> deleteMember(Principal principal){
        Long memberId=Long.parseLong(principal.getName());
        memberService.deleteMember(memberId);
        return ResponseEntity.ok()
                .body(CommonResponse.from(MEMBER_DELETE.getMessage()));
    }
}
