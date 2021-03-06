function ControladorScrum2($scope,$http) {
  $scope.reporteTar="none";
  $scope.scar="none";
  $scope.defta="none";
  $scope.proys="none";
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
  $scope.atBoton = "block";
  $scope.form ="none";
  $scope.formMod="none";
  $scope.tabla ="none";
  
//   Funcion de regreso: inicializa todas las variables a su estado inicial.
  $scope.volverHome=function(){
    $scope.scar="none";
    $scope.defta="none";
    $scope.reporteTar="none";
    $scope.proys="none";
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
     $scope.numcarract="";
     $scope.requisitos=[];
     $scope.tareasCarrera=[];

  };
  
//   Funcion que abre la seccion de seleccion de caracteristicas
    $scope.scarOpen=function(){
      $scope.scar="block";
      $scope.proys="block";
      $scope.reporteTar="none";
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
       $scope.numcarract="";
       $scope.requisitosCarrera=[];
       $scope.requisitos=[];
       $scope.tareasCarrera=[];
    };
    
   //   Funcion que abre la seccion de seleccion de definicion de tareas 
    $scope.deftaOpen=function(){
      $scope.defta="block";
      $scope.proys="block";
      $scope.reporteTar="none";
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
       $scope.numcarract="";
       $scope.requisitos=[];
       $scope.tareasCarrera=[];
    };
    //Funcion que despliega las opciones para reporte de tareas completadas
    $scope.reporteTarOpen=function(){
      $scope.reporteTar="block";
      $scope.proys="block";
      $scope.defta="none";
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
       $scope.numcarract="";
       $scope.requisitos=[];
       $scope.tareasCarrera=[];
    };
  
//     Busca los proyectos, llama al servicio que devuelve un listado de los mismos
    $scope.fetchProjects=function(){
    $http.get('http://0.0.0.0:4567/listarproyectos')
      .success(function(data){
	$scope.parteCreacion="none";
	$scope.parteManejoParticipantes="block";
        $scope.carrs="none";
        $scope.proys="block";
	$scope.proyectos=data;
      })
      .error(function(data,status){
      $scope.mensaje = "error";

      });			  
  };
  
//   Busca las carreras de un proyecto en particular
   $scope.fetchCarreras=function(a){
	$scope.requisitosCarrera=[];
	$scope.requisitos=[];
	$scope.numcarract="";
	$scope.tareasCarrera=[];
	$scope.mensajeCarrerasVacias="";
	$scope.mensajeReqsVacios="";
	$scope.proyectoActual=a; //id del proyecto con el que trabajo actualmente.
	$scope.pacnom=a.nombre; //nombre del proyecto.
        $scope.reqss = "none";
        $scope.at = "none";
        $scope.atBoton = "block";
        $scope.form ="none";
        $scope.proys="none";
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
	  $scope.mensaje="error";
	});
   };
   
   //En esta funcion debo: pedir los requerimientos del proyecto actual
   //y tambien pedir los requerimientos de la carrera.
    $scope.fetchReqs=function(a,b){
	 $scope.mensajeCarrerasVacias="";
	 $scope.mensajeReqsVacios="";
	$scope.reqss="block";
	$scope.numcarract=b;
	$scope.carreraActual=a; //Id de la carrera con la que trabajo actualmente.	
	//Llamada para pedir los requisitos del proyecto: listarrequisitosproyecto/544cf87f44aec7d4ee558e58
	var url='http://0.0.0.0:4567/listarrequisitosdisponibles/'+$scope.proyectoActual._id;
	$http.get(url).
	success(function(data, status, headers, config) {
	  if (data.length == 0){
	    $scope.mensajeReqsVacios="Este proyecto no tiene requisitos disponibles.";
	  }
	  $scope.requisitos=data;
	}).
	error(function(data, status, headers, config) {
	  $scope.mensaje="error de requisitos disponibles";
	});
	
	//Ahora debo pedir los requisitos para la carrera espeficamente.
	//Llamada para pedir los requisitos de la carrera: listarrequisitoscarrera/544d176d4cb85b483264ff0a
	var url='http://0.0.0.0:4567/listarrequisitoscarrera/'+$scope.carreraActual;
	$http.get(url).
	success(function(data, status, headers, config) {
	  $scope.requisitosCarrera=data;
	}).
	error(function(data, status, headers, config) {
	  $scope.mensaje="error";
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
	$http({
	    method  : 'put',
	    url     : 'http://0.0.0.0:4567/asociarrequisitocarrera',
	    data: "carreraId="+$scope.carreraActual+"&requisitoId="+a._id,
	    headers : {'Content-Type':'application/x-www-form-urlencoded; charset=UTF-8'}
	}).success(function(data){
          for(i=0; i<$scope.requisitos.length;i++){
              if($scope.requisitos[i]._id == a._id){
                  $scope.requisitos.splice(i,1);
              }
          }
          if($scope.requisitos.length == 0){
              $scope.mensajeReqsVacios="Este proyecto no tiene requisitos disponibles.";
          }
      
	})
	.error(function(data,status){
	  $scope.mensaje="mal";
	});
	$scope.requisitosCarrera.push(a);
      }

	
   };
   
    //Funcion que busca las tareas que hay para una carrera dada.
      $scope.fetchTareas=function(a,b){
	$scope.requisitosCarrera=[];
	$scope.requisitos=[];
	$scope.numcarract="";
	$scope.mensajeCarrerasVacias="";
	$scope.mensajeReqsVacios="";
	$scope.tareasCarrera=[];
	$scope.carreraActual=a;
	$scope.numcarract=b;
        $scope.carrs="none";
	$scope.at="block";
        $scope.tabla ="block";
	//$scope.tareasCarrera=["t1","t2"];
	//Llamo a servicio que me dara las tareas de esa carrera listartareascarrera/544d176d4cb85b483264ff0a
	var url='http://0.0.0.0:4567/listartareascarrera/'+$scope.carreraActual;
	$http.get(url).
	success(function(data, status, headers, config) {
	  $scope.tareasCarrera=data;
	}).
	error(function(data, status, headers, config) {
	  $scope.mensaje="error";
	});
   };
   
   //Funcion que busca las tareas que hay para una carrera dada.
      $scope.fetchTareasCompletadas=function(a,b){
	$scope.requisitosCarrera=[];
	$scope.requisitos=[];
	$scope.numcarract="";
	$scope.mensajeCarrerasVacias="";
	$scope.mensajeReqsVacios="";
	$scope.tareasCarreraC=[];
	$scope.carreraActual=a;
	$scope.numcarract=b;
        $scope.carrs="none";
	$scope.at="block";
        

	var url='http://0.0.0.0:4567/listartareascompletadascarrera/'+$scope.carreraActual;
	$http.get(url).
	success(function(data, status, headers, config) {
	  $scope.tareasCarreraC=data;
	}).
	error(function(data, status, headers, config) {
	  $scope.mensaje="error";
	});
   };
   
//    Funcion que grega una tarea a la bd y actualiza el arreglo local.
    $scope.addTarea=function(){
      //Actualizo el arreglo
      
      //Guardar en BD
      //Llamo al post para guardar la tarea en la BD.
      //Actualizo en la BD.
      
      //Borro el formulario
 
      $http({
	  method  : 'POST',
	  url     : 'http://0.0.0.0:4567/creartarea',
	  data: "nombre="+$scope.nomTarea+"&peso="+$scope.pesoTarea+"&estado="+$scope.estadoTarea+"&fechaFin="+$scope.fechaTarea+"&carreraId="+$scope.carreraActual,
	  headers : {'Content-Type':'application/x-www-form-urlencoded; charset=UTF-8'}
      }).success(function(data){
      $scope.tareasCarrera.push({nombre:$scope.nomTarea,
                                peso:$scope.pesoTarea,
                                estado:$scope.estadoTarea,
                                fechaFin:$scope.fechaTarea,
                                _id:data._id});
	  $scope.nomTarea="";
      $scope.pesoTarea="";
      $scope.estadoTarea="";
      $scope.fechaTarea="";
      $scope.form="none";
      $scope.atBoton="block";
      })
      .error(function(data,status){
	$scope.mensaje="mal";
      });
      

	
   };
   
   $scope.mostrarFormModificarTarea=function(tarea){
       $scope.formMod ="block";
       $scope.tabla ="none";
       $scope.form ="none";
       $scope.atBoton="none";
       $scope.tId=tarea._id;
       $scope.nomTarea = tarea.nombre;
       $scope.estadoTarea=tarea.estado;
       $scope.pesoTarea=tarea.peso;
       $scope.fechaTarea=tarea.fechaFin;

       
   };
   
   $scope.modificarTarea=function(){
       
          $scope.form="none";
          $scope.formMod="none";
          $scope.tabla ="block";
          $scope.atBoton="block";
	$http({
	    method  : 'put',
	    url     : 'http://0.0.0.0:4567/actualizartarea',
	    data: "tareaId="+$scope.tId+"&peso="+$scope.pesoTarea+"&estado="+$scope.estadoTarea+"&fechaFin="+$scope.fechaTarea,
	    headers : {'Content-Type':'application/x-www-form-urlencoded; charset=UTF-8'}
	}).success(function(data){
          for(i=0; i<$scope.tareasCarrera.length;i++){
              if($scope.tareasCarrera[i]._id == $scope.tId){
                  $scope.tareasCarrera[i].peso = $scope.pesoTarea;
                  $scope.tareasCarrera[i].estado = $scope.estadoTarea;
                  $scope.tareasCarrera[i].fechaFin = $scope.fechaTarea;
              }
          }
	  $scope.mensaje="";
          $scope.nomTarea="";
          $scope.pesoTarea="";
          $scope.estadoTarea="";
          $scope.fechaTarea="";
          $scope.tId="";
          
	})
	.error(function(data,status){
	  $scope.mensaje=$scope.tareaId;
	});
        
        
       
      
   };
   
   $scope.desasociarRequisitoCarrera=function(req){
       $http({
	    method  : 'put',
	    url     : 'http://0.0.0.0:4567/desasociarrequisitocarrera',
	    data: "carreraId="+$scope.carreraActual+"&requisitoId="+req._id,
	    headers : {'Content-Type':'application/x-www-form-urlencoded; charset=UTF-8'}
	}).success(function(data){
          for(i=0; i<$scope.requisitosCarrera.length;i++){
              if($scope.requisitosCarrera[i]._id == req._id){
                  $scope.requisitosCarrera.splice(i,1);
              }
          }
          $scope.mensajeReqsVacios = "";
          $scope.requisitos.push(req);
          
          
	})
	.error(function(data,status){
	  $scope.mensaje=$scope.tareaId;
	});
       
   };
   
   $scope.eliminarTarea=function(tareaId){
       $http({
	    method  : 'put',
	    url     : 'http://0.0.0.0:4567/desasociartareacarrera',
	    data: "carreraId="+$scope.carreraActual+"&tareaId="+tareaId,
	    headers : {'Content-Type':'application/x-www-form-urlencoded; charset=UTF-8'}
	}).success(function(data){
          for(i=0; i<$scope.tareasCarrera.length;i++){
              if($scope.tareasCarrera[i]._id == tareaId){
                  $scope.tareasCarrera.splice(i,1);
              }
          }

          
	})
	.error(function(data,status){
	  $scope.mensaje=$scope.tareaId;
	});
       
   };
   
   $scope.regresarTablaTar=function(){
       $scope.carrs="none";
       $scope.at="block";
       $scope.tabla ="block";
       $scope.formMod="none";
       $scope.atBoton="block";
   };
   
   $scope.mostrarForm = function(){
     $scope.mensaje="";
     $scope.nomTarea="";
     $scope.pesoTarea="";
     $scope.estadoTarea="";
     $scope.fechaTarea="";
     $scope.tId="";
     $scope.atBoton = "none";
     $scope.form ="block";
   };
			  
  }
  
