var rssService = angular.module('RssService', ['ngMaterial'])
rssService.factory('RssDataOp', function ($http, $q, $mdDialog) {
    var service = {};
    var deffered = $q.defer();
	var rssList = '/rssList';
	var rss = '/rss';
	
	var rssListData;
	console.log('I am BigService1');
		
	service.loadRssList = function(){
		$http.get(rssList)
			.success(function (d) {
				rssListData = d;
				console.log(d);
				deffered.resolve();
			});
		return deffered.promise;
	};
		  
	service.data = function() { return rssListData; };

	service.update = function(rssConfig){
		$http.put(rss, rssConfig).then(function(response) {
		}, function(response) {
			$mdDialog.show(
				$mdDialog.alert()
				    .clickOutsideToClose(true)
				    .title('Update Failure: '.concat(response.status))
				    .textContent('rss update for ['.concat(rssConfig.url, '] failed'))
				.ariaLabel('response.statusText')
				.ok('OK')
				.openFrom('#left')
				.closeTo(angular.element(document.querySelector('#right')))
			);
		});
	
	};
	
	service.save = function(rssConfig){
		$http.post(rss, rssConfig).then(function(response) {
		}, function(response) {
			$mdDialog.show(
				$mdDialog.alert()
				    .clickOutsideToClose(true)
				    .title('RssConfig Save Failed: '.concat(response.status))
				    .textContent('rss save for ['.concat(rssConfig.url, '] failed'))
				.ariaLabel('response.statusText')
				.ok('OK')
				.openFrom('#left')
				.closeTo(angular.element(document.querySelector('#right')))
			);
		});
	
	};
	
	service.remove = function(rssConfList){
		angular.forEach(rssConfList, function(config) {
			$http.delete(rss+"/"+config.url).then(function(response) {
			}, function(response) {
				$mdDialog.show(
						$mdDialog.alert()
						.clickOutsideToClose(true)
						.title('RssConfig DELETE Failed: '.concat(response.status))
						.textContent('rss delete for ['.concat(config.url, '] failed'))
						.ariaLabel('response.statusText')
						.ok('OK')
						.openFrom('#left')
						.closeTo(angular.element(document.querySelector('#right')))
				);
			});
		});
	}

	return service;
});