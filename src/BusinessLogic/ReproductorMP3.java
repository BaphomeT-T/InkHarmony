import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public class ReproductorMP3 {
    private static ReproductorMP3 instancia; // Singleton

    private List<byte[]> cancionesBytes;
    private int indiceActual = 0;
    private AdvancedPlayerAcc player;
    private Thread hiloReproduccion;
    private int frameActual = 0;

    private EstadoReproductor estadoActual;

    private ReproductorMP3(List<byte[]> cancionesBytes) {
        this.cancionesBytes = cancionesBytes;
        this.estadoActual = new EstadoDetenido(this);
    }

    public static ReproductorMP3 getInstancia(List<byte[]> cancionesBytes) {
        if (instancia == null) {
            instancia = new ReproductorMP3(cancionesBytes);
        }
        return instancia;
    }

    public void setEstado(EstadoReproductor nuevoEstado) {
        this.estadoActual = nuevoEstado;
    }

    public EstadoReproductor getEstado() {
        return estadoActual;
    }

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

    // --- Métodos auxiliares para los estados ---

    public void iniciarReproduccionDesde(int frameInicial) {
        hiloReproduccion = new Thread(() -> {
            try (InputStream is = new ByteArrayInputStream(cancionesBytes.get(indiceActual))) {
                player = new AdvancedPlayerAcc(is);
                player.setPlayBackListener(new PlaybackListener() {
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
