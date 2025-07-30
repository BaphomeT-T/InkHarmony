package BusinessLogic;

/**
 * Clase que representa el estado "Detenido" de un reproductor MP3.
 * Implementa la interfaz {@link EstadoReproductor} y define el comportamiento
 * del reproductor cuando está detenido.
 *
 * En este estado, solo se permite iniciar la reproducción o cambiar de canción.
 * Las acciones de pausar o reanudar no tienen efecto.
 * 
 * Forma parte del patrón de diseño **State**.
 */
public class EstadoDetenido implements EstadoReproductor {

    /** Referencia al reproductor MP3 que está en este estado. */
    private final ReproductorMP3 reproductor;

    /**
     * Constructor que establece el reproductor asociado a este estado.
     * 
     * @param reproductor Reproductor MP3 que se encuentra detenido
     */
    public EstadoDetenido(ReproductorMP3 reproductor) {
        this.reproductor = reproductor;
    }

    /**
     * Inicia la reproducción desde el principio de la canción actual.
     * Cambia el estado del reproductor a {@link EstadoReproduciendo}.
     */
    @Override
    public void reproducir() {
        reproductor.setFrameActual(0); // Reinicia la posición de reproducción
        reproductor.iniciarReproduccionDesde(0); // Comienza desde el inicio
        reproductor.setEstado(new EstadoReproduciendo(reproductor)); // Cambia el estado
    }

    /**
     * Acción no válida en este estado. Muestra un mensaje indicando que no se puede pausar.
     */
    @Override
    public void pausar() {
        System.out.println("No se puede pausar. El reproductor está detenido.");
    }

    /**
     * Acción no válida en este estado. Muestra un mensaje indicando que no se puede reanudar.
     */
    @Override
    public void reanudar() {
        System.out.println("No se puede reanudar. El reproductor está detenido.");
    }

    /**
     * Acción innecesaria en este estado. Muestra un mensaje indicando que ya está detenido.
     */
    @Override
    public void detener() {
        System.out.println("Ya está detenido.");
    }

    /**
     * Pasa a la siguiente canción en la lista y la reproduce desde el inicio.
     * Si está en la última canción, vuelve a la primera (comportamiento cíclico).
     */
    @Override
    public void siguiente() {
        int siguienteIndice = (reproductor.getIndiceActual() + 1) % reproductor.getCancionesBytes().size();
        reproductor.setIndiceActual(siguienteIndice);
        reproducir();
    }

    /**
     * Retrocede a la canción anterior en la lista y la reproduce desde el inicio.
     * Si está en la primera canción, pasa a la última (comportamiento cíclico).
     */
    @Override
    public void anterior() {
        int nuevaPos = (reproductor.getIndiceActual() - 1 + reproductor.getCancionesBytes().size())
                     % reproductor.getCancionesBytes().size();
        reproductor.setIndiceActual(nuevaPos);
        reproducir();
    }
}
