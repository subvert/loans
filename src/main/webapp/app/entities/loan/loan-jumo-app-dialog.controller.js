(function () {
    'use strict';

    angular
            .module('loansApp')
            .controller('LoanJumoAppDialogController', LoanJumoAppDialogController);

    LoanJumoAppDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Loan', 'Upload', 'Aggregate'];

    function LoanJumoAppDialogController($timeout, $scope, $stateParams, $uibModalInstance, entity, Loan, Upload, Aggregate) {
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
                }, function (evt) {
                    file.progress = Math.min(100, parseInt(100.0 *
                            evt.loaded / evt.total));
                });
            }
        }

        function downloadResultFile() {
            Aggregate.download(onSuccess, onError);
            function onSuccess(data, headers) {
            }

            function onError(error) {
                AlertService.error(error.data.message);
            }
        }
    }
})();
