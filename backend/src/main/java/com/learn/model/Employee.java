package com.learn.model;

import java.util.List;

public class Employee {

    private Integer id;
    private String name;
    private Integer idOrganization;
    private Integer idDirector;

    private String nameDirector;
    private String nameOrganization;
    private List<Employee> children;

    public Employee() {
    }

    public Employee(Integer id, String name, Integer idOrganization, Integer idDirector) {
        this.id = id;
        this.name = name;
        this.idOrganization = idOrganization;
        this.idDirector = idDirector;
        this.nameDirector = null;
        this.nameOrganization = null;
        this.children = null;
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

    public Integer getIdOrganization() {
        return idOrganization;
    }

    public void setIdOrganization(Integer idOrganization) {
        this.idOrganization = idOrganization;
    }

    public Integer getIdDirector() {
        return idDirector;
    }

    public void setIdDirector(Integer idDirector) {
        this.idDirector = idDirector;
    }



    public String getNameDirector() {
        return nameDirector;
    }

    public void setNameDirector(String nameDirector) {
        this.nameDirector = nameDirector;
    }

    public String getNameOrganization() {
        return nameOrganization;
    }

    public void setNameOrganization(String nameOrganization) {
        this.nameOrganization = nameOrganization;
    }

    public List<Employee> getChildren() {
        return children;
    }

    public void setChildren(List<Employee> children) {
        this.children = children;
    }
}
