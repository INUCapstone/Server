package com.CapStone.inu.taxi.domain.chat;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRepository extends MongoRepository<Chat,String> {
}
