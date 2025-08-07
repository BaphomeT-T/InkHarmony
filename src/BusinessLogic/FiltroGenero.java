package BusinessLogic;

import DataAccessComponent.DTO.CancionDTO;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filtro que limita las canciones recomendadas a aquellas que pertenezcan
 * a un género musical específico.
 *
 * <p>Este filtro forma parte de una cadena de recomendadores, y se puede combinar
 * con otros criterios de filtrado.</p>
 *
 * @author Grupo F - InkHarmony Team
 */
public class FiltroGenero extends FiltroRecomendador {

    /** Género musical por el cual se desea filtrar. */
    private final Genero genero;

    /**
     * Crea un filtro de género que se encadena al recomendador siguiente.
     *
     * @param siguiente el recomendador al que se delega si este filtro no aplica
     * @param genero el género musical que se desea utilizar como criterio de filtrado
     */
    public FiltroGenero(Recomendador siguiente, Genero genero) {
        super(siguiente);
        this.genero = genero;
    }

    /**
     * Devuelve una lista de canciones que pertenecen al género indicado.
     *
     * @return lista filtrada de canciones según el género
     */
    @Override
    public List<CancionDTO> recomendar() {
        return siguiente.recomendar().stream()
                .filter(c -> c.getGeneros() != null && c.getGeneros().contains(genero))
                .collect(Collectors.toList());
    }
}
