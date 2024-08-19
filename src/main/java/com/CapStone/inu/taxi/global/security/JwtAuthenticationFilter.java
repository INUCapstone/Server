package com.CapStone.inu.taxi.global.security;

import com.CapStone.inu.taxi.global.common.StatusCode;
import com.CapStone.inu.taxi.global.exception.CustomException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;


    @Override
    //OncePerRequestFilter을 상속받으면 doFilterInternal을 써야한다.
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken=jwtTokenProvider.resolveAccessToken(request);

        if(accessToken==null || !accessToken.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }

        accessToken = accessToken.substring(7);  // "Bearer "를 제거하여 실제 토큰만 추출


        if(jwtTokenProvider.validateToken(accessToken)){

            Authentication authentication=jwtTokenProvider.getAuthentication(accessToken); //MEMBER_NOT_EXIST 오류 가능성

            /*
            contextHolder에 인증객체저장. 인가과정을 거친 후 해당 인증객체는 삭제된다.
            그다음,http session에 authentication을 저장한다.
            이후, SecurityContextPersistenceFilter에서 authentication을 삭제한다.//securityContext를 제거
            jwt는 stateless 설정을 했기때문에 jSessionId를 관리하지 않아서, 세션은 응답이 끝나면 사라지게 된다.
            메모리 저장소에 용도로 잠시 사용하고 버리는 용도이다.
            session을 사용하지 않기로 돼있지만 권한처리를 위해 어쩔 수 없이 사용한다.
            */

            SecurityContextHolder.getContext().setAuthentication(authentication);

        }
        else{
            // Access Token이 유효하지 않을 때 401 에러와 함께 메시지를 JSON으로 클라이언트에 전송
            throw new CustomException(StatusCode.ACCESS_TOKEN_INVALID);
        }
        filterChain.doFilter(request, response);
    }
}
