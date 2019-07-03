module.exports = function(ngModule) {
    ngModule.controller('UpsertEmpCtrl', ['$scope', '$rootScope','$http', '$location', function($scope, $rootScope, $http, $location) {
        let method = "";
        let config = { params: {}};

        if ($location.path() === "/employee/create") {
            $rootScope.title = "Добавление сотрудника";
            $rootScope.btnText = "Добавить";
            method = "POST";
        }
        else {
            $rootScope.title = "Изменение сотрудника";
            $rootScope.btnText = "Изменить";
            method = "PUT";
            config.params.id = $rootScope.employeeForm.id;
        }

        $scope.directors = [{id: null, name: "" }];
        $scope.listOrganizations = [];

        // Запрос списка организаций
        $http.get('http://localhost/organizations/parents', {
            headers: {
                'Content-Type':'text/json'
            }
        }).then(
            function(res) { // success
                $scope.listOrganizations = res.data;
                $scope.getDirectors();
            },
            function(res) { // error
                console.log("Error: " + res.status + " : " + res.data);
            });

        // Запрос сотрудников на роль диркктора
        $scope.getDirectors = function() {
            config.params.idOrganization = $rootScope.employeeForm.idOrganization;

            $http.get('http://localhost/employees/directors', config)
                .then(
                    function (res) { // success
                        $scope.directors = res.data;
                    },
                    function (res) { // error
                        console.log("Error: " + res.status + " : " + res.data);
                    });
        };

        // Функция вызывается при нажатии на кнопку добавить/изменить
        $scope.upsertEmployee = function() {
            $http({
                method: method,
                url: 'http://localhost/employees/list',
                data: angular.toJson($rootScope.employeeForm),
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(
                function(res) { // success
                    $rootScope.$emit("refreshListEmp", {});
                },
                function(res) { // error
                    console.log("Error: " + res.status + " : " + res.data);
                });
        };
    }]);
}