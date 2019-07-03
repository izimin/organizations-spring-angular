module.exports = function(ngModule){
    ngModule.controller('HomeCtrl', ['$scope', '$rootScope','$http', '$location', '$timeout', function($scope, $rootScope, $http, $location, $timeout) {
        $rootScope.title = "Главная";
        $scope.curSlide = 0;
        $scope.slides = [
            { 'image': './dist/img/image1.jpg' },
            { 'image': './dist/img/image2.jpg' },
            { 'image': './dist/img/image3.jpg' },
            { 'image': './dist/img/image4.jpg' },
            { 'image': './dist/img/image5.jpg' }
        ];

        let countImages = $scope.slides.length;

        start();

        function start() {
            $timeout(function () {
                $scope.curSlide = ($scope.curSlide +1) % countImages;
                start();
            }, 5000);
        }
    }]);
};