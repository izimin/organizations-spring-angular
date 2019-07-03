module.exports = function(ngModule) {
    ngModule.controller('EmpListCtrl', ['$scope', '$rootScope', '$http', '$location', function($scope, $rootScope, $http, $location) {
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
            $http({
                method: 'GET',
                url: 'http://localhost/employees/list',
                params: {
                    limit: $scope.limit,
                    offset: ($scope.curPage-1)*$scope.limit,
                    search: $scope.searchStr
                }
            }).then(
                function(res) { // success
                    $scope.employees = res.data;
                    getPages();
                },
                function(res) { // error
                    console.log("Error: " + res.status + " : " + res.data);
                }
            );
        }

        function getPages() {
            $http({
                method: 'GET',
                url: 'http://localhost/employees/list/count',
                params: {
                    search: $scope.searchStr
                }
            }).then(
                function(res) { // success
                    $scope.countPages = Math.ceil(res.data.countEmp / $scope.limit);
                    let begin = $scope.curPage === $scope.countPages ? ($scope.curPage > 5  ?  $scope.curPage - 4 : 1) :
                        $scope.curPage === $scope.countPages - 1 ? ($scope.curPage > 4 ? $scope.curPage - 3 : 1) :
                            $scope.curPage > 2 ? $scope.curPage - 2 : 1;

                    let end = $scope.curPage === 1 ? ($scope.countPages > 5  ?  5 : $scope.countPages) :
                        $scope.curPage === 2 ? ($scope.countPages > 6  ?  5 : $scope.countPages) :
                            $scope.curPage + 2 <= $scope.countPages ? $scope.curPage + 2 : $scope.countPages;
                    $scope.pages = [];
                    for (let i = begin; i <= end; i++) {
                        $scope.pages.push(i);
                    }
                },
                function(res) { // error
                    console.log("Error: " + res.status + " : " + res.data);
                }
            );
        }

        $scope.deleteEmployee = function(employee) {
            $http.delete('http://localhost/employees/' + employee.id)
                .then(
                    function(res) {
                        if (res.data === 0)
                            alert("Удаление не было произведено. \nУ сотрудника есть люди в подчинении.")
                        else
                            _refreshListEmp();
                    },
                    function(res) {
                        console.log("Error: " + res.status + " : " + res.data);
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