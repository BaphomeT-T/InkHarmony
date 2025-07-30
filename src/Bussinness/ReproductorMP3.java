package Bussinness;

import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import Bussinness.utilities.AdvancedPlayerAcc;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Clase Bussinness.ReproductorMP3 que representa el controlador principal de reproducción de audio.
 *
 * <p>Utiliza el patrón Singleton para asegurar que exista una única instancia global,
 * y el patrón State para manejar distintos comportamientos (detenido, reproduciendo, pausado).</p>
 *
 * <p>Administra la reproducción de una lista de canciones representadas como arreglos de bytes (MP3),
 * usando un hilo dedicado y la clase `Bussinness.utilities.AdvancedPlayerAcc` como motor de reproducción.</p>
 *
 * @author Grupo B
 * @version 1.1
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
     * @return Instancia única de Bussinness.ReproductorMP3.
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
     * @return Bussinness.EstadoReproductor actual.
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
                        frameActual = player.getLastPosition();
                        indiceActual = (indiceActual + 1) % cancionesBytes.size();
                        iniciarReproduccionDesde(0);
                    }
                });
                player.play(frameInicial, Integer.MAX_VALUE);
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
                hiloReproduccion.join();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Mueve la reproducción a un nuevo frame y continúa desde ese punto.
     * Este método puede ser llamado desde la interfaz gráfica al mover un deslizador.
     *
     * @param nuevoFrame Frame al que se desea saltar y continuar la reproducción.
     */
    public void moverAFrame(int nuevoFrame) {
        cerrarReproduccion();
        setFrameActual(nuevoFrame);
        iniciarReproduccionDesde(nuevoFrame);
        }
    /**
     * Reestablece la lista de canciones, detiene la reproducción actual y reinicia el estado.
     *
     * @param nuevaLista Nueva lista de canciones en formato binario.
     */
    public void cambiarPlaylist(List<byte[]> nuevaLista) {
        detener();
        cerrarReproduccion();
        this.cancionesBytes = nuevaLista;
        this.indiceActual = 0;
        this.frameActual = 0;
        this.estadoActual = new EstadoDetenido(this);
    }


    public int getFrameActual() {
        return frameActual;
    }

    public void setFrameActual(int frameActual) {
        this.frameActual = frameActual;
    }

    public AdvancedPlayerAcc getPlayer() {
        return player;
    }

    public int getIndiceActual() {
        return indiceActual;
    }

    public void setIndiceActual(int indice) {
        this.indiceActual = indice;
    }

    public List<byte[]> getCancionesBytes() {
        return cancionesBytes;
    }
}
