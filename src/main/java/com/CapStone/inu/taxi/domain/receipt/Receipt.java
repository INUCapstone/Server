package com.CapStone.inu.taxi.domain.receipt;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "taxi_fare")
    private Integer taxiFare;

    @Column(nullable = false, name = "taxi_duration")
    private Integer taxiDuration;

    @Column(nullable = false, name = "driver_id")
    private Long driverId;

    //member에 종속될 필요는 없을 것 같다. id만 갖고있자.
    @ElementCollection
    private List<Long> memberIds = new ArrayList<>();

    @Builder
    private Receipt(Integer taxiFare, Integer taxiDuration, Long driverId) {
        this.taxiFare = taxiFare;
        this.taxiDuration = taxiDuration;
        this.driverId = driverId;
    }
}
