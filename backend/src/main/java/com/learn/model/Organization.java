package com.learn.model;

import java.util.List;

public class Organization {

    private Integer id;
    private String name;
    private Integer idParent;

    private String nameParent;
    private Integer countEmp;
    private List<Organization> children;

    public Organization() {
    }

    public Organization(Integer id, String name, Integer id_parent) {
        this.id = id;
        this.name = name;
        this.idParent = id_parent;
        children = null;
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



    public String getNameParent() {
        return nameParent;
    }

    public void setNameParent(String nameParent) {
        this.nameParent = nameParent;
    }

    public Integer getCountEmp() {
        return countEmp;
    }

    public void setCountEmp(Integer countEmp) {
        this.countEmp = countEmp;
    }

    public List<Organization> getChildren() {
        return children;
    }

    public void setChildren(List<Organization> children) {
        this.children = children;
    }
}
