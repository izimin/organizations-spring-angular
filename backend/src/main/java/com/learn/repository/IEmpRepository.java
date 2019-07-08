package com.learn.repository;

import com.learn.model.Employee;

import java.util.List;

public interface IEmpRepository {
    void add(Employee emp);

    void update(Employee emp);

    Integer delete(Integer id);

    Employee findOne(Integer id);

    List<Employee> findAll(Integer limit, Integer offset, String search);

    List<Employee> findAllDirectors(Integer id, Integer idOrganozation);

    Integer countRecords(String search);

    List<Employee> findRoot();

    List<Employee> findChildren(Integer idDirector, Integer limit, Integer offset);

    void deleteOrgWithEmp(Integer id);
}
