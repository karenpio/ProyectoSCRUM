/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.uniendosparkmongo;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBRef;
import com.mongodb.util.JSON;
import java.net.UnknownHostException;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

/**
 *
 * @author karen
 */
public class Spark {

    public static void main(String[] args) throws UnknownHostException {

        final ServicioBD servicioBDProyecto = new ServicioBD();

        /* Aqui nos muestra un mensaje si en la BD no hay proyectos o nos muestra
         una lista con los nombres de los proyectos.
         Correr y poner en postman: http://localhost:4567/listarproyectos
         */
        get(new Route("/listarproyectos") {
            @Override
            public Object handle(Request request, Response response) {

                /* Creamos una lista de proyectos como un arreglo de JSON
                 en la cual se agregan todos que hay en la BD con el 
                 metodo "leerTodosProyectos"
                 */
                JSONArray proyectos = servicioBDProyecto.leerTodosProyectos();
                return proyectos;
            }
        });


        /* Se agrega en la bd un proyecto con los parametros que son pasados por url
         */
        post(new Route("/crearproyecto") {
            @Override
            public Object handle(Request request, Response response) {
                String nombre = request.queryParams("nombre");
                String descripcion = request.queryParams("descripcion");
                JSONObject doc = new JSONObject();

                JSONObject proy = new JSONObject();

                // Si el nombre del proyecto no existe, entonces se agrega
                if (servicioBDProyecto.estaProyectoNombre(nombre).count() == 0) {
                    proy.put("nombre", nombre);
                    proy.put("descripcion", descripcion);

                    doc = new JSONObject(JSON.serialize(servicioBDProyecto.crearProyecto(proy)));
                    String clean_id = (doc.getJSONObject("_id").get("$oid")).toString();
                    doc.put("_id", clean_id);
                }

                return doc;
            }
        });

        /*
         Dado un nombre de proyecto y un email de participante, se actualiza el proyecto
         agregando al participante a la lista de participantes (si la tiene) o crea una nueva 
         lista de participantes con el correo dado.
         */
        put(new Route("/asociarparticipante") {
            @Override
            public Object handle(Request request, Response response) {
                String nombreProy = request.queryParams("nombreProyecto");
                String email = request.queryParams("email");
                BasicDBList lista;

                // Lamamos a la funcion estaProyectoNombre para tomar el proyecto que queremos
                DBCursor cursorProy = servicioBDProyecto.estaProyectoNombre(nombreProy);

                // Tomamos el proyecto al cual le agregaremos el participante
                BasicDBObject proy = (BasicDBObject) cursorProy.next();

                // Llamamos a la funcion estaEmailParticipante para tomar el participante que queremos
                DBCursor cursorPart = servicioBDProyecto.estaEmailParticipante(email);

                // Tomamos el proyecto al cual le agregaremos el participante
                BasicDBObject part = (BasicDBObject) cursorPart.next();

                if (proy.get("participantes") != null) {
                    lista = (BasicDBList) proy.get("participantes");

                } else {
                    lista = new BasicDBList();
                }

                /*
                 Verificar que el participante no haya sido agregado antes.
                 Se utiliza el email en vez del _id porque agregaba mucha basura,
                 esto debemos acomodarlo mas adelante y preguntarle a Ascander.
                 */
                if (!lista.contains(part.get("email"))) {
                    lista.add(part.get("email"));
                }

                // Por ahora no limpia el _id no estamos seguros si esto sea lo mas conveniente.
                //JSONObject proyDoc = servicioBDProyecto.limpiarID(proy);
                JSONObject proyDoc = new JSONObject(JSON.serialize(proy));

                // Colocamos la lista actualizada en el JSON
                proyDoc.put("participantes", lista);

                // Actualizamos la base de datos dado el JSON actualizado
                BasicDBObject actualizacion = servicioBDProyecto.actualizarProyecto(proyDoc);

                return actualizacion;
            }
        });

        /*
            Eliminar un participante dado de un proyecto
        */
        put(new Route("/desasociarparticipante") {
            @Override
            public Object handle(Request request, Response response) {
                String nombreProy = request.queryParams("nombreProyecto");
                String email = request.queryParams("email");
                BasicDBList lista;
                BasicDBObject actualizacion = null;
                BasicDBObject proy = new BasicDBObject();
                

                // Lamamos a la funcion estaProyectoNombre para tomar el proyecto que queremos 
                // Tomamos el proyecto al cual le agregaremos el participante
                DBCursor cursorProy = servicioBDProyecto.estaProyectoNombre(nombreProy);
                proy = (BasicDBObject) cursorProy.next();

                // Llamamos a la funcion estaEmailParticipante para tomar el participante que queremos
                // Tomamos el proyecto al cual le agregaremos el participante
                DBCursor cursorPart = servicioBDProyecto.estaEmailParticipante(email);
                BasicDBObject part = (BasicDBObject) cursorPart.next();

                //System.out.println("-----------------");
                if (proy.get("participantes") != null) {
                    //System.out.println("    -----------------");
                    lista = (BasicDBList) proy.get("participantes");
                    System.out.println(lista);
                    if (lista.contains(part.get("email"))) {
                        lista.remove(part.get("email"));
                        // Si limpiamos el id se crean dos instancias en la bd lo cual es un problema
                        // JSONObject proyDoc = servicioBDProyecto.limpiarID(proy);
                        JSONObject proyDoc = new JSONObject(JSON.serialize(proy));

                        // Colocamos la lista actualizada en el JSON
                        proyDoc.put("participantes", lista);

                        // Actualizamos la base de datos dado el JSON actualizado
                        actualizacion = servicioBDProyecto.actualizarProyecto(proyDoc);
                    }
                }

                return actualizacion;

            }
        });
        
        /*
            Obtener lista de los id's de las carreras de los proyectos
        */
        
        get(new Route("/obtenerIdCarreras"){
            @Override
            public Object handle(Request request, Response response) {
                String nombre = request.queryParams("nombre");
                JSONObject proy = new JSONObject();
                proy.put("nombre" , nombre);
                JSONObject idCarreras = servicioBDProyecto.obtenerIdCarreras(proy);
                return idCarreras;
            }
        
        });

    }

}
