module.exports = function(ngModule) {
    ngModule.controller('TreeViewCtrl', ['$scope', '$rootScope', '$http', '$location', function($scope, $rootScope, $http, $location) {
         let url = '';
         let isExpand = false;
         if ($location.path() === "/employees/tree") {
             $rootScope.title = "Дерево сотрудников";
             url = 'http://localhost/employees/tree';
         }
         else {
             $rootScope.title = "Дерево организаций";
             url = 'http://localhost/organizations/tree';
         }

         $scope.tree = [];

         // Пэйджинг
         $scope.limit = 5;
         $scope.curPage = 1;
         $scope.isShow = {};
         $scope.btnText = "Развернуть дерево";
         let limitPrev = 5;

         // Запрос изначального дерева с дефолтным количеством потомков
         $http.get(url, {
             params: {
                 limit: $scope.limit,
                 offset: 0
             }
         }).then(
             function(res) { // success
                 $scope.tree = res.data;
                 _setIsShow($scope.tree, isExpand);
             },
             function(res) { // error
                 console.log("Error: " + res.status + " : " + res.data);
             }
         );

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

         function _cutChildren(child) {
             child.length = $scope.limit;
             for (let ch of child){
                 if (ch.children.length > $scope.limit)
                     _cutChildren(ch.children);
             }
         }


         function _addChildren() {
             let lp = limitPrev;
             $http.get(url, {
                 params: {
                     limit: $scope.limit-lp,
                     offset: lp
                 }
             }).then(
                 function(res) { // success
                     for (let i = 0; i < $scope.tree.length; i++) {
                         if ($scope.tree[i].children.length === lp) {
                             _addCh($scope.tree[i], res.data[i]);
                             _setIsShow(res.data[i], isExpand);
                         }
                     }
                 },
                 function(res) { // error
                     console.log("Error: " + res.status + " : " + res.data);
                 }
             );
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