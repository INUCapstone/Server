package com.CapStone.inu.taxi.domain.driver.dto;

import com.CapStone.inu.taxi.domain.driver.Driver;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DriverRes {
    private final String phoneNumber;
    private final String carNumber;
    private final String name;
    private final Integer pickupTime;

    @Builder
    public DriverRes(String phoneNumber, String carNumber, String name, Integer pickupTime) {
        this.phoneNumber = phoneNumber;
        this.carNumber = carNumber;
        this.name = name;
        this.pickupTime = pickupTime;
    }

    public static DriverRes from(Driver driver, Integer pickupTime){
        return DriverRes.builder()
                .phoneNumber(driver.getPhoneNumber())
                .carNumber(driver.getCarNumber())
                .name(driver.getName())
                .pickupTime(pickupTime)
                .build();
    }
}
