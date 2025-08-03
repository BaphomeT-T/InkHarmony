package BusinessLogic;

import java.util.List;
import BusinessLogic.utilities.AdvancedPlayerAcc;

/**
 * Clase principal que representa el controlador de reproducción de audio MP3.
 *
 * <p>Utiliza el patrón de diseño {@code Singleton} para asegurar una única instancia global,
 * y el patrón {@code State} para manejar dinámicamente el comportamiento del reproductor
 * según su estado actual (detenido, reproduciendo o pausado).</p>
 *
 * <p>Gestiona la reproducción de una lista de canciones representadas como arreglos de bytes,
 * usando un motor de reproducción basado en {@link AdvancedPlayerAcc}, ejecutado en un hilo independiente.</p>
 *
 * @author Grupo B
 * @version 1.1
 * @since 25-07-2025
 *
 * @see EstadoReproductor
 * @see EstadoReproduciendo
 * @see EstadoPausado
 * @see EstadoDetenido
 * @see MotorReproduccion
 * @see GestorPlaylist
 */
public class ReproductorMP3 {

    /** Instancia única del reproductor (patrón Singleton). */
    private static ReproductorMP3 instancia;

    /** Gestor de la lista de reproducción. */
    private GestorPlaylist playlist;

    /** Motor que controla la reproducción de audio. */
    private MotorReproduccion motor;

    /** Estado actual del reproductor (patrón State). */
    private EstadoReproductor estadoActual;

    /**
     * Constructor privado. Se invoca solo una vez mediante {@link #getInstancia(List)}.
     *
     * @param canciones Lista de canciones (en formato byte[])
     */
    private ReproductorMP3(List<byte[]> canciones) {
        this.playlist = new GestorPlaylist(canciones);
        this.motor = new MotorReproduccion();
        this.estadoActual = new EstadoDetenido(this);
    }

    /**
     * Devuelve la instancia única del reproductor MP3 (Singleton).
     * Si no existe, la crea con la lista de canciones proporcionada.
     *
     * @param canciones Lista de canciones para inicializar el reproductor
     * @return Instancia única del reproductor
     */
    public static ReproductorMP3 getInstancia(List<byte[]> canciones) {
        if (instancia == null) {
            instancia = new ReproductorMP3(canciones);
        }
        return instancia;
    }

    // --------------------------
    // Métodos públicos del estado
    // --------------------------

    /**
     * Inicia la reproducción desde el estado actual.
     */
    public void reproducir() {
        estadoActual.reproducir();
    }

    /**
     * Pausa la reproducción actual.
     */
    public void pausar() {
        estadoActual.pausar();
    }

    /**
     * Reanuda la reproducción desde el punto en que fue pausada.
     */
    public void reanudar() {
        estadoActual.reanudar();
    }

    /**
     * Detiene completamente la reproducción.
     */
    public void detener() {
        estadoActual.detener();
    }

    /**
     * Reproduce la siguiente canción en la lista.
     */
    public void siguiente() {
        estadoActual.siguiente();
    }

    /**
     * Reproduce la canción anterior en la lista.
     */
    public void anterior() {
        estadoActual.anterior();
    }

    // --------------------------
    // Métodos internos
    // --------------------------

    /**
     * Inicia la reproducción desde un frame específico de la canción actual.
     * Si la canción termina, automáticamente avanza a la siguiente.
     *
     * @param frameInicial Frame desde el cual comenzar la reproducción
     */
    public void iniciarReproduccionDesde(int frameInicial) {
        List<byte[]> lista = playlist.getCanciones();
        if (lista != null) {
            byte[] cancion = playlist.obtenerCancionActual();
            motor.reproducir(cancion, frameInicial, () -> {
                playlist.siguiente();
                iniciarReproduccionDesde(0);
            });
        } else {
            System.out.println("No se encontró la canción.");
        }
    }

    /**
     * Cierra la reproducción actual pero mantiene la playlist.
     */
    public void cerrarReproduccion() {
        motor.cerrar();
    }

    /**
     * Cierra la reproducción actual y limpia completamente la lista de canciones.
     */
    public void cerrarReproduccionTotal() {
        motor.cerrar();
        playlist.setCanciones(null);
    }

    /**
     * Mueve la reproducción a un frame específico de la canción actual.
     *
     * @param nuevoFrame Frame objetivo
     */
    public void moverAFrame(int nuevoFrame) {
        cerrarReproduccion();
        motor.setFrameActual(nuevoFrame);
        iniciarReproduccionDesde(nuevoFrame);
    }

    /**
     * Cambia la lista de canciones activa. Detiene la reproducción y reinicia el estado.
     *
     * @param nuevaLista Nueva lista de canciones (byte[])
     */
    public void cambiarPlaylist(List<byte[]> nuevaLista) {
        detener();
        cerrarReproduccion();
        playlist.setCanciones(nuevaLista);
        estadoActual = new EstadoDetenido(this);
    }

    // --------------------------
    // Getters y Setters
    // --------------------------

    /**
     * Devuelve el gestor de la playlist actual.
     *
     * @return Gestor de playlist
     */
    public GestorPlaylist getPlaylist() {
        return playlist;
    }

    /**
     * Devuelve el motor de reproducción usado internamente.
     *
     * @return Motor de reproducción
     */
    public MotorReproduccion getMotor() {
        return motor;
    }

    /**
     * Devuelve el estado actual del reproductor (State).
     *
     * @return Estado actual
     */
    public EstadoReproductor getEstado() {
        return estadoActual;
    }

    /**
     * Cambia el estado del reproductor (se usa internamente por los estados).
     *
     * @param estado Nuevo estado a establecer
     */
    public void setEstado(EstadoReproductor estado) {
        this.estadoActual = estado;
    }
}
