package com.neu.tms.pojo;

import java.io.Serializable;
import java.util.Date;

public class NurseRecord implements Serializable {
    private Integer id;
    private Integer isDeleted;
    private Integer customerId;
    private Integer itemId;
    private Date nursingTime;
    private String nursingContent;
    private Integer nursingCount;
    private Integer userId;

    public NurseRecord() {
    }

    public NurseRecord(Integer id, Integer isDeleted, Integer customerId, Integer itemId, 
                       Date nursingTime, String nursingContent, Integer nursingCount, Integer userId) {
        this.id = id;
        this.isDeleted = isDeleted;
        this.customerId = customerId;
        this.itemId = itemId;
        this.nursingTime = nursingTime;
        this.nursingContent = nursingContent;
        this.nursingCount = nursingCount;
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Date getNursingTime() {
        return nursingTime;
    }

    public void setNursingTime(Date nursingTime) {
        this.nursingTime = nursingTime;
    }

    public String getNursingContent() {
        return nursingContent;
    }

    public void setNursingContent(String nursingContent) {
        this.nursingContent = nursingContent;
    }

    public Integer getNursingCount() {
        return nursingCount;
    }

    public void setNursingCount(Integer nursingCount) {
        this.nursingCount = nursingCount;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "NurseRecord{" +
                "id=" + id +
                ", isDeleted=" + isDeleted +
                ", customerId=" + customerId +
                ", itemId=" + itemId +
                ", nursingTime=" + nursingTime +
                ", nursingContent='" + nursingContent + '\'' +
                ", nursingCount=" + nursingCount +
                ", userId=" + userId +
                '}';
    }
}
