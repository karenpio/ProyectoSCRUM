function ControladorScrum2($scope,$http) {
  $scope.scar="none";
  $scope.defta="none";
  $scope.proyectoActual="";
  $scope.carreraActual="";
  $scope.reqActual="";
  $scope.carrs="none";
  //$scope.reqs="none";
  $scope.reqss="none";
  $scope.at="none";
  
    $scope.scarOpen=function(){
      $scope.scar="block";
      $scope.defta="none";
      $scope.at="none";
      $scope.carrs="none";
      $scope.tareasCarrera=[];
      $scope.proyectos=[];
      $scope.carreras=[];
      $scope.proyectoActual="";
      $scope.carreraActual="";
      $scope.reqActual="";
    };
    
    $scope.deftaOpen=function(){
      $scope.defta="block";
      $scope.scar="none";
      $scope.carrs="none";
      $scope.at="none";
      $scope.carreras=[];
      $scope.requisitosCarrera=[];
      $scope.proyectos=[];
      $scope.proyectoActual="";
      $scope.carreraActual="";
      $scope.reqActual="";
    };
  
    $scope.fetchProjects=function(){
//     $http.get('http://0.0.0.0:4567/listarproyectos')
//       .success(function(data){
// 	$scope.parteCreacion="none";
// 	$scope.parteManejoParticipantes="block";
// 	$scope.proyectos=data;
// 	//$scope.mensaje=data;
// 	$scope.papi=data;
//       })
//       .error(function(data,status){
//       $scope.mensaje = status;
// 
//       });
	$scope.proyectos=[{nombre: "p1", descripcion: "d1", 
			  participantes: [],
			  requisitos: [], 
			  carreras:[]},{nombre: "p2", descripcion: "d2", 
			  participantes: [],
			  requisitos: [], 
			  carreras:[]}];
			  
  };
  
   $scope.fetchCarreras=function(a){
//     $http.get('http://0.0.0.0:4567/listarproyectos')
//       .success(function(data){
// 	$scope.parteCreacion="none";
// 	$scope.parteManejoParticipantes="block";
// 	$scope.proyectos=data;
// 	//$scope.mensaje=data;
// 	$scope.papi=data;
//       })
//       .error(function(data,status){
//       $scope.mensaje = status;
// 
//       });
	$scope.proyectoActual=a;
	$scope.carrs="block";
	$scope.carreras=[{miembros: "p1", descripcion: "d1", 
			  tareas: [],
			  requisitos: [], 
			  ceremonias:[], numero: "1"}
			  ,{miembros: "p1", descripcion: "d1", 
			  tareas: [],
			  requisitos: [], 
			  ceremonias:[], numero: "2"}];
   };
   
    $scope.fetchReqs=function(a){
//     $http.get('http://0.0.0.0:4567/listarproyectos')
//       .success(function(data){
// 	$scope.parteCreacion="none";
// 	$scope.parteManejoParticipantes="block";
// 	$scope.proyectos=data;
// 	//$scope.mensaje=data;
// 	$scope.papi=data;
//       })
//       .error(function(data,status){
//       $scope.mensaje = status;
// 
//       });
	$scope.reqss="block";
	$scope.carreraActual=a;
	$scope.requisitos=[{nombre: "r1", propiedad: "pro1", 
			 }, {nombre: "r2", propiedad: "pro2", 
			 },{nombre: "r3", propiedad: "pro1"}];
	$scope.requisitosCarrera=["r1"];
   };
   
   $scope.addReq=function(a){
//     $http.get('http://0.0.0.0:4567/listarproyectos')
//       .success(function(data){
// 	$scope.parteCreacion="none";
// 	$scope.parteManejoParticipantes="block";
// 	$scope.proyectos=data;
// 	//$scope.mensaje=data;
// 	$scope.papi=data;
//       })
//       .error(function(data,status){
//       $scope.mensaje = status;
// 
//       });
	$scope.reqActual=a;
	var kiwi=0
	for (j=0; j< $scope.requisitosCarrera.length; j++){
	  if ($scope.requisitosCarrera[j]==a){
	    kiwi=-1;
	  }
	}
	if (kiwi > -1){
	  $scope.requisitosCarrera.push(a);
	}
	
   };
   
      $scope.fetchTareas=function(a){
//     $http.get('http://0.0.0.0:4567/listarproyectos')
//       .success(function(data){
// 	$scope.parteCreacion="none";
// 	$scope.parteManejoParticipantes="block";
// 	$scope.proyectos=data;
// 	//$scope.mensaje=data;
// 	$scope.papi=data;
//       })
//       .error(function(data,status){
//       $scope.mensaje = status;
// 
//       });
	$scope.carreraActual=a;
	$scope.at="block";
	$scope.tareasCarrera=["t1","t2"];
   };
   
         $scope.addTarea=function(){
//     $http.get('http://0.0.0.0:4567/listarproyectos')
//       .success(function(data){
// 	$scope.parteCreacion="none";
// 	$scope.parteManejoParticipantes="block";
// 	$scope.proyectos=data;
// 	//$scope.mensaje=data;
// 	$scope.papi=data;
//       })
//       .error(function(data,status){
//       $scope.mensaje = status;
// 
//       });
	$scope.tareasCarrera.push($scope.nomTarea);
	//Guardar en BD
	
   };
			  
  }
  
