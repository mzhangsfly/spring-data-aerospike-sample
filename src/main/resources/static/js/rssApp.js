var app = angular.module("myapp", ['ngMaterial', 'RssService']);

app.controller("RssController", function($scope, $http, $mdDialog, RssDataOp) {
	RssDataOp.loadRssList().then(function() {
	    $scope.rssConfigs = RssDataOp.data();
	    angular.forEach($scope.rssConfigs, function(config) {
			if (config.expireDate) {
				config.expireDate = new Date(config.expireDate);
			}
		});
	  });
    
	$scope.addNew = function() {
		$scope.rssConfigs.push( $scope.rssConfig);
		RssDataOp.save($scope.rssConfig);
	};

	$scope.enableChange = function enableChange(rssConfig){
		RssDataOp.update(rssConfig);
	};

	$scope.dateChange = function (rssConfig){
		RssDataOp.update(rssConfig)
	}

	$scope.remove = function() {
		var newDataList = [];
		var delList = [];
		$scope.selectedAll = false;
		angular.forEach($scope.rssConfigs, function(selected) {
			if (!selected.selected) {
				newDataList.push(selected);
			}else{
				delList.push(selected)
			}
		});
		$scope.rssConfigs = newDataList;
		RssDataOp.remove(delList);
	};

	$scope.checkAll = function() {
		if (!$scope.selectedAll) {
			$scope.selectedAll = true;
		} else {
			$scope.selectedAll = false;
		}
		angular.forEach($scope.rssConfigs, function(rssConfigs) {
			rssConfigs.selected = $scope.selectedAll;
		});
	};    
});



