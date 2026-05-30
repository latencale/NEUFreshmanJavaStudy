
package com.neu.tms.pojo;

import java.io.Serializable;
import java.util.Date;

public class BackDown implements Serializable {
    private Integer id;
    private String remarks;
    private Integer isDeleted;
    private Integer customerId;
    private Date retreatTime;
    private Integer retreatType;
    private String retreatReason;
    private Integer auditStatus;
    private String auditPerson;
    private Date auditTime;

    public BackDown() {
    }

    public BackDown(Integer id, String remarks, Integer isDeleted, Integer customerId, 
                    Date retreatTime, Integer retreatType, String retreatReason, 
                    Integer auditStatus, String auditPerson, Date auditTime) {
        this.id = id;
        this.remarks = remarks;
        this.isDeleted = isDeleted;
        this.customerId = customerId;
        this.retreatTime = retreatTime;
        this.retreatType = retreatType;
        this.retreatReason = retreatReason;
        this.auditStatus = auditStatus;
        this.auditPerson = auditPerson;
        this.auditTime = auditTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    public Date getRetreatTime() {
        return retreatTime;
    }

    public void setRetreatTime(Date retreatTime) {
        this.retreatTime = retreatTime;
    }

    public Integer getRetreatType() {
        return retreatType;
    }

    public void setRetreatType(Integer retreatType) {
        this.retreatType = retreatType;
    }

    public String getRetreatReason() {
        return retreatReason;
    }

    public void setRetreatReason(String retreatReason) {
        this.retreatReason = retreatReason;
    }

    public Integer getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getAuditPerson() {
        return auditPerson;
    }

    public void setAuditPerson(String auditPerson) {
        this.auditPerson = auditPerson;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    @Override
    public String toString() {
        return "BackDown{" +
                "id=" + id +
                ", remarks='" + remarks + '\'' +
                ", isDeleted=" + isDeleted +
                ", customerId=" + customerId +
                ", retreatTime=" + retreatTime +
                ", retreatType=" + retreatType +
                ", retreatReason='" + retreatReason + '\'' +
                ", auditStatus=" + auditStatus +
                ", auditPerson='" + auditPerson + '\'' +
                ", auditTime=" + auditTime +
                '}';
    }
}
