package BusinessLogic;

import DataAccessComponent.DTO.CancionDTO;
import java.util.List;

/**
 * Interfaz que define el comportamiento de un recomendador de canciones.
 *
 * <p>Permite obtener una lista de canciones recomendadas, ya sea de forma directa
 * o a través de filtros encadenados que aplican criterios específicos.</p>
 *
 * @author Grupo F - InkHarmony Team
 */
public interface Recomendador {

    /**
     * Genera una lista de canciones recomendadas según una lógica determinada.
     *
     * @return lista de canciones recomendadas
     */
    List<CancionDTO> recomendar();
}
