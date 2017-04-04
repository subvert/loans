(function() {
    'use strict';
    angular
        .module('loansApp')
        .factory('Loan', Loan);

    Loan.$inject = ['$resource', 'DateUtils'];

    function Loan ($resource, DateUtils) {
        var resourceUrl =  'api/loans/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.loanDate = DateUtils.convertLocalDateFromServer(data.loanDate);
                    }
                    return data;
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
