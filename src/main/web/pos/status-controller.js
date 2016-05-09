module.exports = function($scope, $http) {
			$http.get('/pos/status').success(function(result) {
				$scope.status = result;
			});
		};