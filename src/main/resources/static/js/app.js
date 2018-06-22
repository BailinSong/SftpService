var sftpuser = angular.module('SftpUser', ['ngRoute','ui.materialize','pageslide-directive','chart.js'])

sftpuser.config(function ($routeProvider, $httpProvider) {
    $routeProvider
        .when('/', {
            templateUrl: 'views/home.html',
            controller: 'home'
        }).when('/shutdown', {
        templateUrl: 'views/shutdown.html',
        controller: 'shutdown'
    })
    $httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
});