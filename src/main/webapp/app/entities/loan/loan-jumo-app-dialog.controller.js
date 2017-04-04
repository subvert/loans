(function () {
    'use strict';

    angular
            .module('loansApp')
            .controller('LoanJumoAppDialogController', LoanJumoAppDialogController);

    LoanJumoAppDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Loan', 'Upload'];

    function LoanJumoAppDialogController($timeout, $scope, $stateParams, $uibModalInstance, entity, Loan, Upload) {
        var vm = this;

        vm.loan = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.uploadFile = uploadFile;

        $timeout(function () {
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear() {
            $uibModalInstance.dismiss('cancel');
        }
        
        function uploadFile(file, errFiles) {
            $scope.f = file;
            $scope.errFile = errFiles && errFiles[0];
            if (file) {
                file.upload = Upload.upload({
                    url: 'api/loans',
                    data: {file: file}
                });

                file.upload.then(function (response) {
                    $timeout(function () {
                        file.result = response.data;
                    });
                }, function (response) {
                    if (response.status > 0)
                        $scope.errorMsg = response.status + ': ' + response.data;
                    loadAll();
                }, function (evt) {
                    file.progress = Math.min(100, parseInt(100.0 *
                            evt.loaded / evt.total));
                });
            }
        }

        function loadAll () {
            Loan.query({
                page: vm.page,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }

            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                for (var i = 0; i < data.length; i++) {
                    vm.loans.push(data[i]);
                }
            }

            function onError(error) {
                AlertService.error(error.data.message);
            }
        }
    }
})();
