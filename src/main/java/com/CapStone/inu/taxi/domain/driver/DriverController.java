package com.CapStone.inu.taxi.domain.driver;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DriverController {
    private final DriverService driverService;

    @MessageMapping("depart/{roomId}")
    public void driverDepart(@DestinationVariable Long roomId){
        driverService.assignDriver(roomId);
    }

    @PatchMapping("/arrive/{driverId}")
    public void driverArrive(@PathVariable Long driverId){
        driverService.arrive(driverId);
    }
}
