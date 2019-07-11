module.exports = function(ngModule){
    ngModule.controller('OrgListCtrl', ['$scope', '$rootScope', '$http', '$location', '$window', 'orgService', 'commonService',
        function($scope, $rootScope, $http, $location, $window, orgService, commonService) {

        // Название вкладки
        $rootScope.title = "Список организаций";

        // Строка поиска
        $scope.searchStr = "";

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
            orgService.checkExistsChildren(organization.id)
                .then(function (res) {
                    if (res.data) {
                        alert("Удаление не было произведено. \nУ организации есть дочерние.");
                        return;
                    }
                    orgService.delete(organization.id)
                        .then(function (res) {
                            $scope.curPage = 1;
                            if (res.data === 0) {
                                let deleteOrg = $window.confirm('В данной организации работают сотрудники, вы точно хотите ее удалить?');
                                if (deleteOrg) {
                                    orgService.deleteOrgWithEmp(organization.id)
                                        .then(_refreshListOrg);
                                }
                            }
                            else
                                _refreshListOrg();
                        });
                });
        };

        // Функция изменения организации
        $scope.editOrganization = function(organization) {
            $scope.organizationForm.id = organization.id;
            $scope.organizationForm.name = organization.name;
            $scope.organizationForm.idParent = organization.idParent;
        };

        // Обновление списка организаций
        function _refreshListOrg() {
            orgService.getList($scope.limit, ($scope.curPage-1)*$scope.limit, $scope.searchStr)
                .then(function (res) {
                   $scope.organizations = res.data;
                    _refreshListPages();
                });
        }

        function _refreshListPages() {
            orgService.getCount($scope.searchStr)
                .then(function(res) {
                        $scope.countPages = Math.ceil(Number(res.data) / $scope.limit);
                        $scope.pages = commonService.getListPages($scope.countPages, $scope.curPage);
                    }
                );
        }
    }]);
};