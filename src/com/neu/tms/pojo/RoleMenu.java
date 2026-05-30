
package com.neu.tms.pojo;

import java.io.Serializable;

public class RoleMenu implements Serializable {
    private Integer id;
    private Integer roleId;
    private Integer menu;

    public RoleMenu() {
    }

    public RoleMenu(Integer id, Integer roleId, Integer menu) {
        this.id = id;
        this.roleId = roleId;
        this.menu = menu;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getMenu() {
        return menu;
    }

    public void setMenu(Integer menu) {
        this.menu = menu;
    }

    @Override
    public String toString() {
        return "RoleMenu{" +
                "id=" + id +
                ", roleId=" + roleId +
                ", menu=" + menu +
                '}';
    }
}
