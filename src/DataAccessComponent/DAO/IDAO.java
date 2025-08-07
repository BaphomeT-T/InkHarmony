package DataAccessComponent.DAO;
import java.util.List;


public interface IDAO<T> {
    public boolean registrar (T entity) throws Exception ;
    public List<T> buscarTodo() throws Exception;
    public T buscarPorId(Integer id) throws Exception;
    public boolean actualizar(T entity) throws Exception;
    public boolean eliminar(Integer id) throws Exception;

}
