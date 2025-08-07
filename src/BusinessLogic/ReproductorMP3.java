package BusinessLogic;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.function.Consumer;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.application.Platform;
import javafx.util.Duration;

/**
 * Clase principal que representa el controlador de reproducción de audio MP3.
 *
 * <p>Utiliza el patrón de diseño {@code Singleton} para asegurar una única instancia global,
 * y el patrón {@code State} para manejar dinámicamente el comportamiento del reproductor
 * según su estado actual (detenido, reproduciendo o pausado).</p>
 *
 * <p>Gestiona la reproducción de una lista de canciones representadas como arreglos de bytes,
 * usando JavaFX MediaPlayer para la reproducción de audio.</p>
 *
 * @author Grupo B
 * @version 2.0
 * @since 25-07-2025
 *
 * @see EstadoReproductor
 * @see EstadoReproduciendo
 * @see EstadoPausado
 * @see EstadoDetenido
 * @see GestorPlaylist
 */
public class ReproductorMP3 {

    /** Instancia única del reproductor (patrón Singleton). */
    private static ReproductorMP3 instancia;

    /** Gestor de la lista de reproducción. */
    private GestorPlaylist playlist;

    /** Motor que controla la reproducción de audio (mantenido para compatibilidad). */
    private MotorReproduccion motor;

    /** Estado actual del reproductor (patrón State). */
    private EstadoReproductor estadoActual;

    /** MediaPlayer de JavaFX para reproducción de audio */
    private MediaPlayer mediaPlayer;

    /** Archivo temporal actual para reproducción */
    private File archivoTemporal;

    /** Callback para cuando termina una canción */
    private Runnable onCancionTerminada;

    /**
     * Constructor privado. Se invoca solo una vez mediante {@link #getInstancia(List)}.
     *
     * @param canciones Lista de canciones (en formato byte[])
     */
    private ReproductorMP3(List<byte[]> canciones) {
        this.playlist = new GestorPlaylist(canciones);
        this.motor = new MotorReproduccion(); // Mantenido para compatibilidad
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
        notificarCambioCancion();
    }

    /**
     * Reproduce la canción anterior en la lista.
     */
    public void anterior() {
        estadoActual.anterior();
        notificarCambioCancion();
    }

    // --------------------------
    // Métodos internos actualizados con JavaFX Media
    // --------------------------

    /**
     * Inicia la reproducción de la canción actual usando JavaFX MediaPlayer.
     * Si la canción termina, automáticamente avanza a la siguiente.
     *
     * @param frameInicial Frame desde el cual comenzar (convertido a tiempo)
     */
    public void iniciarReproduccionDesde(int frameInicial) {
        try {
            byte[] cancion = playlist.obtenerCancionActual();
            if (cancion == null) {
                System.out.println("No se encontró la canción.");
                return;
            }

            // Detener reproducción anterior si existe
            detenerMediaPlayer();

            // Crear archivo temporal
            archivoTemporal = File.createTempFile("cancion_temp", ".mp3");
            archivoTemporal.deleteOnExit();

            try (FileOutputStream fos = new FileOutputStream(archivoTemporal)) {
                fos.write(cancion);
            }

            // Crear Media y MediaPlayer
            Media media = new Media(archivoTemporal.toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            // Configurar eventos
            mediaPlayer.setOnReady(() -> {
                // Cambiar el estado a Reproduciendo
                setEstado(new EstadoReproduciendo(this));

                // Si hay un frame inicial específico, posicionarse ahí
                if (frameInicial > 0) {
                    double segundos = frameInicial / 26.0; // Conversión aproximada frame->segundos
                    mediaPlayer.seek(Duration.seconds(segundos));
                }

                // Iniciar reproducción
                mediaPlayer.play();
                System.out.println("Reproducción iniciada");
            });

            mediaPlayer.setOnEndOfMedia(() -> {
                System.out.println("Canción terminada, avanzando a la siguiente");
                // Callback para avanzar automáticamente
                Platform.runLater(() -> {
                    iniciarReproduccionDesde(0);
                    siguiente();
                });
            });

            mediaPlayer.setOnError(() -> {
                System.err.println("Error en MediaPlayer: " + mediaPlayer.getError());
                setEstado(new EstadoDetenido(this));
            });

        } catch (Exception e) {
            System.err.println("Error al iniciar reproducción: " + e.getMessage());
            e.printStackTrace();
            setEstado(new EstadoDetenido(this));
        }
    }

    /**
     * Pausa el MediaPlayer actual
     */
    public void pausarMediaPlayer() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            System.out.println("Reproducción pausada");
        }
    }

    /**
     * Reanuda el MediaPlayer pausado
     */
    public void reanudarMediaPlayer() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
            mediaPlayer.play();
            System.out.println("Reproducción reanudada");
        }
    }

    /**
     * Detiene y limpia el MediaPlayer actual
     */
    public void detenerMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }

        // Limpiar archivo temporal
        if (archivoTemporal != null && archivoTemporal.exists()) {
            archivoTemporal.delete();
            archivoTemporal = null;
        }
    }

    /**
     * Cierra la reproducción actual pero mantiene la playlist.
     */
    public void cerrarReproduccion() {
        detenerMediaPlayer();
        motor.cerrar(); // Mantener para compatibilidad
    }

    /**
     * Cierra la reproducción actual y limpia completamente la lista de canciones.
     */
    public void cerrarReproduccionTotal() {
        detenerMediaPlayer();
        motor.cerrar();
        playlist.setCanciones(null);
    }

    /**
     * Mueve la reproducción a un tiempo específico de la canción actual.
     *
     * @param nuevoFrame Frame objetivo (convertido a segundos)
     */
    public void moverAFrame(int nuevoFrame) {
        if (mediaPlayer != null) {
            double segundos = nuevoFrame / 26.0; // Conversión aproximada
            mediaPlayer.seek(Duration.seconds(segundos));
            motor.setFrameActual(nuevoFrame); // Mantener sincronizado
        }
    }

    /**
     * Establece el tiempo de reproducción en segundos
     */
    public void setTiempo(double segundos) {
        if (mediaPlayer != null) {
            mediaPlayer.seek(Duration.seconds(segundos));
        }
    }

    /**
     * Obtiene el tiempo actual de reproducción en segundos
     */
    public double getTiempoActual() {
        if (mediaPlayer != null && mediaPlayer.getCurrentTime() != null) {
            return mediaPlayer.getCurrentTime().toSeconds();
        }
        return 0.0;
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

    /**
     * Obtiene la duración total de la canción actual usando JavaFX Media.
     *
     * @param callback Callback que recibe la duración en segundos
     */
    public void obtenerDuracionCancionActual(Consumer<Double> callback) {
        try {
            if (mediaPlayer != null && mediaPlayer.getTotalDuration() != null
                    && !mediaPlayer.getTotalDuration().equals(Duration.UNKNOWN)) {
                // Si ya tenemos la duración disponible
                callback.accept(mediaPlayer.getTotalDuration().toSeconds());
                return;
            }

            // Si no tenemos MediaPlayer o la duración no está lista, crear uno temporal
            byte[] cancion = playlist.obtenerCancionActual();
            if (cancion == null) {
                callback.accept(0.0);
                return;
            }

            File tempFile = File.createTempFile("duracion", ".mp3");
            tempFile.deleteOnExit();

            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(cancion);
            }

            Media media = new Media(tempFile.toURI().toString());
            MediaPlayer tempPlayer = new MediaPlayer(media);

            tempPlayer.setOnReady(() -> {
                double duracion = media.getDuration().toSeconds();
                callback.accept(duracion);
                tempPlayer.dispose();
                tempFile.delete();
            });

            tempPlayer.setOnError(() -> {
                System.err.println("Error al obtener duración: " + tempPlayer.getError());
                callback.accept(0.0);
                tempPlayer.dispose();
                tempFile.delete();
            });

        } catch (Exception e) {
            System.err.println("Error al obtener duración: " + e.getMessage());
            callback.accept(0.0);
        }
    }

    // --------------------------
    // Métodos de compatibilidad y utilidad
    // --------------------------

    /**
     * Verifica si está reproduciendo actualmente
     */
    public boolean estaReproduciendo() {
        return estadoActual instanceof EstadoReproduciendo &&
                mediaPlayer != null &&
                mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }

    /**
     * Verifica si está pausado
     */
    public boolean estaPausado() {
        return estadoActual instanceof EstadoPausado ||
                (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED);
    }

    /**
     * Obtiene el MediaPlayer actual (para acceso directo si es necesario)
     */
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    // --------------------------
    // Getters y Setters existentes
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

    // Callback que se ejecuta cuando cambia de canción
    private Runnable onSongChange;

    /**
     * Permite registrar un callback para cuando cambie la canción
     */
    public void setOnSongChange(Runnable onSongChange) {
        this.onSongChange = onSongChange;
    }

    /**
     * Método interno para notificar cambio de canción
     */
    public void notificarCambioCancion() {
        if (onSongChange != null) {
            onSongChange.run();
        }
    }


}
