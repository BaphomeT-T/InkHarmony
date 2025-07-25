 /**
 * Interfaz EstadoReproductor que representa el comportamiento que debe tener 
 * el reproductor MP3 en cada uno de sus estados (Reproduciendo, Pausado, Detenido).
 *
 * <p>Esta interfaz es parte del patrón de diseño State, que permite que 
 * el comportamiento del reproductor cambie dinámicamente según su estado actual.</p>
 *
 * <p>Cada implementación de esta interfaz define las acciones disponibles en ese estado.</p>
 *
 * @author Grupo B
 * @version 1.0
 * @since 25-07-2025
 */
public interface EstadoReproductor {
    void reproducir();
    void pausar();
    void reanudar();
    void detener();
    void siguiente();
    void anterior();
}
