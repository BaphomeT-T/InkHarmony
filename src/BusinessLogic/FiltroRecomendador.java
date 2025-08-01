package BusinessLogic;

import java.util.List;

/**
 * Clase abstracta que implementa el patrón Decorator para el sistema de recomendaciones en InkHarmony.
 * <p>
 * Permite construir cadenas de filtros sobre componentes que implementan la interfaz {@link Recomendador}.
 * Cada filtro puede modificar o limitar la lista de elementos recomendados antes de delegar al siguiente.
 * </p>
 *
 * @param <T> Tipo de entidad que será recomendada (por ejemplo, {@code CancionDTO})
 *
 * @author Grupo F - InkHarmony Team
 * @version 1.0
 * @since 1.0
 */
public abstract class FiltroRecomendador<T> implements Recomendador<T> {
    
    /**
     * Componente decorado (siguiente en la cadena de recomendación).
     */
    protected final Recomendador<T> siguiente;

    /**
     * Construye un nuevo filtro decorador, especificando el componente al que debe delegar.
     *
     * @param siguiente Componente decorado (puede ser otro filtro o un recomendador base)
     */
    public FiltroRecomendador(Recomendador<T> siguiente) {
        this.siguiente = siguiente;
    }

    /**
     * Delegación por defecto: reenvía la solicitud de recomendación al siguiente componente.
     * <p>
     * Las subclases deben sobrescribir este método para aplicar sus propios criterios de filtrado.
     * </p>
     *
     * @return Lista de elementos recomendados del componente decorado
     */
    @Override
    public List<T> recomendar() {
        return siguiente.recomendar();
    }
}