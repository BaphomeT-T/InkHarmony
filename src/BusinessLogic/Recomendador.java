package BusinessLogic;

import java.util.List;

/**
 * Interfaz genérica que define el contrato para todos los componentes del sistema de recomendaciones en InkHarmony.
 * <p>
 * Cualquier clase que implemente esta interfaz puede actuar como generador de recomendaciones, ya sea directamente
 * o mediante una cadena de decoradores.
 * </p>
 *
 * @param <T> Tipo de entidad que será recomendada (por ejemplo, {@code CancionDTO}, {@code Playlist}, etc.)
 *
 * @author Grupo F - InkHarmony Team
 * @version 1.0
 * @since 1.0
 */
public interface Recomendador<T> {
    
    /**
     * Genera y retorna una lista de elementos recomendados del tipo {@code T}.
     *
     * @return Lista de recomendaciones generadas
     */
    List<T> recomendar();
}