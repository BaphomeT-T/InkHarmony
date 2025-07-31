package BusinessLogic;

import DataAccessComponent.DTO.CancionDTO;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filtro decorador que restringe las recomendaciones a canciones que pertenezcan a un género específico.
 * <p>
 * Este filtro puede combinarse con otros para ofrecer recomendaciones más personalizadas dentro del sistema de InkHarmony.
 * </p>
 *
 * @author Grupo F - InkHarmony Team
 * @version 1.0
 * @since 1.0
 */
public class FiltroGenero extends FiltroRecomendador<CancionDTO> {
    
    /**
     * Género musical sobre el cual se aplica el filtro.
     */
    private Genero genero;

    /**
     * Crea una instancia del filtro de género, especificando el género a filtrar y el componente decorado.
     *
     * @param siguiente Componente decorado (puede ser otro filtro o un recomendador base)
     * @param genero    Género que se desea conservar en la lista resultante
     */
    public FiltroGenero(Recomendador<CancionDTO> siguiente, Genero genero) {
        super(siguiente);
        this.genero = genero;
    }

    /**
     * Filtra la lista de canciones para conservar únicamente aquellas que contienen el género especificado.
     *
     * @return Lista de canciones del género solicitado
     */
    @Override
    public List<CancionDTO> recomendar() {
        return siguiente.recomendar().stream()
                .filter(c -> c.getGeneros() != null && c.getGeneros().contains(genero))
                .collect(Collectors.toList());
    }
}