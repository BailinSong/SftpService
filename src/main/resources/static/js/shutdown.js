var sftpuser = angular.module('SftpUser');
sftpuser.controller('shutdown', function ($scope, $http) {

    $scope.shutdown=function(){
        $http.post('/shutdown').then(function success(response) {
                Materialize.toast('系统关闭成功!', 4000)
            },
            function error(response) {
                console.error(response);
                Materialize.toast('系统关闭失败!', 4000)
            });
    }

});