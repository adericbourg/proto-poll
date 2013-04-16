function QuestionAddChoiceCtrl($scope) {
	$scope.choices = [];

	$scope.addChoice = function() {
		if ($scope.choiceText != null && $scope.choiceText != "") {
			$scope.choices.push({label: $scope.choiceText, sortOrder: $scope.count()});
			$scope.choiceText = '';
		}
	};

	$scope.removeChoice = function(choice) {
		var index = indexOf(choice);
		if (index != -1) {
			$scope.choices.splice(index, 1);
		}
	};

	$scope.moveUp = function(choice) {
		var index = indexOf(choice);
		if (index != -1 && index > 0) {
			$scope.removeChoice(choice);
			$scope.choices.splice(index-1, 0, choice);
		}
	};

	$scope.moveDown = function(choice) {
		var index = indexOf(choice);
		if (index != -1 && index < $scope.choices.length-1) {
			$scope.removeChoice(choice);
			$scope.choices.splice(index+1, 0, choice);
		}
	};

	var indexOf = function(choice) {
		for (var i = 0, len = $scope.choices.length; i < len; i++) {
			if ($scope.choices[i].label == choice.label) {
				return i;
			}
		}
		return -1;
	}

	$scope.count = function() {
		var count = 0;
		angular.forEach($scope.choices, function(choice) {
			count += 1;
		});
		return count;
	};
}
