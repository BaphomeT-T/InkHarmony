import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Clase ReproductorMP3 que representa el controlador principal de reproducción de audio.
 *
 * <p>Utiliza el patrón Singleton para asegurar que exista una única instancia global,
 * y el patrón State para manejar distintos comportamientos (detenido, reproduciendo, pausado).</p>
 *
 * <p>Administra la reproducción de una lista de canciones representadas como arreglos de bytes (MP3),
 * usando un hilo dedicado y la clase `AdvancedPlayerAcc` como motor de reproducción.</p>
 *
 * @author Grupo B
 * @version 1.0
 * @since 25-07-2025
 */
public class ReproductorMP3 {

    /** Instancia única */
    private static ReproductorMP3 instancia;

    /** Lista de canciones como arreglos de bytes (MP3) */
    private List<byte[]> cancionesBytes;

    /** Índice actual de la canción en reproducción */
    private int indiceActual = 0;

    /** Reproductor MP3 extendido basado en JLayer */
    private AdvancedPlayerAcc player;

    /** Hilo de ejecución de la reproducción */
    private Thread hiloReproduccion;

    /** Frame actual en la canción (posición de reproducción) */
    private int frameActual = 0;

    /** Estado actual del reproductor (detenido, pausado, reproduciendo) */
    private EstadoReproductor estadoActual;

    /**
     * Constructor privado que inicializa el reproductor con la lista de canciones.
     *
     * @param cancionesBytes Lista de canciones en formato binario.
     */
    private ReproductorMP3(List<byte[]> cancionesBytes) {
        this.cancionesBytes = cancionesBytes;
        this.estadoActual = new EstadoDetenido(this); // Estado inicial
    }

    /**
     * Obtiene la instancia única del reproductor (Singleton).
     *
     * @param cancionesBytes Lista de canciones (solo usada en la primera llamada).
     * @return Instancia única de ReproductorMP3.
     */
    public static ReproductorMP3 getInstancia(List<byte[]> cancionesBytes) {
        if (instancia == null) {
            instancia = new ReproductorMP3(cancionesBytes);
        }
        return instancia;
    }

    /**
     * Establece el estado actual del reproductor.
     *
     * @param nuevoEstado Nueva instancia de estado.
     */
    public void setEstado(EstadoReproductor nuevoEstado) {
        this.estadoActual = nuevoEstado;
    }

    /**
     * Obtiene el estado actual del reproductor.
     *
     * @return EstadoReproductor actual.
     */
    public EstadoReproductor getEstado() {
        return estadoActual;
    }

    // --- Métodos públicos del reproductor, delegados al estado actual ---

    public void reproducir() {
        estadoActual.reproducir();
    }

    public void pausar() {
        estadoActual.pausar();
    }

    public void reanudar() {
        estadoActual.reanudar();
    }

    public void detener() {
        estadoActual.detener();
    }

    public void siguiente() {
        estadoActual.siguiente();
    }

    public void anterior() {
        estadoActual.anterior();
    }

    // --- Métodos auxiliares utilizados por los estados ---

    /**
     * Inicia la reproducción de la canción actual desde un frame específico.
     *
     * @param frameInicial Número de frame desde el cual iniciar la reproducción.
     */
    public void iniciarReproduccionDesde(int frameInicial) {
        hiloReproduccion = new Thread(() -> {
            try (InputStream is = new ByteArrayInputStream(cancionesBytes.get(indiceActual))) {
                player = new AdvancedPlayerAcc(is);
                player.setPlayBackListener(new PlaybackListener() {
                    @Override
                    public void playbackFinished(PlaybackEvent evt) {
                        frameActual = player.getLastPosition(); // Guardar último frame reproducido
                        indiceActual = (indiceActual + 1) % cancionesBytes.size(); // Avanza a siguiente canción
                        iniciarReproduccionDesde(0); // Reproducir siguiente automáticamente
                    }
                });
                player.play(frameInicial, Integer.MAX_VALUE); // Inicia reproducción
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        hiloReproduccion.start();
    }

    /**
     * Cierra la reproducción actual, deteniendo el hilo y liberando recursos.
     */
    public void cerrarReproduccion() {
        try {
            if (player != null) player.close();
            if (hiloReproduccion != null && hiloReproduccion.isAlive()) {
                hiloReproduccion.join(); // Espera a que el hilo termine
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- Getters y Setters auxiliares ---

    /**
     * Retorna el frame actual de reproducción.
     *
     * @return Frame actual.
     */
    public int getFrameActual() {
        return frameActual;
    }

    /**
     * Establece el frame actual de reproducción.
     *
     * @param frameActual Frame a establecer.
     */
    public void setFrameActual(int frameActual) {
        this.frameActual = frameActual;
    }

    /**
     * Retorna el objeto `AdvancedPlayerAcc` activo.
     *
     * @return Reproductor activo.
     */
    public AdvancedPlayerAcc getPlayer() {
        return player;
    }

    /**
     * Retorna el índice actual de la canción que se está reproduciendo.
     *
     * @return Índice de la canción.
     */
    public int getIndiceActual() {
        return indiceActual;
    }

    /**
     * Establece el índice de la canción a reproducir.
     *
     * @param indice Índice nuevo.
     */
    public void setIndiceActual(int indice) {
        this.indiceActual = indice;
    }

    /**
     * Devuelve la lista completa de canciones en forma de arreglos de bytes.
     *
     * @return Lista de canciones.
     */
    public List<byte[]> getCancionesBytes() {
        return cancionesBytes;
    }
}
