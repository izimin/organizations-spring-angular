package com.learn.service;

import com.learn.model.Employee;
import com.learn.repository.EmpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EmpService implements IEmpService {

    @Autowired
    private EmpRepository empRepos;

    @Override
    public void insert(Employee emp) {
        empRepos.add(emp);
    }

    @Override
    public void update(Employee emp) {
        empRepos.update(emp);
    }

    @Override
    public Integer delete(Integer id) {
        return empRepos.delete(id);
    }

    @Override
    public Employee getById(Integer id) {
        return empRepos.findOne(id);
    }

    @Override
    public List<Employee> getList(Integer limit, Integer offset, String search) {
        return empRepos.findAll(limit, offset, search);
    }

    @Override
    public List<Employee> getListDirectors(Integer id, Integer idOrganozation) {
        return empRepos.findAllDirectors(id, idOrganozation);
    }

    @Override
    public Integer getCount(String search) {
        return empRepos.countRecords(search);
    }

    @Override
    public List<Employee> getTree(Integer limit, Integer offset) {

        List<Employee> root = empRepos.findRoot();

        for (Employee r: root)
            getChildren(r, limit, offset);

        // Костыль для подгрузки (элементы, которые подгружаются offset'ом не возьмет первые элементы для дочерних подгруженных)
        if  (offset != 0) {
            for (Employee r: root) {
                for (Employee c:  r.getChildren())
                    getChildren(c, limit + offset, 0);
            }
        }

        return root;
    }

    private void getChildren(Employee root, Integer limit, Integer offset) {
        if (root == null)
            return;
        List<Employee> child = empRepos.findChildren(root.getId(), limit, offset);
        for (Employee c: child) {
            getChildren(c, limit, offset);
        }
        root.setChildren(child);
    }

    @Override
    public void deleteOrgWithEmp(Integer id) {
        empRepos.deleteOrgWithEmp(id);
    }
}
