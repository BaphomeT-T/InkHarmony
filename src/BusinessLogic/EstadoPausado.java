package BusinessLogic;

/**
 * Clase que representa el estado "Pausado" de un reproductor MP3.
 * Implementa la interfaz {@link EstadoReproductor} y define el comportamiento
 * del reproductor cuando la reproducción ha sido pausada.
 *
 * En este estado se puede reanudar la reproducción, detenerla o cambiar de canción.
 * 
 * Forma parte del patrón de diseño **State**.
 */
public class EstadoPausado implements EstadoReproductor {

    /** Referencia al reproductor MP3 que está en este estado. */
    private final ReproductorMP3 reproductor;

    /**
     * Constructor que establece el reproductor asociado a este estado.
     *
     * @param reproductor Reproductor MP3 que se encuentra en pausa
     */
    public EstadoPausado(ReproductorMP3 reproductor) {
        this.reproductor = reproductor;
    }

    /**
     * Reinicia la reproducción desde el inicio de la canción actual.
     * Cambia el estado del reproductor a {@link EstadoReproduciendo}.
     */
    @Override
    public void reproducir() {
        reproductor.iniciarReproduccionDesde(0);
        reproductor.setEstado(new EstadoReproduciendo(reproductor));
    }

    /**
     * Acción innecesaria en este estado. Muestra un mensaje indicando que ya está pausado.
     */
    @Override
    public void pausar() {
        System.out.println("Ya está pausado.");
    }

    /**
     * Reanuda la reproducción desde el frame actual.
     * Cambia el estado a {@link EstadoReproduciendo}.
     */
    @Override
    public void reanudar() {
        reproductor.iniciarReproduccionDesde(reproductor.getFrameActual());
        reproductor.setEstado(new EstadoReproduciendo(reproductor));
    }

    /**
     * Detiene la reproducción. Guarda el frame actual, cierra el reproductor
     * y cambia el estado a {@link EstadoDetenido}.
     */
    @Override
    public void detener() {
        reproductor.setFrameActual(reproductor.getPlayer().getLastPosition());
        reproductor.cerrarReproduccion();
        reproductor.setEstado(new EstadoDetenido(reproductor));
    }

    /**
     * Cambia a la siguiente canción en la lista y la reproduce desde el inicio.
     * Si está en la última canción, vuelve a la primera (comportamiento cíclico).
     * Se detiene la reproducción actual antes de cambiar de canción.
     */
    @Override
    public void siguiente() {
        detener();
        int siguienteIndice = (reproductor.getIndiceActual() + 1) % reproductor.getCancionesBytes().size();
        reproductor.setIndiceActual(siguienteIndice);
        reproductor.reproducir();
    }

    /**
     * Retrocede a la canción anterior en la lista y la reproduce desde el inicio.
     * Si está en la primera canción, pasa a la última (comportamiento cíclico).
     * Se detiene la reproducción actual antes de cambiar de canción.
     */
    @Override
    public void anterior() {
        detener();
        int nuevaPos = (reproductor.getIndiceActual() - 1 + reproductor.getCancionesBytes().size()) % reproductor.getCancionesBytes().size();
        reproductor.setIndiceActual(nuevaPos);
        reproductor.reproducir();
    }
}
