module.exports = function(ngModule) {
    ngModule.controller('UpsertEmpCtrl', ['$window','$scope', '$rootScope', '$http', '$location', '$routeParams', 'empService',
        function($window, $scope, $rootScope, $http, $location, $routeParams, empService) {
        let method = "";
        let config = { params: {}};

        if ($routeParams.action === "create") {
            $rootScope.title = "Добавление сотрудника";
            $rootScope.btnText = "Добавить";
            method = "POST";
        }
        else if ($routeParams.action === "update"){
            $rootScope.title = "Изменение сотрудника";
            $rootScope.btnText = "Изменить";
            method = "PUT";
            config.params.id = $rootScope.employeeForm.id;
        }
        else $window.location.href = '/';

        $scope.directors = [{id: null, name: "" }];
        $scope.listOrganizations = [];

        // Запрос списка организаций
        empService.getOrgList()
            .then(function(res) {
                $scope.listOrganizations = res.data;
                $scope.getDirectors();
            });

        // Запрос сотрудников на роль диркктора
        $scope.getDirectors = function() {
            config.params.idOrganization = $rootScope.employeeForm.idOrganization;

            empService.getDirectors(config)
                .then(function (res) {
                        $scope.directors = res.data;
                    });
        };

        // Функция вызывается при нажатии на кнопку добавить/изменить
        $scope.upsertEmployee = function() {
            empService.upsert(method, $scope.employeeForm)
                .then(function(res) {
                    $rootScope.$emit("refreshListEmp", {});
                });
        };
    }]);
}