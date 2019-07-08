package com.learn.repository;

import com.learn.model.Organization;

import java.util.List;

public interface IOrgRepository {

    void add(Organization org);

    void update(Organization org);

    Integer delete(Integer id);

    Organization findOne(Integer id);

    List<Organization> findAll(Integer limit, Integer offset, String search);

    List<Organization> findAllParent(Integer id);

    Integer countRecords(String search);

    Boolean checkExistsChildren(Integer id);

    List<Organization> findRoot();

    List<Organization> findChildren(Integer idParent, Integer limit, Integer offset);

}
