/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend.mongo;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;
import org.bson.types.ObjectId;
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

    public ServicioBD() {

        try {
            // Se conecta a mongoDB (Connect to MongoDB)
            mongo = new MongoClient("localhost", 27017);

            // Crear la base de datos (si no existe) (Get database)
            db = mongo.getDB("SCRUM");

            // Se crea la coleccion "proyecto"
            proyecto = db.getCollection("proyecto");

            // Se crea la coleccion "requisito"
            requisito = db.getCollection("requisito");

            // Se crea la coleccion "participante"
            participante = db.getCollection("participante");

            System.out.println("Connecting to MongoDB@" + mongo.getAllAddress());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /* Se crea un documento (BasicDBObject) con un nombre y descripcion y se inserta
     en la coleccion proyecto.
     */
    public JSONObject crearProyecto(JSONObject proy) {

        BasicDBObject doc = new BasicDBObject("nombre", proy.get("nombre"))
                .append("descripcion", proy.get("descripcion"));

        proyecto.insert(doc);
        return formatearJSON(doc);
    }

    /* 
     Funcion utilizada para asegurar la unicidad del nombre del proyecto
     Retorna true si ya existe un proyecto con el nombre dado
     */
    public Boolean buscarNombreRepetido(String nombreProy) {
        BasicDBObject query = new BasicDBObject("nombre", new BasicDBObject("$eq", nombreProy));
        DBCursor cursor = proyecto.find(query);

        return (cursor.count() == 0);

    }

    // Esta funcion le da un formato distinto al _id del objeto
    // atrapado por el driver de Java y ademas convierte en JSON
    // el resultado.
    public JSONObject formatearJSON(DBObject obj) {
        JSONObject doc = new JSONObject(JSON.serialize(obj));
        String clean_idPart = (doc.getJSONObject("_id").get("$oid")).toString();
        doc.put("_id", clean_idPart);

        return doc;
    }

    // Obtiene el proyecto utilizando el _id.
    public JSONObject obtenerProyecto(String projectId) {

        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(projectId));

        DBObject doc = proyecto.findOne(query);

        if (doc == null) {
            JSONObject result = new JSONObject();
            result.put("error", "INVALID_ID");
            return result;
        }

        return formatearJSON(doc);

    }

    // Se asocia un email a la lista de participantes
    // Por ahora sin chequear si el usuario existe.
    public JSONObject asociarParticipante(String email, String projectId) {

        BasicDBList lista;
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(projectId));

        DBObject proy = proyecto.findOne(query);

        // Se revisa si se encontro el proyecto.
        if (proy == null) {
            JSONObject result = new JSONObject();
            result.put("error", "INVALID_ID");
            return result;
        }

        // Se revisa si el proyecto ya tiene participantes.
        if (proy.containsField("participantes")) {
            lista = (BasicDBList) proy.get("participantes");

        } else {
            lista = new BasicDBList();

        }

        // Se revisa si el proyecto ya tiene a ese participante.
        if (!lista.contains(email)) {
            lista.add(email);
            proy.put("participantes", lista);
            proyecto.save(proy);
        }

        return formatearJSON(proy);

    }


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

}
