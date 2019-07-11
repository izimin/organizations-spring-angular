module.exports = function(ngModule) {
    ngModule.controller('EmpListCtrl', ['$scope', '$rootScope', '$http', '$location', 'empService', 'commonService',
        function($scope, $rootScope, $http, $location, empService, commonService) {
        $rootScope.title = "Список сотрудников";

        // Строка поиска
        $scope.searchStr = "";

        // Пэйджинг
        $scope.limit = 5;
        $scope.countPages = 0;
        $scope.curPage = 1;
        $scope.pages = [];

        // Список  струдников
        $scope.employees = [];
        $scope.directors = [];
        $rootScope.employeeForm = {
            id: -1,
            name: "",
            idDirector: null,
            idOrganization: null
        };

        $rootScope.$on("refreshListEmp", function(){
            _refreshListEmp();
        });

        $scope.refresh = function() {
            $scope.curPage = 1;
            _refreshListEmp();
        };

        _refreshListEmp();

        $scope.changeNumPage = function(elem) {
            if ($scope.curPage === elem)
                return;
            $scope.curPage = elem;
            _refreshListEmp();
        };

        function _refreshListEmp() {
            empService.getList($scope.limit, ($scope.curPage-1)*$scope.limit, $scope.searchStr)
            .then(function(res) {
                    $scope.employees = res.data;
                    _refreshListPages();
                });
        }

        function _refreshListPages() {
            empService.getCount($scope.searchStr)
                .then(function(res) {
                    $scope.countPages = Math.ceil(Number(res.data) / $scope.limit);
                    $scope.pages = commonService.getListPages($scope.countPages, $scope.curPage);
                });
        }

        $scope.deleteEmployee = function(employee) {
            empService.delete(employee.id)
                .then(function(res) {
                        if (res.data === 0)
                            alert("Удаление не было произведено. \nУ сотрудника есть люди в подчинении.")
                        else
                            _refreshListEmp();
                });
        };

        $scope.editEmployee = function(employee) {
            $scope.employeeForm.id = employee.id;
            $scope.employeeForm.name = employee.name;
            $scope.employeeForm.idDirector = employee.idDirector;
            $scope.employeeForm.idOrganization = employee.idOrganization;
        };
    }]);
}