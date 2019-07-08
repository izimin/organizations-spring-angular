module.exports = function(ngModule){
    ngModule.controller('OrgListCtrl', ['$scope', '$rootScope', '$http', '$location', '$window',  function($scope, $rootScope, $http, $location, $window) {

        // Название вкладки
        $rootScope.title = "Список организаций";

        // Строка поиска
        $scope.search = "";

        // Пэйджинг
        $scope.limit = 5;
        $scope.countPages = 0;
        $scope.curPage = 1;
        $scope.pages = [];

        // Список  организаций
        $scope.organizations = [];

        // Форма для создания/изменения орнанизации
        $rootScope.organizationForm = {
            id: -1,
            name: "",
            idParent: null
        };

        _refreshListOrg();

        // Функция вызывется из контроллеров создания/изменения
        $rootScope.$on("refreshListOrg", function(){
            _refreshListOrg();
        });

        // Функция вызывается при изменении limit/search
        $scope.refresh = function() {
            $scope.curPage = 1;
            _refreshListOrg();
        };

        // Функция вызывается при изменении номера страницы
        $scope.changeNumPage = function(elem) {
            if ($scope.curPage === elem)
                return;
            $scope.curPage = elem;
            _refreshListOrg();
        };

        // Функция удаления организации
        $scope.deleteOrganization = function(organization) {
            $http.get('http://localhost/organizations/checkExistsChildren/' + organization.id)
                .then(
                    function (res) {
                        if (res.data) {
                            alert("Удаление не было произведено. \nУ организации есть дочерние.");
                            return;
                        }
                        $http.delete('http://localhost/organizations/' + organization.id)
                            .then(function (res) {
                                    $scope.curPage = 1;
                                    if (res.data === 0) {
                                        let deleteOrg = $window.confirm('В данной организации работают сотрудники, вы точно хотите ее удалить?');
                                        if (deleteOrg) {
                                            $http.put('http://localhost/employees/list/deleteOrganization', organization.id)
                                                .then(_refreshListOrg, function (res) {
                                                        console.log("Error: " + res.status + " : " + res.data);
                                                    }
                                                );
                                        }                                }
                                    else
                                        _refreshListOrg();
                                },
                                function (res) {

                                });
                    },
                    function (res) {

                    }
                );
        };

        // Функция изменения организации
        $scope.editOrganization = function(organization) {
            $scope.organizationForm.id = organization.id;
            $scope.organizationForm.name = organization.name;
            $scope.organizationForm.idParent = organization.idParent;
        };

        // Обновление списка организаций
        function _refreshListOrg() {
            $http.get('http://localhost/organizations/list', {
                params: {
                    limit: $scope.limit,
                    offset: ($scope.curPage-1)*$scope.limit,
                    search: $scope.search
                }
            }).then(
                function(res) { // success
                    $scope.organizations = res.data;
                    _refreshListPages();
                },
                function(res) { // error
                    console.log("Error: " + res.status + " : " + res.data);
                }
            );
        }

        function _refreshListPages() {
            $http.get('http://localhost/organizations/list/count', {
                params: {
                    search: $scope.search
                }
            }).then(
                function(res) { // success
                    $scope.countPages = Math.ceil(Number(res.data) / $scope.limit);
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
                    alert("Error: " + res.status + " : " + res.data);
                }
            );
        }
    }]);
};