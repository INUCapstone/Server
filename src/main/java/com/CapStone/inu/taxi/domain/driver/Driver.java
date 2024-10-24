package com.CapStone.inu.taxi.domain.driver;


import com.CapStone.inu.taxi.global.common.BaseEntity;
import com.CapStone.inu.taxi.global.common.State;
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

    @Column(nullable = false, name = "phone_number")
    private String phoneNumber;

    @Column(nullable = false, name = "car_number")
    private String carNumber;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private State state;

    @Column(nullable = false)
    private Double x;

    @Column(nullable = false)
    private Double y;

    public void setState(State state) {
        this.state = state;
    }

    @Builder
    private Driver(String phoneNumber, String carNumber, String name, Double x, Double y) {
        this.phoneNumber = phoneNumber;
        this.carNumber = carNumber;
        this.name = name;
        this.state = State.STAND;
        this.x = x;
        this.y = y;
    }
}
