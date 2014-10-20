Ejemplo uniendo MongoDB y Spark

En este ejemplo se logra unir mongoDB con Spark. Se tienen 2 archivos source (.java)
en los cuales tenemos un servicio de la BD (alli tendremos todo lo que serian las operaciones CRUD de la
BD) y el main de Spark tenemos el servicio web (REST).

En la carpeta resources, es donde se supone que deben ir las vistas o el manejador del 
front end en Angular js.

Abrir el proyecto en Netbeans (Es un proyecto maven)

Fuentes:

Instalar Maven3 en Ubuntu: http://www.sysads.co.uk/2014/05/install-apache-maven-3-2-1-ubuntu-14-04/

Instalar mongoDB en Ubuntu: http://docs.mongodb.org/manual/tutorial/install-mongodb-on-ubuntu/

Tutorial de ayuda para Spark: http://www.taywils.me/2013/11/05/javasparkframeworktutorial.html

Java MongoBD driver: http://docs.mongodb.org/ecosystem/drivers/java/

Nota: Asegurense de instalar bien mongo y maven porque sino ocurriran problemas. 
Cuando abran el proyecto en netbeans asegurense de resolver las dependencias (click derecho sobre
el nombre del proyecto -> resolve problems).

