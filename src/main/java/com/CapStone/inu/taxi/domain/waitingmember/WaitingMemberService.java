package com.CapStone.inu.taxi.domain.waitingmember;

import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WaitingMemberService {
    private final WaitingMemberRepository waitingMemberRepository;

    private HashMap<Long, HashSet<Long>> matched_2 = new HashMap<>();
    private HashMap<Long, HashSet<Pair<Long, Long>>> matched_3 = new HashMap<>();
    private HashMap<Long, HashSet<List<Long>>> matched_4 = new HashMap<>();

    public void addUser() {

    }

    public void matchUser() {
        List<WaitingMember> waitingMembers = waitingMemberRepository.findAll();
        /*.add() 함수 호출하는 부분마다, 프론트한테 매칭됐다고 알려주고, db에 넣어야함.
        그리고, n명이 매칭되면 n-1명인 매칭들은 지우라고 알려줘야함.
        * */

        int n = waitingMembers.size();

        for (int i = 0; i < n; i++)
            for (int j = i + 1; j < n; j++) {
                WaitingMember A = waitingMembers.get(i), B = waitingMembers.get(j);
                Long A_Id = A.getUserId(), B_Id = B.getUserId();
                //이미 a-b가 매칭되어있으므로, 또 검사할 필요 없음.

                if (!matched_2.containsKey(A_Id)) matched_2.put(A_Id, new HashSet<>());
                if (matched_2.get(A_Id).contains(B_Id)) continue;

                Integer A_start_x = A.getStartX(), A_start_y = A.getStartY();
                Integer B_start_x = B.getStartX(), B_start_y = B.getStartY();
                Integer A_end_x = A.getEndX(), A_end_y = A.getEndY();
                Integer B_end_x = B.getEndX(), B_end_y = B.getEndY();

                double start_distance = Math.sqrt((A_start_x - B_start_x) * (A_start_x - B_start_x) +
                        (A_start_y - B_start_y) * (A_start_y - B_start_y));
                double end_distance = Math.sqrt((A_end_x - B_end_x) * (A_end_x - B_end_x) +
                        (A_end_y - B_end_y) * (A_end_y - B_end_y));

                //가중치 설정해줄 필요.
                double now = LocalDateTime.now().getSecond();
                double A_range = now - (double) waitingMembers.get(i).getCreatedDate().getSecond();
                double B_range = now - (double) waitingMembers.get(j).getCreatedDate().getSecond();

                boolean match_success = A_range >= start_distance && B_range >= start_distance
                        && A_range >= end_distance && B_range >= end_distance;

                if (match_success) {
                    //if (!matched_2.containsKey(A_Id)) matched_2.put(A_Id, new HashSet<>());
                    matched_2.get(A_Id).add(B_Id);

                    if (!matched_2.containsKey(B_Id)) matched_2.put(B_Id, new HashSet<>());
                    matched_2.get(B_Id).add(A_Id);

                    //(a<->b), (b<->c), (c<->a)인 경우 확인.
                    for (Long C_Id : matched_2.get(A_Id)) {
                        //if (!matched_2.containsKey(B_Id)) matched_2.put(B_Id, new HashSet<>());
                        if (!matched_2.get(B_Id).contains(C_Id)) continue;

                        Pair<Long, Long> bc = Pair.of(Math.min(B_Id, C_Id), Math.max(B_Id, C_Id));
                        //이미 a-b-c는 매칭되어있었음.
                        if (!matched_3.containsKey(A_Id)) matched_3.put(A_Id, new HashSet<>());
                        if (matched_3.get(A_Id).contains(bc)) continue;

                        //a-b-c 매칭 성공
                        //if (!matched_3.containsKey(A_Id)) matched_3.put(A_Id, new HashSet<>());
                        matched_3.get(A_Id).add(bc);

                        Pair<Long, Long> ac = Pair.of(Math.min(A_Id, C_Id), Math.max(A_Id, C_Id));
                        if (!matched_3.containsKey(B_Id)) matched_3.put(B_Id, new HashSet<>());
                        matched_3.get(B_Id).add(ac);

                        Pair<Long, Long> ab = Pair.of(Math.min(A_Id, B_Id), Math.max(A_Id, B_Id));
                        if (!matched_3.containsKey(C_Id)) matched_3.put(C_Id, new HashSet<>());
                        matched_3.get(C_Id).add(ab);


                        //(a<->b), (a<->c), (a<->d), (b<->c), (b<->d), (c<->d)인 경우 확인.
                        //a,b,c는 이미 한 묶음이므로, (a<->d), (b<->d), (c<->d)만 확인하면 된다. 이건 (a<->cd), (b<->cd)만 확인하면 된다.
                        for (Pair<Long, Long> cd : matched_3.get(A_Id)) {
                            //if (!matched_3.containsKey(B_Id)) matched_3.put(B_Id, new HashSet<>());
                            if (!matched_3.get(B_Id).contains(cd)) continue;
                            Long D_Id;
                            if (!cd.getFirst().equals(C_Id)) D_Id = cd.getFirst();
                            else D_Id = cd.getSecond();

                            List<Long> bcd = new ArrayList<>(List.of(B_Id, C_Id, D_Id));
                            Collections.sort(bcd);
                            //이미 a-b-c-d는 매칭되어있었음.
                            if (!matched_4.containsKey(A_Id)) matched_4.put(A_Id, new HashSet<>());
                            if (matched_4.get(A_Id).contains(bcd)) continue;

                            //a-b-c-d 매칭 성공
                            //if (!matched_4.containsKey(A_Id)) matched_4.put(A_Id, new HashSet<>());
                            matched_4.get(A_Id).add(bcd);

                            List<Long> acd = new ArrayList<>(List.of(A_Id, C_Id, D_Id));
                            Collections.sort(acd);
                            if (!matched_4.containsKey(B_Id)) matched_4.put(B_Id, new HashSet<>());
                            matched_4.get(B_Id).add(acd);

                            List<Long> abd = new ArrayList<>(List.of(A_Id, B_Id, D_Id));
                            Collections.sort(abd);
                            if (!matched_4.containsKey(C_Id)) matched_4.put(C_Id, new HashSet<>());
                            matched_4.get(C_Id).add(abd);

                            List<Long> abc = new ArrayList<>(List.of(A_Id, B_Id, C_Id));
                            Collections.sort(abc);
                            if (!matched_4.containsKey(D_Id)) matched_4.put(D_Id, new HashSet<>());
                            matched_4.get(D_Id).add(abc);
                        }
                    }
                }
            }
    }

    private void del(List<Long> go) {

    }
}
