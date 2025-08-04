package BusinessLogic;

/**
 * Clase que representa el estado "Detenido" de un reproductor MP3.
 * Implementa la interfaz {@link EstadoReproductor} y define el comportamiento
 * del reproductor cuando no está reproduciendo ni pausado.
 *
 * En este estado se puede iniciar la reproducción o navegar entre canciones.
 *
 * Forma parte del patrón de diseño **State**.
 */
public class EstadoDetenido implements EstadoReproductor {

    /** Referencia al controlador del reproductor MP3. */
    private final ReproductorMP3 reproductor;

    /**
     * Constructor que asocia este estado con un reproductor dado.
     *
     * @param reproductor Controlador del reproductor que se encuentra detenido.
     */
    public EstadoDetenido(ReproductorMP3 reproductor) {
        this.reproductor = reproductor;
    }

    /**
     * Inicia la reproducción desde el principio y cambia al estado Reproduciendo.
     */
    @Override
    public void reproducir() {
        reproductor.iniciarReproduccionDesde(0);
        reproductor.setEstado(new EstadoReproduciendo(reproductor));
    }

    /**
     * Acción no válida. No se puede pausar si no está reproduciendo.
     */
    @Override
    public void pausar() {
        System.out.println("No se puede pausar. El reproductor está detenido.");
    }

    /**
     * Acción no válida. Solo se puede reanudar si está pausado.
     */
    @Override
    public void reanudar() {
        System.out.println("No se puede reanudar. El reproductor está detenido.");
    }

    /**
     * Acción sin efecto. Ya está detenido.
     */
    @Override
    public void detener() {
        System.out.println("El reproductor ya está detenido.");
    }

    /**
     * Cambia a la siguiente canción en la playlist sin iniciar reproducción.
     */
    @Override
    public void siguiente() {
        System.out.println("No se puede avanzar si el reproductor esta detenido");
    }

    /**
     * Cambia a la canción anterior en la playlist sin iniciar reproducción.
     */
    @Override
    public void anterior() {
        System.out.println("No se puede retorceder si el reproductor esta detenido");
    }
}

