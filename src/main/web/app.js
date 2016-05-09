var angular = require('angular');

require('angular-ui-bootstrap');
require('angular-route');
require('angular-chart.js');

var app = angular.module('corpman', [ 'ui.bootstrap', 'ngRoute', 'chart.js' ]).config(
		function($routeProvider) {
			$routeProvider.when("/", {
				templateUrl : "dashboard.html",
				name : "Dashboard"
			}).when("/pos", {
				templateUrl : "pos.html",
				name : "POS Manager"
			}).when("/ratting", {
				templateUrl : "ratting.html",
				name : "Ratting Statistics"
			}).otherwise({
				redirectTo : "/"
			});
		});
// POS
app.controller('posController', [ "$scope", "$http", require('./pos/controller')]);
app.controller('statusController', [ "$scope", "$http", require('./pos/status-controller')]);
app.directive('posmodule', require('./pos/directive'));
// Ratting
app.controller('rattingController', [ "$scope", "$http", require('./ratting')]);
// Eveicon
app.directive('eveicon', ["$sce", require('./eveicon')]);

app.directive('navigation', function (routeNavigation) {
	  return {
	    restrict: "E",
	    replace: true,
	    templateUrl: "navigation-directive.tpl.html",
	    controller: function ($scope) {
	      $scope.routes = routeNavigation.routes;
	      $scope.activeRoute = routeNavigation.activeRoute;
	    }
	  };
	});

app.factory('routeNavigation', function($route, $location) {
	  var routes = [];
	  angular.forEach($route.routes, function (route, path) {
	    if (route.name) {
	      routes.push({
	        path: path,
	        name: route.name
	      });
	    }
	  });
	  return {
	    routes: routes,
	    activeRoute: function (route) {
	      return route.path === $location.path();
	    }
	  };
	});

app.filter('dayHour', function() {
	return function(value) {
		var days = Math.floor(value / 24);
		var hours = value - (days * 24);
		if (days === 0) {
			return hours + 'h';
		}
		return days + 'd ' + hours + 'h';
	};
});
