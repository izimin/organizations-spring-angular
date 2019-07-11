module.exports = function(ngModule, template) {
    ngModule.directive('treeItem', function () {
        return {
            template: template
        }
    });
}