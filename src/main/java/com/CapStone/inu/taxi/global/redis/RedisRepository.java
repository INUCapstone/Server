package com.CapStone.inu.taxi.global.redis;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    // ValueOperations는 Redis에서 단순한 키-값 쌍을 조작하는 데 사용
    // 이를 통해 문자열 값 또는 객체를 Redis에 저장하거나 조회
    // opsForValue() 메소드를 사용하여 ValueOperations 객체를 가져올 수 있다.
    private ValueOperations<String,Object> valueOperations;

    // ListOperations는 Redis의 queue(list)를 조작하는 데 사용
    private ListOperations<String,Object> listOperations;

    // HashOperations는 Redis의 해시를 조작하는 데 사용
    private HashOperations<String, String, Object> hashOperations;

    // ZSetOperations는 Redis의 Sorted Set(정렬된 집합)을 조작하는 데 사용
    // Sorted Set은 요소들이 점수(score)에 따라 정렬되는 데이터 구조
    // opsForZSet() 메소드를 사용하여 ZSetOperations 객체를 가져올 수 있다.
    private ZSetOperations<String, Object> zSetOperations;


    @PostConstruct
    private void init() {
        valueOperations = redisTemplate.opsForValue();
        listOperations = redisTemplate.opsForList();
        hashOperations = redisTemplate.opsForHash();
        zSetOperations = redisTemplate.opsForZSet();
    }

    // Sorted set
    @Value("${redis.sortedset.key}")
    private String sortedSetBindingKey;

    @Value("${redis.list.key}")
    private String listBindingKey;

    @Value("${redis.hash.key}")
    private String hashBindingKey;


    /******************************
     * Key-Value 작업
     ******************************/
    // Redis 값을 등록/수정
    public void saveKeyValue(String key, Object value) {
        valueOperations.set(key, value);
    }

    // Redis 값을 등록/수정
    public void saveKeyValueWithTTL(String key, Object value, Duration duration) {
        valueOperations.set(key, value, duration);
    }

    // Redis 키를 기반으로 값을 조회
    public Object getKeyValue(String key) {
        return valueOperations.get(key);
    }

    // Redis 키값을 기반으로 row 삭제합니다.
    public void deleteKeyValue(String key) {
        redisTemplate.delete(key);
    }



    /******************************
     * Sorted Set 작업
     ******************************/
    // Redis Sorted Set에 항목 추가
    public void addToSortedset(Object task, double score) {
        zSetOperations.add(sortedSetBindingKey, task, score);
    }

    // 특정 범위의 항목 조회
    public Set<Object> getFromSortedsetByScore(double minScore, double maxScore) {
        return zSetOperations.rangeByScore(sortedSetBindingKey, minScore, maxScore);
    }

    // Sorted Set에서 최상위 항목 제거 및 반환
    public Object popFromSortedset() {
        Set<Object> tasks = zSetOperations.range(sortedSetBindingKey, 0, 0); // 최상위 항목 조회
        if (tasks != null && !tasks.isEmpty()) {
            Object task = tasks.iterator().next();
            zSetOperations.remove(sortedSetBindingKey, task); // 최상위 항목 제거
            return task;
        }
        return null;
    }

    // Sorted Set의 모든 항목 조회
    public Set<Object> getAllFromSortedset() {
        return zSetOperations.range(sortedSetBindingKey, 0, -1); // 전체 범위 조회
    }

    // Redis 키값을 기반으로 row 삭제합니다.
    public void clearSortedset() {
        redisTemplate.delete(sortedSetBindingKey);
    }

    /******************************
     * List(Queue) 작업
     ******************************/
    // Redis 리스트에 항목 추가 (오른쪽 끝에 추가)
    public void addToListRight(Object value) {
        listOperations.rightPush(listBindingKey, value);
    }

    // Redis 리스트에 항목 추가 (왼쪽 끝에 추가)
    public void addToListLeft(Object value) {
        listOperations.leftPush(listBindingKey, value);
    }

    // Redis 리스트에서 특정 인덱스의 항목 조회
    public Object getFromList(long index) {
        return listOperations.index(listBindingKey, index);
    }

    // Redis 리스트에서 특정 값 삭제
    public void deleteFromList(Object value) {
        listOperations.remove(listBindingKey, 1, value);
    }

    // Redis 리스트의 전체 항목 조회
    public List<Object> getAllFromList() {
        return listOperations.range(listBindingKey, 0, -1);
    }

    // Redis 리스트의 길이 조회
    public Long getListSize() {
        return listOperations.size(listBindingKey);
    }

    /******************************
     * Hash 작업
     ******************************/
    // 해시 키를 만드는 헬퍼 메소드
    private String getHashKey(Long memberId) {
        return "member:" + memberId;
    }

    // Redis 해시에 필드와 값을 추가하거나 업데이트
    public void putInHash(Long memberId, String field, Object value) {
        hashOperations.put(getHashKey(memberId), field, value);
    }

    // Redis 해시에서 특정 필드의 값을 조회
    public Object getFromHash(Long memberId, String field) {
        return hashOperations.get(getHashKey(memberId), field);
    }

    // Redis 해시에서 특정 필드를 삭제
    public void deleteFromHash(Long memberId, String field) {
        hashOperations.delete(getHashKey(memberId), field);
    }

    // Redis 해시에서 모든 필드와 값 조회
    public Map<String, Object> getAllFromHash(Long memberId) {
        return hashOperations.entries(getHashKey(memberId));
    }

    // Redis 해시 전체 삭제
    public void deleteHash(Long memberId) {
        redisTemplate.delete(getHashKey(memberId));
    }
}
