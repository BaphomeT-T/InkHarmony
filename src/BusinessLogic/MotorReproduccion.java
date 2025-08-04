package BusinessLogic;

import BusinessLogic.utilities.AdvancedPlayerAcc;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Clase que actúa como motor de reproducción de canciones MP3.
 *
 * Utiliza {@link AdvancedPlayerAcc} para permitir la reproducción desde un frame específico
 * y ejecuta la reproducción en un hilo separado para no bloquear el hilo principal.
 *
 * Se encarga de iniciar, cerrar y monitorear la reproducción, así como almacenar el frame actual.
 */
public class MotorReproduccion {
    /** Reproductor de audio que permite control avanzado sobre los frames. */
    private AdvancedPlayerAcc player;

    /** Hilo que ejecuta la reproducción en segundo plano. */
    private Thread hilo;

    /** Frame actual de reproducción (útil para pausar y reanudar). */
    private int frameActual = 0;

    /**
     * Reproduce una canción desde un frame específico.
     * Se ejecuta en un hilo separado y al finalizar invoca una acción proporcionada.
     *
     * @param cancion     Canción en formato byte[]
     * @param desdeFrame  Frame desde el cual iniciar la reproducción
     * @param alFinalizar Acción a ejecutar cuando la reproducción termina
     */
    public void reproducir(byte[] cancion, int desdeFrame, Runnable alFinalizar) {
        hilo = new Thread(() -> {
            try (InputStream is = new ByteArrayInputStream(cancion)) {
                player = new AdvancedPlayerAcc(is);
                player.setPlayBackListener(new PlaybackListener() {
                    @Override
                    public void playbackFinished(PlaybackEvent evt) {
                        frameActual = player.getLastPosition();
                        alFinalizar.run();
                    }
                });
                player.play(desdeFrame, Integer.MAX_VALUE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        hilo.start();
    }

    /**
     * Detiene la reproducción actual y espera a que el hilo de reproducción finalice.
     * También guarda el frame actual antes de cerrar el reproductor.
     */
    public void cerrar() {
        try {
            if (player != null) player.close();
            if (hilo != null && hilo.isAlive()) hilo.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Devuelve el frame actual en el que se detuvo la reproducción.
     *
     * @return Frame actual
     */
    public int getFrameActual() {
        return frameActual;
    }

    /**
     * Establece manualmente el frame actual (por ejemplo, al reanudar).
     *
     * @param frame Frame a establecer
     */
    public void setFrameActual(int frame) {
        this.frameActual = frame;
    }

    /**
     * Devuelve el reproductor avanzado utilizado internamente.
     *
     * @return Instancia de {@link AdvancedPlayerAcc}
     */
    public AdvancedPlayerAcc getPlayer() {
        return player;
    }
}
