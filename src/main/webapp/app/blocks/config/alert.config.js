(function() {
    'use strict';

    angular
        .module('loansApp')
        .config(alertServiceConfig);

    alertServiceConfig.$inject = ['AlertServiceProvider'];

    function alertServiceConfig(AlertServiceProvider) {
        AlertServiceProvider.showAsToast(false);
    }
})();
