
package com.neu.tms.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

public class Food implements Serializable {
    private Integer id;
    private String foodName;
    private String foodType;
    private BigDecimal price;
    private Integer isHalal;
    private String foodImg;

    public Food() {
    }

    public Food(Integer id, String foodName, String foodType, BigDecimal price, 
                Integer isHalal, String foodImg) {
        this.id = id;
        this.foodName = foodName;
        this.foodType = foodType;
        this.price = price;
        this.isHalal = isHalal;
        this.foodImg = foodImg;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getIsHalal() {
        return isHalal;
    }

    public void setIsHalal(Integer isHalal) {
        this.isHalal = isHalal;
    }

    public String getFoodImg() {
        return foodImg;
    }

    public void setFoodImg(String foodImg) {
        this.foodImg = foodImg;
    }

    @Override
    public String toString() {
        return "Food{" +
                "id=" + id +
                ", foodName='" + foodName + '\'' +
                ", foodType='" + foodType + '\'' +
                ", price=" + price +
                ", isHalal=" + isHalal +
                ", foodImg='" + foodImg + '\'' +
                '}';
    }
}
