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
  $scope.mensajeCarrerasVacias="";
  $scope.mensajeReqsVacios="";
  $scope.pacnom="";
  
//   Funcion de regreso: inicializa todas las variables a su estado inicial.
  $scope.volverHome=function(){
    $scope.scar="none";
    $scope.defta="none";
    $scope.at="none";
    $scope.carrs="none";
    $scope.tareasCarrera=[];
    $scope.proyectos=[];
    $scope.carreras=[];
    $scope.proyectoActual="";
    $scope.carreraActual="";
    $scope.reqActual="";
    $scope.mensajeCarrerasVacias="";
    $scope.mensajeReqsVacios="";
    $scope.tareasCarrera="";
    $scope.requisitos="";
    $scope.pacnom="";
     $scope.mensajeCarrerasVacias="";

  };
  
//   Funcion que abre la seccion de seleccion de caracteristicas
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
      $scope.mensajeCarrerasVacias="";
      $scope.mensajeReqsVacios="";
      $scope.tareasCarrera="";
      $scope.requisitos="";
      $scope.pacnom="";
       $scope.mensajeCarrerasVacias="";
    };
    
   //   Funcion que abre la seccion de seleccion de definicion de tareas 
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
      $scope.mensajeCarrerasVacias="";
      $scope.mensajeReqsVacios="";
      $scope.tareasCarrera="";
      $scope.requisitos="";
      $scope.pacnom="";
       $scope.mensajeCarrerasVacias="";
    };
  
//     Busca los proyectos, llama al servicio que devuelve un listado de los mismos
    $scope.fetchProjects=function(){
    $http.get('http://0.0.0.0:4567/listarproyectos')
      .success(function(data){
	$scope.parteCreacion="none";
	$scope.parteManejoParticipantes="block";
	$scope.proyectos=data;
      })
      .error(function(data,status){
      $scope.mensaje = "error";

      });			  
  };
  
//   Busca las carreras de un proyecto en particular
   $scope.fetchCarreras=function(a){

	$scope.proyectoActual=a._id; //id del proyecto con el que trabajo actualmente.
	$scope.pacnom=a.nombre; //nombre del proyecto.
	$scope.carrs="block";
	//Debo llamar al servicio que me dara las carreras para el proyecto de _id
	//Armo el string:
	var url='http://0.0.0.0:4567/listarcarrerasproyecto/'+a._id;
	$http.get(url).
	success(function(data, status, headers, config) {
	  if (data.length == 0){
	    $scope.mensajeCarrerasVacias="Este proyecto NO tiene carreras";
	  }
	  $scope.carreras=data;
	}).
	error(function(data, status, headers, config) {
	  $scope.mensaje="error papillo";
	});
   };
   
   //En esta funcion debo: pedir los requerimientos del proyecto actual
   //y tambien pedir los requerimientos de la carrera.
    $scope.fetchReqs=function(a){
	 $scope.mensajeCarrerasVacias="";
	$scope.reqss="block";
	$scope.carreraActual=a; //Id de la carrera con la que trabajo actualmente.	
	//Llamada para pedir los requisitos del proyecto: listarrequisitosproyecto/544cf87f44aec7d4ee558e58
	var url='http://0.0.0.0:4567/listarrequisitosproyecto/'+$scope.proyectoActual;
	$http.get(url).
	success(function(data, status, headers, config) {
	  if (data.length == 0){
	    $scope.mensajeReqsVacios="Este proyecto NO tiene reqs";
	  }
	  $scope.requisitos=data;
	}).
	error(function(data, status, headers, config) {
	  $scope.mensaje="error papillo";
	});
	
	//Ahora debo pedir los requisitos para la carrera espeficamente.
	//Llamada para pedir los requisitos de la carrera: listarrequisitoscarrera/544d176d4cb85b483264ff0a
	var url='http://0.0.0.0:4567/listarrequisitoscarrera/'+$scope.carreraActual;
	$http.get(url).
	success(function(data, status, headers, config) {
	  $scope.requisitosCarrera=data;
	}).
	error(function(data, status, headers, config) {
	  $scope.mensaje="error papillo";
	});
   };
   
   //Anhado un requerimiento a la bd. Tambien actualizo el arreglo local 
   //para que se refleje en pantalla instantaneamente.
   $scope.addReq=function(a){

      $scope.reqActual=a._id; //El requisito que deseo agregar. Primero debo verificar que no este ya agregado.
      var kiwi=0
      for (j=0; j< $scope.requisitosCarrera.length; j++){
	if ($scope.requisitosCarrera[j]._id==a._id){
	  kiwi=-1;
	}
      }
      //Si el elemento ya no estaba: Actualizo el arreglo para que se actualice instaneamente en pantalla
      //y hago un post para que se guarde.
      if (kiwi > -1){
	//LLamada para asociar un requisito con una carrera.
	//asociarrequisitocarrera
	$scope.mensaje="carreraId="+$scope.carreraActual+"&requisitoId="+a._id;
	$http({
	    method  : 'put',
	    url     : 'http://localhost:4567/asociarrequisitocarrera',
	    data: "carreraId="+$scope.carreraActual+"&requisitoId="+a._id,
	    headers : {'Content-Type':'application/x-www-form-urlencoded; charset=UTF-8'}
	}).success(function(data){
	  $scope.mensaje="";
      
	})
	.error(function(data,status){
	  $scope.mensaje="mal";
	});
	$scope.requisitosCarrera.push(a);
      }

	
   };
   
    //Funcion que busca las tareas que hay para una carrera dada.
      $scope.fetchTareas=function(a){

	$scope.carreraActual=a;
	$scope.at="block";
	//$scope.tareasCarrera=["t1","t2"];
	//Llamo a servicio que me dara las tareas de esa carrera listartareascarrera/544d176d4cb85b483264ff0a
	var url='http://0.0.0.0:4567/listartareascarrera/'+$scope.carreraActual;
	$http.get(url).
	success(function(data, status, headers, config) {
	  $scope.tareasCarrera=data;
	}).
	error(function(data, status, headers, config) {
	  $scope.mensaje="error papillo";
	});
   };
   
//    Funcion que grega una tarea a la bd y actualiza el arreglo local.
    $scope.addTarea=function(){
      //Actualizo el arreglo
      $scope.tareasCarrera.push({nombre:$scope.nomTarea});
      //Guardar en BD
      //Llamo al post para guardar la tarea en la BD.
      //Actualizo en la BD.
      $http({
	  method  : 'POST',
	  url     : 'http://localhost:4567/creartarea',
	  data: "nombre="+$scope.nomTarea+"&peso="+$scope.pesoTarea+"&estado="+$scope.estadoTarea+"&fechaFin="+$scope.fechaTarea+"&carreraId="+$scope.carreraActual,
	  headers : {'Content-Type':'application/x-www-form-urlencoded; charset=UTF-8'}
      }).success(function(data){
    
      })
      .error(function(data,status){
	$scope.mensaje="mal";
      });
	
   };
			  
  }
  
