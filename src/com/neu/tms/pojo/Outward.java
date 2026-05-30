
package com.neu.tms.pojo;

import java.io.Serializable;
import java.util.Date;

public class Outward implements Serializable {
    private Integer id;
    private String remarks;
    private Integer isDeleted;
    private Integer customerId;
    private String outgoingReason;
    private Date outgoingTime;
    private Date expectedReturnTime;
    private Date actualReturnTime;
    private String escorted;
    private String relation;
    private String escortedTel;
    private Integer auditStatus;
    private String auditPerson;
    private Date auditTime;

    public Outward() {
    }

    public Outward(Integer id, String remarks, Integer isDeleted, Integer customerId, 
                   String outgoingReason, Date outgoingTime, Date expectedReturnTime, 
                   Date actualReturnTime, String escorted, String relation, 
                   String escortedTel, Integer auditStatus, String auditPerson, Date auditTime) {
        this.id = id;
        this.remarks = remarks;
        this.isDeleted = isDeleted;
        this.customerId = customerId;
        this.outgoingReason = outgoingReason;
        this.outgoingTime = outgoingTime;
        this.expectedReturnTime = expectedReturnTime;
        this.actualReturnTime = actualReturnTime;
        this.escorted = escorted;
        this.relation = relation;
        this.escortedTel = escortedTel;
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

    public String getOutgoingReason() {
        return outgoingReason;
    }

    public void setOutgoingReason(String outgoingReason) {
        this.outgoingReason = outgoingReason;
    }

    public Date getOutgoingTime() {
        return outgoingTime;
    }

    public void setOutgoingTime(Date outgoingTime) {
        this.outgoingTime = outgoingTime;
    }

    public Date getExpectedReturnTime() {
        return expectedReturnTime;
    }

    public void setExpectedReturnTime(Date expectedReturnTime) {
        this.expectedReturnTime = expectedReturnTime;
    }

    public Date getActualReturnTime() {
        return actualReturnTime;
    }

    public void setActualReturnTime(Date actualReturnTime) {
        this.actualReturnTime = actualReturnTime;
    }

    public String getEscorted() {
        return escorted;
    }

    public void setEscorted(String escorted) {
        this.escorted = escorted;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getEscortedTel() {
        return escortedTel;
    }

    public void setEscortedTel(String escortedTel) {
        this.escortedTel = escortedTel;
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

    public void setCreateTime(Date date) {
        this.outgoingTime = date;
    }
    @Override
    public String toString() {
        return "Outward{" +
                "id=" + id +
                ", remarks='" + remarks + '\'' +
                ", isDeleted=" + isDeleted +
                ", customerId=" + customerId +
                ", outgoingReason='" + outgoingReason + '\'' +
                ", outgoingTime=" + outgoingTime +
                ", expectedReturnTime=" + expectedReturnTime +
                ", actualReturnTime=" + actualReturnTime +
                ", escorted='" + escorted + '\'' +
                ", relation='" + relation + '\'' +
                ", escortedTel='" + escortedTel + '\'' +
                ", auditStatus=" + auditStatus +
                ", auditPerson='" + auditPerson + '\'' +
                ", auditTime=" + auditTime +
                '}';
    }


}
