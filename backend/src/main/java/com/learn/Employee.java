package com.learn;

public class Employee {
    private Integer id;
    private String name;
    private Integer idOrganization;
    private Integer idDirector;

    public Employee() {
    }

    public Employee(Integer id, String name, Integer idOrganization, Integer idDirector) {
        this.id = id;
        this.name = name;
        this.idOrganization = idOrganization;
        this.idDirector = idDirector;
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
}
