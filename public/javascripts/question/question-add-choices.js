function QuestionAddChoiceCtrl($scope) {
	$scope.choices = [];
	
	$scope.addChoice = function () {
		if ($scope.choiceText != null && $scope.choiceText != "") {
			$scope.choices.push({label: $scope.choiceText, sortOrder: $scope.count()});
			$scope.choiceText = '';
		}
	};
	
	$scope.removeChoice = function(choice) {
		var index = $scope.choices.indexOf(choice);
		$scope.choices.splice(index, 1);
	};
	
	$scope.count = function() {
		var count = 0;
		angular.forEach($scope.choices, function(choice) {
			count += 1;
		});
		return count;
	};
}
