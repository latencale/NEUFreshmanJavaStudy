package com.neu.tms.pojo;


import java.io.Serializable;
import java.util.Date;

public class TUser implements Serializable {
    private Integer id;
    private Date createTime;
    private String createBy;
    private Date updateTime;
    private Integer updateBy;
    private Integer isDeleted;
    private String nickname;
    private String username;
    private String password;
    private Integer sex;
    private String email;
    private String phoneNumber;
    private Integer roleId;

    public TUser() {
    }

    public TUser(Integer id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public TUser(Integer id, String username, String password, Integer roleId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roleId = roleId;
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

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "用户信息{" +
                "id=" + id +
                ", 创建时间=" + createTime +
                ", 创建人=" + createBy +
                ", 更新时间=" + updateTime +
                ", 更新人=" + updateBy +
                ", 是否删除=" + isDeleted +
                ", 姓名='" + nickname + '\'' +
                ", 账号='" + username + '\'' +
                ", 密码='" + password + '\'' +
                ", 性别=" + (sex != null ? (sex == 1 ? "男" : "女") : "未知") +
                ", 邮箱='" + email + '\'' +
                ", 电话='" + phoneNumber + '\'' +
                ", 角色=" + (roleId != null ? (roleId == 1 ? "管理员" : "健康管家") : "未知") +
                '}';
    }
}
