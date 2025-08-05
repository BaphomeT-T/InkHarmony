package UserInterface.CustomerControl;

import BusinessLogic.ReproductorMP3;
import BusinessLogic.EstadoPausado;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import DataAccessComponent.DAO.CancionDAO;
import DataAccessComponent.DTO.CancionDTO;

/**
 * Controlador actualizado para trabajar con la arquitectura existente de ReproductorMP3
 * usando JavaFX MediaPlayer
 */
public class ReproduccionCancionController implements Initializable {

    private ReproductorMP3 reproductor;
    private Timeline timeline;
    private double duracionRealCancion = 0;
    private double tiempoActualSegundos = 0;
    private List<CancionDTO> cancionesDTO; // Para mostrar información
    private List<byte[]> cancionesByteArray; // Para el reproductor

    @FXML private Label lblArtista;
    @FXML private Label lblArtista1;
    @FXML private Label lblNombreCancion;
    @FXML private Label lblNombreCancion1;
    @FXML private Label lblTiempoCancion;
    @FXML private Pane panImageAlbum;
    @FXML private Pane panImageAlbum1;
    @FXML private ProgressBar pgbProgresoCancion;
    @FXML private TextField txtBuscarCancion;
    @FXML private Button btnReproducir;
    @FXML private ImageView imgPlayPause;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Cargar canciones desde la base de datos
            CancionDAO cancionDAO = new CancionDAO();
            cancionesDTO = cancionDAO.buscarTodo();

            if (!cancionesDTO.isEmpty()) {
                // Convertir a byte arrays para el reproductor
                cancionesByteArray = new ArrayList<>();
                for (CancionDTO cancion : cancionesDTO) {
                    if (cancion.getArchivoMP3() != null) {
                        cancionesByteArray.add(cancion.getArchivoMP3());
                    }
                }

                // Inicializar reproductor usando Singleton
                reproductor = ReproductorMP3.getInstancia(cancionesByteArray);

                // Mostrar información de la primera canción
                mostrarInformacionCancion(cancionesDTO.get(0));

                System.out.println("Reproductor inicializado con " + cancionesByteArray.size() + " canciones");
            } else {
                System.out.println("No hay canciones en la base de datos");
            }

        } catch (Exception e) {
            System.err.println("Error al cargar canciones: " + e.getMessage());
            e.printStackTrace();
        }

        inicializarTimeline();
    }

    /**
     * Inicializa el Timeline para actualizar el progreso
     */
    private void inicializarTimeline() {
        timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> actualizarProgressBar()));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Actualiza la barra de progreso y el tiempo
     */
    private void actualizarProgressBar() {
        if (reproductor != null && reproductor.estaReproduciendo()) {
            try {
                // Obtener tiempo actual del MediaPlayer
                tiempoActualSegundos = reproductor.getTiempoActual();

                // Actualizar barra de progreso
                if (duracionRealCancion > 0) {
                    double progreso = tiempoActualSegundos / duracionRealCancion;
                    progreso = Math.max(0.0, Math.min(1.0, progreso));
                    pgbProgresoCancion.setProgress(progreso);
                }

                // Actualizar etiqueta de tiempo
                actualizarTiempoCancion(tiempoActualSegundos, duracionRealCancion);

                // Verificar si la canción ha terminado (se maneja automáticamente en el reproductor)

            } catch (Exception ex) {
                pgbProgresoCancion.setProgress(0.0);
                lblTiempoCancion.setText("00:00 / 00:00");
            }
        } else if (reproductor != null && reproductor.estaPausado()) {
            // Si está pausado, mantener el tiempo actual
            actualizarTiempoCancion(tiempoActualSegundos, duracionRealCancion);
        }
    }

    /**
     * Actualiza la etiqueta de tiempo
     */
    private void actualizarTiempoCancion(double segundosActuales, double segundosTotales) {
        try {
            String tiempoActual = formatearTiempo(segundosActuales);
            String tiempoTotal = formatearTiempo(segundosTotales > 0 ? segundosTotales : 0);
            lblTiempoCancion.setText(tiempoActual + " / " + tiempoTotal);
        } catch (Exception e) {
            lblTiempoCancion.setText("00:00 / 00:00");
        }
    }

    /**
     * Formatea segundos a MM:SS
     */
    private String formatearTiempo(double segundos) {
        int minutos = (int) (segundos / 60);
        int segundosRestantes = (int) (segundos % 60);
        return String.format("%02d:%02d", minutos, segundosRestantes);
    }

    @FXML
    void clickAnterior(ActionEvent event) {
        if (reproductor != null) {
            reproductor.anterior();
            actualizarInformacionCancionActual();
            reiniciarProgresoYTiempo();
        }
    }

    @FXML
    void clickBuscarCancion(ActionEvent event) {
        System.out.println("Función de búsqueda por implementar");
    }

    @FXML
    void clickRegresarPagina(ActionEvent event) {
        cleanup();
        System.out.println("Regresando a la página anterior");
    }

    @FXML
    void clickReproducir(ActionEvent event) {
        if (reproductor == null) {
            System.out.println("Reproductor no inicializado");
            return;
        }

        try {
            if (reproductor.estaReproduciendo()) {
                // Pausar
                reproductor.pausar();
                timeline.stop();
                cambiarAImagenPlay();
                System.out.println("Reproducción pausada");

            } else if (reproductor.getEstado() instanceof EstadoPausado) {
                // Reanudar
                reproductor.reanudar();
                timeline.play();
                cambiarAImagenPause();
                System.out.println("Reproducción reanudada");

            } else {
                // Iniciar nueva reproducción
                reproductor.reproducir();
                timeline.play();
                cambiarAImagenPause();
                actualizarDuracionActual();
                System.out.println("Iniciando reproducción");
            }

        } catch (Exception e) {
            System.err.println("Error al controlar reproducción: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void clickSiguiente(ActionEvent event) {
        if (reproductor != null) {
            boolean estabaReproduciendo = reproductor.estaReproduciendo();

            reproductor.siguiente();
            actualizarInformacionCancionActual();
            reiniciarProgresoYTiempo();

            // Si estaba reproduciendo, continuar reproduciendo la nueva canción
            if (estabaReproduciendo) {
                timeline.play();
                cambiarAImagenPause();
            }
        }
    }

    /**
     * Actualiza la información mostrada de la canción actual
     */
    private void actualizarInformacionCancionActual() {
        if (reproductor != null && cancionesDTO != null) {
            int indiceActual = reproductor.getPlaylist().getIndiceActual();
            if (indiceActual >= 0 && indiceActual < cancionesDTO.size()) {
                mostrarInformacionCancion(cancionesDTO.get(indiceActual));
            }
        }
    }

    /**
     * Reinicia el progreso cuando cambia de canción
     */
    private void reiniciarProgresoYTiempo() {
        timeline.stop();
        pgbProgresoCancion.setProgress(0.0);
        tiempoActualSegundos = 0.0;
        duracionRealCancion = 0.0;
        lblTiempoCancion.setText("00:00 / 00:00");

        // Obtener duración de la nueva canción
        Platform.runLater(() -> {
            actualizarDuracionActual();
        });
    }

    /**
     * Actualiza la duración de la canción actual
     */
    private void actualizarDuracionActual() {
        if (reproductor != null) {
            reproductor.obtenerDuracionCancionActual(duracion -> {
                Platform.runLater(() -> {
                    if (duracion > 0) {
                        duracionRealCancion = duracion;
                        actualizarTiempoCancion(tiempoActualSegundos, duracionRealCancion);
                    }
                });
            });
        }
    }

    /**
     * Cambia la imagen a play
     */
    private void cambiarAImagenPlay() {
        try {
            Image imagenPlay = new Image(getClass().getResourceAsStream("../Resources/img/boton-de-play.png"));
            imgPlayPause.setImage(imagenPlay);
            imgPlayPause.setFitWidth(29.0);
            imgPlayPause.setFitHeight(26.0);
        } catch (Exception e) {
            try {
                Image imagenPlay = new Image(getClass().getResourceAsStream("../Resources/img/play.png"));
                imgPlayPause.setImage(imagenPlay);
                imgPlayPause.setFitWidth(29.0);
                imgPlayPause.setFitHeight(26.0);
            } catch (Exception ex) {
                System.out.println("No se pudo cargar imagen de play");
            }
        }
        btnReproducir.setStyle("-fx-background-color: #070F2B; -fx-border-radius: 25px; -fx-background-radius: 25px;");
    }

    /**
     * Cambia la imagen a pause
     */
    private void cambiarAImagenPause() {
        try {
            Image imagenPause = new Image(getClass().getResourceAsStream("../Resources/img/boton-de-pausa.png"));
            imgPlayPause.setImage(imagenPause);
            imgPlayPause.setFitWidth(40.0);
            imgPlayPause.setFitHeight(35.0);
        } catch (Exception e) {
            try {
                Image imagenPause = new Image(getClass().getResourceAsStream("../Resources/img/pause.png"));
                imgPlayPause.setImage(imagenPause);
                imgPlayPause.setFitWidth(35.0);
                imgPlayPause.setFitHeight(35.0);
            } catch (Exception ex) {
                System.out.println("No se pudo cargar imagen de pause");
            }
        }
        btnReproducir.setStyle("-fx-background-color: #070F2B; -fx-border-radius: 25px; -fx-background-radius: 25px;");
    }

    /**
     * Muestra información de la canción en la interfaz
     */
    public void mostrarInformacionCancion(CancionDTO cancion) {
        if (cancion != null) {
            // Mostrar título
            lblNombreCancion.setText(cancion.getTitulo());
            lblNombreCancion1.setText(cancion.getTitulo());

            // Mostrar artista
            if (cancion.getArtistas() != null && !cancion.getArtistas().isEmpty()) {
                String nombreArtista = cancion.getArtistas().get(0).getNombre();
                lblArtista.setText(nombreArtista);
                lblArtista1.setText(nombreArtista);
            } else {
                lblArtista.setText("Desconocido");
                lblArtista1.setText("Desconocido");
            }

            // Mostrar duración
            double duracion = cancion.getDuracion();
            String duracionFormateada = formatearTiempo(duracion);
            lblTiempoCancion.setText("00:00 / " + duracionFormateada);

            // Actualizar duración para el control
            duracionRealCancion = duracion;
            tiempoActualSegundos = 0.0;

            // Mostrar portada
            mostrarPortadaCancion(cancion);
        }
    }

    /**
     * Muestra la portada de la canción
     */
    private void mostrarPortadaCancion(CancionDTO cancion) {
        try {
            Image portada;

            if (cancion.getPortada() != null && cancion.getPortada().length > 0) {
                java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(cancion.getPortada());
                portada = new Image(bis);
            } else {
                portada = new Image(getClass().getResourceAsStream("../Resources/img/portada-generica.png"));
                if (portada.isError()) {
                    portada = new Image(getClass().getResourceAsStream("../Resources/img/default-cover.png"));
                }
                if (portada.isError()) {
                    portada = new Image(getClass().getResourceAsStream("../Resources/img/no-image.png"));
                }
                if (portada.isError()) {
                    portada = new Image(getClass().getResourceAsStream("../Resources/img/album-cover.png"));
                }
            }

            if (!portada.isError()) {
                // Panel grande
                ImageView imageViewGrande = new ImageView(portada);
                imageViewGrande.setFitWidth(panImageAlbum.getPrefWidth());
                imageViewGrande.setFitHeight(panImageAlbum.getPrefHeight());
                imageViewGrande.setPreserveRatio(true);
                imageViewGrande.setSmooth(true);

                panImageAlbum.getChildren().clear();
                panImageAlbum.getChildren().add(imageViewGrande);

                // Panel pequeño
                ImageView imageViewPequeno = new ImageView(portada);
                imageViewPequeno.setFitWidth(panImageAlbum1.getPrefWidth());
                imageViewPequeno.setFitHeight(panImageAlbum1.getPrefHeight());
                imageViewPequeno.setPreserveRatio(true);
                imageViewPequeno.setSmooth(true);

                panImageAlbum1.getChildren().clear();
                panImageAlbum1.getChildren().add(imageViewPequeno);
            } else {
                mostrarPortadaPorDefecto();
            }

        } catch (Exception e) {
            System.out.println("Error al cargar portada: " + e.getMessage());
            mostrarPortadaPorDefecto();
        }
    }

    /**
     * Muestra portada por defecto
     */
    private void mostrarPortadaPorDefecto() {
        panImageAlbum.getChildren().clear();
        panImageAlbum1.getChildren().clear();

        panImageAlbum.setStyle("-fx-background-color: #9290C2; -fx-border-color: #070F2B; -fx-border-width: 2px;");
        panImageAlbum1.setStyle("-fx-background-color: #9290C2; -fx-border-color: #070F2B; -fx-border-width: 2px;");

        Label lblSinPortada = new Label("♪");
        lblSinPortada.setStyle("-fx-text-fill: white; -fx-font-size: 48px;");
        lblSinPortada.setLayoutX(panImageAlbum.getPrefWidth() / 2 - 15);
        lblSinPortada.setLayoutY(panImageAlbum.getPrefHeight() / 2 - 25);
        panImageAlbum.getChildren().add(lblSinPortada);

        Label lblSinPortadaPequeno = new Label("♪");
        lblSinPortadaPequeno.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");
        lblSinPortadaPequeno.setLayoutX(panImageAlbum1.getPrefWidth() / 2 - 8);
        lblSinPortadaPequeno.setLayoutY(panImageAlbum1.getPrefHeight() / 2 - 10);
        panImageAlbum1.getChildren().add(lblSinPortadaPequeno);
    }

    /**
     * Limpia recursos al cerrar
     */
    public void cleanup() {
        if (timeline != null) {
            timeline.stop();
        }
        if (reproductor != null) {
            reproductor.detener();
        }
    }
}
