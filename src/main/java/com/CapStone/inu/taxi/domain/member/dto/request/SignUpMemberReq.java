package com.CapStone.inu.taxi.domain.member.dto.request;

import com.CapStone.inu.taxi.domain.member.Member;
import com.CapStone.inu.taxi.global.common.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@NoArgsConstructor
public class SignUpMemberReq {

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "잘못된 이메일 형식입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(message = "비밀번호는 8자 이상의 영어, 숫자, @,$,!,%,*,#,?,& 중 하나 이상을 포함한 특수문자로 이루어져야 합니다."
            , regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수 입력 값입니다.")
    private String confirmPassword;

    @NotBlank(message = "nickname은 필수 입력 값입니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,10}$" , message = "닉네임은 특수문자를 포함하지 않은 2~10자리여야 합니다.")
    private String nickname;

    @NotBlank(message = "휴대폰 번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^010\\d{8}$" , message = "휴대폰 번호는 총 11자리입니다.")
    private String phoneNumber;


    public Member toEntity(PasswordEncoder passwordEncoder){
        return Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .phoneNumber(phoneNumber)
                .point(0)
                .role(Role.ROLE_USER)
                .build();
    }
}
