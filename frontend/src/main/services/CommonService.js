module.exports = function(ngModule) {
    ngModule.service('commonService', ['$http', function ($http) {

        this.getListPages = function (countPages, curPage) {
            let begin = curPage === countPages ? (curPage > 5 ? curPage - 4 : 1) :
                curPage === countPages - 1 ? (curPage > 4 ? curPage - 3 : 1) :
                    curPage > 2 ? curPage - 2 : 1;

            let end = curPage === 1 ? (countPages > 5 ? 5 : countPages) :
                curPage === 2 ? (countPages > 6 ? 5 : countPages) :
                    curPage + 2 <= countPages ? curPage + 2 : countPages;

            let pages = [];

            for (let i = begin; i <= end; i++) {
                pages.push(i);
            }
            return pages;
        }

        this.getTree = function (url, limit, offset) {
            return $http.get(url, {
                params: {
                    limit: limit,
                    offset: offset
                }
            });
        }
    }]);
}
