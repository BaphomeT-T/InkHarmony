package BusinessLogic;

import DataAccessComponent.DTO.CancionDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filtro decorador que limita las recomendaciones a canciones añadidas recientemente al catálogo.
 * <p>
 * Se considera una canción como “estreno” si su fecha de registro está dentro de los últimos {@code diasEstreno} días.
 * Este filtro puede combinarse con otros para formar cadenas de recomendación más complejas.
 * </p>
 *
 * @author Grupo F - InkHarmony Team
 * @version 1.0
 * @since 1.0
 */
public class FiltroEstreno extends FiltroRecomendador<CancionDTO> {
    
    /**
     * Número de días desde hoy para considerar una canción como estreno.
     */
    private int diasEstreno = 7;

    /**
     * Crea una instancia del filtro de estrenos que decorará a otro componente recomendador.
     *
     * @param siguiente Componente a decorar (puede ser otro filtro o un recomendador base)
     */
    public FiltroEstreno(Recomendador<CancionDTO> siguiente) {
        super(siguiente);
    }

    /**
     * Retorna únicamente las canciones que fueron registradas dentro del período de estrenos definido.
     *
     * @return Lista de canciones recientes filtradas desde el componente decorado
     */
    @Override
    public List<CancionDTO> recomendar() {
        LocalDateTime limite = LocalDateTime.now().minusDays(diasEstreno);
        return siguiente.recomendar().stream()
                .filter(c -> c.getFechaRegistro() != null && c.getFechaRegistro().isAfter(limite))
                .collect(Collectors.toList());
    }
}