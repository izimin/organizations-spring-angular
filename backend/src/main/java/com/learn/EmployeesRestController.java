package com.learn;

import org.jooq.DSLContext;
import org.jooq.util.maven.example.tables.Employees;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.jooq.impl.DSL.*;
import static org.jooq.util.maven.example.tables.Employees.EMPLOYEES;
import static org.jooq.util.maven.example.tables.Organizations.ORGANIZATIONS;

@RestController
public class EmployeesRestController {
    private final DSLContext ctx;

    @Autowired
    public EmployeesRestController(DSLContext dslContext) {
        this.ctx = dslContext;
    }

    // Создание сотрудника
    @Transactional
    @PostMapping("/employees/list")
    public void createOrganization(@RequestBody Employee emp) {
        ctx.insertInto(EMPLOYEES)
                .columns(EMPLOYEES.NAME, EMPLOYEES.ID_ORGANIZATION, EMPLOYEES.ID_DIRECTOR)
                .values(emp.getName(), emp.getIdOrganization(), emp.getIdDirector())
                .execute();
    }

    // Изменение сотрудника
    @Transactional
    @PutMapping("/employees/list")
    public void updateOrganization (@RequestBody Employee emp) {
        ctx.update(EMPLOYEES)
                .set(EMPLOYEES.NAME, emp.getName())
                .set(EMPLOYEES.ID_DIRECTOR, emp.getIdDirector())
                .set(EMPLOYEES.ID_ORGANIZATION, emp.getIdOrganization())
                .where(EMPLOYEES.ID.eq(emp.getId()))
                .execute();
    }

    // Удаление сотрудника
    @Transactional
    @DeleteMapping("/employees/{id}")
    public Integer deleteEmployee(@PathVariable("id") Integer id) {
        return ctx.deleteFrom(EMPLOYEES)
                .where(EMPLOYEES.ID.eq(id)
                        .and(EMPLOYEES.ID.notIn(ctx.select(EMPLOYEES.ID_DIRECTOR)
                                .from(EMPLOYEES).where(EMPLOYEES.ID_DIRECTOR.isNotNull())))).execute();

        /*ctx.deleteFrom(EMPLOYEES)
                .where(EMPLOYEES.ID.eq(id)
                        .and(notExists(ctx.selectFrom(EMPLOYEES)
                                .where(EMPLOYEES.ID_DIRECTOR.eq(id)))))
                .execute();*/
    }

    // Список сотрудников, претендующих на роль руководителя определенного сотрудника
    @GetMapping("/employees/directors")
    public List<Map<String, Object>> listParentOrg(Integer id, Integer idOrganization) {
        return ctx.withRecursive("tree", "id")
                .as(select(EMPLOYEES.ID).from(EMPLOYEES).where(EMPLOYEES.ID.eq(id))
                        .unionAll(select(EMPLOYEES.ID)
                                .from(EMPLOYEES).innerJoin(table(name("tree")))
                                .on(EMPLOYEES.ID_DIRECTOR.eq(field(name("tree", "id"), Integer.class)))))
                .select(EMPLOYEES.ID, EMPLOYEES.NAME)
                .from(EMPLOYEES).where(EMPLOYEES.ID.notIn(
                        select(field(name("tree", "id"), Integer.class))
                        .from(table((name("tree")))))).and(EMPLOYEES.ID_ORGANIZATION.eq(idOrganization))
                .fetchMaps();
    }

    // Данные об одном сотруднике
    @GetMapping("/employees/{id}")
    public Map<String, Object> listOrg(@PathVariable("id") Integer id) {
        return ctx.select(EMPLOYEES.ID.as("id"),
                EMPLOYEES.NAME.as("name"),
                EMPLOYEES.ID_DIRECTOR.as("idParent"),
                EMPLOYEES.ID_ORGANIZATION.as("idOrganization"))
                .from(EMPLOYEES).where(EMPLOYEES.ID.eq(id)).fetchAnyMap();
    }

    // Список сотрудников
    @GetMapping("/employees/list")
    public List<Map<String, Object>> listOrg(@RequestParam Integer limit, @RequestParam Integer offset, @RequestParam String search) {
        Employees E1 = EMPLOYEES.as("e1");
        Employees E2 = EMPLOYEES.as("e2");
        return ctx.select(
                E1.ID.as("id"),
                E1.NAME.as("name"),
                E1.ID_DIRECTOR.as("idDirector"),
                E2.NAME.as("nameDirector"),
                ORGANIZATIONS.ID.as("idOrganization"),
                ORGANIZATIONS.NAME.as("nameOrganization"))
                .from(E1.leftJoin(E2).on(E1.ID_DIRECTOR.eq(E2.ID)).leftJoin(ORGANIZATIONS).on(ORGANIZATIONS.ID.eq(E1.ID_ORGANIZATION)))
                .where(E1.NAME.like("%"+search.trim()+"%").or(ORGANIZATIONS.NAME.like("%"+search.trim()+"%")))
                .orderBy(E1.ID).limit(limit).offset(offset).fetchMaps();
    }

    // Общее количество строк результата запроса
    @GetMapping("/employees/list/count")
    public Map<String, Object> listOrg(@RequestParam String search) {
        return ctx.select(count(EMPLOYEES.ID).as("countEmp"))
                .from(EMPLOYEES).leftJoin(ORGANIZATIONS).on(ORGANIZATIONS.ID.eq(EMPLOYEES.ID_ORGANIZATION))
                .where(EMPLOYEES.NAME.like("%"+search.trim()+"%")
                        .or(ORGANIZATIONS.NAME.like("%"+search.trim()+"%"))).fetchAnyMap();
    }

    // Список сотрудников в виде дерева
    @GetMapping("/employees/tree")
    public List<Map<String, Object>> treeListEmp(@RequestParam Integer limit, @RequestParam Integer offset) {
        List<Map<String, Object>> root = ctx.select(EMPLOYEES.ID.as("id"),
                EMPLOYEES.NAME.as("name"))
                .from(EMPLOYEES)
                .where(EMPLOYEES.ID_DIRECTOR.isNull())
                .fetchMaps();
        for (Map<String, Object> r: root) {
            getChildren(r, limit, offset);
        }

        // Костыль для подгрузки (элементы, которые подгружаются offset'ом не возьмет первые элементы для дочерних подгруженных)
        if  (offset != 0) {
            for (Map<String, Object> r: root) {
                for (Map<String, Object> c: (List<Map<String, Object>>) r.get("children"))
                    getChildren(c, limit+offset, 0);
            }
        }

        return root;
    }

    private void getChildren(Map<String, Object> root, Integer limit, Integer offset) {
        if (root == null)
            return;
        List<Map<String, Object>> child = ctx.select(EMPLOYEES.ID, EMPLOYEES.NAME).from(EMPLOYEES)
                .where(EMPLOYEES.ID_DIRECTOR.eq((Integer) root.get("id")))
                .orderBy(EMPLOYEES.ID).limit(limit).offset(offset).fetchMaps();
        for (Map<String, Object> c: child) {
            getChildren(c, limit, offset);
        }
        root.put("children", child);
    }

    // Удаление организации с сотруднками
    @Transactional
    @PutMapping("/employees/list/deleteOrganization")
    public void changeOrgId(@RequestBody Integer id) {
        ctx.update(EMPLOYEES)
                .set(EMPLOYEES.ID_ORGANIZATION, (Integer) null)
                .where(EMPLOYEES.ID_ORGANIZATION.eq(id))
                .execute();
        ctx.deleteFrom(ORGANIZATIONS)
                .where(ORGANIZATIONS.ID.eq(id)).execute();
    }
}
