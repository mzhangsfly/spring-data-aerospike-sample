var app = angular.module("myapp", []);

	app.controller("ListController", ['$scope', '$http', function($scope, $http) {
	    $http.get('/hello').success(function(data) {
	        $scope.greeting = data;
	    });
	    
		$scope.personalDetails = [
	        {
	            'fname':'Muhammed',
	            'lname':'Shanid',
	            'email':'shanid@shanid.com',
	            'enabled':true
	        },
	        {
	            'fname':'John',
	            'lname':'Abraham',
	            'email':'john@john.com',
	            'enabled':true
	        },
	        {
	            'fname':'Roy',
	            'lname':'Mathew',
	            'email':'roy@roy.com',
	            'enabled':true
	        }];
	    
	        $scope.addNew = function(){
	            $scope.personalDetails.push({ 
	                'fname': $scope.fname, 
	                'lname': $scope.lname,
	                'email': $scope.email,
	            });
	            $scope.PD = {};
	        };
	    
	        $scope.remove = function(){
	            var newDataList=[];
	            $scope.selectedAll = false;
	            angular.forEach($scope.personalDetails, function(selected){
	                if(!selected.selected){
	                    newDataList.push(selected);
	                }
	            }); 
	            $scope.personalDetails = newDataList;
	        };
	    
	        $scope.checkAll = function () {
	            if (!$scope.selectedAll) {
	                $scope.selectedAll = true;
	            } else {
	                $scope.selectedAll = false;
	            }
	            angular.forEach($scope.personalDetails, function (personalDetails) {
	                personalDetails.selected = $scope.selectedAll;
	            });
	        };    
	}]);