package com.lusle.soon.Model;

import java.io.Serializable;

import androidx.annotation.Nullable;

public class Company implements Serializable {
    private Integer id;
    private String logo_path;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogo_path() {
        return logo_path;
    }

    public void setLogo_path(String logo_path) {
        this.logo_path = logo_path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean isT = false;
        Company cm = (Company) obj;
        if(id.equals(cm.getId())){
            isT = true;
        }
        return isT;
    }
}
