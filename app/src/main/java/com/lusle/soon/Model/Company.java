package com.lusle.soon.Model;

public class Company {
    private Integer id;
    private String logo_path;
    private String name;

    public Company(Integer id, String logo_path, String name) {
        this.id = id;
        this.logo_path = logo_path;
        this.name = name;
    }

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
}
