"use strict";

import  '../css/bootstrap.css'
import  '../css/style.css'
const angular = require('angular');

const app = angular.module('OrganizationManager',  [require("angular-route")]);

require("./appConfig")(app);
require("./controllers/HomeCtrl")(app);
require("./controllers/OrgListCtrl")(app);
require("./controllers/EmpListCtrl")(app);
require("./controllers/TreeViewCtrl")(app);
require("./controllers/UpsertEmpCtrl")(app);
require("./controllers/UpsertOrgCtrl")(app);