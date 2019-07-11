module.exports = function(ngModule) {
    ngModule.controller('UpsertOrgCtrl', ['$window','$scope', '$rootScope', '$http', '$location', '$routeParams', 'orgService',
        function($window, $scope, $rootScope, $http, $location, $routeParams, orgService) {

        let method = "";
        let config = {};

        if ($routeParams.action === "create") {
            $rootScope.title = "Создание организации";
            $rootScope.btnText = "Создать";
            method = "POST";
        }
        else if ($routeParams.action === "update") {
            $rootScope.title = "Изменение организации";
            $rootScope.btnText = "Изменить";
            method = "PUT";
            config = {
                params: {
                    id: $rootScope.organizationForm.id
                }
            }
        }
        else $window.location.href = '/';

        $scope.parentOrganizations = [];

        orgService.getParentList(config)
            .then(function(res) {
                $scope.parentOrganizations = res.data;
            });

        // Функция вызывается при нажатии на кнопку "создать/изменить"
        $scope.upsertOrganization = function() {
            orgService.upsert(method, $rootScope.organizationForm)
                .then(function(res) {
                    $rootScope.$emit("refreshListOrg", {});
                });
        };
    }]);
}