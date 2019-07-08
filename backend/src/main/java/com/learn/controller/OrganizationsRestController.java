package com.learn.controller;

import com.learn.model.Organization;
import com.learn.service.OrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class OrganizationsRestController {

    @Autowired
    private OrgService orgService;

    // Создание организации
    @Transactional
    @PostMapping("/organizations/list")
    public void createOrganization(@RequestBody Organization org) {
        orgService.insert(org);
    }

    // Изменение организации
    @Transactional
    @PutMapping("/organizations/list")
    public void updateOrganization (@RequestBody Organization org) {
        orgService.update(org);
    }

    // Удаление организации
    @Transactional
    @DeleteMapping("/organizations/{id}")
    public Integer deleteOrganization(@PathVariable("id") Integer id) {
        return orgService.delete(id);
    }

    // Данные об одной организации
    @GetMapping("/organizations/{id}")
    public Organization listOrg(@PathVariable("id") Integer id) {
        return orgService.getById(id);
    }

    // Проверка, есть ли дочерние элементы у организации
    @GetMapping("/organizations/checkExistsChildren/{id}")
    public Boolean checkChildrenOrganizations(@PathVariable("id") Integer id) {
        return orgService.checkForChildren(id);
    }

    // Список организаций, которые претендуют на роль родительско организации
    @GetMapping("/organizations/parents")
    public List<Organization> listParentOrg(Integer id) {
        return orgService.getListParent(id);
    }

    // Список организаций
    @GetMapping("/organizations/list")
    public List<Organization> listOrg(@RequestParam Integer limit, @RequestParam Integer offset, @RequestParam String search) {
        return orgService.getList(limit, offset, search);
    }

    // Общее количество организаций в запросе
    @GetMapping("/organizations/list/count")
    public Integer listOrg(@RequestParam String search) {
        return orgService.getCount(search);
    }

    // Список организаций в виде дерева
    @GetMapping("/organizations/tree")
    public List<Organization> treeListOrg(@RequestParam Integer limit, @RequestParam Integer offset) {
        return orgService.getTree(limit, offset);
    }
}
