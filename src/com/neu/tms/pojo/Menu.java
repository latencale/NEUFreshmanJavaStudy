
package com.neu.tms.pojo;

import java.io.Serializable;

public class Menu implements Serializable {
    private Integer id;
    private String menusIndex;
    private String title;
    private String icon;
    private String path;
    private Integer parentId;

    public Menu() {
    }

    public Menu(Integer id, String menusIndex, String title, String icon, 
                String path, Integer parentId) {
        this.id = id;
        this.menusIndex = menusIndex;
        this.title = title;
        this.icon = icon;
        this.path = path;
        this.parentId = parentId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMenusIndex() {
        return menusIndex;
    }

    public void setMenusIndex(String menusIndex) {
        this.menusIndex = menusIndex;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "id=" + id +
                ", menusIndex='" + menusIndex + '\'' +
                ", title='" + title + '\'' +
                ", icon='" + icon + '\'' +
                ", path='" + path + '\'' +
                ", parentId=" + parentId +
                '}';
    }
}
