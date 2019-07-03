package com.learn;

public class Organization {
    Integer id;
    String name;
    Integer idParent;

    public Organization() {
    }

    public Organization(Integer id, String name, Integer id_parent) {
        this.id = id;
        this.name = name;
        this.idParent = id_parent;
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

    public Integer getIdParent() {
        return idParent;
    }

    public void setIdParent(Integer idParent) {
        this.idParent = idParent;
    }
}
