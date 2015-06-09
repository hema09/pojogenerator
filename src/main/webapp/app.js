var app = angular.module("myapp",[]);
app.controller('mainCtrl',function($scope, crudService) {
    $scope.message = "Hi there!";
    $scope.fields = {};
    $scope.classname = null;
    $scope.types = ['String', 'Integer', 'Double', 'Long', 'List', 'Map', 'Date', 'OtherClass'];
    $scope.simpletypes = ['String', 'Integer', 'Double', 'Long', 'Date'];
    $scope.validInput = /^[a-zA-Z_$][a-zA-Z_$0-9]*$/;
    $scope.submit = function(field) {
        if(field.mapValueTypeSimple || field.mapValueTypeClass) {
            field.mapValueType = field.mapValueTypeSimple ? field.mapValueTypeSimple : field.mapValueTypeClass;
            delete field.mapValueTypeSimple;
            delete field.mapValueTypeClass;
        }
        var fieldName = field.name;
        delete field.name;
        $scope.fields[fieldName] = field;
        console.log("name=" + fieldName + ", value=" + field);
        $scope.field = null;
    }
    $scope.isList = function(val) {
        if(val == 'List')
            return true;
        return false;
    }
    $scope.isMap = function(val) {
        if(val == 'Map')
            return true;
        return false;
    }
    $scope.isOtherClass = function(val) {
        if(val == 'OtherClass')
            return true;
        val = null; // clear the class if not 'OtherClass'
        return false;
    }
    $scope.isTypeSelected = function(val) {
        if(val != '')
            return true;
        return false;
    }
    $scope.getDtoClass = function() {
        var classname = "JavaClass";
        if($scope.classname != null) {
            classname = $scope.classname;
        }
        var json = JSON.stringify($scope.fields);
        console.log(json);
        crudService.getDtoClass(json, classname)
            .then(
            function(response) {
                var output = response;
                console.log(output);
                $scope.dtoclassdata = output;
            },
            function(errorMessage) {
                console.warn(errorMessage);
            }
        );
    }

    $scope.clearAll = function() {
        $scope.fields = {};
        $scope.dtoclassdata = "";
        $scope.classname = null;
    }

    $scope.remove = function(field) {
        delete $scope.fields[field];
    }

    $scope.isFieldsEmpty = function() {
        return Object.keys($scope.fields).length == 0;
    }

    $scope.getTypeString = function(value) {
        var type = value['type'];
        if(value['classname']){
            return capitalize(value['classname']);
        } else if(value['listObjectTypeBasic']) {
            type = type + '<' + value['listObjectTypeBasic'] + '>';
        } else if(value['listObjectTypeOther']) {
            type = type + '<' + capitalize(value['listObjectTypeOther']) + '>';
        } else if(value['mapKeyType']) {
            type = type + '<' + value['mapKeyType'] + ', ' + capitalize(value['mapValueType']) + '>';
        }
        return type;
    }

    function capitalize(str) {
        return str.charAt(0).toUpperCase() + str.slice(1);
    }

    function loadData(data) {
        $scope.dtoclassdata = data;
        console.log(data);
    }
});

app.service('crudService', function($http, $q) {

    // return public methods
    return ({
        getDtoClass : getDtoClass
    });

    function getDtoClass(fieldmap, className) {
        console.log(fieldmap);
        var request = $http({
            method: "post",
            url : "http://localhost:8080/pojo/getclass?classname="+className,
            headers : {'Accept': '*/*'},
            params: {
                action : "post"
            },
            data : fieldmap
        });
        return(request.then(handleSuccess, handleFailure));
    }


    // private methods

    function handleSuccess(response) {
        return(response.data);
    }

    function handleFailure(response) {
        if(!angular.isObject(response.data) || !response.data.message) {
            return ($q.reject("An unknown error occurred."));
        }
        return ($q.reject(response.data.message));
    }

});
