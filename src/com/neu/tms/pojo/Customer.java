
package com.neu.tms.pojo;

import java.io.Serializable;
import java.util.Date;

public class Customer implements Serializable {
    private Integer id;
    private Integer isDeleted;
    private String customerName;
    private Integer customerAge;
    private Integer customerSex;
    private String idCard;
    private String roomNo;
    private String buildingNo;
    private Date checkinDate;
    private Date expirationDate;
    private String contactTel;
    private Integer bedId;
    private String psychosomaticState;
    private String attention;
    private Date birthday;
    private String height;
    private String weight;
    private String bloodType;
    private String filepath;
    private Integer userId;
    private Integer levelId;
    private String familyMember;

    public Customer() {
    }

    public Customer(Integer id, Integer isDeleted, String customerName, Integer customerAge, 
                    Integer customerSex, String idCard, String roomNo, String buildingNo, 
                    Date checkinDate, Date expirationDate, String contactTel, Integer bedId, 
                    String psychosomaticState, String attention, Date birthday, 
                    String height, String weight, String bloodType, String filepath, 
                    Integer userId, Integer levelId, String familyMember) {
        this.id = id;
        this.isDeleted = isDeleted;
        this.customerName = customerName;
        this.customerAge = customerAge;
        this.customerSex = customerSex;
        this.idCard = idCard;
        this.roomNo = roomNo;
        this.buildingNo = buildingNo;
        this.checkinDate = checkinDate;
        this.expirationDate = expirationDate;
        this.contactTel = contactTel;
        this.bedId = bedId;
        this.psychosomaticState = psychosomaticState;
        this.attention = attention;
        this.birthday = birthday;
        this.height = height;
        this.weight = weight;
        this.bloodType = bloodType;
        this.filepath = filepath;
        this.userId = userId;
        this.levelId = levelId;
        this.familyMember = familyMember;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Integer getCustomerAge() {
        return customerAge;
    }

    public void setCustomerAge(Integer customerAge) {
        this.customerAge = customerAge;
    }

    public Integer getCustomerSex() {
        return customerSex;
    }

    public void setCustomerSex(Integer customerSex) {
        this.customerSex = customerSex;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getBuildingNo() {
        return buildingNo;
    }

    public void setBuildingNo(String buildingNo) {
        this.buildingNo = buildingNo;
    }

    public Date getCheckinDate() {
        return checkinDate;
    }

    public void setCheckinDate(Date checkinDate) {
        this.checkinDate = checkinDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getContactTel() {
        return contactTel;
    }

    public void setContactTel(String contactTel) {
        this.contactTel = contactTel;
    }

    public Integer getBedId() {
        return bedId;
    }

    public void setBedId(Integer bedId) {
        this.bedId = bedId;
    }

    public String getPsychosomaticState() {
        return psychosomaticState;
    }

    public void setPsychosomaticState(String psychosomaticState) {
        this.psychosomaticState = psychosomaticState;
    }

    public String getAttention() {
        return attention;
    }

    public void setAttention(String attention) {
        this.attention = attention;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getLevelId() {
        return levelId;
    }

    public void setLevelId(Integer levelId) {
        this.levelId = levelId;
    }

    public String getFamilyMember() {
        return familyMember;
    }

    public void setFamilyMember(String familyMember) {
        this.familyMember = familyMember;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", isDeleted=" + isDeleted +
                ", customerName='" + customerName + '\'' +
                ", customerAge=" + customerAge +
                ", customerSex=" + customerSex +
                ", idCard='" + idCard + '\'' +
                ", roomNo='" + roomNo + '\'' +
                ", buildingNo='" + buildingNo + '\'' +
                ", checkinDate=" + checkinDate +
                ", expirationDate=" + expirationDate +
                ", contactTel='" + contactTel + '\'' +
                ", bedId=" + bedId +
                ", psychosomaticState='" + psychosomaticState + '\'' +
                ", attention='" + attention + '\'' +
                ", birthday=" + birthday +
                ", height='" + height + '\'' +
                ", weight='" + weight + '\'' +
                ", bloodType='" + bloodType + '\'' +
                ", filepath='" + filepath + '\'' +
                ", userId=" + userId +
                ", levelId=" + levelId +
                ", familyMember='" + familyMember + '\'' +
                '}';
    }
}
