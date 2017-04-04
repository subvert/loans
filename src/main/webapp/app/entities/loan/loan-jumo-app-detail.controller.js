(function() {
    'use strict';

    angular
        .module('loansApp')
        .controller('LoanJumoAppDetailController', LoanJumoAppDetailController);

    LoanJumoAppDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Loan'];

    function LoanJumoAppDetailController($scope, $rootScope, $stateParams, previousState, entity, Loan) {
        var vm = this;

        vm.loan = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('loansApp:loanUpdate', function(event, result) {
            vm.loan = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
