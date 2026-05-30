
package com.neu.tms.pojo;

import java.io.Serializable;

public class NurseContent implements Serializable {
    private Integer id;
    private String serialNumber;
    private String nursingName;
    private String servicePrice;
    private String message;
    private Integer status;
    private String executionCycle;
    private String executionTimes;
    private Integer isDeleted;

    public NurseContent() {
    }

    public NurseContent(Integer id, String serialNumber, String nursingName, String servicePrice, 
                        String message, Integer status, String executionCycle, 
                        String executionTimes, Integer isDeleted) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.nursingName = nursingName;
        this.servicePrice = servicePrice;
        this.message = message;
        this.status = status;
        this.executionCycle = executionCycle;
        this.executionTimes = executionTimes;
        this.isDeleted = isDeleted;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getNursingName() {
        return nursingName;
    }

    public void setNursingName(String nursingName) {
        this.nursingName = nursingName;
    }

    public String getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(String servicePrice) {
        this.servicePrice = servicePrice;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getExecutionCycle() {
        return executionCycle;
    }

    public void setExecutionCycle(String executionCycle) {
        this.executionCycle = executionCycle;
    }

    public String getExecutionTimes() {
        return executionTimes;
    }

    public void setExecutionTimes(String executionTimes) {
        this.executionTimes = executionTimes;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public String toString() {
        return "NurseContent{" +
                "id=" + id +
                ", serialNumber='" + serialNumber + '\'' +
                ", nursingName='" + nursingName + '\'' +
                ", servicePrice='" + servicePrice + '\'' +
                ", message='" + message + '\'' +
                ", status=" + status +
                ", executionCycle='" + executionCycle + '\'' +
                ", executionTimes='" + executionTimes + '\'' +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
