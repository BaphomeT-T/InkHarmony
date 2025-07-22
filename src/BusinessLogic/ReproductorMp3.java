import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import java.io.FileInputStream;
import java.util.List;

/**
 * Clase ReproductorMp3 que gestiona la reproducción de canciones en formato MP3 en el sistema InkHarmony.
 * Implementa el patrón Singleton para mantener una única instancia del reproductor a lo largo de la aplicación.
 */
public class ReproductorMp3 {

    private static ReproductorMp3 instancia; // Instancia única Singleton

    private List<Cancion> listaCanciones; // Lista de canciones a reproducir
    private int indiceActual = 0; // Índice de la canción que se está reproduciendo
    private AdvancedPlayer player; // Reproductor de la librería JLayer
    private Thread thread; // Hilo que gestiona la reproducción en segundo plano
    private boolean pausado = false; // Indicador si la reproducción está pausada
    private int framePausado = 0; // Frame donde se pausó la canción para reanudar luego

    /**
     * Constructor privado para impedir instanciación externa (Patrón Singleton).
     */
    private ReproductorMp3() {}

    /**
     * Devuelve la instancia única del reproductor MP3.
     * @return instancia única del reproductor.
     */
    public static ReproductorMp3 getInstancia() {
        if (instancia == null) {
            instancia = new ReproductorMp3();
        }
        return instancia;
    }

    /**
     * Carga la lista de canciones en el reproductor.
     * @param canciones Lista de objetos Cancion.
     */
    public void cargarListaCanciones(List<Cancion> canciones) {
        this.listaCanciones = canciones;
        this.indiceActual = 0;
        System.out.println("Lista de canciones cargada con " + canciones.size() + " canciones.");
    }

    /**
     * Inicia la reproducción de la canción actual.
     * Si la lista está vacía, muestra un mensaje de advertencia.
     */
    public void reproducir() {
        if (listaCanciones == null || listaCanciones.isEmpty()) {
            System.out.println("No hay canciones en la lista.");
            return;
        }

        detener(); // Detiene cualquier reproducción en curso
        Cancion cancionActual = listaCanciones.get(indiceActual);
        System.out.println("Reproduciendo: " + cancionActual.getTitulo());

        thread = new Thread(() -> {
            try (FileInputStream fis = new FileInputStream(cancionActual.getRutaArchivo())) {
                player = new AdvancedPlayer(fis);
                player.setPlayBackListener(new PlaybackListener() {
                    @Override
                    public void playbackFinished(PlaybackEvent evt) {
                        if (!pausado) {
                            siguiente(); // Cuando termina, automáticamente pasa a la siguiente canción
                        }
                    }
                });
                player.play(framePausado, Integer.MAX_VALUE);
            } catch (Exception e) {
                System.out.println("Error al reproducir: " + e.getMessage());
            }
        });
        thread.start();
    }

    /**
     * Pausa la reproducción de la canción actual.
     * Guarda el frame actual para poder reanudar desde allí.
     */
    public void pausar() {
        if (player != null && !pausado) {
            pausado = true;
            framePausado = player.getPosition();
            detener();
            System.out.println("Pausado en el frame: " + framePausado);
        }
    }

    /**
     * Reanuda la reproducción desde donde fue pausada.
     */
    public void reanudar() {
        if (pausado) {
            pausado = false;
            System.out.println("Reanudando...");
            reproducir();
        }
    }

    /**
     * Avanza a la siguiente canción en la lista, si existe.
     * Si ya está en la última, notifica al usuario.
     */
    public void siguiente() {
        if (listaCanciones == null || listaCanciones.isEmpty()) {
            System.out.println("No hay canciones en la lista.");
            return;
        }

        if (indiceActual < listaCanciones.size() - 1) {
            indiceActual++;
            framePausado = 0;
            System.out.println("Saltando a la siguiente canción.");
            reproducir();
        } else {
            System.out.println("Ya estás en la última canción.");
        }
    }

    /**
     * Retrocede a la canción anterior en la lista, si existe.
     * Si ya está en la primera, notifica al usuario.
     */
    public void anterior() {
        if (listaCanciones == null || listaCanciones.isEmpty()) {
            System.out.println("No hay canciones en la lista.");
            return;
        }

        if (indiceActual > 0) {
            indiceActual--;
            framePausado = 0;
            System.out.println("Regresando a la canción anterior.");
            reproducir();
        } else {
            System.out.println("Ya estás en la primera canción.");
        }
    }

    /**
     * Elimina la canción actual de la lista y reproduce la siguiente.
     * Si la lista queda vacía, lo notifica.
     */
    public void quitarCancionActual() {
        if (listaCanciones == null || listaCanciones.isEmpty()) {
            System.out.println("No hay canciones en la lista.");
            return;
        }

        System.out.println("Quitando: " + listaCanciones.get(indiceActual).getTitulo());
        listaCanciones.remove(indiceActual);
        if (indiceActual >= listaCanciones.size()) {
            indiceActual = listaCanciones.size() - 1;
        }
        detener();
        if (!listaCanciones.isEmpty()) {
            reproducir();
        } else {
            System.out.println("La lista de reproducción está vacía.");
        }
    }

    /**
     * Detiene cualquier reproducción en curso.
     */
    public void detener() {
        if (player != null) {
            player.close();
            player = null;
            System.out.println("Reproducción detenida.");
        }
    }
}