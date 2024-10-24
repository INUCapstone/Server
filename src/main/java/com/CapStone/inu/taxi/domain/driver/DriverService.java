package com.CapStone.inu.taxi.domain.driver;

import com.CapStone.inu.taxi.domain.driver.dto.DriverRes;
import com.CapStone.inu.taxi.domain.member.Member;
import com.CapStone.inu.taxi.domain.member.MemberRepository;
import com.CapStone.inu.taxi.domain.receipt.Receipt;
import com.CapStone.inu.taxi.domain.receipt.ReceiptRepository;
import com.CapStone.inu.taxi.domain.room.Room;
import com.CapStone.inu.taxi.domain.room.RoomRepository;
import com.CapStone.inu.taxi.domain.waitingmemberRoom.WaitingMemberRoom;
import com.CapStone.inu.taxi.global.common.State;
import com.CapStone.inu.taxi.global.common.StatusCode;
import com.CapStone.inu.taxi.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.CapStone.inu.taxi.global.common.StatusCode.MEMBER_NOT_EXIST;
import static com.CapStone.inu.taxi.global.common.StatusCode.ROOM_NOT_EXIST;

@Transactional
@Service
@RequiredArgsConstructor
public class DriverService {
    private final DriverRepository driverRepository;
    private final RoomRepository roomRepository;
    private final MemberRepository memberRepository;
    private final ReceiptRepository receiptRepository;
    private final SimpMessagingTemplate template;

    public void assignDriver(Long roomId) {

        /*일단 findFirstByState(State.STAND)로 아무나 데려오자.

         * 방에서 모든 유저가 레디 -> 대기중인 기사님에게 신호를 보냄 -> 기사님이 수락하면 room에 기사님이 배정됨
         * 위의 로직으로 가려면, request와 response dto를 주고받아서 해야될것같고, 이렇게 하면 안됨.

         * 택시 경로가 시작되는 지점으로부터 가장 가까운 driver를 가져오려면, findByState(State state)로 다 가져와서 가까운 기사님 찾으면 됨.
         * */
        Room room= roomRepository.findById(roomId).orElseThrow(() -> new CustomException(ROOM_NOT_EXIST));
        Driver driver = driverRepository.findFirstByState(State.STAND).orElseThrow(() -> new CustomException(StatusCode.DRIVER_NO_AVAILABLE));
        room.setDriverId(driver.getId());
        depart(room,driver);
    }

    //유저들이 모두 레디를 마쳐 택시를 타고 떠났다.
    public void depart(Room room,Driver driver) {

        //결제 기록 남겨두기
        Receipt receipt = Receipt.builder()
                .driverId(room.getDriverId())
                .taxiDuration(room.getTaxiDuration())
                .taxiFare(room.getTaxiFare())
                .build();

        for (WaitingMemberRoom waitingMemberRoom : room.getWaitingMemberRoomList()) {
            receipt.getMemberIds().add(waitingMemberRoom.getId());
        }
        driver.setState(State.DEPART);//영속성 컨텍스트에 의해 변경사항 자동으로 반영.

        receiptRepository.save(receipt);

        //유저 금액 차감 기능 구현
        for (WaitingMemberRoom waitingMemberRoom : room.getWaitingMemberRoomList()) {
            Member member = memberRepository.findById(waitingMemberRoom.getWaitingMember().getId())
                    .orElseThrow(() -> new CustomException(MEMBER_NOT_EXIST));
            member.chargePoint(-waitingMemberRoom.getCharge());
        }

        template.convertAndSend("/sub/taxi/" + room.getRoomId(), DriverRes.from(driver));

    }
}
