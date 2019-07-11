module.exports = function(ngModule, template) {
    ngModule.directive('searchDirective', [function(){
        return {
            template: template,
            link:  function (scope, element, attrs) {
                scope.placeholder = attrs.placeholder;
            }
        }
    }]);
}