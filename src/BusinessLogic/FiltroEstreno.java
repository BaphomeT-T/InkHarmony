package BusinessLogic;

import DataAccessComponent.DTO.CancionDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filtro que limita las canciones recomendadas a aquellas registradas recientemente,
 * dentro de un período definido como "estreno".
 *
 * <p>Por defecto, solo se consideran estrenos las canciones registradas en los últimos 7 días.
 * Este filtro se aplica como parte de una cadena de recomendadores, permitiendo combinarlo
 * con otros criterios.</p>
 *
 * @author Grupo F - InkHarmony Team
 */
public class FiltroEstreno extends FiltroRecomendador {

    /** Número de días considerado para que una canción sea un estreno. */
    private final int diasEstreno = 7;

    /**
     * Crea un filtro de estrenos que se encadena al recomendador siguiente.
     *
     * @param siguiente el recomendador al que se delega si este filtro no aplica
     */
    public FiltroEstreno(Recomendador siguiente) {
        super(siguiente);
    }

    /**
     * Devuelve una lista de canciones que hayan sido registradas
     * dentro del periodo de estrenos (últimos 7 días).
     *
     * @return lista filtrada de canciones recientes
     */
    @Override
    public List<CancionDTO> recomendar() {
        LocalDateTime limite = LocalDateTime.now().minusDays(diasEstreno);
        return siguiente.recomendar().stream()
                .filter(c -> c.getFechaRegistro() != null &&
                             c.getFechaRegistro().isAfter(limite))
                .collect(Collectors.toList());
    }
}
