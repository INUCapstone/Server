package com.CapStone.inu.taxi.domain.member;

import com.CapStone.inu.taxi.domain.requirement.Requirement;
import com.CapStone.inu.taxi.global.common.BaseEntity;
import com.CapStone.inu.taxi.global.common.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false,unique = true)
    private String nickname;

    @Column(nullable = false,unique = true, name = "phone_number")
    private String phoneNumber;

    @Column(nullable = false)
    private Integer point;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(mappedBy = "member",cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    private Requirement requirement;

    @Builder
    private Member(String email, String password, String nickname, String phoneNumber, Integer point, Role role) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.point = point;
        this.role = role;
    }

    public void changePassword(String password){
        this.password=password;
    }
    public void changeNickname(String nickname){ this.nickname=nickname;}
    public void chargePoint(Integer point){this.point+=point;}

    //grantedAuthority는 부여된 권한을 갖는 인터페이스,SimpleGrantedAuthority는 granteAuthority를 구현한 간단한 클래스
    //"ROLE_USER", "ROLE_ADMIN"과 같은 권한을 나타내는 문자열을 SimpleGrantedAuthority 객체로 생성하여 사용할 수 있다.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> auth=new ArrayList<>();
        auth.add(new SimpleGrantedAuthority(role.name()));
        return auth;
    }

    @Override
    public String getUsername() {
        return id.toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
