package com.neu.tms.pojo;

import java.io.Serializable;

public class Bed implements Serializable {
    private Integer id;
    private Integer roomNo;
    private Integer bedStatus;
    private String remarks;
    private String bedNo;
    private Integer isDeleted;

    public Bed() {
    }

    public Bed(Integer id, Integer roomNo, Integer bedStatus, String remarks, String bedNo) {
        this.id = id;
        this.roomNo = roomNo;
        this.bedStatus = bedStatus;
        this.remarks = remarks;
        this.bedNo = bedNo;
        this.isDeleted = 0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(Integer roomNo) {
        this.roomNo = roomNo;
    }

    public Integer getBedStatus() {
        return bedStatus;
    }

    public void setBedStatus(Integer bedStatus) {
        this.bedStatus = bedStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getBedNo() {
        return bedNo;
    }

    public void setBedNo(String bedNo) {
        this.bedNo = bedNo;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public String toString() {
        return "Bed{" +
                "id=" + id +
                ", roomNo=" + roomNo +
                ", bedStatus=" + bedStatus +
                ", remarks='" + remarks + '\'' +
                ", bedNo='" + bedNo + '\'' +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
