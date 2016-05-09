module.exports = function() {
	return {
		restrict : 'A',
		replace : false,
		scope : {
			module : '=module'
		},
		templateUrl : '/module.html',
		link : function(scope, element, attrs) {
			var date = new Date();
			date.setHours(date.getHours() + scope.module.timeLeft);
			scope.end = date;
			if (scope.module.timeLeft < 48) {
				scope.type = 'danger';
			} else if (scope.module.timeLeft < 120) {
				scope.type = 'warning';
			} else {
				scope.type = 'success';
			}
			if (scope.module.type.indexOf("Reactor") > -1) {
				scope.reactor = true;
			} else {
				scope.reactor = false;
			}
			scope.time = dayHour(scope.module.timeLeft);
		}
	};
};