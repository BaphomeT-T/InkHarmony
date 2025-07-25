package BusinessLogic.ReproductorEstados;

/**
 * Clase EstadoReproduciendo que implementa la interfaz EstadoReproductor.
 *
 * <p>Representa el estado activo de reproducción de una canción. Desde este estado
 * se puede pausar, detener o cambiar de pista.</p>
 *
 * <p>Este estado actualiza constantemente la posición de reproducción en frames
 * y gestiona transiciones hacia pausa o detención según las acciones del usuario.</p>
 *
 * @author Grupo B
 * @version 1.0
 * @since 25-07-2025
 */
public class EstadoReproduciendo implements EstadoReproductor {

    /** Referencia al contexto del reproductor MP3 */
    private final ReproductorMP3 reproductor;

    /**
     * Constructor que recibe el reproductor asociado a este estado.
     *
     * @param reproductor Instancia del reproductor en uso.
     */
    public EstadoReproduciendo(ReproductorMP3 reproductor) {
        this.reproductor = reproductor;
    }

    /**
     * No realiza ninguna acción ya que la canción ya está en reproducción.
     * Se notifica al usuario mediante un mensaje en consola.
     */
    @Override
    public void reproducir() {
        System.out.println("Ya está reproduciendo.");
    }

    /**
     * Pausa la reproducción actual.
     * Guarda el frame actual, cierra la reproducción y cambia al estado pausado.
     */
    @Override
    public void pausar() {
        reproductor.setFrameActual(reproductor.getPlayer().getLastPosition());
        reproductor.cerrarReproduccion();
        reproductor.setEstado(new EstadoPausado(reproductor));
        System.out.println("Pausado en frame: " + reproductor.getFrameActual());
    }

    /**
     * No realiza ninguna acción ya que la canción ya se encuentra reproduciendo.
     * Se notifica al usuario mediante un mensaje en consola.
     */
    @Override
    public void reanudar() {
        System.out.println("Ya está reproduciendo.");
    }

    /**
     * Detiene completamente la reproducción.
     * Guarda el frame actual y cambia al estado detenido.
     */
    @Override
    public void detener() {
        reproductor.setFrameActual(reproductor.getPlayer().getLastPosition());
        reproductor.cerrarReproduccion();
        reproductor.setEstado(new EstadoDetenido(reproductor));
    }

    /**
     * Detiene la reproducción actual y avanza a la siguiente canción.
     * Inicia automáticamente su reproducción desde el inicio.
     */
    @Override
    public void siguiente() {
        detener();
        int siguiente = (reproductor.getIndiceActual() + 1) % reproductor.getCancionesBytes().size();
        reproductor.setIndiceActual(siguiente);
        reproductor.reproducir();
    }

    /**
     * Detiene la reproducción actual y retrocede a la canción anterior.
     * Inicia automáticamente su reproducción desde el inicio.
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
