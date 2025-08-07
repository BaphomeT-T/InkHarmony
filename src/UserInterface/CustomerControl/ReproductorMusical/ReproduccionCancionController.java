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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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
    private List<CancionDTO> cancionesOriginales; // Lista completa sin filtrar
    private List<byte[]> cancionesByteArray;
    private boolean usuarioArrastrando = false;

    // Variables para la búsqueda
    private VBox resultadosBusqueda;
    private Timeline busquedaTimeline;
    private AnchorPane contenedorTop; // Referencia al contenedor top

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
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarCanciones();
        inicializarTimeline();
        configurarBusqueda();

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
     * Configura la funcionalidad de búsqueda en tiempo real
     */
    private void configurarBusqueda() {
        // Timeline para búsqueda con delay
        busquedaTimeline = new Timeline(new KeyFrame(Duration.millis(300), e -> realizarBusqueda()));
        busquedaTimeline.setCycleCount(1);

        // Listener para el campo de texto
        txtBuscarCancion.textProperty().addListener((observable, oldValue, newValue) -> {
            if (busquedaTimeline != null) {
                busquedaTimeline.stop();
            }
            if (newValue != null && !newValue.trim().isEmpty()) {
                if (busquedaTimeline != null) {
                    busquedaTimeline.play();
                }
            } else {
                ocultarResultadosBusqueda();
            }
        });

        // Ocultar resultados al perder el foco
        txtBuscarCancion.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                Timeline delay = new Timeline(new KeyFrame(Duration.millis(150), e -> ocultarResultadosBusqueda()));
                delay.play();
            }
        });

        // Configurar el contenedor de resultados después de que la scene esté lista
        Platform.runLater(() -> configurarContenedorResultados());
    }

    /**
     * Configura el contenedor de resultados de búsqueda
     */
    private void configurarContenedorResultados() {
        if (txtBuscarCancion.getScene() == null) return;

        // Obtener el StackPane raíz
        StackPane root = (StackPane) txtBuscarCancion.getScene().lookup("#rootStackPane");

        if (resultadosBusqueda == null && root != null) {
            resultadosBusqueda = new VBox();
            resultadosBusqueda.setSpacing(2);
            resultadosBusqueda.setPadding(new Insets(5));
            resultadosBusqueda.setMaxWidth(400);
            resultadosBusqueda.setMaxHeight(200);
            resultadosBusqueda.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-border-color: #ccc;" +
                            "-fx-border-radius: 6;" +
                            "-fx-background-radius: 6;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 8, 0, 0, 3);"
            );
            resultadosBusqueda.setManaged(false);
            resultadosBusqueda.setVisible(false);

            root.getChildren().add(resultadosBusqueda);

            // Coloca los resultados justo debajo del campo de texto
            Platform.runLater(() -> {
                double x = txtBuscarCancion.localToScene(0, 0).getX();
                double y = txtBuscarCancion.localToScene(0, 0).getY() + txtBuscarCancion.getHeight();

                resultadosBusqueda.setLayoutX(x);
                resultadosBusqueda.setLayoutY(y);
            });
        }
    }


    /**
     * Realiza la búsqueda de canciones
     */
    private void realizarBusqueda() {
        if (txtBuscarCancion == null || cancionesOriginales == null) {
            return;
        }

        String textoBusqueda = txtBuscarCancion.getText().trim().toLowerCase();

        if (textoBusqueda.isEmpty()) {
            ocultarResultadosBusqueda();
            return;
        }

        try {
            List<CancionDTO> resultados = cancionesOriginales.stream()
                    .filter(cancion -> {
                        if (cancion == null || cancion.getTitulo() == null) {
                            return false;
                        }

                        // Buscar por título
                        boolean coincideTitulo = cancion.getTitulo().toLowerCase().contains(textoBusqueda);

                        // Buscar por artista
                        boolean coincideArtista = false;
                        if (cancion.getArtistas() != null) {
                            coincideArtista = cancion.getArtistas().stream()
                                    .anyMatch(artista -> artista != null &&
                                            artista.getNombre() != null &&
                                            artista.getNombre().toLowerCase().contains(textoBusqueda));
                        }

                        return coincideTitulo || coincideArtista;
                    })
                    .limit(5)
                    .collect(Collectors.toList());

            Platform.runLater(() -> mostrarResultadosBusqueda(resultados, textoBusqueda));

        } catch (Exception e) {
            System.err.println("Error en búsqueda: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Muestra los resultados de la búsqueda
     */
    private void mostrarResultadosBusqueda(List<CancionDTO> resultados, String textoBusqueda) {
        if (resultadosBusqueda == null) {
            return;
        }

        resultadosBusqueda.getChildren().clear();

        if (resultados.isEmpty()) {
            Label noResultados = new Label("No se encontró la canción: \"" + textoBusqueda + "\"");
            noResultados.setTextFill(Color.web("#AFAFC7"));
            noResultados.setStyle(
                    "-fx-font-size: 13px; " +
                            "-fx-padding: 12px 15px; " +
                            "-fx-font-style: italic;"
            );
            resultadosBusqueda.getChildren().add(noResultados);
        } else {
            for (CancionDTO cancion : resultados) {
                try {
                    Button itemCancion = crearItemResultadoHorizontal(cancion);
                    resultadosBusqueda.getChildren().add(itemCancion);
                } catch (Exception e) {
                    System.err.println("Error creando item: " + e.getMessage());
                }
            }
        }

        // Ajustar la altura del contenedor top para acomodar los resultados
        if (contenedorTop != null) {
            double alturaResultados = resultados.isEmpty() ? 50 : Math.min(resultados.size() * 55 + 20, 200);
        }
        resultadosBusqueda.setPrefWidth(500);
        resultadosBusqueda.setVisible(true);
        resultadosBusqueda.toFront();
    }

    /**
     * Crea un item visual horizontal para un resultado de búsqueda
     */
    private Button crearItemResultadoHorizontal(CancionDTO cancion) {
        if (cancion == null || cancion.getTitulo() == null) {
            return new Button("Error en canción");
        }

        String artista = "Desconocido";
        if (cancion.getArtistas() != null && !cancion.getArtistas().isEmpty() &&
                cancion.getArtistas().get(0) != null &&
                cancion.getArtistas().get(0).getNombre() != null) {
            artista = cancion.getArtistas().get(0).getNombre();
        }

        // Contenedor horizontal
        HBox contenido = new HBox(10);
        contenido.setAlignment(Pos.CENTER_LEFT);
        contenido.setPadding(new Insets(10, 12, 10, 12));
        contenido.setPrefHeight(50);
        contenido.setMaxWidth(Double.MAX_VALUE);


        Label icono = new Label("🎵");
        icono.setStyle("-fx-font-size: 18px;");
        icono.setMinWidth(25);

        // Información de la canción
        VBox infoCancion = new VBox(2);
        infoCancion.setMaxWidth(Double.MAX_VALUE);

        Label titulo = new Label(cancion.getTitulo());
        titulo.setTextFill(Color.WHITE);
        titulo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        titulo.setWrapText(true);
        titulo.setMaxWidth(Double.MAX_VALUE);
        titulo.setMinWidth(150);
        HBox.setHgrow(titulo, Priority.ALWAYS);


        Label artistaLabel = new Label(artista);
        artistaLabel.setTextFill(Color.web("#AFAFC7"));
        artistaLabel.setStyle("-fx-font-size: 12px;");
        artistaLabel.setWrapText(true);
        artistaLabel.setMaxWidth(Double.MAX_VALUE);
        artistaLabel.setMinWidth(150);
        HBox.setHgrow(artistaLabel, Priority.ALWAYS);


        infoCancion.getChildren().addAll(titulo, artistaLabel);
        HBox.setHgrow(infoCancion, Priority.ALWAYS);

        contenido.getChildren().addAll(icono, infoCancion);

        // Botón principal
        Button item = new Button();
        item.setGraphic(contenido);
        item.setMaxWidth(Double.MAX_VALUE);
        item.setPrefHeight(60);
        item.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 0;" +
                        "-fx-cursor: hand;"
        );

        // Hover
        item.setOnMouseEntered(e -> item.setStyle(
                "-fx-background-color: #2A2A4A;" +
                        "-fx-border-color: #5A5A80;" +
                        "-fx-border-width: 1;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 0;" +
                        "-fx-cursor: hand;"
        ));
        item.setOnMouseExited(e -> item.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 0;" +
                        "-fx-cursor: hand;"
        ));

        // Acción al seleccionar
        item.setOnAction(e -> {
            try {
                seleccionarCancion(cancion);
                txtBuscarCancion.clear();
                ocultarResultadosBusqueda();
            } catch (Exception ex) {
                System.err.println("Error al seleccionar canción: " + ex.getMessage());
            }
        });

        return item;
    }


    /**
     * Selecciona y reproduce una canción específica
     */
    private void seleccionarCancion(CancionDTO cancion) {
        if (cancion == null || cancionesOriginales == null || reproductor == null) {
            System.err.println("Error: datos nulos en seleccionarCancion");
            return;
        }

        try {
            // Encontrar el índice de la canción en la lista original
            int indice = -1;
            for (int i = 0; i < cancionesOriginales.size(); i++) {
                if (cancionesOriginales.get(i).getIdCancion() == cancion.getIdCancion()) {
                    indice = i;
                    break;
                }
            }

            if (indice != -1) {
                // Cambiar a la canción seleccionada
                reproductor.getPlaylist().setIndiceActual(indice);
                reproductor.detener();

                // Actualizar la información de la canción
                mostrarInformacionCancion(cancion);
                reiniciarProgresoYTiempo();

                // Reproducir la nueva canción
                reproductor.reproducir();
                cambiarAImagenPause();
                timeline.play();

                System.out.println("Canción seleccionada: " + cancion.getTitulo());
            } else {
                System.err.println("No se encontró el índice de la canción");
            }
        } catch (Exception e) {
            System.err.println("Error al seleccionar canción: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Oculta los resultados de búsqueda
     */
    private void ocultarResultadosBusqueda() {
        if (resultadosBusqueda != null) {
            resultadosBusqueda.setVisible(false);
        }

        // Restaurar la altura original del contenedor top
        if (contenedorTop != null) {
            contenedorTop.setPrefHeight(70.0);
        }
    }

    /**
     * Salta a un punto específico de la canción basado en el progreso del slider.
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
            cancionesOriginales = cancionDAO.buscarTodo(); // Guardar lista completa
            cancionesDTO = new ArrayList<>(cancionesOriginales); // Lista de trabajo
            cancionesByteArray = new ArrayList<>();

            System.out.println("Canciones cargadas: " + cancionesOriginales.size());

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
     */
    private void actualizarTiempoCancion(double segundosActuales, double segundosTotales) {
        String tiempoActual = formatearTiempo(segundosActuales);
        String tiempoTotal = formatearTiempo(segundosTotales > 0 ? segundosTotales : 0);
        lblTiempoCancion.setText(tiempoActual + " / " + tiempoTotal);
    }

    /**
     * Convierte un valor en segundos a un formato de string "mm:ss".
     */
    private String formatearTiempo(double segundos) {
        int minutos = (int) (segundos / 60);
        int segundosRestantes = (int) (segundos % 60);
        return String.format("%02d:%02d", minutos, segundosRestantes);
    }

    /**
     * Maneja el evento de clic en el botón "Anterior".
     */
    @FXML void clickAnterior(ActionEvent event) {
        if (reproductor != null) reproductor.anterior();
    }

    /**
     * Maneja el evento de clic en el botón "Siguiente".
     */
    @FXML void clickSiguiente(ActionEvent event) {
        if (reproductor != null) reproductor.siguiente();
    }

    /**
     * Maneja el evento de clic en el botón de "Reproducir/Pausar".
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
     * Maneja el evento de clic en el botón de búsqueda.
     */
    @FXML void clickBuscarCancion(ActionEvent event) {
        realizarBusqueda();
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
     */
    private void mostrarPortadaCancion(CancionDTO cancion) {
        try {
            Image portada;
            if (cancion.getPortada() != null && cancion.getPortada().length > 0) {
                portada = new Image(new ByteArrayInputStream(cancion.getPortada()));
            } else {
                portada = new Image(getClass().getResourceAsStream("/UserInterface/Resources/img/portada-generica.jpg"));
            }
            imgAlbumArtCentral.setImage(portada);
            imgAlbumActualAbajo.setImage(portada);
        } catch (Exception e) {
            System.err.println("Error cargando portada: " + e.getMessage());
            Image errorImage = new Image(getClass().getResourceAsStream("/UserInterface/Resources/img/portada-generica.jpg"));
            imgAlbumArtCentral.setImage(errorImage);
            imgAlbumActualAbajo.setImage(errorImage);
        }
    }

    /**
     * Método de limpieza para detener los recursos activos cuando la vista se cierra.
     */
    public void cleanup() {
        if (timeline != null) timeline.stop();
        if (busquedaTimeline != null) busquedaTimeline.stop();
        if (reproductor != null) reproductor.detener();
    }
}