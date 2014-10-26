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
         Dado un id de proyecto y de requisito, se asocia dicho requisito
         al proyecto, es decir, se anade a la lista de requisitos dentro
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
                return carrera;

            }
        });
        
        /*
         Dado un id de una carrera y de una tarea, se asocia dicha tarea
         a la carrera, es decir, se anade a la lista de tareas dentro
         de la carrera.
        */
        put(new Route("/asociartareacarrera") {
            @Override
            public Object handle(Request request, Response response) {
                String idCarr = request.queryParams("carreraId");
                String idTarea = request.queryParams("tareaId");

                JSONObject carrera = servicioBDProyecto.asociarTareaCarrera(idCarr, idTarea);
                if (carrera.has("error")) {
                    response.status(404);
                    return carrera;
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
       for (int i = 0; i < lista.length(); i++){
           aux = (JSONObject) lista.get(i);
           result.put(aux.get("$oid"));
       }
       return result;
   }

}
