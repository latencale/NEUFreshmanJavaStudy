package com.neu.tms.pojo;

import java.io.Serializable;

public class NurseLevelItem implements Serializable {
    private Integer id;
    private Integer levelId;
    private Integer itemId;

    public NurseLevelItem() {
    }

    public NurseLevelItem(Integer id, Integer levelId, Integer itemId) {
        this.id = id;
        this.levelId = levelId;
        this.itemId = itemId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLevelId() {
        return levelId;
    }

    public void setLevelId(Integer levelId) {
        this.levelId = levelId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    @Override
    public String toString() {
        return "NurseLevelItem{" +
                "id=" + id +
                ", levelId=" + levelId +
                ", itemId=" + itemId +
                '}';
    }
}
