function ControladorScrum($scope,$http) {
  //Variables de control de partes visibles de la pagina
  $scope.parteCreacion="none";
  $scope.parteManejoParticipantes="none";
  $scope.mensajeExito="none";
  $scope.home="block";
  $scope.parteAgregarParticipantes="none";
  $scope.mensaje="iksajiasjidasjio";
  $scope.papi="papi";
  
  //Variable del modelo referente al participante que se agregara a un
  //proyecto.
  $scope.participante="";
  
  //Variables que son arreglos aca, pero que deberan ser elementos
  //traidos o guardados de la base de datos una vez que el backend
  //funcione.
  $scope.proyectos=[];
  
  //Funcion que despliega la parte de crear un proyecto en el html
  $scope.abrirCreacionProyectos=function(){
    $scope.parteCreacion="block";
    $scope.parteManejoParticipantes="none";
  };
  
  //Funcion que despliega la seccion de manejo de participantes en el html.
  $scope.abrirManejoParticipantes=function(){
    $http.get('http://0.0.0.0:4567/listarproyectos')
      .success(function(data){
	$scope.parteCreacion="none";
	$scope.parteManejoParticipantes="block";
	$scope.proyectos=data;
	//$scope.mensaje=data;
	$scope.papi=data;
      })
      .error(function(data,status){
      $scope.mensaje = status;

      });
  };
  
  //Funcion para volver al inicio despues de haber estado en una de las
  //dos secciones ofrecidas.
  $scope.volverHome=function(){
    $scope.mensajeExito="none";
    $scope.home="block";
    $scope.proyectoActual=null;
    $scope.parteCreacion="none";
    $scope.parteAgregarParticipantes="none";
    $scope.parteManejoParticipantes="none"; 
    $scope.nombreProyecto="";
    $scope.descrProyecto="";
    $scope.productoProyecto="";
    $scope.participantes=[];
  };
  
  //Funcion que crea un proyecto: por ahora, simplemente lo agregamos
  //a nuestro arreglo de proyectos (dicho arreglo debe ser traido de la
  //base de datos).
  $scope.CrearProyecto=function(){
    //Almaceno el proyecto a guardar en mi arreglo global de proyectos
    //en fases posteriores sera llamar al servicio que permite almacenar
    //proyectos en la base de datos.
    //$scope.proyectos.push({nombre:$scope.nombreProyecto, descr:$scope.descrProyecto, producto: $scope.productoProyecto,participantes:["Carlos","Pedro"]});
    
    $http({
        method  : 'POST',
        url     : 'http://localhost:4567/crearproyecto',
	data: "nombre="+$scope.nombreProyecto+"&descripcion="+$scope.descrProyecto,
	headers : {'Content-Type':'application/x-www-form-urlencoded; charset=UTF-8'}
    }).success(function(data){
      $scope.parteCreacion="none";
      $scope.parteManejoParticipantes="none";
      $scope.home="none";
      $scope.mensajeExito="block";  
      $scope.nombreProyecto="";
      $scope.descrProyecto="";
      $scope.productoProyecto=""; 
      $scope.mensaje="iasdiasjdiaojio";
  
    })
    .error(function(data,status){
      $scope.mensaje=status;
      $scope.mensaje=data;
    });
  };
  
  //Para agregar un paricipante primero ofreceremos los proyectos en forma
  //de lista, y al hacer click en uno de ellos se pasara a manejar los
  //participantes para dicho proyecto. Esta funcion ubica el proyecto
  //deseado y lo asigna a la variable proyectoActual para tenerlo siempre
  //presente.
  $scope.abrirProyecto=function(a){
//     for (j=0; j< $scope.proyectos.length; j++){
//       if ($scope.proyectos[j].nombre==nombreProyecto){
// 	$scope.proyectoActual=$scope.proyectos[j];
//       }
//     }
    
    $scope.proyectoActual=a; //Objeto del proyecto actual.
    //Debo listar los participantes que hay en la bd.
     $http.get('http://0.0.0.0:4567/participantes')
      .success(function(data){
	$scope.participantes=data;
	$scope.mensaje="yipi";
      })
      .error(function(data,status){
      $scope.mensaje = status;

      });
    $scope.parteAgregarParticipantes="block";
    $scope.parteManejoParticipantes="none";
  };
  
  //Agrega un participante a la lista de participantes del proyectoActual
  $scope.agregarParticipante=function(a){
    //Lo agrego a la variable proyecto actual para reflejar el cambio
    //instantanemante. Debo guardar el cambio hecho en la base de datos.
    //En la variable de scope proyectoActual tengo la info del proyecto
    //a modificar en la base de datos.
    
    //Llamo al servicio para asociar participante con proyecto.

    $http({
        method  : 'PUT',
        url     : 'http://localhost:4567/asociarparticipante',
	data: "email="+a+"&proyectoId="+$scope.proyectoActual._id,
	headers : {'Content-Type':'application/x-www-form-urlencoded; charset=UTF-8'}
    }).success(function(data){
      $scope.parteCreacion="none";
      $scope.parteManejoParticipantes="none";
      $scope.home="none";
      $scope.mensajeExito="block";  
      $scope.nombreProyecto="";
      $scope.descrProyecto="";
      $scope.productoProyecto=""; 
      $scope.mensaje="iasdiasjdiaojio";
  
    })
    .error(function(data,status){
      $scope.mensaje=status;
      $scope.mensaje=data;
    });
    
    $scope.participante="";
    $scope.participanteMail="";
    $scope.participanteTlf="";
    $scope.parteAgregarParticipantes="none";
    $scope.parteManejoParticipantes="none";
    $scope.mensajeExito="block";  
  };
  
  //Funcion para eliminar un participante del proyecto actual con el que
  //se trabaja.
  $scope.eliminarPart=function(re){
    //Busco index en el arreglo del req a eliminar y luego uso splice

    for (w=0;w<$scope.proyectoActual.participantes.length;w++){
      if ($scope.proyectoActual.participantes[w]== re){
	$scope.proyectoActual.participantes.splice(w,1);
      }
    }
  }
  
  
}