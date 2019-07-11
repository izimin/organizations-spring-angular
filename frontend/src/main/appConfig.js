module.exports = function(ngModule){
    ngModule.config(['$routeProvider', function ($routeProvider) {
        $routeProvider
            .when('/', {
                template: require('../pages/home/homeView.html'),
                controller: 'HomeCtrl'
            })
            .when('/organizations/list', {
                template: require('../pages/lists/organizations/orgListView.html'),
                controller: 'OrgListCtrl'
            })
            .when('/employees/list', {
                template: require('../pages/lists/employees/empListView.html'),
                controller: 'EmpListCtrl'
            })
            .when('/:objects/tree', {
                template: require('../pages/tree/treeView.html'),
                controller: 'TreeViewCtrl'
            })
            .when('/organization/:action', {
                template: require('../pages/upsert/organization/upsertOrgView.html'),
                controller: 'UpsertOrgCtrl'
            })
            .when('/employee/:action', {
                template: require('../pages/upsert/employee/upsertEmpView.html'),
                controller: 'UpsertEmpCtrl'
            })
            .otherwise({
                redirectTo: '/'
            });
    }]);
};