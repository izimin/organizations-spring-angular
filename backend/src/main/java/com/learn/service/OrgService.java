package com.learn.service;

import com.learn.model.Organization;
import com.learn.repository.OrgRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrgService implements IOrgService{

    @Autowired
    private OrgRepository orgRepository;

    @Override
    public void insert(Organization org) {
        orgRepository.add(org);
    }

    @Override
    public void update(Organization org) {
        orgRepository.update(org);
    }

    @Override
    public Integer delete(Integer id) {
        return orgRepository.delete(id);
    }

    @Override
    public Organization getById(Integer id) {
        return orgRepository.findOne(id);
    }

    @Override
    public List<Organization> getList(Integer limit, Integer offset, String search) {
        return orgRepository.findAll(limit, offset, search);
    }

    @Override
    public List<Organization> getListParent(Integer id) {
        return orgRepository.findAllParent(id);
    }

    @Override
    public Integer getCount(String search) {
        return orgRepository.countRecords(search);
    }

    @Override
    public List<Organization> getTree(Integer limit, Integer offset) {

        List<Organization> root = orgRepository.findRoot();

        for (Organization r: root)
            getChildren(r, limit, offset);

        // Костыль для подгрузки (элементы, которые подгружаются offset'ом не возьмет первые элементы для дочерних подгруженных)
        if  (offset != 0) {
            for (Organization r: root) {
                for (Organization c:  r.getChildren())
                    getChildren(c, limit + offset, 0);
            }
        }

        return root;
    }

    private void getChildren(Organization root, Integer limit, Integer offset) {
        if (root == null)
            return;
        List<Organization> child = orgRepository.findChildren(root.getId(), limit, offset);
        for (Organization c: child) {
            getChildren(c, limit, offset);
        }
        root.setChildren(child);
    }

    @Override
    public Boolean checkForChildren(Integer id) {
        return orgRepository.checkExistsChildren(id);
    }
}
