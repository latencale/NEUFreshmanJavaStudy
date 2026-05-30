
package com.neu.tms.pojo;

import java.io.Serializable;
import java.util.Date;

public class Role implements Serializable {
    private Integer id;
    private Date createTime;
    private Date updateTime;
    private Integer updateBy;
    private Integer isDeleted;
    private String name;

    public Role() {
    }

    public Role(Integer id, Date createTime, Date updateTime, Integer updateBy, 
                Integer isDeleted, String name) {
        this.id = id;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.updateBy = updateBy;
        this.isDeleted = isDeleted;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", updateBy=" + updateBy +
                ", isDeleted=" + isDeleted +
                ", name='" + name + '\'' +
                '}';
    }
}
