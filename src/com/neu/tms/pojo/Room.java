
package com.neu.tms.pojo;

import java.io.Serializable;

public class Room implements Serializable {
    private Integer id;
    private String roomFloor;
    private Integer roomNo;

    public Room() {
    }

    public Room(Integer id, String roomFloor, Integer roomNo) {
        this.id = id;
        this.roomFloor = roomFloor;
        this.roomNo = roomNo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoomFloor() {
        return roomFloor;
    }

    public void setRoomFloor(String roomFloor) {
        this.roomFloor = roomFloor;
    }

    public Integer getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(Integer roomNo) {
        this.roomNo = roomNo;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", roomFloor='" + roomFloor + '\'' +
                ", roomNo=" + roomNo +
                '}';
    }
}
