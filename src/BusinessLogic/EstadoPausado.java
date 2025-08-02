package BusinessLogic;

/**
 * Clase que representa el estado "Pausado" de un reproductor MP3.
 * Implementa la interfaz {@link EstadoReproductor} y define el comportamiento
 * cuando el reproductor se encuentra pausado temporalmente.
 *
 * En este estado se puede reanudar la reproducción, detener o cambiar de canción.
 *
 * Forma parte del patrón de diseño **State**.
 */
public class EstadoPausado implements EstadoReproductor {

    /** Referencia al controlador del reproductor MP3. */
    private final ReproductorMP3 reproductor;

    /**
     * Constructor que asocia este estado con un reproductor dado.
     *
     * @param reproductor Controlador del reproductor que se encuentra pausado.
     */
    public EstadoPausado(ReproductorMP3 reproductor) {
        this.reproductor = reproductor;
    }

    /**
     * Acción no válida. Ya está pausado.
     */
    @Override
    public void reproducir() {
        System.out.println("El reproductor está pausado. Usa reanudar.");
    }

    /**
     * Acción sin efecto. Ya está pausado.
     */
    @Override
    public void pausar() {
        System.out.println("El reproductor ya está pausado.");
    }

    /**
     * Reanuda la reproducción desde el último frame y cambia al estado Reproduciendo.
     */
    @Override
    public void reanudar() {
        int frame = reproductor.getMotor().getPlayer().getLastPosition();
        reproductor.iniciarReproduccionDesde(frame);
        reproductor.setEstado(new EstadoReproduciendo(reproductor));
        System.out.println("Reanudando desde el frame: " + frame);
    }

    /**
     * Detiene la reproducción pausada y cambia al estado Detenido.
     */
    @Override
    public void detener() {
        reproductor.cerrarReproduccionTotal();
        reproductor.setEstado(new EstadoDetenido(reproductor));
        System.out.println("Reproducción detenida.");
    }

    /**
     * Pasa a la siguiente canción y comienza su reproducción desde el inicio.
     */
    @Override
    public void siguiente() {
        reproductor.getPlaylist().siguiente();
        reproductor.iniciarReproduccionDesde(0);
        reproductor.setEstado(new EstadoReproduciendo(reproductor));
        System.out.println("Siguiente canción en reproducción.");
    }

    /**
     * Retrocede a la canción anterior y comienza su reproducción desde el inicio.
     */
    @Override
    public void anterior() {
        reproductor.getPlaylist().anterior();
        reproductor.iniciarReproduccionDesde(0);
        reproductor.setEstado(new EstadoReproduciendo(reproductor));
        System.out.println("Canción anterior en reproducción.");
    }
}
