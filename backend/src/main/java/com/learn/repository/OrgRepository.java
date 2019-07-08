package com.learn.repository;

import com.learn.model.Organization;
import org.jooq.DSLContext;
import org.jooq.util.maven.example.tables.Organizations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.jooq.impl.DSL.*;
import static org.jooq.util.maven.example.tables.Employees.EMPLOYEES;
import static org.jooq.util.maven.example.tables.Organizations.ORGANIZATIONS;

@Repository
public class OrgRepository implements IOrgRepository{

    private final DSLContext ctx;

    @Autowired
    public OrgRepository(DSLContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void add(Organization org) {
        ctx.insertInto(ORGANIZATIONS)
                .columns(ORGANIZATIONS.NAME, ORGANIZATIONS.ID_PARENT)
                .values(org.getName(), org.getIdParent())
                .execute();
    }

    @Override
    public void update(Organization org) {
        ctx.update(ORGANIZATIONS)
                .set(ORGANIZATIONS.NAME, org.getName())
                .set(ORGANIZATIONS.ID_PARENT, org.getIdParent())
                .where(ORGANIZATIONS.ID.eq(org.getId()))
                .execute();
    }

    @Override
    public Integer delete(Integer id) {
        return ctx.deleteFrom(ORGANIZATIONS)
                .where(ORGANIZATIONS.ID.eq(id)
                        .and(notExists(ctx.selectFrom(EMPLOYEES)
                                .where(EMPLOYEES.ID_ORGANIZATION.eq(id)))))
                .execute();
    }

    @Override
    public Organization findOne(Integer id) {
        return ctx.select(ORGANIZATIONS.ID.as("id"),
                ORGANIZATIONS.NAME.as("name"),
                ORGANIZATIONS.ID_PARENT.as("idParent"))
                .from(ORGANIZATIONS).where(ORGANIZATIONS.ID.eq(id)).fetchAnyInto(Organization.class);
    }

    @Override
    public List<Organization> findAll(Integer limit, Integer offset, String search) {
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
                .orderBy(O1.ID).limit(limit).offset(offset).fetchInto(Organization.class);
    }

    @Override
    public List<Organization> findAllParent(Integer id) {
        return ctx.withRecursive("tree", "id")
                .as(select(ORGANIZATIONS.ID).from(ORGANIZATIONS).where(ORGANIZATIONS.ID.eq(id))
                        .unionAll(select(ORGANIZATIONS.ID)
                                .from(ORGANIZATIONS).innerJoin(table(name("tree")))
                                .on(ORGANIZATIONS.ID_PARENT.eq(field(name("tree", "id"), Integer.class)))))
                .select(ORGANIZATIONS.ID, ORGANIZATIONS.NAME)
                .from(ORGANIZATIONS).where(ORGANIZATIONS.ID.notIn(
                        select(field(name("tree", "id"), Integer.class))
                                .from(table((name("tree"))))))
                .fetchInto(Organization.class);
    }

    @Override
    public Integer countRecords(String search) {
        return ctx.select(count(ORGANIZATIONS.ID).as("countOrg"))
                .from(ORGANIZATIONS)
                .where(ORGANIZATIONS.NAME.like("%"+search.trim()+"%"))
                .fetchAnyInto(Integer.class);
    }

    @Override
    public Boolean checkExistsChildren(Integer id) {
        return ctx.fetchExists(ctx.selectFrom(ORGANIZATIONS).where(ORGANIZATIONS.ID_PARENT.eq(id)));
    }

    @Override
    public List<Organization> findRoot() {
        return ctx.select(ORGANIZATIONS.ID.as("id"), ORGANIZATIONS.NAME.as("name"))
                .from(ORGANIZATIONS)
                .where(ORGANIZATIONS.ID_PARENT.isNull())
                .fetchInto(Organization.class);
    }

    @Override
    public List<Organization> findChildren(Integer idParent, Integer limit, Integer offset) {
        return ctx.select(ORGANIZATIONS.ID, ORGANIZATIONS.NAME).from(ORGANIZATIONS)
                .where(ORGANIZATIONS.ID_PARENT.eq(idParent))
                .orderBy(ORGANIZATIONS.ID).limit(limit).offset(offset)
                .fetchInto(Organization.class);
    }
}
