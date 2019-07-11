"use strict";

import  '../css/bootstrap.css'
import  '../css/style.css'
import  '../pages/home/homeStyle.css'
import  '../pages/lists/listsStyle.css'
import  '../pages/tree/treeStyle.css'
import  '../pages/upsert/upsertStyle.css'
import  './directives/search/searchStyle.css'
import  './directives/paging/pagingStyle.css'
import  './directives/treeItem/treeItemStyle.css'

import treeItemTemplate from './directives/treeItem/treeItemTemplate.html';
import pagingTemplate from './directives/paging/pagingTemplate.html';
import searchTemplate from './directives/search/searchTemplate.html';

const angular = require('angular');

const app = angular.module('OrganizationManager',  [require("angular-route")]);

require("./appConfig")(app);
require("./directives/paging/PagingDirective") (app, pagingTemplate);
require("./directives/search/SearchDirective") (app, searchTemplate);
require("./directives/treeItem/TreeItem") (app, treeItemTemplate);
require("./services/OrgService")(app);
require("./services/EmpService")(app);
require("./services/CommonService")(app);
require("../pages/home/HomeCtrl")(app);
require("../pages/lists/organizations/OrgListCtrl")(app);
require("../pages/lists/employees/EmpListCtrl")(app);
require("../pages/tree/TreeCtrl")(app);
require("../pages/upsert/employee/UpsertEmpCtrl")(app);
require("../pages/upsert/organization/UpsertOrgCtrl")(app);
