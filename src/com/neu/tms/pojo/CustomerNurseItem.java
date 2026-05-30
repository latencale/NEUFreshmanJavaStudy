
package com.neu.tms.pojo;

import java.io.Serializable;
import java.util.Date;

public class CustomerNurseItem implements Serializable {
    private Integer id;
    private Integer itemId;
    private Integer customerId;
    private Integer levelId;
    private Integer nurseNumber;
    private Integer isDeleted;
    private Date buyTime;
    private Date maturityTime;

    public CustomerNurseItem() {
    }

    public CustomerNurseItem(Integer id, Integer itemId, Integer customerId, Integer levelId, 
                             Integer nurseNumber, Integer isDeleted, Date buyTime, Date maturityTime) {
        this.id = id;
        this.itemId = itemId;
        this.customerId = customerId;
        this.levelId = levelId;
        this.nurseNumber = nurseNumber;
        this.isDeleted = isDeleted;
        this.buyTime = buyTime;
        this.maturityTime = maturityTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getLevelId() {
        return levelId;
    }

    public void setLevelId(Integer levelId) {
        this.levelId = levelId;
    }

    public Integer getNurseNumber() {
        return nurseNumber;
    }

    public void setNurseNumber(Integer nurseNumber) {
        this.nurseNumber = nurseNumber;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Date getBuyTime() {
        return buyTime;
    }

    public void setBuyTime(Date buyTime) {
        this.buyTime = buyTime;
    }

    public Date getMaturityTime() {
        return maturityTime;
    }

    public void setMaturityTime(Date maturityTime) {
        this.maturityTime = maturityTime;
    }

    @Override
    public String toString() {
        return "CustomerNurseItem{" +
                "id=" + id +
                ", itemId=" + itemId +
                ", customerId=" + customerId +
                ", levelId=" + levelId +
                ", nurseNumber=" + nurseNumber +
                ", isDeleted=" + isDeleted +
                ", buyTime=" + buyTime +
                ", maturityTime=" + maturityTime +
                '}';
    }
}
