module.exports = function(ngModule, template) {
    ngModule.directive('pagingDirective', [function(){
        return {
            template: template,
            link:  function (scope, element, attrs) {
                scope.btnText = attrs.btnText;
                scope.url = attrs.url;
                scope.obj = attrs.obj;
            }
        }
    }]);
}