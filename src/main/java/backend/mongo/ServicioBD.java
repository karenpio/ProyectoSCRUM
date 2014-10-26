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
    DBCollection carrera;
    DBCollection tarea;

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
            
            // Se crea la coleccion "carrera"
            carrera = db.getCollection("carrera");
            
            // Se crea la coleccion "tarea"
            tarea = db.getCollection("tarea");

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
    
    /*
        Obtener un requisito dado su id 
    */
    public JSONObject obtenerRequisito(String reqId) {

        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(reqId));

        DBObject doc = requisito.findOne(query);

        if (doc == null) {
            JSONObject result = new JSONObject();
            result.put("error", "INVALID_ID");
            return result;
        }

        return formatearJSON(doc);

    }
    
    /*
        Obtener una carrera dado su id 
    */
    public JSONObject obtenerCarrera(String carrId) {

        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(carrId));

        DBObject doc = carrera.findOne(query);

        if (doc == null) {
            JSONObject result = new JSONObject();
            result.put("error", "INVALID_ID");
            return result;
        }

        return formatearJSON(doc);

    }
    
    /*
        Obtener una tarea dado su id 
    */
    public JSONObject obtenerTarea(String tareaId) {

        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(tareaId));

        DBObject doc = tarea.findOne(query);

        if (doc == null) {
            JSONObject result = new JSONObject();
            result.put("error", "INVALID_ID");
            return result;
        }

        return formatearJSON(doc);

    }
    
    /*
        Dado el id de un proyecto, buscamos dicho proyecto, obtenemos
        su lista de requisitos y formamos una lista con los nombres de
        esos requisitos
    */
    public JSONArray listarRequisitosProy(String proyectoId){
        
        JSONObject proy = obtenerProyecto(proyectoId);
        
        
        JSONArray listaReqId = (JSONArray) proy.get("requisitos");
        String idReq;
        BasicDBList nombresReq = new BasicDBList();

        for (int i = 0; i < listaReqId.length(); i++) {
            idReq = listaReqId.getJSONObject(i).get("$oid").toString();
            nombresReq.add(obtenerRequisito(idReq));
        }
        JSONArray resultado = new JSONArray(nombresReq);
        return resultado;
        
    }
    
    /*
        Dado el id de un proyecto, buscamos dicho proyecto, obtenemos
        su lista de carreras y formamos una lista con los numeros de
        esas carreras
    */
    public JSONArray listarCarrerasProy(String proyectoId){
        
        JSONObject proy = obtenerProyecto(proyectoId);
        
        
        JSONArray listaCarrId = (JSONArray) proy.get("carreras");
        System.out.println(listaCarrId);
        String idCarr;
        BasicDBList numeroCarrera = new BasicDBList();

        for (int i = 0; i < listaCarrId.length(); i++) {
            System.out.println(listaCarrId.getJSONObject(i).get("$oid"));
            idCarr = listaCarrId.getJSONObject(i).get("$oid").toString();
            numeroCarrera.add(obtenerCarrera(idCarr));
        }
        JSONArray resultado = new JSONArray(numeroCarrera);
        return resultado;
        
    }
    
    /*
        Dado el id de una carrera, buscamos dicha carrera, obtenemos
        su lista de requisitos y formamos una lista con los nombres de
        esos requisitos
    */
    public JSONArray listarRequisitosCarrera(String carreraId){
        
        JSONObject carr = obtenerCarrera(carreraId);

        JSONArray listaReqId = (JSONArray) carr.get("requisitos");
        String idReq;
        BasicDBList nombresReq = new BasicDBList();

        for (int i = 0; i < listaReqId.length(); i++) {
            idReq = listaReqId.getJSONObject(i).get("$oid").toString();
            nombresReq.add(obtenerRequisito(idReq));
        }
        JSONArray resultado = new JSONArray(nombresReq);
        return resultado;
        
    }
    
    /*
        Dado el id de una carrera, buscamos dicha carrera, obtenemos
        su lista de tareas y formamos una lista de json con dichas tareas
    */
    public JSONArray listarTareasCarrera(String carreraId){
        
        JSONObject carr = obtenerCarrera(carreraId);

        JSONArray listaTareasId = (JSONArray) carr.get("tareas");
        String idTarea;
        BasicDBList listaTarea = new BasicDBList();

        for (int i = 0; i < listaTareasId.length(); i++) {
            idTarea = listaTareasId.getJSONObject(i).get("$oid").toString();
            listaTarea.add(obtenerTarea(idTarea));
        }
        JSONArray resultado = new JSONArray(listaTarea);
        return resultado;
        
    }
    
    
    /*
        Asociamos Carrera a un proyecto para poder hacer puebas
    */
    public JSONObject asociarCarrera(String projectId, String carrId){
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

        // Se revisa si el proyecto ya tiene carreras.
        if (proy.containsField("carreras")) {
            lista = (BasicDBList) proy.get("carreras");

        } else {
            lista = new BasicDBList();

        }

        // Se revisa si el proyecto ya tiene a ese requisito.
        ObjectId idCarrera = new ObjectId(carrId);
        if (!lista.contains(idCarrera)) {
            lista.add(idCarrera);
            proy.put("carreras", lista);
            proyecto.save(proy);
        }
        
        return formatearJSON(proy);
    }
    
    /*
        Asociar un requisito dado su ID a un proyecto. En proyecto se tiene una
        lista de requisitos en la que adentro se tienen los objectId de todos
        los requisitos asociados. 
    */
    public JSONObject asociarRequisito(String projectId, String reqId){
        BasicDBList lista;
        
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(projectId));
        


        DBObject proy = proyecto.findOne(query);

        
        
        // Se revisa si se encontro el proyecto. Falta revisar los requisitos
        if (proy == null) {
            JSONObject result = new JSONObject();
            result.put("error", "INVALID_ID");
            return result;
        }


        // Se revisa si el proyecto ya tiene carreras.
        if (proy.containsField("requisitos")) {
            lista = (BasicDBList) proy.get("requisitos");

        } else {
            lista = new BasicDBList();

        }

        // Se revisa si el proyecto ya tiene a ese requisito.
        ObjectId idReq = new ObjectId(reqId);
        if (!lista.contains(idReq)) {
            lista.add(idReq);
            proy.put("requisitos", lista);
            proyecto.save(proy);
        }
        
        return formatearJSON(proy);
    }
    
    /*
        Asociar un requisito a una carrera dado su ID. En carrera se tiene una
        lista de requisitos en la que adentro se tienen los objectId de todos
        los requisitos asociados. 
    */
    public JSONObject asociarRequisitoCarrera(String carreraId, String reqId){
        BasicDBList lista;
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(carreraId));
        
        System.out.println(query);


        DBObject carr = carrera.findOne(query);

        // Se revisa si se encontro la carrera. Falta revisar el requisito
        if (carr == null) {
            JSONObject result = new JSONObject();
            result.put("error", "INVALID_ID");
            return result;
        }


        // Se revisa si la carrera ya tiene requisitos asociados.
        if (carr.containsField("requisitos")) {
            lista = (BasicDBList) carr.get("requisitos");

        } else {
            lista = new BasicDBList();

        }

        // Se revisa si la carrera ya tiene a ese requisito.
        ObjectId idReq = new ObjectId(reqId);
        if (!lista.contains(idReq)) {
            lista.add(idReq);
            carr.put("requisitos", lista);
            carrera.save(carr);
        }
        
        return formatearJSON(carr);
    }
    
    
    /*
        Asociar una tarea a una carrera dado su ID. En carrera se tiene una
        lista de tareas en la que adentro se tienen los objectId de todas
        las tareas asociadas. 
    */
    public JSONObject asociarTareaCarrera(String carreraId, String tareaId){
        BasicDBList lista;
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(carreraId));
        
        DBObject carr = carrera.findOne(query);

        // Se revisa si se encontro la carrera. Falta revisar el requisito
        if (carr == null) {
            JSONObject result = new JSONObject();
            result.put("error", "INVALID_ID");
            return result;
        }


        // Se revisa si la carrera ya tiene requisitos asociados.
        if (carr.containsField("tareas")) {
            lista = (BasicDBList) carr.get("tareas");

        } else {
            lista = new BasicDBList();

        }

        // Se revisa si la carrera ya tiene a ese requisito.
        ObjectId idTarea = new ObjectId(tareaId);
        if (!lista.contains(idTarea)) {
            lista.add(idTarea);
            carr.put("tareas", lista);
            carrera.save(carr);
        }
        
        return formatearJSON(carr);
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

    // Se desasocia un email a la lista de participantes
    public JSONObject desasociarParticipante(String email, String projectId) {

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
            
            if (lista.contains(email)) {
                lista.remove(email);
                proy.put("participantes", lista);
                proyecto.save(proy);
            }

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
    
    /* 
        Obtener la identificacion de las carreras asociadas a un proyecto
    */
    public JSONObject obtenerIdCarreras(JSONObject proy){
        
        BasicDBObject query = new BasicDBObject();
        query.put("nombre", proy.get("nombre"));
        DBCursor cursor = proyecto.find(query);
        
        
        JSONObject resultados = new JSONObject();
        try {
            while(cursor.hasNext()){
                DBObject c = cursor.next();
                BasicDBList carreras = (BasicDBList) c.get("carreras");
                resultados.put("_id",carreras);         
            } 
        } finally {
            cursor.close();
        }
        return resultados;
    }

}
