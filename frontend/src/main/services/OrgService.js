module.exports = function(ngModule) {
    ngModule.service('orgService', ['$http', function ($http) {

        this.upsert = function (method, obj) {
            return $http({
                    url: '/organizations/list',
                    method: method,
                    data: angular.toJson(obj),
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }
            );
        }

        this.delete = function (id) {
            return $http.delete('/organizations/' + id);
        }

        this.deleteOrgWithEmp = function(id) {
            return $http.put('/employees/list/deleteOrganization', id)
        }

        this.getParentList = function (config) {
            return $http.get('/organizations/parents', config)
        }

        this.getList = function(limit, offset, search) {
            return $http.get('/organizations/list', {
                params: {
                    limit: limit,
                    offset: offset,
                    search: search
                }
            });
        }

        this.getCount = function (search) {
            return $http.get('/organizations/list/count', {
                params: {
                    search: search
                }
            });
        }

        this.checkExistsChildren = function (id) {
            return $http.get('/organizations/checkExistsChildren/' + id);
        }

    }]);
}