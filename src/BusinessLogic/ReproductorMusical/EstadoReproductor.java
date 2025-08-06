package BusinessLogic.ReproductorMusical;

/**
 * Interfaz que define el comportamiento de los diferentes estados del reproductor MP3.
 * Cada estado (Reproduciendo, Pausado, Detenido) implementará estas acciones
 * de forma específica.
 */
public interface EstadoReproductor {
    
    /**
     * Inicia la reproducción de una canción.
     */
    void reproducir();
    
    /**
     * Pausa la reproducción actual.
     */
    void pausar();
    
    /**
     * Reanuda la reproducción desde donde se pausó.
     */
    void reanudar();
    
    /**
     * Detiene la reproducción actual.
     */
    void detener();
    
    /**
     * Salta a la siguiente canción en la lista de reproducción.
     */
    void siguiente();
    
    /**
     * Vuelve a la canción anterior en la lista de reproducción.
     */
    void anterior();
}
