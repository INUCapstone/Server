package com.CapStone.inu.taxi.global.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.CONFLICT;

@Getter
@RequiredArgsConstructor
public enum StatusCode {
    /* 2xx: 성공 */
    // Member
    MEMBER_CREATE(CREATED, "회원 가입 완료"),
    MEMBER_FOUND(OK, "멤버 조회 완료"),
    MEMBER_UPDATE(OK, "멤버 수정 완료"),
    MEMBER_CHARGE_POINT(OK, "멤버 포인트 충전 완료"),
    MEMBER_DELETE(OK, "회원탈퇴 완료"),
    MEMBER_PROFILE_UPLOAD(OK, "멤버 프로필 이미지 업로드 완료"),
    MEMBER_PROFILE_DELETE(OK, "멤버 프로필 이미지 삭제 완료"),
    MEMBER_LOGIN(OK, "로그인 완료"),


    /* 400 BAD_REQUEST : 잘못된 요청 */
    LOGIN_ID_INVALID(BAD_REQUEST, "아이디가 틀렸습니다."),
    PASSWORD_INVALID(BAD_REQUEST, "비밀번호가 틀렸습니다."),
    PASSWORD_INCORRECT(BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    INPUT_VALUE_INVALID(BAD_REQUEST, "유효하지 않은 입력입니다."),
    PROFILE_INVALID(BAD_REQUEST, "유효하지 않은 프로필 이미지입니다."),

    /* 401 UNAUTHORIZED : 비인증 사용자 */
    ACCESS_TOKEN_INVALID(UNAUTHORIZED, "jwt 토큰이 유효하지 않습니다."),
    UNAUTHORIZED_MEMBER(UNAUTHORIZED, "인증되지 않은 멤버입니다. 로그인 해주세요."),

    /* 403 FORBIDDEN : 권한 없음 */
    AUTHORIZATION_INVALID(FORBIDDEN, "권한이 없습니다."),

    /* 404 NOT_FOUNT : 존재하지 않는 리소스 */
    ROOM_NOT_EXIST(NOT_FOUND, "존재하지 않는 방입니다."),
    MEMBER_NOT_EXIST(NOT_FOUND, "존재하지 않는 멤버입니다."),
    DRIVER_NOT_EXIST(NOT_FOUND, "존재하지 않는 기사입니다."),
    DRIVER_NO_AVAILABLE(NOT_FOUND, "대기중인 기사가 없습니다."),
    ROOM_MEMBER_NOT_EXIST(NOT_FOUND, "해당 방에 존재하지 않는 멤버입니다."),

    /* 409 CONFLICT : 리소스 충돌 */
    EMAIL_DUPLICATED(CONFLICT, "이미 존재하는 이메일입니다."),
    NICKNAME_DUPLICATED(CONFLICT, "이미 존재하는 닉네임입니다."),
    ROOM_DUPLICATED(CONFLICT, "이미 채팅방에 존재하는 멤버입니다."),
    PHONE_NUMBER_DUPLICATED(CONFLICT, "이미 존재하는 휴대폰 번호입니다."),

    /* 429 TOO MANY REQUESTS : 요청이 많아 처리할 수 없음 */
    LOCK_ACQUISITION_TOO_MANY_REQUESTS(TOO_MANY_REQUESTS, "락 획득 시도가 너무 많습니다. 잠시 후 다시 시도하세요.");


    private final HttpStatus status;
    private final String message;

}
