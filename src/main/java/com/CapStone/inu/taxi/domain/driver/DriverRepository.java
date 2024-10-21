package com.CapStone.inu.taxi.domain.driver;

import com.CapStone.inu.taxi.global.common.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver,Long> {

    //어떤 방의 경로가 시작되는 유저를 기준으로 가장 가까운 기사님을 매칭할 때, 모든 기사님을 가져오기 위해 사용.
    List<Driver> findByState(State state);

    //기사님의 위치와 관계 없이, 아무나 데려올 때 사용.
    Optional<Driver> findFirstByState(State state);
}
