var sftpuser = angular.module('SftpUser');

sftpuser.controller('home', function ($scope, $http) {
    $scope.users = [];


    $scope.addUser = function () {
        console.info($scope.userForm)
        if(!$scope.userForm.$invalid) {
            $http.post('/users', $scope.user).then(function success(response) {
                    $scope.users.push(response.data);
                    $scope.user = {};
                    Materialize.toast('用户创建成功!', 4000)
                    // $scope.userForm.$setPristine();
                    // $scope.$apply();
                },
                function error(response) {
                    console.error(response);
                    Materialize.toast('用户创建失败!', 4000)
                });
        }
    }

    $http.get("/users").then(function success(response) {
        angular.forEach(response.data, function (p, key) {
            $scope.users.push(p);
        }, function error(response) {
            console.error(response);
            Materialize.toast('拉取用户列表失败!', 4000)
        })
    });

    $scope.deleteUser = function (userName) {
        $http.delete('/users/' + userName).then(function success(response) {
            $scope.users.forEach(function (p) {
                if (p.userName == userName){
                    p.delete = true;
                    Materialize.toast('用户删除成功!', 4000)
                }

            });
        }, function error(response) {
            console.error(response);
            Materialize.toast('用户删除失败!', 4000)
        });
    }

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