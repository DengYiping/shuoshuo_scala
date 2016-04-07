var app = angular.module('shuoshuo', []);

app.controller("search", function($scope, $http) {
  $scope.query = '';
  $scope.results = [];
});
