(function () {
    'use strict';
    angular
            .module('loansApp')
            .factory('Loan', Loan)
            .factory('Aggregate', Aggregate);

    Loan.$inject = ['$resource', 'DateUtils'];
    Aggregate.$inject = ['$q', '$timeout', '$window'];

    function Loan($resource, DateUtils) {
        var resourceUrl = 'api/loans/:id';

        return $resource(resourceUrl, {}, {
            'query': {method: 'GET', isArray: true},
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

    function Aggregate($q, $timeout, $window) {
        var resourceUrl = '/loans/aggregate';

        return {
            download: function () {

                var defer = $q.defer();

                $timeout(function () {
                    $window.open(resourceUrl, '_blank');
                }, 1000)
                        .then(function () {
                            defer.resolve('success');
                        }, function () {
                            defer.reject('error');
                        });
                return defer.promise;
            }
        };
    }
})();
