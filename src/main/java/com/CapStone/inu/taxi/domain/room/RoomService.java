package com.CapStone.inu.taxi.domain.room;

import com.CapStone.inu.taxi.domain.driver.DriverRepository;
import com.CapStone.inu.taxi.domain.waitingmember.WaitingMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomService {
    private final WaitingMemberRepository waitingMemberRepository;
    private final DriverRepository driverRepository;

}
