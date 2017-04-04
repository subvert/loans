(function() {
    'use strict';

    angular
        .module('loansApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('loan-jumo-app', {
            parent: 'entity',
            url: '/loan-jumo-app',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'loansApp.loan.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/loan/loansjumoApp.html',
                    controller: 'LoanJumoAppController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('loan');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('loan-jumo-app-detail', {
            parent: 'loan-jumo-app',
            url: '/loan-jumo-app/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'loansApp.loan.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/loan/loan-jumo-app-detail.html',
                    controller: 'LoanJumoAppDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('loan');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Loan', function($stateParams, Loan) {
                    return Loan.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'loan-jumo-app',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('loan-jumo-app.upload', {
            parent: 'loan-jumo-app',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/loan/loan-jumo-app-dialog.html',
                    controller: 'LoanJumoAppDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                msisdn: null,
                                network: null,
                                loanDate: null,
                                product: null,
                                amount: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('loan-jumo-app', null, { reload: 'loan-jumo-app' });
                }, function() {
                    $state.go('loan-jumo-app');
                });
            }]
        });
    }

})();
