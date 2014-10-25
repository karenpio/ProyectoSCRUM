/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.uniendosparkmongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.BasicDBList;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Servicio de la BD con mongo en el cual (por ahora) podemos crear/insertar
 * proyectos o listarlos, para ello debemos conectarnos a mongo, crear la BD,
 * sus colecciones para poder realizar las operaciones.
 *
 * @author karen
 */
public class ServicioBD {

    MongoClient mongo;
    DB db;
    DBCollection proyecto;
    DBCollection requisito;
    DBCollection participante;
    DBCollection carrera;

    public ServicioBD() {

        try {
            // Se conecta a mongoDB (Connect to MongoDB)
            mongo = new MongoClient("localhost", 27017);

            // Crear la base de datos (si no existe) (Get database)
            db = mongo.getDB("pruebaConSpark");

            // Se crea la coleccion "proyecto"
            proyecto = db.getCollection("proyecto");

            // Se crea la coleccion "requisito"
            requisito = db.getCollection("requisito");

            // Se crea la coleccion "participante"
            participante = db.getCollection("participante");
            
            // Se crea la coleccion "carrera"
            carrera = db.getCollection("carrera");

            System.out.println("Connecting to MongoDB@" + mongo.getAllAddress());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /* Se crea un documento (BasicDBObject) con un nombre y descripcion y se inserta
     en la coleccion proyecto.
     */
    public BasicDBObject crearProyecto(JSONObject proy) {

        BasicDBObject doc = new BasicDBObject("nombre", proy.get("nombre"))
                .append("descripcion", proy.get("descripcion"));

        proyecto.insert(doc);

        return doc;
    }
    
    
    
    public BasicDBObject actualizarProyecto(JSONObject proy) {

        DBCursor proyCursor = estaProyectoNombre(proy.get("nombre").toString());
        BasicDBObject proyect = (BasicDBObject) proyCursor.next();
        
        Object o = JSON.parse(proy.toString());
        BasicDBObject proyActualizado = (BasicDBObject) o;

        
        proyecto.save(proyActualizado);

        //proyecto.update(proyect, proyActualizado);

        return proyActualizado;
    }
    
    


    /* 
     Funcion utilizada para asegurar la unicidad del nombre del proyecto
     Retorna true si ya existe un proyecto con el nombre dado
     */
    public DBCursor estaProyectoNombre(String nombreProy) {
        BasicDBObject query = new BasicDBObject("nombre", new BasicDBObject("$eq", nombreProy));
        DBCursor cursor = proyecto.find(query);

        return cursor;

    }

    /* Se crea un documento (BasicDBObject) con email, nombre y telefono y se inserta
     en la coleccion participante.
     */
    public BasicDBObject crearParticipante(JSONObject part) {

        BasicDBObject doc = new BasicDBObject("email", part.get("email"))
                .append("nombre", part.get("nombre")).append("telefono", part.get("telefono"));

        participante.insert(doc);

        return doc;
    }

    /* 
     Unicidad del email del participante  
     */
    public DBCursor estaEmailParticipante(String emailPart) {
        BasicDBObject query = new BasicDBObject("email", new BasicDBObject("$eq", emailPart));
        DBCursor cursor = participante.find(query);

        return cursor;

    }

    public JSONObject limpiarID(BasicDBObject obj) {
        JSONObject doc = new JSONObject(JSON.serialize(obj));
        String clean_idPart = (doc.getJSONObject("_id").get("$oid")).toString();
        doc.put("_id", clean_idPart);
        
        return doc;
    }

    //public BasicDBObject buscarProyecto(String nombre){}
    /* Coloca en una lista todos los proyectos que estan en la BD, iterando
     sobre la coleccion con un cursor para tomar cada documento de ella.
     */
    @SuppressWarnings("unchecked")
    public JSONArray leerTodosProyectos() {
        DBCursor cursor = proyecto.find();
        JSONArray resultados = new JSONArray();

        try {
            while (cursor.hasNext()) {
                JSONObject doc = new JSONObject(JSON.serialize(cursor.next()));
                String clean_id = (doc.getJSONObject("_id").get("$oid")).toString();
                doc.put("_id", clean_id);

                resultados.put(doc);
            }
        } finally {
            cursor.close();
        }
        return resultados;
    }
    /* 
        Obtener la identificacion de las carreras asociadas a un proyecto
    */
    public JSONObject obtenerIdCarreras(JSONObject proy){
        
        BasicDBObject campos = new BasicDBObject("carreras",1);
        
        DBCursor cursor = estaProyectoNombre(proy.get("nombre").toString());
        
        JSONObject resultados = new JSONObject();
        try {
            while(cursor.hasNext()){
                DBObject c = cursor.next();
                BasicDBList carreras = (BasicDBList) c.get("carreras");
                resultados.put("carreras",carreras);         
            } 
        } finally {
            cursor.close();
        }
        return resultados;
    }

}
