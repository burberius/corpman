module.exports = function($sce) {
							return {
								restrict : 'E',
								replace : true,
								link : function(scope, element, attrs) {
									if (!attrs.type) {
										scope.type = 'type';
									} else {
										scope.type = attrs.type;
									}
									if (!attrs.size) {
										scope.size = 32;
									} else {
										scope.size = attrs.size;
									}
									scope.name = attrs.name;
									scope.url = $sce
											.trustAsUrl("https://image.eveonline.com/"
													+ scope.type
													+ "/"
													+ attrs.id
													+ "_"
													+ scope.size + ".png");
								},
								template : '<img ng-src="{{url}}" class="eveicon" alt="{{name}}" tooltip="{{name}}" tooltip-trigger="mouseenter"/>'
							};
						}