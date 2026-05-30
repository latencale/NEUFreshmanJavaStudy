
package com.neu.tms.pojo;

import java.io.Serializable;

public class CustomerPreference implements Serializable {
    private Integer id;
    private Integer customerId;
    private String preferences;
    private String attention;
    private String remark;
    private Integer isDeleted;

    public CustomerPreference() {
    }

    public CustomerPreference(Integer id, Integer customerId, String preferences, 
                              String attention, String remark, Integer isDeleted) {
        this.id = id;
        this.customerId = customerId;
        this.preferences = preferences;
        this.attention = attention;
        this.remark = remark;
        this.isDeleted = isDeleted;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    public String getAttention() {
        return attention;
    }

    public void setAttention(String attention) {
        this.attention = attention;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public String toString() {
        return "CustomerPreference{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", preferences='" + preferences + '\'' +
                ", attention='" + attention + '\'' +
                ", remark='" + remark + '\'' +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
