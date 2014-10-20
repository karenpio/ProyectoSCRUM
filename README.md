Ejemplo uniendo MongoDB y Spark

	En este ejemplo se logra unir mongoDB con Spark. Se tienen 3 archivos source (.java)
en los cuales tenemos una clase Proyecto (se necesitara una clase para cada coleccion/entidad
de la BD), un servicio de la BD (alli tendremos todo lo que serian las operaciones CRUD de la
BD) y el main de Spark tenemos el servicio web (REST).

	En la carpeta resources, es donde se supone que deben ir las vistas o el manejador del 
front end en Angular js.

	Abrir el proyecto en Netbeans (Es un proyecto maven)

Fuentes:

	Instalar Maven3: http://www.sysads.co.uk/2014/05/install-apache-maven-3-2-1-ubuntu-14-04/

	Tutorial de ayuda para Spark: http://www.taywils.me/2013/11/05/javasparkframeworktutorial.html

	Java MongoBD driver: http://docs.mongodb.org/ecosystem/drivers/java/

