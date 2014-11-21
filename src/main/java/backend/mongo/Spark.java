package backend.mongo;

import java.net.UnknownHostException;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;
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
                if (proyecto.has("requisitos")) {
                    JSONArray auxList = proyecto.getJSONArray("requisitos");
                    proyecto.put("requisitos", limpiarListaId(auxList));
                }
                if (proyecto.has("carreras")) {
                    JSONArray auxList = proyecto.getJSONArray("carreras");
                    proyecto.put("carreras", limpiarListaId(auxList));
                }

                return proyecto;
            }
        });

        /*
         Dado un id de proyecto pasado por url, buscamos todos los requisitos
         que estan asociados a el y devolvemos una lista de json de requisitos
         */
        get(new Route("/listarrequisitosproyecto/:idProyecto") {
            @Override
            public Object handle(Request request, Response response) {
                String idProy = request.params(":idProyecto");

                JSONArray listaReq = servicioBDProyecto.listarRequisitosProy(idProy);

                return listaReq;
            }
        });
        
        
        /*
            Dado el id de un proyecto, devuelve todos sus requisitos disponibles,
        esto es, que no hayan sido agregados a ninguna carrera
        */
        get(new Route("/listarrequisitosdisponibles/:idProyecto") {
            @Override
            public Object handle(Request request, Response response) {
                String idProy = request.params(":idProyecto");
                
                JSONArray listaReqDisponibles = servicioBDProyecto.listarRequisitosDisponiblesProyecto(idProy); 

                return listaReqDisponibles;
            }

        });
        

        /*
         Dado un id de proyecto pasado por url, buscamos todas las carreras
         que estan asociados a el y devolvemos una lista de json de carreras
         */
        get(new Route("/listarcarrerasproyecto/:idProyecto") {
            @Override
            public Object handle(Request request, Response response) {
                String idProy = request.params(":idProyecto");

                JSONArray listaCarr = servicioBDProyecto.listarCarrerasProy(idProy);

                return listaCarr;
            }
        });

        /*
         Dado un id de carrera pasado por url, buscamos todos los requisitos
         que estan asociados a ella y devolvemos una lista de json de requisitos
         */
        get(new Route("/listarrequisitoscarrera/:idCarrera") {
            @Override
            public Object handle(Request request, Response response) {
                String idCarr = request.params(":idCarrera");

                JSONArray listaReq = servicioBDProyecto.listarRequisitosCarrera(idCarr);

                return listaReq;
            }
        });
        
        

        /*
         Dado un id de carrera pasado por url, buscamos todos las tareas
         que estan asociados a ella y devolvemos una lista de json de tareas
         */
        get(new Route("/listartareascarrera/:idCarrera") {
            @Override
            public Object handle(Request request, Response response) {
                String idCarr = request.params(":idCarrera");

                JSONArray listaTareas = servicioBDProyecto.listarTareasCarrera(idCarr);

                return listaTareas;
            }
        });
        

        /*
         Dado un id de carrera, buscamos todos las tareas que estan asociados a ella 
         cuyo estado sea "Completada" y devolvemos una lista de json de tareas
         */
        get(new Route("/listartareascompletadascarrera/:idCarrera") {

            @Override
            public Object handle(Request request, Response response) {
                String idCarr = request.params(":idCarrera");


                JSONArray listaTareas = servicioBDProyecto.listarTareasCompletadasCarrera(idCarr);

                return listaTareas;
            }
        });

        /*
         Se busca la lista de participantes y se devuelve
         */
        get(new Route("/participantes") {
            @Override
            public Object handle(Request request, Response response) {

                JSONArray participantes
                        = servicioBDProyecto.listarParticipantes();

                return participantes;
            }
        });

        get(new Route("/getParticipantesDisponibles/:proyectoId") {
            @Override
            public Object handle(Request request, Response response) {
                String proyId = request.params(":proyectoId");
                JSONArray participantesProy
                        = servicioBDProyecto.obtenerParticipantesProyecto(proyId);

                JSONArray participantes = servicioBDProyecto.listarParticipantes();
                JSONArray participantesDisponibles = new JSONArray();
                boolean[] noDisp = new boolean[participantes.length()];

                for (int i = 0; i < participantesProy.length(); i++) {
                    String emailParticipanteNoDisponible = participantesProy.getString(i);
                    for (int j = 0; j < participantes.length(); j++) {
                        JSONObject participante = participantes.getJSONObject(j);
                        if (participante.getString("email").equals(emailParticipanteNoDisponible)) {
                            noDisp[j] = true;

                        }
                    }
                }

                for (int i = 0; i < participantes.length(); i++) {
                    JSONObject participante = participantes.getJSONObject(i);
                    if (!noDisp[i]) {
                        participantesDisponibles.put(participante);
                    }

                }

                return participantesDisponibles;
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
         Dado un id de proyecto y de carrera, se asocia dicha carrera
         al proyecto, es decir, se anade a la lista de carreras dentro
         del proyecto. 
         */
        put(new Route("/asociarcarrera") {
            @Override
            public Object handle(Request request, Response response) {
                String idProy = request.queryParams("proyectoId");
                String idCarr = request.queryParams("carreraId");

                JSONObject proyecto = servicioBDProyecto.asociarCarrera(idProy, idCarr);
                if (proyecto.has("error")) {
                    response.status(404);
                    return proyecto;
                }
                if (proyecto.has("requisitos")) {
                    JSONArray auxList = proyecto.getJSONArray("requisitos");
                    proyecto.put("requisitos", limpiarListaId(auxList));
                }
                if (proyecto.has("carreras")) {
                    JSONArray auxList = proyecto.getJSONArray("carreras");
                    proyecto.put("carreras", limpiarListaId(auxList));
                }
                return proyecto;

            }
        });

        /*
         Dado un id de proyecto y de requisito, se asocia dicho requisito
         al proyecto, es decir, se anade a la lista de requisitos dentro
         del proyecto.
         */
        put(new Route("/asociarrequisito") {
            @Override
            public Object handle(Request request, Response response) {
                String idProy = request.queryParams("proyectoId");
                String idReq = request.queryParams("requisitoId");

                JSONObject proyecto = servicioBDProyecto.asociarRequisito(idProy, idReq);
                if (proyecto.has("error")) {
                    response.status(404);
                    return proyecto;
                }
                if (proyecto.has("requisitos")) {
                    JSONArray auxList = proyecto.getJSONArray("requisitos");
                    proyecto.put("requisitos", limpiarListaId(auxList));
                }
                if (proyecto.has("carreras")) {
                    JSONArray auxList = proyecto.getJSONArray("carreras");
                    proyecto.put("carreras", limpiarListaId(auxList));
                }
                return proyecto;

            }
        });

        /*
         Dado un id de una carrera y de un requisito, se asocia dicho requisito
         a la carrera, es decir, se anade a la lista de requisitos dentro
         de la carrera.
         */
        put(new Route("/asociarrequisitocarrera") {
            @Override
            public Object handle(Request request, Response response) {
                String idCarr = request.queryParams("carreraId");
                String idReq = request.queryParams("requisitoId");

                JSONObject carrera = servicioBDProyecto.asociarRequisitoCarrera(idCarr, idReq);
                if (carrera.has("error")) {
                    response.status(404);
                    return carrera;
                }
                if (carrera.has("requisitos")) {
                    JSONArray auxList = carrera.getJSONArray("requisitos");
                    carrera.put("requisitos", limpiarListaId(auxList));
                }
                if (carrera.has("tareas")) {
                    JSONArray auxList = carrera.getJSONArray("tareas");
                    carrera.put("tareas", limpiarListaId(auxList));
                }
                return carrera;

            }
        });
        
        put(new Route("/desasociarrequisitocarrera") {
            @Override
            public Object handle(Request request, Response response) {
                String idCarr = request.queryParams("carreraId");
                String idReq = request.queryParams("requisitoId");

                JSONObject carrera = servicioBDProyecto.desasociarRequisitoCarrera(idCarr, idReq);
                if (carrera.has("error")) {
                    response.status(404);
                    return carrera;
                }
                if (carrera.has("requisitos")) {
                    JSONArray auxList = carrera.getJSONArray("requisitos");
                    carrera.put("requisitos", limpiarListaId(auxList));
                }
                if (carrera.has("tareas")) {
                    JSONArray auxList = carrera.getJSONArray("tareas");
                    carrera.put("tareas", limpiarListaId(auxList));
                }
                return carrera;

            }
        });


        /*
         Se crea una tarea a partir de los datos que son suministrados.
         Posteriormente se debe asociar tarea con carrera
         */
        post(new Route("/creartarea") {
            @Override
            public Object handle(Request request, Response response) {
                String nombre = request.queryParams("nombre");
                String peso = request.queryParams("peso");
                String estado = request.queryParams("estado");
                String fechaFin = request.queryParams("fechaFin");
                String idCarr = request.queryParams("carreraId");

                JSONObject doc;
                JSONObject tarea = new JSONObject();

                tarea.put("nombre", nombre);
                tarea.put("peso", peso);
                tarea.put("estado", estado);
                tarea.put("fechaFin", fechaFin);

                doc = servicioBDProyecto.crearTarea(tarea);
                JSONObject carrera
                        = servicioBDProyecto.asociarTareaCarrera(idCarr, doc.getString("_id"));

                return doc;
            }
        });
        
        /*
            Se actualiza una tarea a partir de los datos que se reciben sobre la misma.
            Se pueden actualizar todos los parametros menos el nombre
        */
        put(new Route("/actualizartarea") {
            @Override
            public Object handle(Request request, Response response) {
                String idTar = request.queryParams("tareaId");
                String peso = request.queryParams("peso");
                String estado = request.queryParams("estado");
                String fechaFin = request.queryParams("fechaFin");

                JSONObject doc;
                JSONObject tarea = new JSONObject();

                tarea.put("_id", idTar);
                tarea.put("peso", peso);
                tarea.put("estado", estado);
                tarea.put("fechaFin", fechaFin);

                doc = servicioBDProyecto.actualizarTarea(tarea);

                return doc;
            }
        });
        
        /*
            Desasociar una tarea de una carrera y eliminar la tarea de la bd, ya que
        la tarea sola no existe.
        */
        put(new Route("/desasociartareacarrera") {
            @Override
            public Object handle(Request request, Response response) {
                String idCarr = request.queryParams("carreraId");
                String idTar = request.queryParams("tareaId");

                JSONObject carrera = servicioBDProyecto.desasociarTareaCarrera(idCarr, idTar);
                
                JSONObject doc = servicioBDProyecto.eliminarTarea(idTar);
                if (carrera.has("error")) {
                    response.status(404);
                    return carrera;
                }
                if (carrera.has("requisitos")) {
                    JSONArray auxList = carrera.getJSONArray("requisitos");
                    carrera.put("requisitos", limpiarListaId(auxList));
                }
                if (carrera.has("tareas")) {
                    JSONArray auxList = carrera.getJSONArray("tareas");
                    carrera.put("tareas", limpiarListaId(auxList));
                }
                return carrera;

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
                if (proyecto.has("requisitos")) {
                    JSONArray auxList = proyecto.getJSONArray("requisitos");
                    proyecto.put("requisitos", limpiarListaId(auxList));
                }
                if (proyecto.has("carreras")) {
                    JSONArray auxList = proyecto.getJSONArray("carreras");
                    proyecto.put("carreras", limpiarListaId(auxList));
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
                if (proyecto.has("requisitos")) {
                    JSONArray auxList = proyecto.getJSONArray("requisitos");
                    proyecto.put("requisitos", limpiarListaId(auxList));
                }
                if (proyecto.has("carreras")) {
                    JSONArray auxList = proyecto.getJSONArray("carreras");
                    proyecto.put("carreras", limpiarListaId(auxList));
                }
                return proyecto;

            }
        });

    }

    public static JSONArray limpiarListaId(JSONArray lista) {
        JSONArray result = new JSONArray();
        JSONObject aux;
        for (int i = 0; i < lista.length(); i++) {
            aux = (JSONObject) lista.get(i);
            result.put(aux.get("$oid"));
        }
        return result;
    }

}
