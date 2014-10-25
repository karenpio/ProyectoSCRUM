/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.uniendosparkmongo;

import com.mongodb.util.JSON;
import java.net.UnknownHostException;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;
import static spark.Spark.get;
import static spark.Spark.post;

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
                if (!servicioBDProyecto.estaProyectoNombre(nombre)) {
                    proy.put("nombre", nombre);
                    proy.put("descripcion", descripcion);

                    doc = new JSONObject(JSON.serialize(servicioBDProyecto.crearProyecto(proy)));
                    String clean_id = (doc.getJSONObject("_id").get("$oid")).toString();
                    doc.put("_id", clean_id);
                }

                return doc;
            }
        });
    }

}
