module.exports = function(ngModule) {
    ngModule.service('empService', ['$http', function ($http) {

        this.upsert = function (method, obj) {
            return $http({
                    url: '/employees/list',
                    method: method,
                    data: angular.toJson(obj),
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }
            );
        }

        this.delete = function (id) {
            return $http.delete('/employees/' + id);
        }

        this.getOrgList = function () {
            return $http.get('/organizations/parents');
        }

        this.getDirectors = function (config) {
            return $http.get('/employees/directors', config)
        }

        this.getList = function(limit, offset, search) {
            return $http.get('/employees/list', {
                params: {
                    limit: limit,
                    offset: offset,
                    search: search
                }
            });
        }

        this.getCount = function (search) {
            return $http.get('/employees/list/count', {
                params: {
                    search: search
                }
            });
        }

    }]);
}