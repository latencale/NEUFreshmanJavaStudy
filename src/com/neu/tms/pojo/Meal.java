
package com.neu.tms.pojo;

import java.io.Serializable;

public class Meal implements Serializable {
    private Integer id;
    private Integer customerId;
    private String weekDay;
    private Integer foodId;
    private Integer mealType;
    private String taste;
    private Integer isDeleted;

    public Meal() {
    }

    public Meal(Integer id, Integer customerId, String weekDay, Integer foodId, 
                Integer mealType, String taste, Integer isDeleted) {
        this.id = id;
        this.customerId = customerId;
        this.weekDay = weekDay;
        this.foodId = foodId;
        this.mealType = mealType;
        this.taste = taste;
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

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public Integer getFoodId() {
        return foodId;
    }

    public void setFoodId(Integer foodId) {
        this.foodId = foodId;
    }

    public Integer getMealType() {
        return mealType;
    }

    public void setMealType(Integer mealType) {
        this.mealType = mealType;
    }

    public String getTaste() {
        return taste;
    }

    public void setTaste(String taste) {
        this.taste = taste;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public String toString() {
        return "Meal{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", weekDay='" + weekDay + '\'' +
                ", foodId=" + foodId +
                ", mealType=" + mealType +
                ", taste='" + taste + '\'' +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
