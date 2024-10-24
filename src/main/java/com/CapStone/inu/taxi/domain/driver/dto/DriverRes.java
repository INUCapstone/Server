package com.CapStone.inu.taxi.domain.driver.dto;

import com.CapStone.inu.taxi.domain.driver.Driver;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DriverRes {
    private final String phoneNumber;
    private final String carNumber;
    private final String name;

    @Builder
    public DriverRes(String phoneNumber, String carNumber, String name) {
        this.phoneNumber = phoneNumber;
        this.carNumber = carNumber;
        this.name = name;
    }

    public static DriverRes from(Driver driver){
        return DriverRes.builder()
                .phoneNumber(driver.getPhoneNumber())
                .carNumber(driver.getCarNumber())
                .name(driver.getName())
                .build();
    }
}
