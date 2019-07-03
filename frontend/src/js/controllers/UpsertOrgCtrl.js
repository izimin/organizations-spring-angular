module.exports = function(ngModule) {
    ngModule.controller('UpsertOrgCtrl', ['$scope', '$rootScope','$http', '$location', function($scope, $rootScope, $http, $location) {
        let method = "";
        let config = {};

        if ($location.path() === "/organization/create") {
            $rootScope.title = "Создание организации";
            $rootScope.btnText = "Создать";
            method = "POST";
        }
        else {
            $rootScope.title = "Изменение организации";
            $rootScope.btnText = "Изменить";
            method = "PUT";
            config = {
                params: {
                    id: $rootScope.organizationForm.id
                }
            }
        }

        $scope.parentOrganizations = [];

        $http.get('http://localhost/organizations/parents', config)
            .then(
                function(res) {
                    $scope.parentOrganizations = res.data;
                },
                function(res) {
                    console.log("Error: " + res.status + " : " + res.data);
                });

        // Фцнкция вызывается при нажатии на кнопку "создать/изменить"
        $scope.upsertOrganization = function() {
            $http({
                    url: 'http://localhost/organizations/list',
                    method: method,
                    data: angular.toJson($scope.organizationForm),
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }
            ).then(
                function(res) { // success
                    $rootScope.$emit("refreshListOrg", {});
                },
                function(res) { // error
                    console.log("Error: " + res.status + " : " + res.data);
                });
        };
    }]);
}