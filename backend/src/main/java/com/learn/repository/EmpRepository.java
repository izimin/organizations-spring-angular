package com.learn.repository;

import com.learn.model.Employee;
import org.jooq.DSLContext;
import org.jooq.util.maven.example.tables.Employees;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.jooq.impl.DSL.*;
import static org.jooq.util.maven.example.tables.Employees.EMPLOYEES;
import static org.jooq.util.maven.example.tables.Organizations.ORGANIZATIONS;

@Repository
public class EmpRepository implements IEmpRepository{

    private final DSLContext ctx;

    @Autowired
    public EmpRepository(DSLContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void add(Employee emp) {
        ctx.insertInto(EMPLOYEES)
                .columns(EMPLOYEES.NAME, EMPLOYEES.ID_ORGANIZATION, EMPLOYEES.ID_DIRECTOR)
                .values(emp.getName(), emp.getIdOrganization(), emp.getIdDirector())
                .execute();
    }

    @Override
    public void update(Employee emp) {
        ctx.update(EMPLOYEES)
                .set(EMPLOYEES.NAME, emp.getName())
                .set(EMPLOYEES.ID_DIRECTOR, emp.getIdDirector())
                .set(EMPLOYEES.ID_ORGANIZATION, emp.getIdOrganization())
                .where(EMPLOYEES.ID.eq(emp.getId()))
                .execute();
    }

    @Override
    public Integer delete(Integer id) {
        return ctx.deleteFrom(EMPLOYEES)
                .where(EMPLOYEES.ID.eq(id)
                        .and(EMPLOYEES.ID.notIn(ctx.select(EMPLOYEES.ID_DIRECTOR)
                                .from(EMPLOYEES).where(EMPLOYEES.ID_DIRECTOR.isNotNull())))).execute();
    }

    @Override
    public Employee findOne(Integer id) {
        return ctx.select(EMPLOYEES.ID.as("id"),
                EMPLOYEES.NAME.as("name"),
                EMPLOYEES.ID_DIRECTOR.as("idParent"),
                EMPLOYEES.ID_ORGANIZATION.as("idOrganization"))
                .from(EMPLOYEES).where(EMPLOYEES.ID.eq(id)).fetchAnyInto(Employee.class);
    }

    @Override
    public List<Employee> findAll(Integer limit, Integer offset, String search) {
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
                .orderBy(E1.ID).limit(limit).offset(offset).fetchInto(Employee.class);
    }

    @Override
    public List<Employee> findAllDirectors(Integer id, Integer idOrganization) {
        return ctx.withRecursive("tree", "id")
                .as(select(EMPLOYEES.ID).from(EMPLOYEES).where(EMPLOYEES.ID.eq(id))
                        .unionAll(select(EMPLOYEES.ID)
                                .from(EMPLOYEES).innerJoin(table(name("tree")))
                                .on(EMPLOYEES.ID_DIRECTOR.eq(field(name("tree", "id"), Integer.class)))))
                .select(EMPLOYEES.ID, EMPLOYEES.NAME)
                .from(EMPLOYEES).where(EMPLOYEES.ID.notIn(
                        select(field(name("tree", "id"), Integer.class))
                                .from(table((name("tree")))))).and(EMPLOYEES.ID_ORGANIZATION.eq(idOrganization))
                .fetchInto(Employee.class);
    }

    @Override
    public Integer countRecords(String search) {
        return ctx.select(count(EMPLOYEES.ID))
                .from(EMPLOYEES).leftJoin(ORGANIZATIONS).on(ORGANIZATIONS.ID.eq(EMPLOYEES.ID_ORGANIZATION))
                .where(EMPLOYEES.NAME.like("%"+search.trim()+"%")
                        .or(ORGANIZATIONS.NAME.like("%"+search.trim()+"%"))).fetchAnyInto(Integer.class);
    }

    @Override
    public List<Employee> findRoot() {
        return ctx.select(EMPLOYEES.ID.as("id"),
                EMPLOYEES.NAME.as("name"))
                .from(EMPLOYEES)
                .where(EMPLOYEES.ID_DIRECTOR.isNull())
                .fetchInto(Employee.class);
    }

    @Override
    public List<Employee> findChildren(Integer idDirector, Integer limit, Integer offset) {
        return ctx.select(EMPLOYEES.ID, EMPLOYEES.NAME).from(EMPLOYEES)
                .where(EMPLOYEES.ID_DIRECTOR.eq(idDirector))
                .orderBy(EMPLOYEES.ID).limit(limit).offset(offset).fetchInto(Employee.class);
    }

    @Override
    public void deleteOrgWithEmp(Integer id) {
        ctx.update(EMPLOYEES)
                .set(EMPLOYEES.ID_ORGANIZATION, (Integer) null)
                .where(EMPLOYEES.ID_ORGANIZATION.eq(id))
                .execute();
        ctx.deleteFrom(ORGANIZATIONS)
                .where(ORGANIZATIONS.ID.eq(id)).execute();
    }
}
