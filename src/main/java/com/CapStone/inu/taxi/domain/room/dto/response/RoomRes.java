package com.CapStone.inu.taxi.domain.room.dto.response;

import com.CapStone.inu.taxi.domain.room.Room;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

import java.util.*;

@Getter
public class RoomRes {
    private final Long roomId;
    private final Integer currentMemberCnt;
    private final List<pathInfo> pathInfoList;
    private final Integer time;
    private final Integer charge;
    private final List<memberInfo> memberList;
    private final Boolean isDelete;
    private final Boolean isStart;

    @Builder
    public RoomRes(Long roomId, Integer currentMemberCnt, List<pathInfo> pathInfoList, Integer time,
                   Integer charge, List<memberInfo> memberList, Boolean isDelete, Boolean isStart) {
        this.roomId = roomId;
        this.currentMemberCnt = currentMemberCnt;
        this.pathInfoList = pathInfoList;
        this.time = time;
        this.charge = charge;
        this.memberList = memberList;
        this.isDelete = isDelete;
        this.isStart = isStart;
    }

    public static RoomRes from(Room room, ResponseEntity<String> responseEntity,List<memberInfo> memberList, Boolean isDelete, Boolean isStart) {
        //responseEntity 파싱해서 path,time,charge등 구하기.
        Gson gson = new Gson();
        Route route = gson.fromJson(responseEntity.getBody(), ApiResponse.class).getRoutes()[0];//1가지 경로만 탐색함.(getRoutes()[0])

        List<pathInfo> pathInfoList = new ArrayList<>();
        for (Section section : route.getSections()) {
            for (Road road : section.getRoads()) {
                Double[] vertexes = road.getVertexes();
                for (int i = 0; i < vertexes.length; i += 2) {
                    double x = vertexes[i], y = vertexes[i + 1];
                    pathInfo waypoint = new pathInfo();
                    waypoint.setX(x);
                    waypoint.setY(y);
                    pathInfoList.add(waypoint);
                }
            }
        }
        Integer charge = route.getSummary().getFare().getTaxi() + route.getSummary().getFare().getToll();
        Integer time = route.getSummary().getDuration();

        return RoomRes.builder()
                .roomId(room.getRoomId())
                .pathInfoList(pathInfoList)
                .time(time)
                .charge(charge)
                .isDelete(isDelete)
                .isStart(isStart)
                .memberList(memberList)
                .currentMemberCnt(memberList.size())
                .build();
    }
}

@Getter
class ApiResponse {
    @Expose(deserialize = false)
    private String trans_id;
    private Route[] routes;
}

@Getter
class Route {
    @Expose(deserialize = false)
    private Integer result_code;
    @Expose(deserialize = false)
    private String result_msg;
    private Summary summary;
    private Section[] sections;
}

@Getter
class Summary {
    @Expose(deserialize = false)
    private Origin origin;
    @Expose(deserialize = false)
    private Destination destination;
    @Expose(deserialize = false)
    private Waypoint[] waypoints;
    @Expose(deserialize = false)
    private String priority;
    @Expose(deserialize = false)
    private Bound bound;
    private Fare fare;
    private Integer distance;
    private Integer duration;
}

class Origin {
    private String name;
    private Double x;
    private Double y;
}

class Destination {
    private String name;
    private Double x;
    private Double y;
}

class Waypoint {
    private String name;
    private Double x;
    private Double y;
}

class Bound {
    private Double min_x;
    private Double min_y;
    private Double max_x;
    private Double max_y;
}

@Getter
class Fare {
    private Integer taxi;
    private Integer toll;
}

@Getter
class Section {
    @Expose(deserialize = false)
    private Integer distance;
    @Expose(deserialize = false)
    private Integer duration;
    @Expose(deserialize = false)
    private Bound bound;
    private Road[] roads;
    @Expose(deserialize = false)
    private Guide[] guides;
}

@Getter
class Road {
    @Expose(deserialize = false)
    private String name;
    @Expose(deserialize = false)
    private Integer distance;
    @Expose(deserialize = false)
    private Integer duration;
    @Expose(deserialize = false)
    private Double traffic_speed;
    @Expose(deserialize = false)
    private Integer traffic_state;
    private Double[] vertexes;
}

class Guide {
    private String name;
    private Double x;
    private Double y;
    private Integer distance;
    private Integer duration;
    private Integer type;
    private String guidance;
    private Integer road_index;
}

class memberInfo {
    private String nickname;
    private Long memberId;
    private Boolean isReady;
}

@Setter
class pathInfo {
    private Double x;
    private Double y;
}