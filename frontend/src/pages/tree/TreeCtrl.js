module.exports = function(ngModule) {
    ngModule.controller('TreeViewCtrl', ['$scope', '$rootScope', '$http', '$location', '$window', '$routeParams', 'commonService',
        function($scope, $rootScope, $http, $location, $window, $routeParams, commonService) {
         let url = '';
         let isExpand = false;
         if ($routeParams.objects === "employees") {
             $rootScope.title = "Дерево сотрудников";
             url = '/employees/tree';
         }
         else if ($routeParams.objects === "organizations")  {
             $rootScope.title = "Дерево организаций";
             url = '/organizations/tree';
         }
         else $window.location.href = '/';

         $scope.tree = [];

         // Пэйджинг
         $scope.limit = 5;
         $scope.curPage = 1;
         $scope.isShow = {};
         $scope.btnText = "Развернуть дерево";
         let limitPrev = 5;

         // Запрос изначального дерева с дефолтным количеством потомков
         commonService.getTree(url, $scope.limit, 0)
         .then(function(res) {
                 $scope.tree = res.data;
                 _setIsShow($scope.tree, isExpand);
         });

         $scope.refresh = function () {
             if ($scope.limit !== null) {
                 if ($scope.limit < limitPrev)
                     for (let root of $scope.tree){
                         if (root.children.length > $scope.limit)
                             _cutChildren(root.children);
                     }
                 else {
                     _addChildren();
                 }
                 limitPrev = $scope.limit;
             }
         };

         // Обрезаем дочерние элементы, если текущий лимит ниже предыдущего
         function _cutChildren(child) {
             child.length = $scope.limit;
             for (let ch of child){
                 if (ch.children.length > $scope.limit)
                     _cutChildren(ch.children);
             }
         }

         // Подгрузка дочерних элементов 
         function _addChildren() {
             let lp = limitPrev;
             commonService.getTree(url, $scope.limit-lp, lp)
             .then(function(res) { 
                     for (let i = 0; i < $scope.tree.length; i++) {
                         if ($scope.tree[i].children.length === lp) {
                             _addCh($scope.tree[i], res.data[i]);
                             _setIsShow(res.data[i], isExpand);
                         }
                     }
             });
         }

         function _addCh(tree, data) {
             for (let item of data.children)
                 tree.children.push(item);
             for (let i = 0; i < tree.length; i++) {
                 if (tree[i].children.length === limitPrev) {
                     _addCh(tree[i].children, data[i].children);
                     _setIsShow(data[i].children, isExpand);
                 }
             }
         }

         function _setIsShow(data, value) {
             if (data != null) {
                 for (let i = 0; i < data.length; i++)
                     data[i].isShow = value;
                 if (data.children === null)
                     return;
                 _setIsShow(data.children, value)
             }
         }

         $scope.isShowChildren = function (data) {
             data.isShow = !data.isShow;
         }

         $scope.expandTree = () => {
             _setIsShow($scope.tree, !isExpand);
             isExpand = !isExpand;
             if (isExpand)
                 $scope.btnText = "Свернуть дерево";
             else $scope.btnText = "Развернуть дерево";
         }

     }]);
}