package BusinessLogic;

/**
 * Clase que representa el estado "Reproduciendo" de un reproductor MP3.
 * Implementa la interfaz {@link EstadoReproductor} y define el comportamiento
 * del reproductor cuando está reproduciendo una canción.
 *
 * En este estado se puede pausar, detener o cambiar de canción, pero no se puede
 * iniciar la reproducción ni reanudarla (ya está en curso).
 *
 * Forma parte del patrón de diseño **State**.
 */
public class EstadoReproduciendo implements EstadoReproductor {

    /** Referencia al controlador del reproductor MP3. */
    private final ReproductorMP3 reproductor;

    /**
     * Constructor que establece el reproductor asociado a este estado.
     *
     * @param reproductor Reproductor MP3 que se encuentra reproduciendo
     */
    public EstadoReproduciendo(ReproductorMP3 reproductor) {
        this.reproductor = reproductor;
    }

    /**
     * Acción no válida en este estado. Muestra un mensaje indicando que ya está reproduciendo.
     */
    @Override
    public void reproducir() {
        System.out.println("Ya está reproduciendo.");
    }

    /**
     * Pausa la reproducción en el frame actual, cierra el reproductor
     * y cambia el estado a {@link EstadoPausado}.
     */
    @Override
    public void pausar() {
        int frameActual = reproductor.getMotor().getPlayer().getLastPosition();
        reproductor.cerrarReproduccion();
        reproductor.getMotor().setFrameActual(frameActual);
        reproductor.setEstado(new EstadoPausado(reproductor));
        System.out.println("Pausado en frame: " + frameActual);
    }

    /**
     * Acción no válida en este estado. Muestra un mensaje indicando que ya está reproduciendo.
     */
    @Override
    public void reanudar() {
        System.out.println("Ya está reproduciendo.");
    }

    /**
     * Detiene la reproducción, cierra el motor y cambia al estado {@link EstadoDetenido}.
     */
    @Override
    public void detener() {
        reproductor.cerrarReproduccion();
        reproductor.setEstado(new EstadoDetenido(reproductor));
    }

    /**
     * Cambia a la siguiente canción en la lista y la reproduce desde el inicio.
     * Detiene la reproducción actual antes de cambiar de canción.
     */
    @Override
    public void siguiente() {
        detener();
        reproductor.getPlaylist().siguiente();
        reproductor.iniciarReproduccionDesde(0);
        reproductor.setEstado(new EstadoReproduciendo(reproductor));
        System.out.println("Siguiente canción en reproducción.");
    }

    /**
     * Retrocede a la canción anterior en la lista y la reproduce desde el inicio.
     * Detiene la reproducción actual antes de cambiar de canción.
     */
    @Override
    public void anterior() {
        detener();
        reproductor.getPlaylist().anterior();
        reproductor.iniciarReproduccionDesde(0);
        reproductor.setEstado(new EstadoReproduciendo(reproductor));
    }
}
