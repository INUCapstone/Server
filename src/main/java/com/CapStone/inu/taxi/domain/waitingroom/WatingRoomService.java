package com.CapStone.inu.taxi.domain.waitingroom;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WatingRoomService {
    private final WaitingRoomRepository waitingRoomRepository;

    public
}
