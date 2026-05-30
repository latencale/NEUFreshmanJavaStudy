
package com.neu.tms.pojo;

import java.io.Serializable;
import java.util.Date;

public class BedDetails implements Serializable {
    private Integer id;
    private Date startDate;
    private Date endDate;
    private String bedDetails;
    private Integer customerId;
    private Integer bedId;
    private Integer isDeleted;

    public BedDetails() {
    }

    public BedDetails(Integer id, Date startDate, Date endDate, String bedDetails, 
                      Integer customerId, Integer bedId, Integer isDeleted) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.bedDetails = bedDetails;
        this.customerId = customerId;
        this.bedId = bedId;
        this.isDeleted = isDeleted;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getBedDetails() {
        return bedDetails;
    }

    public void setBedDetails(String bedDetails) {
        this.bedDetails = bedDetails;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getBedId() {
        return bedId;
    }

    public void setBedId(Integer bedId) {
        this.bedId = bedId;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public String toString() {
        return "BedDetails{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", bedDetails='" + bedDetails + '\'' +
                ", customerId=" + customerId +
                ", bedId=" + bedId +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
