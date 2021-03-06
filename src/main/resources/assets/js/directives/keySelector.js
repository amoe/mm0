MicroMacroApp.directive('keySelector', function(Collapse) {
    return {
        restrict: 'A',
        scope : {
            "selectedKeys" : "=",
            "keys" : "=",
            "invert" : "=",
            "msg": "=",
            "useField": "=?",
            "onSelect": "&?"
        },
        templateUrl : 'html/directives/key-selector.html',
        link: function($scope, element, attr) {
            $scope.kz = Collapse.newCollapseZone();
            $scope.selectedKeys = $scope.selectedKeys || [];
            $scope.showing = false;

            var useField = 'useField' in attr? $scope.useField : false;
            var onChange = 'onSelect' in attr? $scope.onSelect : function(){};

            $scope.$watch(useField? 'keys.'+useField: 'keys.incoming', function (val) {
                if (val) {
                    $scope.selectedKeys = $scope.selectedKeys || [];
                    $scope.namespace2keys = {}
                    val = Object.keys(val);
                    $scope.showing = val.length > 0;
                    val.sort();
                    angular.forEach(val, function (keyName) {
                        var key = {str: keyName};
                        if(!$scope.invert) {
                            if ($scope.selectedKeys.indexOf(keyName) > -1) {
                                key.selected = true;
                            } else {
                                key.selected = false;
                            }
                        } else {
                            if ($scope.selectedKeys.indexOf(keyName) > -1) {
                                key.selected = false;
                            } else {
                                key.selected = true;
                            }
                        }

                        var idx = keyName.indexOf("/"),
                            ns,
                            name,
                            ks;

                        if (idx > -1) {
                            ns = keyName.substring(0, idx);
                            name = keyName.substring(idx+1);
                        } else {
                            ns = "ε";
                            name = keyName;
                        }

                        key.name = name;

                        if ((ks = $scope.namespace2keys[ns]) == null) {
                            ks = $scope.namespace2keys[ns] = [];
                        }

                        ks.push(key);
                    });
                }
            });

            $scope.selectKey = function (key, noSave) {
                if(!$scope.invert) {
                    if(!key.selected) {
                        $scope.selectedKeys.push(key.str);
                        key.selected = true;
                        if (!noSave) onChange();
                    }
                } else {
                    if(!key.selected) {
                        $scope.selectedKeys.splice($scope.selectedKeys.indexOf(key.str), 1);
                        key.selected = true;
                        if (!noSave) onChange();
                    }
                }
            };

            $scope.deselectKey = function (key, noSave) {
                if(!$scope.invert) {
                    if (key.selected) {
                        $scope.selectedKeys.splice($scope.selectedKeys.indexOf(key.str), 1);
                        key.selected = false;
                        if (!noSave) onChange();
                    }
                } else {
                    if (key.selected) {
                        $scope.selectedKeys.push(key.str);
                        key.selected = false;
                        if (!noSave) onChange();
                    }
                }
            }

            $scope.toggleKey = function (key) {
               if (!key.selected) {
                   $scope.selectKey(key);
               } else {
                   $scope.deselectKey(key);
               }
               onChange();
            };


            $scope.selectAll = function (ns, noSave) {
                if (ns){
                    $scope.namespace2keys[ns].forEach(function(element){
                        $scope.selectKey(element, true);
                    });
                } else {
                    angular.forEach($scope.namespace2keys, function(value, key) {
                        $scope.selectAll(key, true);
                    });
                }
                if (!noSave) {
                    onChange();
                }
            };

            $scope.deselectAll = function (ns, noSave) {
                if (ns){
                    $scope.namespace2keys[ns].forEach(function(element){
                        $scope.deselectKey(element, true);
                    });
                } else {
                    angular.forEach($scope.namespace2keys, function(value, key) {
                        $scope.deselectAll(key, true);
                    });
                }
                if (!noSave) {
                    onChange();
                }
            };

            $scope.nsMixed = function (ns) {
                var isSelecting = false;
                var isDeselecting = false;
                for (var i in $scope.namespace2keys[ns]) {
                    var k = $scope.namespace2keys[ns][i];
                    if (k.selected) {
                        isSelecting = true;
                    } else {
                        isDeselecting = true;
                    }
                    if (isDeselecting && isSelecting) return true;
                }
            };

            $scope.nsDisabled = function (ns) {
                var isSelecting = false;
                var isDeselecting = false;
                for (var i in $scope.namespace2keys[ns]) {
                    var k = $scope.namespace2keys[ns][i];
                    if (k.selected) {
                        isSelecting = true;
                    } else {
                        isDeselecting = true;
                    }
                }

                return isDeselecting && !isSelecting;
            };
        }
    };
});