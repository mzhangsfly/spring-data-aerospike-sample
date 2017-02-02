angular.module("mapApp", ['ngMap']).controller('mapController', function($scope, $http, NgMap) {
//	var vm = this;
	$scope.umap = {
	    "center": {
	        "lat": 37.4212221,
	        "lng": -122.0983744
	    },
	    "zoom": 15
	};
	
	$scope.searchParam = {
		address : "2525 E Charleston Road, Mountain View, CA 94043",
		distance : 3.50,
		hours : 4
	};
	
	$http.get("https://maps.googleapis.com/maps/api/geocode/json?address=" + encodeURI($scope.searchParam.address))
		.success(function (d) {
			$scope.umap.center = d.results[0].geometry.location;
		});
	
	$scope.propertyData=[];
	
	$scope.submit = function() {
		$http.get("https://maps.googleapis.com/maps/api/geocode/json?address=" + encodeURI($scope.searchParam.address))
		.success(function (d) {
			$scope.umap.center = d.results[0].geometry.location;
		
			$http.get("rental?lat="+ $scope.umap.center.lat + "&lng=" + $scope.umap.center.lng + "&radius=" + ($scope.searchParam.distance * 1609) +"&hoursAgo=" + $scope.searchParam.hours)
				.success(function(data){
					$scope.mCluster.clearMarkers();
					$scope.bounds = new google.maps.LatLngBounds();
	
					$scope.bounds.extend($scope.umap.center);
					$scope.mCluster.addMarker(new google.maps.Marker($scope.createMarkerForProperty({id:1, name:"Your Location",pos: $scope.umap.center})));
					data.map(function (property) {
						var geo = JSON.parse(property.geoLocation.object);
						console.log(geo.coordinates);
			        	$scope.bounds.extend(new google.maps.LatLng(geo.coordinates[1], geo.coordinates[0]));
			        	var imageUrl = "http://s3-us-west-1.amazonaws.com/aerospike-fd/wp-content/uploads/2015/05/YourNameHere.png";
			        	if(property.images.length>0) imageUrl = property.images[0];
			        	$scope.mCluster.addMarker($scope.createMarkerForProperty({
			        		id:property.id, 
			        		name:"$"+property.price + " - " + property.bedroom + "/" + property.bath, 
			        		image: imageUrl,
			        		url:property.url,
			        		description: "<img src='image/YourNameHere.png'/>", 
			        		pos:[geo.coordinates[1], geo.coordinates[0]]}));
			        });
					$scope.map.fitBounds($scope.bounds);
				});
		});

	};
	
	$scope.bounds = new google.maps.LatLngBounds();

    NgMap.getMap().then(function (map) {
        $scope.map = map;
        $scope.mCluster = initMarkerClusterer();
        map.fitBounds($scope.bounds);
    });

    $scope.cities = [
        { id: 1, name: 'you', description: 'Your Location', pos: [37.4212221, -122.0983744] }
    ];
    


   initMarkerClusterer = function () {
        var markers = $scope.cities.map(function (city) {
        	$scope.bounds.extend(new google.maps.LatLng(city.pos[0], city.pos[1]));
            return $scope.createMarkerForProperty(city);
        });
        var mcOptions = { imagePath: 'https://cdn.rawgit.com/googlemaps/js-marker-clusterer/gh-pages/images/m' };
        return new MarkerClusterer($scope.map, markers, mcOptions);
    };


    $scope.createMarkerForProperty = function (property) {
        var marker = new google.maps.Marker({
            position: new google.maps.LatLng(property.pos[0], property.pos[1]),
            title: property.name
        });
        google.maps.event.addListener(marker, 'click', function () {
            $scope.selectedProperty = property;
            $scope.map.showInfoWindow('myInfoWindow', this);
        });
        return marker;
    }});