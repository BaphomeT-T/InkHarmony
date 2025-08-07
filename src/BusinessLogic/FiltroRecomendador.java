package BusinessLogic;

import DataAccessComponent.DTO.CancionDTO;
import java.util.List;

/**
 * Clase abstracta que representa un filtro en la cadena de recomendación de canciones.
 *
 * <p>Actúa como decorador para aplicar filtros adicionales sobre los resultados de otro
 * recomendador. Cada subclase debe sobrescribir el método {@code recomendar()} para aplicar
 * su lógica de filtrado específica.</p>
 *
 * @author Grupo F - InkHarmony Team
 */
public abstract class FiltroRecomendador implements Recomendador {

    /** Recomendador al que se delega la llamada tras aplicar el filtro. */
    protected final Recomendador siguiente;

    /**
     * Crea un nuevo filtro encadenado al recomendador especificado.
     *
     * @param siguiente el recomendador al que se delegará si se cumplen las condiciones
     */
    protected FiltroRecomendador(Recomendador siguiente) {
        this.siguiente = siguiente;
    }

    /**
     * Retorna las recomendaciones del siguiente filtro sin modificaciones.
     * Las subclases deben sobrescribir este método para aplicar lógica de filtrado.
     *
     * @return lista de canciones recomendadas
     */
    @Override
    public List<CancionDTO> recomendar() {
        return siguiente.recomendar();
    }
}
