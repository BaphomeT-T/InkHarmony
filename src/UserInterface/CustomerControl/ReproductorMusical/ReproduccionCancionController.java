package UserInterface.CustomerControl.ReproductorMusical;

import BusinessLogic.ReproductorMP3;
import BusinessLogic.EstadoPausado;
import DataAccessComponent.DAO.CancionDAO;
import DataAccessComponent.DTO.CancionDTO;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controlador para la vista de reproducción de una sola canción o una playlist continua.
 * Gestiona la carga de canciones, la actualización de la interfaz de usuario (UI),
 * el control de la reproducción (play, pause, etc.), la barra de progreso y el cambio de canciones.
 * @author Grupo B
 */
public class ReproduccionCancionController implements Initializable {

    private ReproductorMP3 reproductor;
    private Timeline timeline;
    private double duracionRealCancion = 0;
    private double tiempoActualSegundos = 0;
    private List<CancionDTO> cancionesDTO;
    private List<byte[]> cancionesByteArray;
    private boolean usuarioArrastrando = false;

    @FXML private Slider pgbProgresoCancion;
    @FXML private Label lblArtista;
    @FXML private Label lblArtista1;
    @FXML private Label lblNombreCancion;
    @FXML private Label lblNombreCancion1;
    @FXML private Label lblTiempoCancion;
    @FXML private TextField txtBuscarCancion;
    @FXML private ImageView imgPlayPause;
    @FXML private ImageView imgAlbumArtCentral;
    @FXML private ImageView imgAlbumActualAbajo;
    @FXML private Pane panImageAlbum1;

    /**
     * Se ejecuta al inicializar el controlador.
     * Carga las canciones, configura el timeline y los listeners de la UI.
     * @param location La ubicación utilizada para resolver rutas relativas para el objeto raíz, o null si la ubicación no se conoce.
     * @param resources Los recursos utilizados para localizar el objeto raíz, o null si el objeto raíz no fue localizado.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarCanciones();
        inicializarTimeline();

        pgbProgresoCancion.setOnMousePressed(e -> usuarioArrastrando = true);
        pgbProgresoCancion.setOnMouseReleased(e -> {
            usuarioArrastrando = false;
            double progreso = pgbProgresoCancion.getValue();
            saltarAProgreso(progreso);
        });

        if (reproductor != null) {
            reproductor.setOnSongChange(() -> {
                Platform.runLater(() -> {
                    actualizarInformacionCancionActual();
                    reiniciarProgresoYTiempo();
                    cambiarAImagenPause();
                    timeline.play();
                });
            });
        }
    }

    /**
     * Salta a un punto específico de la canción basado en el progreso del slider.
     * @param progreso El valor del slider, de 0.0 a 1.0.
     */
    private void saltarAProgreso(double progreso) {
        if (duracionRealCancion > 0 && reproductor != null && reproductor.getMediaPlayer() != null) {
            double segundos = progreso * duracionRealCancion;
            reproductor.getMediaPlayer().seek(Duration.seconds(segundos));
            tiempoActualSegundos = segundos;
            actualizarTiempoCancion(tiempoActualSegundos, duracionRealCancion);
        }
    }

    /**
     * Carga todas las canciones desde la base de datos a través del DAO,
     * prepara los datos para el reproductor e inicializa la UI con la primera canción.
     */
    private void cargarCanciones() {
        try {
            CancionDAO cancionDAO = new CancionDAO();
            cancionesDTO = cancionDAO.buscarTodo();
            cancionesByteArray = new ArrayList<>();

            for (CancionDTO cancion : cancionesDTO) {
                if (cancion.getArchivoMP3() != null) {
                    cancionesByteArray.add(cancion.getArchivoMP3());
                }
            }

            if (!cancionesByteArray.isEmpty()) {
                reproductor = ReproductorMP3.getInstancia(cancionesByteArray);
                actualizarInformacionCancionActual();
            } else {
                System.out.println("No hay canciones en la base de datos");
            }

        } catch (Exception e) {
            System.err.println("Error al cargar canciones: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Configura el Timeline para que actualice la barra de progreso periódicamente.
     */
    private void inicializarTimeline() {
        timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> actualizarProgressBar()));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Método llamado por el Timeline. Actualiza el valor del slider de progreso
     * y el label del tiempo mientras la canción se reproduce.
     */
    private void actualizarProgressBar() {
        if (reproductor != null && reproductor.estaReproduciendo()) {
            tiempoActualSegundos = reproductor.getTiempoActual();
            if (duracionRealCancion > 0 && !usuarioArrastrando) {
                double progreso = Math.max(0.0, Math.min(1.0, tiempoActualSegundos / duracionRealCancion));
                pgbProgresoCancion.setValue(progreso);
            }
            actualizarTiempoCancion(tiempoActualSegundos, duracionRealCancion);
        } else if (reproductor != null && reproductor.estaPausado()) {
            actualizarTiempoCancion(tiempoActualSegundos, duracionRealCancion);
        }
    }

    /**
     * Actualiza el texto de la etiqueta de tiempo con el formato mm:ss.
     * @param segundosActuales El tiempo transcurrido de la canción.
     * @param segundosTotales La duración total de la canción.
     */
    private void actualizarTiempoCancion(double segundosActuales, double segundosTotales) {
        String tiempoActual = formatearTiempo(segundosActuales);
        String tiempoTotal = formatearTiempo(segundosTotales > 0 ? segundosTotales : 0);
        lblTiempoCancion.setText(tiempoActual + " / " + tiempoTotal);
    }

    /**
     * Convierte un valor en segundos a un formato de string "mm:ss".
     * @param segundos El tiempo en segundos a formatear.
     * @return El tiempo formateado como un String.
     */
    private String formatearTiempo(double segundos) {
        int minutos = (int) (segundos / 60);
        int segundosRestantes = (int) (segundos % 60);
        return String.format("%02d:%02d", minutos, segundosRestantes);
    }

    /**
     * Maneja el evento de clic en el botón "Anterior".
     * @param event El evento de acción.
     */
    @FXML void clickAnterior(ActionEvent event) {
        if (reproductor != null) reproductor.anterior();
    }

    /**
     * Maneja el evento de clic en el botón "Siguiente".
     * @param event El evento de acción.
     */
    @FXML void clickSiguiente(ActionEvent event) {
        if (reproductor != null) reproductor.siguiente();
    }

    /**
     * Maneja el evento de clic en el botón de "Reproducir/Pausar".
     * @param event El evento de acción.
     */
    @FXML void clickReproducir(ActionEvent event) {
        if (reproductor == null) return;
        if (reproductor.estaReproduciendo()) {
            reproductor.pausar();
            timeline.stop();
            cambiarAImagenPlay();
        } else {
            reproductor.reproducir();
            if (reproductor.getEstado() instanceof EstadoPausado) {
                reproductor.reanudar();
            }
            actualizarDuracionActual();
            timeline.play();
            cambiarAImagenPause();
        }
    }

    /**
     * Maneja el evento de clic en el botón de búsqueda (actualmente es un placeholder).
     * @param event El evento de acción.
     */
    @FXML void clickBuscarCancion(ActionEvent event) {
        System.out.println("Función de búsqueda por implementar");
    }

    /**
     * Reinicia la barra de progreso y las etiquetas de tiempo cuando cambia una canción.
     */
    private void reiniciarProgresoYTiempo() {
        timeline.stop();
        pgbProgresoCancion.setValue(0.0);
        tiempoActualSegundos = 0.0;
        duracionRealCancion = 0.0;
        lblTiempoCancion.setText("00:00 / 00:00");
        javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(Duration.millis(500));
        delay.setOnFinished(e -> actualizarDuracionActual());
        delay.play();
    }

    /**
     * Obtiene de forma asíncrona la duración de la canción actual y actualiza la UI.
     */
    private void actualizarDuracionActual() {
        if (reproductor == null) return;
        reproductor.obtenerDuracionCancionActual(duracion -> {
            Platform.runLater(() -> {
                this.duracionRealCancion = duracion;
                actualizarTiempoCancion(tiempoActualSegundos, duracionRealCancion);
            });
        });
    }

    /**
     * Obtiene la información de la canción que se está reproduciendo actualmente
     * y llama al método para mostrarla en la UI.
     */
    private void actualizarInformacionCancionActual() {
        if (reproductor == null) return;
        int indiceActual = reproductor.getPlaylist().getIndiceActual();
        if (indiceActual >= 0 && indiceActual < cancionesDTO.size()) {
            mostrarInformacionCancion(cancionesDTO.get(indiceActual));
        }
    }

    /**
     * Cambia el icono del botón de reproducción a "Play".
     */
    private void cambiarAImagenPlay() {
        imgPlayPause.setImage(new Image(getClass().getResourceAsStream("/UserInterface/Resources/img/boton-de-play.png")));
    }

    /**
     * Cambia el icono del botón de reproducción a "Pausa".
     */
    private void cambiarAImagenPause() {
        imgPlayPause.setImage(new Image(getClass().getResourceAsStream("/UserInterface/Resources/img/boton-de-pausa.png")));
    }

    /**
     * Muestra la información de una canción (título, artista, portada) en la UI.
     * @param cancion El DTO de la canción a mostrar.
     */
    public void mostrarInformacionCancion(CancionDTO cancion) {
        lblNombreCancion.setText(cancion.getTitulo());
        lblNombreCancion1.setText(cancion.getTitulo());
        if (cancion.getArtistas() != null && !cancion.getArtistas().isEmpty()) {
            String artista = cancion.getArtistas().get(0).getNombre();
            lblArtista.setText(artista);
            lblArtista1.setText(artista);
        } else {
            lblArtista.setText("Desconocido");
            lblArtista1.setText("Desconocido");
        }
        duracionRealCancion = cancion.getDuracion();
        actualizarTiempoCancion(0, duracionRealCancion);
        mostrarPortadaCancion(cancion);
    }

    /**
     * Carga la imagen de portada de una canción en los `ImageView` correspondientes.
     * @param cancion El DTO de la canción cuya portada se va a mostrar.
     */
    private void mostrarPortadaCancion(CancionDTO cancion) {
        try {
            Image portada;
            if (cancion.getPortada() != null && cancion.getPortada().length > 0) {
                portada = new Image(new ByteArrayInputStream(cancion.getPortada()));
            } else {
                portada = new Image(getClass().getResourceAsStream("/UserInterface/Resources//img/portada-generica.jpg"));
            }
            imgAlbumArtCentral.setImage(portada);
            imgAlbumActualAbajo.setImage(portada);
        } catch (Exception e) {
            System.err.println("Error cargando portada: " + e.getMessage());
            Image errorImage = new Image(getClass().getResourceAsStream("/UserInterface/Resources//img/portada-generica.jpg"));
            imgAlbumArtCentral.setImage(errorImage);
            imgAlbumActualAbajo.setImage(errorImage);
        }
    }

    /**
     * Método de limpieza para detener los recursos activos (Timeline, Reproductor)
     * cuando la vista se cierra.
     */
    public void cleanup() {
        if (timeline != null) timeline.stop();
        if (reproductor != null) reproductor.detener();
    }
}
