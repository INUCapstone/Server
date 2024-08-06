package com.CapStone.inu.taxi.domain.waitingroom;

import com.CapStone.inu.taxi.global.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WaitingRoomRepository {
    private final RedisRepository redisRepository;

}
