package com.CapStone.inu.taxi.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final MemberDetailsService memberDetailsService;

    @Value("${jwt.secret.key}")
    private String secretKey;
    @Value("${jwt.expire-time}")
    private long ACCESS_TOKEN_EXPIRE_TIME;

    // 시크릿키를 암호화해서 메소드에 전달한다. 메소드에 시크릿키를 직접 넣지 않고 암호화한 키를 넣어서 보안상 이점을 가진다.
    private Key key;


    @PostConstruct // 객체 의존설정이 끝난 뒤 자동으로 실행, 객체 초기화의 명시성
    protected void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        key = Keys.hmacShaKeyFor(keyBytes);
    }


    public String createAccessToken(Long memberId, String role,String nickname) {
        //subject는 주제로 제목이라고 보면 된다.제목에 memberId를 넣는다.
        Claims claims = Jwts.claims().setSubject(memberId.toString());
        claims.put("role", role);
        claims.put("nickname",nickname);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 정보 저장(클레임 저장)
                .setIssuedAt(now) // 토큰 발행 시간
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME)) // 토큰 유효 시간
                .signWith(key, SignatureAlgorithm.HS256)  // 사용할 암호화 알고리즘
                .compact();
    }

    /*
       토큰에서 인증정보를 가져오는 메소드.
       토큰을 바탕으로 UsernamePasswordAuthenticationToken을 생성하고 SprintContextHolder에 저장해야한다.
       따라서 토큰에서 username을 추출한 뒤 이 값으로 UserDetails객체를 생성한다.
       그 뒤 UserDetails객체와 Authority를 통해 UsernamePasswordAuthenticationToken를 생성해 반환한다.
    */
    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = memberDetailsService.loadUserByUsername(this.getMemberId(token));
        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), "", userDetails.getAuthorities());
    }

    public Long getExpiration(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration().getTime();

    }

    /*
       토큰을 받아 subject(memberId)값을 추출하는 메소드.
    */
    public String getMemberId(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }
    public String getNickname(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("nickname",String.class);
    }

    // Request 의 Header 에서 token 값을 가져옵니다. "Authorization" : "Bearer TOKEN값'
    public String resolveAccessToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    /*
       토큰의 조작여부와 유효여부를 판단하는 메소드.
       claims.getBody().getExpiration().before(new Date())를 통해 유효여부를 판단한다.
       Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token)를 통해 조작여부를 판단한다.
       만약 조작이 의심되면 예외를 던진다.
    */
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
