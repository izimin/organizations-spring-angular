package com.learn.service;

import com.learn.model.Organization;

import java.util.List;

public interface IOrgService {

    void insert(Organization org);

    void update(Organization org);

    Integer delete(Integer id);

    Organization getById(Integer id);

    List<Organization> getList(Integer limit, Integer offset, String search);

    List<Organization> getListParent(Integer id);

    Integer getCount(String search);

    List<Organization> getTree(Integer limit, Integer offset);

    Boolean checkForChildren(Integer id);
}
