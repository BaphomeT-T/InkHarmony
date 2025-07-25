package BusinessLogic.ReproductorEstados;

/**
 * Clase EstadoPausado que implementa la interfaz EstadoReproductor.
 *
 * <p>Representa el estado en el que el reproductor ha sido pausado. Desde este estado
 * es posible reanudar, detener o cambiar de pista, pero no pausar nuevamente.</p>
 *
 * <p>Este estado guarda el frame actual reproducido antes de detener la reproducción,
 * permitiendo reanudar exactamente desde el mismo punto.</p>
 *
 * @author Grupo B
 * @version 1.0
 * @since 25-07-2025
 */
public class EstadoPausado implements EstadoReproductor {

    /** Referencia al contexto del reproductor MP3 */
    private final ReproductorMP3 reproductor;

    /**
     * Constructor que recibe el reproductor asociado a este estado.
     *
     * @param reproductor Instancia del reproductor en uso.
     */
    public EstadoPausado(ReproductorMP3 reproductor) {
        this.reproductor = reproductor;
    }

    /**
     * Reinicia la reproducción desde el inicio de la canción actual.
     * Cambia el estado a EstadoReproduciendo.
     */
    @Override
    public void reproducir() {
        reproductor.iniciarReproduccionDesde(0);
        reproductor.setEstado(new EstadoReproduciendo(reproductor));
    }

    /**
     * No se puede pausar nuevamente si ya está pausado.
     * Muestra un mensaje de advertencia.
     */
    @Override
    public void pausar() {
        System.out.println("Ya está pausado.");
    }

    /**
     * Reanuda la reproducción desde el frame donde fue pausada.
     * Cambia el estado a EstadoReproduciendo.
     */
    @Override
    public void reanudar() {
        reproductor.iniciarReproduccionDesde(reproductor.getFrameActual());
        reproductor.setEstado(new EstadoReproduciendo(reproductor));
    }

    /**
     * Detiene completamente la reproducción y cambia al estado detenido.
     * Guarda la posición actual en frames para referencia futura.
     */
    @Override
    public void detener() {
        reproductor.setFrameActual(reproductor.getPlayer().getLastPosition());
        reproductor.cerrarReproduccion();
        reproductor.setEstado(new EstadoDetenido(reproductor));
    }

    /**
     * Detiene la canción actual y avanza a la siguiente pista.
     * Comienza su reproducción desde el inicio.
     */
    @Override
    public void siguiente() {
        detener();
        int siguiente = (reproductor.getIndiceActual() + 1) % reproductor.getCancionesBytes().size();
        reproductor.setIndiceActual(siguiente);
        reproductor.reproducir();
    }

    /**
     * Detiene la canción actual y retrocede a la pista anterior.
     * Comienza su reproducción desde el inicio.
     */
    @Override
    public void anterior() {
        detener();
        int nuevaPos = (reproductor.getIndiceActual() - 1 + reproductor.getCancionesBytes().size()) 
                       % reproductor.getCancionesBytes().size();
        reproductor.setIndiceActual(nuevaPos);
        reproductor.reproducir();
    }
}
