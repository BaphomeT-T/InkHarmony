package BusinessLogic;

import DataAccessComponent.DAO.CancionDAO;
import DataAccessComponent.DTO.CancionDTO;
import java.util.List;

/**
 * Componente base del sistema de recomendaciones que recupera todas las canciones disponibles del catálogo.
 * <p>
 * Actúa como punto de inicio en la cadena de recomendación, devolviendo la lista completa de canciones sin aplicar filtros.
 * Esta lista puede ser modificada posteriormente por filtros decoradores que implementen {@link FiltroRecomendador}.
 * </p>
 *
 * @author Grupo F - InkHarmony Team
 * @version 1.0
 * @since 1.0
 */
public class RecomendadorCanciones implements Recomendador<CancionDTO> {
    
    /**
     * Acceso al repositorio de canciones desde el módulo de datos.
     */
    private final CancionDAO cancionDAO = new CancionDAO();

    /**
     * Retorna todas las canciones disponibles en el catálogo.
     * <p>
     * Esta implementación no aplica ningún criterio de filtrado; simplemente actúa como proveedor de datos base
     * para el sistema de recomendaciones.
     * </p>
     *
     * @return Lista completa de canciones disponibles o una lista vacía si ocurre un error
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
