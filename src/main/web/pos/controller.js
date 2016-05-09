'use strict';


module.exports = function($scope, $http) {
	$scope.update = function() {
		$http.get('/pos/all').success(function(result) {
			$scope.posList = result;
		});
		$http.get('/pos/modules').success(function(result) {
			$scope.moduleList = result;
		});
	};
	$scope.getFuelProgressType = function(value) {
		if (value < 48) {
			return 'danger';
		} else if (value < 120) {
			return 'warning';
		} else {
			return 'success';
		}
	};
	$scope.getEndDate = function(value) {
		var date = new Date();
		date.setHours(date.getHours() + value);
		return date;
	};
	$scope.getPosStateColor = function(value) {
		if (value == 'Online') {
			return 'green';
		}
		return 'red';
	};
	$scope.setPos = function(mod, pos) {
		for (var count = 0; count < $scope.moduleList.length; count++) {
			if ($scope.moduleList[count].itemID === mod) {
				$scope.moduleList[count].posItemID = pos;
				$scope.updateModule(mod, $scope.moduleList[count]);
			}
		}
	};
	$scope.getClass = function(index, modules) {
		var mod = modules[index];
		return "x" + mod.x + " y" + mod.y;
	};
	$scope.getModulesContainerStyle = function(modules) {
		var maxX = 0, maxY = 0, mod;
		for (var count = 0; count < modules.length; count++) {
			var mod = modules[count];
			if (mod.x > maxX) {
				maxX = mod.x;
			}
			if (mod.y > maxY) {
				maxY = mod.y;
			}
		}
		maxX = (maxX * 74 / 10) + 2;
		maxY = (maxY * 74 / 10) + 2;
		return {
			'width' : maxX + "px",
			'height' : maxY + "px"
		};
	};

	$scope.updateModule = function(modId, module) {
		console.log(angular.toJson(module, true));
		$http.put('/pos/modules/' + modId, module).success(function() {
			// update
			$scope.update();
		});
	}

	// init
	$scope.update();
};
