
package com.lusle.android.soon.Model.Schema;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Genre implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("icon_path")
    @Expose
    private String icon_path;
    @SerializedName("name")
    @Expose
    private String name;

    public Genre(Integer id, String icon_path, String name) {
        this.id = id;
        this.icon_path = icon_path;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon_path() {
        return icon_path;
    }

    public void setIcon_path(String icon_path) {
        this.icon_path = icon_path;
    }

    @Override
    public String toString() {
        return "Genre{" +
                "id=" + id +
                ", icon_path='" + icon_path + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
