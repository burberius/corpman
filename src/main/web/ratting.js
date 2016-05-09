 module.exports = function($scope, $http) {
	$scope.update = function() {
		$http.get('/ratting/revenue').success(function(result) {
			$scope.revenue = result;
		});
		$http.get('/ratting/monthlymemberrevenue').success(function(result) {
			$scope.monthlymemberrevenue = result;
		});
		$http.get('/ratting/weeklymemberrevenue').success(function(result) {
			$scope.weeklymemberrevenue = result;
		});
		$http.get('/ratting/rats').success(function(result) {
			$scope.rats = result;
		});
		$http.get('/ratting/ratgroups').success(function(result) {
			$scope.ratgroups = result;
		});
		$http.get('/ratting/fancyrats').success(function(result) {
			$scope.fancyrats = result;
		});
		$http.get('/ratting/systems').success(function(result) {
			$scope.systems = result;
		});
		$http.get('/ratting/highestticks').success(function(result) {
			$scope.highesticks = result;
		});
		$http.get('/ratting/perday').success(function(result) {
			$scope.perdaydata = [];
			var data = [];
			for (var entry in result) {
				data.push(Math.round(result[entry].value));
			}
			$scope.perdaydata.push(data);
		});
		$http.get('/ratting/perhour').success(function(result) {
			$scope.perhourdata = [];
			$scope.perhourlabels = [];
			var data = [];
			for (var entry in result) {
				data.push(Math.round(result[entry].value));
				$scope.perhourlabels.push(result[entry].hour);
			}
			$scope.perhourdata.push(data);
		});
	};
	
	$scope.perdaylabels = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
	
	$scope.options = {
			scaleLineColor: 'rgba(191,205,228,0.6)',
			scaleFontColor: 'white',
			scaleGridLineColor: 'rgba(148,159,177,0.4)',
			scaleLabel: function (valuePayload) {
			    return valuePayload.value.toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, "$1,");
			},
			tooltipTemplate: function (label) {
			    return label.value.toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, "$1,") + " ISK";
			}
	};
	
	// init
	$scope.update();
};
