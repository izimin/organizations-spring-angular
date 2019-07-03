module.exports = function(ngModule){
    ngModule.config(['$routeProvider','$locationProvider', function ($routeProvider, $locationProvider) {
        $routeProvider
            .when('/', {
                template: require('../views/home.html'),
                controller: 'HomeCtrl'
            })
            .when('/organizations/list', {
                template: require('../views/orgList.html'),
                controller: 'OrgListCtrl'
            })
            .when('/employees/list', {
                template: require('../views/empList.html'),
                controller: 'EmpListCtrl'
            })
            .when('/organizations/tree', {
                template: require('../views/treeView.html'),
                controller: 'TreeViewCtrl'
            })
            .when('/employees/tree', {
                template: require('../views/treeView.html'),
                controller: 'TreeViewCtrl'
            })
            .when('/organization/create', {
                template: require('../views/upsertOrg.html'),
                controller: 'UpsertOrgCtrl'
            })
            .when('/organization/update', {
                template: require('../views/upsertOrg.html'),
                controller: 'UpsertOrgCtrl'
            })
            .when('/employee/create', {
                template: require('../views/upsertEmp.html'),
                controller: 'UpsertEmpCtrl'
            })
            .when('/employee/update', {
                template: require('../views/upsertEmp.html'),
                controller: 'UpsertEmpCtrl'
            })
            .otherwise({
                redirectTo: '/'
            });
    }]);
};