package BusinessLogic;

import DataAccessComponent.DAO.CancionDAO;
import DataAccessComponent.DTO.CancionDTO;
import java.util.List;

/**
 * Implementación base del recomendador que proporciona todas las canciones disponibles.
 *
 * <p>Este recomendador actúa como punto de partida para la aplicación de filtros en cadena.
 * Accede directamente a la base de datos para obtener la lista completa de canciones.</p>
 *
 * @author Grupo F - InkHarmony Team
 */
public class RecomendadorCanciones implements Recomendador {

    /** Acceso a datos de canciones. */
    private final CancionDAO cancionDAO = new CancionDAO();

    /**
     * Obtendrá todas las canciones disponibles desde la base de datos.
     *
     * @return lista de canciones recuperadas o una lista vacía si ocurre un error
     */
    @Override
    public List<CancionDTO> recomendar() {
        try {
            return cancionDAO.buscarTodo();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
