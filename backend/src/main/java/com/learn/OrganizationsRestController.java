package com.learn;

import org.jooq.DSLContext;
import org.jooq.util.maven.example.tables.Organizations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.jooq.impl.DSL.*;
import static org.jooq.util.maven.example.tables.Employees.EMPLOYEES;
import static org.jooq.util.maven.example.tables.Organizations.ORGANIZATIONS;

@RestController
public class OrganizationsRestController {
    private final DSLContext ctx;

    @Autowired
    public OrganizationsRestController(DSLContext dslContext) {
        this.ctx = dslContext;
    }

    // Создание организации
    @Transactional
    @PostMapping("/organizations/list")
    public void createOrganization(@RequestBody Organization org) {
        ctx.insertInto(ORGANIZATIONS)
                .columns(ORGANIZATIONS.NAME, ORGANIZATIONS.ID_PARENT)
                .values(org.name, org.idParent)
                .execute();
    }

    // Изменение организации
    @Transactional
    @PutMapping("/organizations/list")
    public void updateOrganization (@RequestBody Organization org) {
        ctx.update(ORGANIZATIONS)
                .set(ORGANIZATIONS.NAME, org.getName())
                .set(ORGANIZATIONS.ID_PARENT, org.getIdParent())
                .where(ORGANIZATIONS.ID.eq(org.getId()))
                .execute();
    }

    // Удаление организации
    @Transactional
    @DeleteMapping("/organizations/{id}")
    public Integer deleteOrganization(@PathVariable("id") Integer id) {
        return ctx.deleteFrom(ORGANIZATIONS)
                .where(ORGANIZATIONS.ID.eq(id)
                        .and(notExists(ctx.selectFrom(EMPLOYEES)
                                .where(EMPLOYEES.ID_ORGANIZATION.eq(id)))))
                .execute();
    }

    // Данные об одной организации
    @GetMapping("/organizations/{id}")
    public Map<String, Object> listOrg(@PathVariable("id") Integer id) {
        return ctx.select(ORGANIZATIONS.ID.as("id"),
                ORGANIZATIONS.NAME.as("name"),
                ORGANIZATIONS.ID_PARENT.as("idParent"))
                .from(ORGANIZATIONS).where(ORGANIZATIONS.ID.eq(id)).fetchAnyMap();
    }

    // Проверка, есть ли дочерние элементы у организации
    @GetMapping("/organizations/checkExistsChildren/{id}")
    public Boolean checkChildrenOrganizations(@PathVariable("id") Integer id) {
        return ctx.fetchExists(ctx.selectFrom(ORGANIZATIONS).where(ORGANIZATIONS.ID_PARENT.eq(id)));
    }

    // Список организаций, которые претендуют на роль родительско организации
    @GetMapping("/organizations/parents")
    public List<Map<String, Object>> listParentOrg(Integer id) {
        return ctx.withRecursive("tree", "id")
                .as(select(ORGANIZATIONS.ID).from(ORGANIZATIONS).where(ORGANIZATIONS.ID.eq(id))
                        .unionAll(select(ORGANIZATIONS.ID)
                                .from(ORGANIZATIONS).innerJoin(table(name("tree")))
                                .on(ORGANIZATIONS.ID_PARENT.eq(field(name("tree", "id"), Integer.class)))))
                .select(ORGANIZATIONS.ID, ORGANIZATIONS.NAME)
                .from(ORGANIZATIONS).where(ORGANIZATIONS.ID.notIn(
                        select(field(name("tree", "id"), Integer.class))
                                .from(table((name("tree"))))))
                .fetchMaps();
    }

    // Список организаций
    @GetMapping("/organizations/list")
    public List<Map<String, Object>> listOrg(@RequestParam Integer limit, @RequestParam Integer offset, @RequestParam String search) {
        Organizations O1 = ORGANIZATIONS.as("o1");
        Organizations O2 = ORGANIZATIONS.as("o2");
        return ctx.select(
                O1.ID.as("id"),
                O1.NAME.as("name"),
                O1.ID_PARENT.as("idParent"),
                O2.NAME.as("nameParent"),
                count(EMPLOYEES.ID).as("countEmp"))
                .from(O1.leftJoin(O2).on(O1.ID_PARENT.eq(O2.ID)).leftJoin(EMPLOYEES).on(EMPLOYEES.ID_ORGANIZATION.eq(O1.ID)))
                .where(O1.NAME.like("%"+search.trim()+"%"))
                .groupBy(O1.ID, O1.NAME, O1.ID_PARENT, O2.NAME)
                .orderBy(O1.ID).limit(limit).offset(offset).fetchMaps();
    }

    // Общее количество организаций в запросе
    @GetMapping("/organizations/list/count")
    public Map<String, Object> listOrg(@RequestParam String search) {
        return ctx.select(count(ORGANIZATIONS.ID).as("countOrg")).from(ORGANIZATIONS).where(ORGANIZATIONS.NAME.like("%"+search.trim()+"%")).fetchAnyMap();
    }

    // Список организаций в виде дерева
    @GetMapping("/organizations/tree")
    public List<Map<String, Object>> treeListOrg(@RequestParam Integer limit, @RequestParam Integer offset) {

        List<Map<String, Object>> root =
                ctx.select(ORGANIZATIONS.ID.as("id"), ORGANIZATIONS.NAME.as("name"))
                .from(ORGANIZATIONS)
                .where(ORGANIZATIONS.ID_PARENT.isNull())
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
        List<Map<String, Object>> child = ctx.select(ORGANIZATIONS.ID, ORGANIZATIONS.NAME).from(ORGANIZATIONS)
                .where(ORGANIZATIONS.ID_PARENT.eq((Integer) root.get("id")))
                .orderBy(ORGANIZATIONS.ID).limit(limit).offset(offset).fetchMaps();
        for (Map<String, Object> c : child) {
            getChildren(c, limit, offset);
        }
        root.put("children", child);
    }
}
