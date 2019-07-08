package com.learn.controller;

import com.learn.model.Employee;
import com.learn.service.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EmployeesRestController {

    @Autowired
    private EmpService empService;

    // Создание сотрудника
    @Transactional
    @PostMapping("/employees/list")
    public void createEmployee(@RequestBody Employee emp) {
        empService.insert(emp);
    }

    // Изменение сотрудника
    @Transactional
    @PutMapping("/employees/list")
    public void updateEmployee (@RequestBody Employee emp) {
        empService.update(emp);
    }

    // Удаление сотрудника
    @Transactional
    @DeleteMapping("/employees/{id}")
    public Integer deleteEmployee(@PathVariable("id") Integer id) {
        return empService.delete(id);
    }

    // Список сотрудников, претендующих на роль руководителя определенного сотрудника
    @GetMapping("/employees/directors")
    public List<Employee> listDirectors(Integer id, Integer idOrganization) {
        return empService.getListDirectors(id, idOrganization);
    }

    // Данные об одном сотруднике
    @GetMapping("/employees/{id}")
    public Employee listOrg(@PathVariable("id") Integer id) {
        return empService.getById(id);
    }

    // Список сотрудников
    @GetMapping("/employees/list")
    public List<Employee> listOrg(@RequestParam Integer limit, @RequestParam Integer offset, @RequestParam String search) {
        return empService.getList(limit, offset, search);
    }

    // Общее количество строк результата запроса
    @GetMapping("/employees/list/count")
    public Integer listOrg(@RequestParam String search) {
        return empService.getCount(search);
    }

    // Список сотрудников в виде дерева
    @GetMapping("/employees/tree")
    public List<Employee> treeListEmp(@RequestParam Integer limit, @RequestParam Integer offset) {
        return empService.getTree(limit, offset);
    }

    // Удаление организации с сотруднками
    @Transactional
    @PutMapping("/employees/list/deleteOrganization")
    public void changeOrgId(@RequestBody Integer id) {
        empService.deleteOrgWithEmp(id);
    }
}
