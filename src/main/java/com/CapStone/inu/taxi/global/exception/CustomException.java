package com.CapStone.inu.taxi.global.exception;

import com.CapStone.inu.taxi.global.common.StatusCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException{
    private final StatusCode statusCode;
}
