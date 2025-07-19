/*
|-----------------------------------------------|
| © 2025 EPN-FIS, Todos los derechos reservados |
| GR1SW                                         |
|-----------------------------------------------|
Autores: Samira Arízaga, Paul Dávila, Sebastián Ramos
Descripción: Clase de lógica de negocio (BL) que gestiona operaciones sobre canciones dentro del sistema InkHarmony.
*/

package BusinessLogic;

import DataAccessComponent.DAO.CancionDAO;
import DataAccessComponent.DTO.CancionDTO;

import java.util.List;

/**
 * Clase CancionBL que representa la lógica de negocio relacionada con canciones.
 *
 * <p>Actúa como intermediaria entre la capa de presentación y la capa de acceso a datos (DAO),
 * proporcionando métodos para registrar, consultar, actualizar y eliminar canciones,
 * así como buscar por nombre.</p>
 *
 * <p>Esta clase no contiene lógica de acceso a la base de datos directamente,
 * sino que delega dicha responsabilidad al CancionDAO.</p>
 *
 * @author Grupo A
 * @version 1.0
 * @since 19-07-2025
 */
public class Cancion {

    /** Objeto temporal para almacenar la canción actual manipulada */
    private CancionDTO cancion;

    /** Objeto DAO que se encarga del acceso a la base de datos */
    private CancionDAO cancionDAO = new CancionDAO();

    /**
     * Constructor vacío.
     */
    public Cancion() {}

    /**
     * Recupera todas las canciones registradas en la base de datos.
     *
     * @return Lista de objetos CancionDTO con todos los registros.
     * @throws Exception si ocurre algún error en el acceso a datos.
     */
    public List<CancionDTO> buscarTodo() throws Exception {
        return cancionDAO.buscarTodo();
    }

    /**
     * Recupera una canción específica por su identificador.
     *
     * @param idCancion ID de la canción a consultar.
     * @return Objeto CancionDTO con los datos completos.
     * @throws Exception si ocurre algún error.
     */
    public CancionDTO buscarPorId(int idCancion) throws Exception {
        cancion = cancionDAO.buscarPorId(idCancion);
        return cancion;
    }

    /**
     * Registra una nueva canción en la base de datos.
     *
     * @param cancionDTO Objeto con los datos de la canción a insertar.
     * @return true si se registró correctamente.
     * @throws Exception si ocurre algún error.
     */
    public boolean registrar(CancionDTO cancionDTO) throws Exception {
        return cancionDAO.registrar(cancionDTO);
    }

    /**
     * Actualiza los datos de una canción ya existente.
     *
     * @param cancionDTO Objeto con los nuevos datos.
     * @return true si se actualizó correctamente.
     * @throws Exception si ocurre algún error.
     */
    public boolean actualizar(CancionDTO cancionDTO) throws Exception {
        return cancionDAO.actualizar(cancionDTO);
    }

    /**
     * Elimina una canción según su ID.
     *
     * @param idCancion ID de la canción a eliminar.
     * @return true si se eliminó correctamente.
     * @throws Exception si ocurre algún error.
     */
    public boolean eliminar(int idCancion) throws Exception {
        return cancionDAO.eliminar(idCancion);
    }

    /**
     * Busca canciones por coincidencia exacta del título.
     *
     * @param titulo Nombre de la canción a buscar.
     * @return Lista de canciones con ese título.
     * @throws Exception si ocurre algún error.
     */
    public List<CancionDTO> buscarPorTitulo(String titulo) throws Exception {
        return cancionDAO.buscarPorNombre(titulo);
    }
}
