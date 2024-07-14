package com.CapStone.inu.taxi.domain.chat;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter
// mongodb에서 쓰는 @Entity, collection은 테이블 이름
// jpa를 쓰지 않아서 기본 생성자를 생성할 필요 없음. 역직렬화를 할 때 jackson 등의 라이브러리를 씀
@Document(collection = "chat")
public class Chat {

    @Id
    private String id;
    private String sender;
    private String content;

    @Field("room_id")
    private Long roomId;

    @CreatedDate
    @Field("created_date")
    private LocalDateTime createdDate;

    @Builder
    private Chat(String sender, String content, Long roomId) {
        this.sender = sender;
        this.content = content;
        this.roomId = roomId;
    }
}
