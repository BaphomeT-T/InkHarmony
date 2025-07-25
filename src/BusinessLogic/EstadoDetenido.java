package BusinessLogic.ReproductorEstados;

/**
 * Clase EstadoDetenido que implementa la interfaz EstadoReproductor.
 *
 * <p>Representa el estado en el que el reproductor se encuentra detenido, es decir,
 * no está reproduciendo ni pausado.</p>
 *
 * <p>Este estado permite iniciar la reproducción desde el principio, 
 * pero no permite pausar ni reanudar.</p>
 *
 * @author Grupo B
 * @version 1.0
 * @since 25-07-2025
 */
public class EstadoDetenido implements EstadoReproductor {

    /** Referencia al contexto del reproductor MP3 */
    private final ReproductorMP3 reproductor;

    /**
     * Constructor que recibe el reproductor asociado a este estado.
     *
     * @param reproductor Instancia del reproductor en uso.
     */
    public EstadoDetenido(ReproductorMP3 reproductor) {
        this.reproductor = reproductor;
    }

    /**
     * Inicia la reproducción desde el principio del archivo MP3.
     * Cambia el estado del reproductor a EstadoReproduciendo.
     */
    @Override
    public void reproducir() {
        reproductor.setFrameActual(0); // Reiniciar desde el primer frame
        reproductor.iniciarReproduccionDesde(0);
        reproductor.setEstado(new EstadoReproduciendo(reproductor)); // Cambiar a estado reproduciendo
    }

    /**
     * No es posible pausar cuando el reproductor está detenido.
     * Muestra un mensaje de advertencia.
     */
    @Override
    public void pausar() {
        System.out.println("No se puede pausar. El reproductor está detenido.");
    }

    /**
     * No es posible reanudar cuando el reproductor está detenido.
     * Muestra un mensaje de advertencia.
     */
    @Override
    public void reanudar() {
        System.out.println("No se puede reanudar. El reproductor está detenido.");
    }

    /**
     * Muestra un mensaje indicando que ya se encuentra detenido.
     */
    @Override
    public void detener() {
        System.out.println("Ya está detenido.");
    }

    /**
     * Cambia a la siguiente canción en la lista y comienza su reproducción.
     * Si está en la última canción, vuelve a la primera (modo circular).
     */
    @Override
    public void siguiente() {
        int nuevaPos = (reproductor.getIndiceActual() + 1) % reproductor.getCancionesBytes().size();
        reproductor.setIndiceActual(nuevaPos);
        reproducir();
    }

    /**
     * Cambia a la canción anterior en la lista y comienza su reproducción.
     * Si está en la primera canción, va a la última (modo circular inverso).
     */
    @Override
    public void anterior() {
        int nuevaPos = (reproductor.getIndiceActual() - 1 + reproductor.getCancionesBytes().size()) 
                       % reproductor.getCancionesBytes().size();
        reproductor.setIndiceActual(nuevaPos);
        reproducir();
    }
}
