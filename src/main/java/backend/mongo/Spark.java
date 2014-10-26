/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend.mongo;

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

        /* 
         * Se obtienen los datos de un proyecto dado su _id.
         */
        get(new Route("/proyectos/:projectId") {
            @Override
            public Object handle(Request request, Response response) {

                String projectId = request.params(":projectId");
                JSONObject proyecto
                        = servicioBDProyecto.obtenerProyecto(projectId);

                if (proyecto.has("error")) {
                    response.status(404);
                }
                return proyecto;
            }
        });

        /*
         Dado un nombre de proyecto pasado por url, buscamos todos los requisitos
         que estan asociados a el y devolvemos una lista de Json
         */
        /*
         get(new Route("/listarrequisitosproyecto/:nombreProyecto") {
         @Override
         public Object handle(Request request, Response response) {
         String nombreProy = new String(request.params(":nombreProyecto"));

         DBCursor proyectosCursor = servicioBDProyecto.estaProyectoNombre(nombreProy);
         BasicDBObject proy = (BasicDBObject) proyectosCursor.next();

                
         BasicDBList listaReq= (BasicDBList) proy.get("requisitos");

         return listaReq;
         }
         });
         */
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
                if (servicioBDProyecto.buscarNombreRepetido(nombre)) {
                    proy.put("nombre", nombre);
                    proy.put("descripcion", descripcion);

                    doc = servicioBDProyecto.crearProyecto(proy);
                } else {
                    response.status(400);
                    doc.put("error", "INVALID_NAME");
                }

                return doc;
            }
        });

        /*
         Dado un _id de proyecto y un email de participante, se actualiza el proyecto
         agregando al participante a la lista de participantes (si la tiene) o crea una nueva 
         lista de participantes con el correo dado.
         */
        put(new Route("/asociarparticipante") {
            @Override
            public Object handle(Request request, Response response) {
                String nombreProy = request.queryParams("proyectoId");
                String email = request.queryParams("email");

                JSONObject proyecto = servicioBDProyecto.asociarParticipante(email, nombreProy);
                if (proyecto.has("error")) {
                    response.status(404);
                    return proyecto;
                }
                return proyecto;

            }
        });

        /*
         Eliminar un participante dado de un proyecto
         */
        put(new Route("/desasociarparticipante") {
            @Override
            public Object handle(Request request, Response response) {
                String nombreProy = request.queryParams("proyectoId");
                String email = request.queryParams("email");

                JSONObject proyecto = servicioBDProyecto.desasociarParticipante(email, nombreProy);
                if (proyecto.has("error")) {
                    response.status(404);
                    return proyecto;
                }
                return proyecto;

            }
        });

    }

}
