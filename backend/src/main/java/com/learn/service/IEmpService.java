package com.learn.service;

import com.learn.model.Employee;

import java.util.List;

public interface IEmpService {

    void insert(Employee emp);

    void update(Employee emp);

    Integer delete(Integer id);

    Employee getById(Integer id);

    List<Employee> getList(Integer limit, Integer offset, String search);

    List<Employee> getListDirectors(Integer id, Integer idOrganozation);

    Integer getCount(String search);

    List<Employee> getTree(Integer limit, Integer offset);

    void deleteOrgWithEmp(Integer id);
}
