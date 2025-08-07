package DataAccessComponent.DAO;
import java.util.List;
/**
 * Interfaz genérica que define las operaciones básicas de acceso a datos (CRUD).
 * 
 * <p>Esta interfaz proporciona un contrato común para todas las clases DAO (Data Access Object)
 * del sistema, definiendo las operaciones fundamentales de persistencia de datos:
 * crear, leer, actualizar y eliminar entidades.</p>
 * 
 * <p>La interfaz utiliza genéricos para permitir que cada implementación trabaje
 * con su tipo específico de entidad, manteniendo la flexibilidad y reutilización del código.</p>
 * 
 * @param <T> El tipo de entidad que manejará el DAO
 * @author Grupo C
 * @version 1.0
 * @since 31-07-2025
 */
public interface IDAO<T> {
    /**
     * Registra una nueva entidad en la base de datos.
     * 
     * <p>Esta operación corresponde al "Create" del patrón CRUD. La entidad
     * debe contener todos los datos necesarios para su persistencia, excepto
     * el ID que será generado automáticamente por la base de datos.</p>
     * 
     * @param entity La entidad a registrar en la base de datos
     * @return true si la operación fue exitosa, false en caso contrario
     * @throws Exception Si ocurre un error durante la operación de base de datos
     *                   o si los datos de la entidad son inválidos
     */
    public boolean registrar (T entity) throws Exception;
    /**
     * Recupera todas las entidades del tipo especificado desde la base de datos.
     * 
     * <p>Esta operación corresponde al "Read" del patrón CRUD para múltiples registros.
     * Retorna una lista con todas las entidades encontradas, ordenadas según
     * la implementación específica del DAO.</p>
     * 
     * @return Lista de todas las entidades encontradas en la base de datos
     * @throws Exception Si ocurre un error durante la consulta a la base de datos
     */
    public List<T> buscarTodo() throws Exception;
    /**
     * Busca una entidad específica por su identificador único.
     * 
     * <p>Esta operación corresponde al "Read" del patrón CRUD para un registro específico.
     * Busca la entidad que coincida con el ID proporcionado y retorna la primera
     * coincidencia encontrada.</p>
     * 
     * @param id El identificador único de la entidad a buscar
     * @return La entidad encontrada, o null si no existe una entidad con ese ID
     * @throws Exception Si ocurre un error durante la consulta a la base de datos
     */
    public T buscarPorId(Integer id) throws Exception;
    /**
     * Actualiza una entidad existente en la base de datos.
     * 
     * <p>Esta operación corresponde al "Update" del patrón CRUD. La entidad
     * debe contener un ID válido y los datos actualizados que se desean
     * persistir en la base de datos.</p>
     * 
     * @param entity La entidad con los datos actualizados a persistir
     * @return true si la operación fue exitosa, false en caso contrario
     * @throws Exception Si ocurre un error durante la operación de base de datos
     *                   o si la entidad no existe o es inválida
     */
    public boolean actualizar(T entity) throws Exception;
    /**
     * Elimina una entidad de la base de datos por su identificador único.
     * 
     * <p>Esta operación corresponde al "Delete" del patrón CRUD. Elimina
     * permanentemente la entidad que coincida con el ID proporcionado.
     * Esta operación no se puede deshacer.</p>
     * 
     * @param id El identificador único de la entidad a eliminar
     * @return true si la operación fue exitosa, false en caso contrario
     * @throws Exception Si ocurre un error durante la operación de base de datos
     *                   o si la entidad no existe
     */
    public boolean eliminar(Integer id) throws Exception;

}

