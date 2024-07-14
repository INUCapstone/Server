package com.CapStone.inu.taxi.domain.member;

import com.CapStone.inu.taxi.domain.requirement.Requirement;
import com.CapStone.inu.taxi.domain.room.Room;
import com.CapStone.inu.taxi.global.common.BaseEntity;
import com.CapStone.inu.taxi.global.common.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

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
    private Long point;

    private String profile;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(mappedBy = "member",cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    private Requirement requirement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Builder
    private Member(String email, String password, String nickname, String phoneNumber, Long point, String profile, Role role) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.point = point;
        this.profile = profile;
        this.role = role;
    }
}
