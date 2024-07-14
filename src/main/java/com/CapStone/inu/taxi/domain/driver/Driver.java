package com.CapStone.inu.taxi.domain.driver;


import com.CapStone.inu.taxi.domain.room.Room;
import com.CapStone.inu.taxi.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Driver extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,name = "phone_number")
    private String phoneNumber;

    @Column(nullable = false,name = "car_number")
    private String carNumber;

    @Column(nullable = false)
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id",nullable = false)
    private Room room;

    @Builder
    private Driver(String phoneNumber, String carNumber, String name, Room room) {
        this.phoneNumber = phoneNumber;
        this.carNumber = carNumber;
        this.name = name;
        this.room = room;
    }
}
