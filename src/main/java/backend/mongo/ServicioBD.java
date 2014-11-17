package backend.mongo;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

        BasicDBObject doc
                = new BasicDBObject("nombre", proy.get("nombre"))
                .append("descripcion", proy.get("descripcion"));

        proyecto.insert(doc);
        return formatearJSON(doc);
    }

    /* Se crea un documento de tarea (BasicDBObject) con sus atributos y se inserta
     en la coleccion tarea.
     */
    public JSONObject crearTarea(JSONObject tar) {

        BasicDBObject doc = new BasicDBObject("nombre", tar.get("nombre"))
                .append("peso", tar.get("peso"))
                .append("estado", tar.get("estado"))
                .append("fechaFin", tar.get("fechaFin"));

        tarea.insert(doc);
        return formatearJSON(doc);
    }

    /*
     Se borra de la bd el documento correspondiente a la tarea que se
     quiere eliminar.
     */
    public JSONObject eliminarTarea(String tareaId) {

        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(tareaId));

        DBObject doc = tarea.findOne(query);
        tarea.remove(doc);

        return formatearJSON(doc);
    }

    /*
     Se actualizan todos los parametros de una tarea a excepcion del nombre
     */
    public JSONObject actualizarTarea(JSONObject tar) {
        BasicDBObject nuevoDoc = new BasicDBObject();
        nuevoDoc.append("$set", new BasicDBObject().append("peso", tar.get("peso"))
                .append("estado", tar.get("estado"))
                .append("fechaFin", tar.get("fechaFin")));

        System.out.println(nuevoDoc);

        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(tar.get("_id").toString()));

        tarea.update(query, nuevoDoc);
        DBObject doc = tarea.findOne(query);

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

    public JSONArray obtenerParticipantesProyecto(String proyectoId) {
        JSONObject proy = obtenerProyecto(proyectoId);
        if (!proy.has("participantes")) {
            return new JSONArray();
        }
        JSONArray listaPartProy = proy.getJSONArray("participantes");

        return listaPartProy;
    }

    /*
     Dado el id de un proyecto, buscamos dicho proyecto, obtenemos
     su lista de requisitos y formamos una lista con los nombres de
     esos requisitos
     */
    public JSONArray listarRequisitosProy(String proyectoId) {

        JSONObject proy = obtenerProyecto(proyectoId);
        if (!proy.has("requisitos")) {
            return new JSONArray();
        }

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
        Dado el id de un proyecto, obtenemos aquellos requisitos que esten
        disponibles, esto es, que no esten asignados a ninguna carrera
    */
    public JSONArray listarRequisitosDisponiblesProyecto(String proyectoId) {
        
        // En esta lista se colocaran los id de requisitos no disponibles
        ArrayList<String> requisitosNoDisponibles = new ArrayList<>();

        // Obtenemos todos los requisitos del proyecto
        JSONArray requisitosProyecto = listarRequisitosProy(proyectoId);
        ArrayList<String> listaRequisitosString = new ArrayList<String>();

        /* Convertimos el JSONArray de requisitos de proyecto a un ArrayList 
          para poder operar sobre el
         */  
        if (requisitosProyecto != null) {
            for (int i = 0; i < requisitosProyecto.length(); i++) {
                listaRequisitosString.add(requisitosProyecto.getJSONObject(i).get("_id").toString());
            }
        }
        
        // Si el proyecto no tiene requisitos devolvemos un arreglo de JSON vacio
        JSONObject proy = obtenerProyecto(proyectoId);
        if (!proy.has("requisitos")) {
            return new JSONArray();
        }

        // Si el proyecto no tiene carreras asociadas devuelve todos los requisitos
        if (!proy.has("carreras")) {
            return requisitosProyecto;
        }

        JSONArray listaCarreras = proy.getJSONArray("carreras");
        

        // Iterar para obtener una lista con los requisitos no disponibles
        for (int i = 0; i < listaCarreras.length(); i++) {
            String carrId = listaCarreras.getJSONObject(i).get("$oid").toString();
            JSONObject carreraSeleccionada =  obtenerCarrera(carrId);

            JSONArray listaRequisitosCarrera = carreraSeleccionada.getJSONArray("requisitos");

            for (int j = 0; j < listaRequisitosCarrera.length(); j++) {
                requisitosNoDisponibles.add(listaRequisitosCarrera.getJSONObject(j).get("$oid").toString());
            }
        }

        
        // Eliminamos los requisitos no disponibles de la lista de disponibles 
        listaRequisitosString.removeAll(requisitosNoDisponibles);

        JSONArray requisitosDisponibles = new JSONArray(Arrays.asList(listaRequisitosString));

        return requisitosDisponibles;

    }

    /*
     Dado el id de un proyecto, buscamos dicho proyecto, obtenemos
     su lista de carreras y formamos una lista con los numeros de
     esas carreras
     */
    public JSONArray listarCarrerasProy(String proyectoId) {

        JSONObject proy = obtenerProyecto(proyectoId);

        if (!proy.has("carreras")) {
            return new JSONArray();
        }

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
    public JSONArray listarRequisitosCarrera(String carreraId) {

        JSONObject carr = obtenerCarrera(carreraId);

        if (!carr.has("requisitos")) {
            return new JSONArray();
        }

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
     su lista de tareas completadas y formamos una lista de json 
     con dichas tareas
     */
    public JSONArray listarTareasCompletadasCarrera(String carreraId) {

        JSONObject carr = obtenerCarrera(carreraId);

        if (!carr.has("tareas")) {
            return new JSONArray();
        }

        JSONArray listaTareasId = (JSONArray) carr.get("tareas");
        String idTarea;
        BasicDBList listaTarea = new BasicDBList();

        BasicDBObject query = new BasicDBObject();

        for (int i = 0; i < listaTareasId.length(); i++) {
            idTarea = listaTareasId.getJSONObject(i).get("$oid").toString();
            query.put("_id", new ObjectId(idTarea));
            DBObject tareaSeleccionada = tarea.findOne(query);

            if ("Completada".equals(tareaSeleccionada.get("estado").toString())) {
                listaTarea.add(obtenerTarea(idTarea));
            }
        }

        JSONArray resultado = new JSONArray(listaTarea);
        return resultado;

    }

    public JSONArray listarTareasCarrera(String carreraId) {

        JSONObject carr = obtenerCarrera(carreraId);

        if (!carr.has("tareas")) {
            return new JSONArray();
        }

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
    public JSONObject asociarCarrera(String projectId, String carrId) {
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
    public JSONObject asociarRequisito(String projectId, String reqId) {
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
    public JSONObject asociarRequisitoCarrera(String carreraId, String reqId) {
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
     Desasociar un requisito a una carrera dado su ID. En carrera se tiene una
     lista de requisitos en la que adentro se tienen los objectId de todos
     los requisitos asociados. 
     */
    public JSONObject desasociarRequisitoCarrera(String carreraId, String reqId) {
        BasicDBList lista;
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(carreraId));

        System.out.println(query);

        DBObject carr = carrera.findOne(query);

        // Se revisa si se encontro la carrera.
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

        // Se revisa si la carrera tiene a ese requisito.
        ObjectId idReq = new ObjectId(reqId);
        if (lista.contains(idReq)) {
            lista.remove(idReq);
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
    public JSONObject asociarTareaCarrera(String carreraId, String tareaId) {
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

    public JSONObject desasociarTareaCarrera(String carreraId, String tareaId) {
        BasicDBList lista;
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(carreraId));

        DBObject carr = carrera.findOne(query);

        // Se revisa si se encontro la carrera
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

        // Se revisa si la carrera tiene a ese requisito para eliminarlo.
        ObjectId idTarea = new ObjectId(tareaId);
        if (lista.contains(idTarea)) {
            lista.remove(idTarea);
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
                JSONObject doc = new JSONObject();
                JSONObject consultado = new JSONObject(JSON.serialize(cursor.next()));
                String clean_id = (consultado.getJSONObject("_id").get("$oid")).toString();
                doc.put("_id", clean_id);
                doc.put("descripcion", consultado.getString("descripcion"));
                doc.put("nombre", consultado.getString("nombre"));
                if (consultado.has("participantes")) {
                    doc.put("participantes", consultado.getJSONArray("participantes"));
                }

                resultados.put(doc);
            }
        } finally {
            cursor.close();
        }
        return resultados;
    }

    // Coloca en una lista todos los participantes que estan en la BD
    @SuppressWarnings("unchecked")
    public JSONArray listarParticipantes() {
        DBCursor cursor = participante.find();
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
