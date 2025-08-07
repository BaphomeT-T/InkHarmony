package UserInterface.CustomerControl.Playlist;

import BusinessLogic.ReproductorMP3;
import BusinessLogic.EstadoPausado;
import DataAccessComponent.DTO.CancionDTO;
import DataAccessComponent.DTO.PlaylistDTO;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controlador para la barra de reproducción específica de playlists.
 * Se encarga de manejar la reproducción de una playlist seleccionada,
 * mostrando los controles de reproducción en la parte inferior de la interfaz.
 * 
 * @author Grupo B
 */
public class BarraReproduccionPlaylistController implements Initializable {

    @FXML private HBox barraReproduccion;
    @FXML private ImageView imgCancionActual;
    @FXML private Label lblNombreCancion;
    @FXML private Label lblNombreArtista;
    @FXML private Button btnAnterior;
    @FXML private Button btnPlayPause;
    @FXML private Button btnSiguiente;
    @FXML private Slider pgbProgresoCancion;
    @FXML private Label lblTiempoActual;
    @FXML private Label lblTiempoTotal;
    @FXML private Button btnVolumen;
    @FXML private Slider sliderVolumen;

    private ReproductorMP3 reproductor;
    private Timeline timeline;
    private PlaylistDTO playlistActual;
    private List<CancionDTO> cancionesActuales;
    private boolean usuarioArrastrando = false;
    private double duracionRealCancion = 0;
    private double tiempoActualSegundos = 0;
    private Image imagenPlay;
    private Image imagenPause;
    private Image imagenPortadaGenerica;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inicializarImagenes();
        inicializarTimeline();
        configurarSliders();
        ocultarBarra();
        
        // Configurar el reproductor como singleton
        reproductor = ReproductorMP3.getInstancia(new ArrayList<>());
        
        // Listener para cambios de canción
        if (reproductor != null) {
            reproductor.setOnSongChange(() -> {
                Platform.runLater(() -> {
                    actualizarInformacionCancionActual();
                    reiniciarProgresoYTiempo();
                    cambiarIconoReproduccion(false);
                    timeline.play();
                });
            });
        }
    }

    /**
     * Inicializa las imágenes que se usarán en la interfaz
     */
    private void inicializarImagenes() {
        try {
            imagenPlay = new Image(getClass().getResourceAsStream("/UserInterface/Resources/img/boton-de-play.png"));
            imagenPause = new Image(getClass().getResourceAsStream("/UserInterface/Resources/img/boton-de-pausa.png"));
            imagenPortadaGenerica = new Image(getClass().getResourceAsStream("/UserInterface/Resources/img/portada-generica.jpg"));
        } catch (Exception e) {
            System.err.println("Error cargando imágenes para la barra de reproducción: " + e.getMessage());
        }
    }

    /**
     * Configura el Timeline para actualización periódica de la barra de progreso
     */
    private void inicializarTimeline() {
        timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> actualizarProgressBar()));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Configura los sliders de progreso y volumen
     */
    private void configurarSliders() {
        // Configurar slider de progreso
        pgbProgresoCancion.setOnMousePressed(e -> usuarioArrastrando = true);
        pgbProgresoCancion.setOnMouseReleased(e -> {
            usuarioArrastrando = false;
            double progreso = pgbProgresoCancion.getValue();
            saltarAProgreso(progreso);
        });

        // Configurar slider de volumen
        sliderVolumen.setValue(0.5); // Volumen inicial al 50%
        sliderVolumen.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (reproductor != null && reproductor.getMediaPlayer() != null) {
                reproductor.getMediaPlayer().setVolume(newValue.doubleValue());
            }
        });
    }

    /**
     * Inicia la reproducción de una playlist completa
     * @param playlist La playlist a reproducir
     * @param canciones Lista de canciones de la playlist
     */
    public void iniciarReproduccionPlaylist(PlaylistDTO playlist, List<CancionDTO> canciones) {
        if (playlist == null || canciones == null || canciones.isEmpty()) {
            System.out.println("No se puede reproducir: playlist o canciones nulas/vacías");
            return;
        }

        this.playlistActual = playlist;
        this.cancionesActuales = new ArrayList<>(canciones);

        // Convertir canciones a byte arrays para el reproductor
        List<byte[]> archivosMp3 = canciones.stream()
                .map(CancionDTO::getArchivoMP3)
                .filter(archivo -> archivo != null)
                .collect(Collectors.toList());

        if (archivosMp3.isEmpty()) {
            System.out.println("No hay archivos MP3 válidos en la playlist");
            return;
        }

        // Cambiar playlist en el reproductor
        reproductor.cambiarPlaylist(archivosMp3);
        reproductor.reproducir();

        // Mostrar barra y actualizar información
        mostrarBarra();
        actualizarInformacionCancionActual();
        timeline.play();
        cambiarIconoReproduccion(false);

        System.out.println("Iniciando reproducción de playlist: " + playlist.getTituloPlaylist());
    }

    /**
     * Inicia la reproducción desde una canción específica de la playlist
     * @param playlist La playlist a reproducir
     * @param canciones Lista de canciones de la playlist
     * @param indiceCancion Índice de la canción desde la cual iniciar
     */
    public void iniciarReproduccionDesdeCancion(PlaylistDTO playlist, List<CancionDTO> canciones, int indiceCancion) {
        iniciarReproduccionPlaylist(playlist, canciones);
        
        if (indiceCancion >= 0 && indiceCancion < canciones.size()) {
            reproductor.getPlaylist().setIndiceActual(indiceCancion);
            reproductor.reproducir();
            actualizarInformacionCancionActual();
        }
    }

    /**
     * Muestra la barra de reproducción
     */
    public void mostrarBarra() {
        barraReproduccion.setVisible(true);
        barraReproduccion.setManaged(true);
    }

    /**
     * Oculta la barra de reproducción
     */
    public void ocultarBarra() {
        barraReproduccion.setVisible(false);
        barraReproduccion.setManaged(false);
    }

    /**
     * Verifica si la barra está visible
     * @return true si la barra está visible
     */
    public boolean estaVisible() {
        return barraReproduccion.isVisible();
    }

    /**
     * Maneja el clic en el botón anterior
     */
    @FXML
    private void clickAnterior(ActionEvent event) {
        if (reproductor != null) {
            reproductor.anterior();
        }
    }

    /**
     * Maneja el clic en el botón play/pause
     */
    @FXML
    private void clickPlayPause(ActionEvent event) {
        if (reproductor == null) return;

        if (reproductor.estaReproduciendo()) {
            reproductor.pausar();
            timeline.stop();
            cambiarIconoReproduccion(true);
        } else {
            reproductor.reproducir();
            if (reproductor.getEstado() instanceof EstadoPausado) {
                reproductor.reanudar();
            }
            actualizarDuracionActual();
            timeline.play();
            cambiarIconoReproduccion(false);
        }
    }

    /**
     * Maneja el clic en el botón siguiente
     */
    @FXML
    private void clickSiguiente(ActionEvent event) {
        if (reproductor != null) {
            reproductor.siguiente();
        }
    }

    /**
     * Maneja el clic en el botón de volumen (mute/unmute)
     */
    @FXML
    private void clickVolumen(ActionEvent event) {
        if (reproductor != null && reproductor.getMediaPlayer() != null) {
            boolean esMuted = reproductor.getMediaPlayer().isMute();
            reproductor.getMediaPlayer().setMute(!esMuted);
            
            // Cambiar icono del botón según el estado
            btnVolumen.setText(esMuted ? "🔊" : "🔇");
        }
    }

    /**
     * Actualiza la barra de progreso y información de tiempo
     */
    private void actualizarProgressBar() {
        if (reproductor != null && reproductor.estaReproduciendo()) {
            tiempoActualSegundos = reproductor.getTiempoActual();
            if (duracionRealCancion > 0 && !usuarioArrastrando) {
                double progreso = Math.max(0.0, Math.min(1.0, tiempoActualSegundos / duracionRealCancion));
                pgbProgresoCancion.setValue(progreso);
            }
            actualizarLabelsTime(tiempoActualSegundos, duracionRealCancion);
        } else if (reproductor != null && reproductor.estaPausado()) {
            actualizarLabelsTime(tiempoActualSegundos, duracionRealCancion);
        }
    }

    /**
     * Salta a un punto específico de la canción
     * @param progreso Progreso entre 0.0 y 1.0
     */
    private void saltarAProgreso(double progreso) {
        if (duracionRealCancion > 0 && reproductor != null && reproductor.getMediaPlayer() != null) {
            double segundos = progreso * duracionRealCancion;
            reproductor.getMediaPlayer().seek(Duration.seconds(segundos));
            tiempoActualSegundos = segundos;
            actualizarLabelsTime(tiempoActualSegundos, duracionRealCancion);
        }
    }

    /**
     * Actualiza los labels de tiempo actual y total
     */
    private void actualizarLabelsTime(double segundosActuales, double segundosTotales) {
        String tiempoActual = formatearTiempo(segundosActuales);
        String tiempoTotal = formatearTiempo(segundosTotales > 0 ? segundosTotales : 0);
        lblTiempoActual.setText(tiempoActual);
        lblTiempoTotal.setText(tiempoTotal);
    }

    /**
     * Formatea tiempo en segundos a formato mm:ss
     */
    private String formatearTiempo(double segundos) {
        if (Double.isNaN(segundos)) return "00:00";
        int minutos = (int) (segundos / 60);
        int segundosRestantes = (int) (segundos % 60);
        return String.format("%02d:%02d", minutos, segundosRestantes);
    }

    /**
     * Actualiza la información de la canción actual en la barra
     */
    private void actualizarInformacionCancionActual() {
        if (reproductor == null || cancionesActuales == null || cancionesActuales.isEmpty()) return;

        int indiceActual = reproductor.getPlaylist().getIndiceActual();
        if (indiceActual >= 0 && indiceActual < cancionesActuales.size()) {
            CancionDTO cancionActual = cancionesActuales.get(indiceActual);
            mostrarInformacionCancion(cancionActual);
        }
    }

    /**
     * Muestra la información de una canción específica en la barra
     */
    private void mostrarInformacionCancion(CancionDTO cancion) {
        if (cancion == null) return;

        // Actualizar título
        lblNombreCancion.setText(cancion.getTitulo());

        // Actualizar artista
        if (cancion.getArtistas() != null && !cancion.getArtistas().isEmpty()) {
            String artista = cancion.getArtistas().stream()
                    .map(a -> a.getNombre())
                    .collect(Collectors.joining(", "));
            lblNombreArtista.setText(artista);
        } else {
            lblNombreArtista.setText("Artista Desconocido");
        }

        // Actualizar portada
        try {
            Image portada;
            if (cancion.getPortada() != null && cancion.getPortada().length > 0) {
                portada = new Image(new ByteArrayInputStream(cancion.getPortada()));
            } else {
                portada = imagenPortadaGenerica;
            }
            imgCancionActual.setImage(portada);
        } catch (Exception e) {
            imgCancionActual.setImage(imagenPortadaGenerica);
        }

        // Actualizar duración
        duracionRealCancion = cancion.getDuracion();
        actualizarLabelsTime(0, duracionRealCancion);
    }

    /**
     * Reinicia el progreso y tiempo cuando cambia la canción
     */
    private void reiniciarProgresoYTiempo() {
        timeline.stop();
        pgbProgresoCancion.setValue(0.0);
        tiempoActualSegundos = 0.0;
        duracionRealCancion = 0.0;
        actualizarLabelsTime(0, 0);
        
        // Pequeño delay para obtener la duración real
        javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(Duration.millis(500));
        delay.setOnFinished(e -> actualizarDuracionActual());
        delay.play();
    }

    /**
     * Obtiene la duración real de la canción actual de forma asíncrona
     */
    private void actualizarDuracionActual() {
        if (reproductor == null) return;
        
        reproductor.obtenerDuracionCancionActual(duracion -> {
            Platform.runLater(() -> {
                this.duracionRealCancion = duracion;
                actualizarLabelsTime(tiempoActualSegundos, duracionRealCancion);
            });
        });
    }

    /**
     * Cambia el icono del botón de reproducción
     * @param aPlay true para mostrar icono de play, false para pause
     */
    private void cambiarIconoReproduccion(boolean aPlay) {
        if (aPlay && imagenPlay != null) {
            ImageView iconView = new ImageView(imagenPlay);
            iconView.setFitHeight(24);
            iconView.setFitWidth(24);
            btnPlayPause.setGraphic(iconView);
        } else if (!aPlay && imagenPause != null) {
            ImageView iconView = new ImageView(imagenPause);
            iconView.setFitHeight(24);
            iconView.setFitWidth(24);
            btnPlayPause.setGraphic(iconView);
        }
    }

    /**
     * Detiene la reproducción y oculta la barra
     */
    public void detenerReproduccion() {
        if (timeline != null) {
            timeline.stop();
        }
        if (reproductor != null) {
            reproductor.detener();
        }
        ocultarBarra();
    }

    /**
     * Limpieza de recursos cuando se cierra la vista
     */
    public void cleanup() {
        detenerReproduccion();
    }

    /**
     * Obtiene la playlist actualmente en reproducción
     * @return La playlist actual o null si no hay ninguna
     */
    public PlaylistDTO getPlaylistActual() {
        return playlistActual;
    }

    /**
     * Verifica si hay una reproducción activa
     * @return true si hay reproducción activa
     */
    public boolean hayReproduccionActiva() {
        return reproductor != null && 
               (reproductor.estaReproduciendo() || reproductor.estaPausado()) &&
               playlistActual != null;
    }
}
