package UserInterface.CustomerControl.ReproductorMusical;

import BusinessLogic.ReproductorMP3;
import DataAccessComponent.DAO.CancionDAO;
import DataAccessComponent.DAO.PlaylistDAO;
import DataAccessComponent.DTO.CancionDTO;
import DataAccessComponent.DTO.PlaylistDTO;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controlador para la interfaz de reproducción de múltiples canciones y playlists.
 * Gestiona la navegación entre la vista de playlists y la de canciones,
 * controla la reproducción y sincroniza la UI con el reproductor de música global.
 * @author Grupo B
 */
public class ReproduccionVariasCancionesController implements Initializable {

    @FXML private Button btnAtras;
    @FXML private ListView<PlaylistDTO> listViewPlaylists;
    @FXML private VBox vistaCuadricula, vistaDetalle;
    @FXML private TilePane tilePaneContenido;
    @FXML private TableView<CancionDTO> tablaCanciones;
    @FXML private TableColumn<CancionDTO, ImageView> colPortada;
    @FXML private TableColumn<CancionDTO, String> colTitulo, colArtista, colDuracion;
    @FXML private Label lblPlaylistTituloHeader, lblAutorHeader;
    @FXML private ImageView imgPlaylistHeader;
    @FXML private Slider pgbProgresoCancion;
    @FXML private ImageView imgAlbumActual, imgPlayPause;
    @FXML private Label lblNombreCancionActual, lblArtistaActual, lblTiempo;
    @FXML private TextField txtBuscarCancion;
    @FXML private Button btnAgregarPlaylist;
    @FXML private Button btnRecomendaciones;
    @FXML private TextField txtBuscarPlaylist;

    private VBox resultadosBusquedaPlaylists;
    private Timeline busquedaPlaylistsTimeline;
    private ReproductorMP3 reproductor;
    private PlaylistDAO playlistDAO;
    private List<PlaylistDTO> todasLasPlaylists;
    private List<CancionDTO> cancionesMostradas;
    private Timeline timeline;
    private boolean usuarioArrastrando = false;
    private Image portadaGenerica;
    private int indiceCancionAnterior = -1;
    private VBox resultadosBusqueda;
    private Timeline busquedaTimeline;
    private List<CancionDTO> todasLasCanciones;

    /**
     * Inicializa el controlador, configura los componentes y carga la vista principal.
     * @param location La ubicación utilizada para resolver rutas relativas para el objeto raíz.
     * @param resources Los recursos utilizados para localizar el objeto raíz.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        InputStream stream = getClass().getResourceAsStream("/UserInterface/Resources/img/portada-generica.jpg");
        portadaGenerica = (stream != null) ? new Image(stream) : null;

        playlistDAO = new PlaylistDAO();
        reproductor = ReproductorMP3.getInstancia(new ArrayList<>());

        configurarListViewPlaylists();
        configurarTablaCanciones();
        inicializarTimeline();
        configurarBusquedaPlaylists();
        cargarYMostrarVistaPrincipal();

        if (reproductor.getPlaylist() != null && reproductor.getPlaylist().getCanciones() != null) {
            List<byte[]> canciones = reproductor.getPlaylist().getCanciones();
            for (PlaylistDTO playlist : todasLasPlaylists) {
                if (playlist.getIdPlaylist() <= 0) continue;

                try {
                    List<CancionDTO> cancionesDeEsta = playlistDAO.obtenerCancionesCompletasDePlaylist(playlist.getIdPlaylist());

                    if (cancionesDeEsta.size() == canciones.size()) {
                        boolean coinciden = true;
                        for (int i = 0; i < canciones.size(); i++) {
                            if (!java.util.Arrays.equals(canciones.get(i), cancionesDeEsta.get(i).getArchivoMP3())) {
                                coinciden = false;
                                break;
                            }
                        }
                        if (coinciden) {
                            cancionesMostradas = cancionesDeEsta;
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.err.println(" Error al obtener canciones para playlist: " + playlist.getTituloPlaylist());
                    e.printStackTrace();
                }
            }
        }


        sincronizarInterfazConEstadoActual();
        imgAlbumActual.setOnMouseClicked(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/ReproductorMusical/reproduccionCancion.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) imgAlbumActual.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        cargarCancionesParaBusqueda();
        configurarBusqueda();
    }


    /**
     * Cambia entre la vista de cuadrícula de playlists y la vista de detalle de canciones.
     * @param aDetalle true para mostrar la vista de detalle, false para la cuadrícula.
     */
    private void cambiarVista(boolean aDetalle) {
        vistaCuadricula.setVisible(!aDetalle);
        vistaDetalle.setVisible(aDetalle);
        btnAtras.setVisible(aDetalle);
    }

    /**
     * Carga todas las playlists desde la base de datos y las muestra en una cuadrícula.
     */
    private void cargarYMostrarVistaPrincipal() {
        try {
            todasLasPlaylists = playlistDAO.buscarTodo();
            tilePaneContenido.getChildren().clear();
            for (PlaylistDTO playlist : todasLasPlaylists) {
                tilePaneContenido.getChildren().add(crearPlaylistCard(playlist));
            }
            cambiarVista(false);
        } catch (Exception e) { e.printStackTrace(); }
    }


    private void cargarCancionesParaBusqueda() {
        try {
            CancionDAO dao = new CancionDAO();
            todasLasCanciones = dao.buscarTodo();
        } catch (Exception e) {
            System.err.println("Error al cargar canciones para búsqueda: " + e.getMessage());
            todasLasCanciones = new ArrayList<>();
        }
    }




    private void configurarBusqueda() {
        busquedaTimeline = new Timeline(new KeyFrame(Duration.millis(300), e -> realizarBusqueda()));
        busquedaTimeline.setCycleCount(1);

        txtBuscarCancion.textProperty().addListener((observable, oldVal, newVal) -> {
            if (busquedaTimeline != null) busquedaTimeline.stop();
            if (newVal != null && !newVal.trim().isEmpty()) {
                busquedaTimeline.play();
            } else {
                ocultarResultadosBusqueda();
            }
        });

        txtBuscarCancion.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                Timeline delay = new Timeline(new KeyFrame(Duration.millis(150), e -> ocultarResultadosBusqueda()));
                delay.play();
            }
        });

        Platform.runLater(this::configurarContenedorResultados);
    }



    private void configurarContenedorResultados() {
        if (txtBuscarCancion.getScene() == null) return;

        BorderPane root = (BorderPane) txtBuscarCancion.getScene().getRoot();

        if (resultadosBusqueda == null && root != null) {
            resultadosBusqueda = new VBox();
            resultadosBusqueda.setSpacing(2);
            resultadosBusqueda.setPadding(new Insets(5));
            resultadosBusqueda.setMaxWidth(700);
            resultadosBusqueda.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-border-color: #ccc;" +
                            "-fx-border-radius: 6;" +
                            "-fx-background-radius: 6;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 8, 0, 0, 3);"
            );
            resultadosBusqueda.setVisible(false);
            root.getChildren().add(resultadosBusqueda);

            Platform.runLater(() -> {
                double x = txtBuscarCancion.localToScene(0, 0).getX();
                double y = txtBuscarCancion.localToScene(0, 0).getY() + txtBuscarCancion.getHeight();
                resultadosBusqueda.setLayoutX(x);
                resultadosBusqueda.setLayoutY(y);
            });
        }
    }



    private void realizarBusqueda() {
        if (txtBuscarCancion == null || todasLasCanciones == null) return;

        String texto = txtBuscarCancion.getText().trim().toLowerCase();
        if (texto.isEmpty()) {
            ocultarResultadosBusqueda();
            return;
        }

        List<CancionDTO> resultados = todasLasCanciones.stream()
                .filter(c -> c.getTitulo().toLowerCase().contains(texto) ||
                        c.getArtistas().stream().anyMatch(a -> a.getNombre().toLowerCase().contains(texto)))
                .limit(5)
                .collect(Collectors.toList());

        Platform.runLater(() -> mostrarResultadosBusqueda(resultados, texto));
    }



    private void mostrarResultadosBusqueda(List<CancionDTO> resultados, String texto) {
        if (resultadosBusqueda == null) return;

        resultadosBusqueda.getChildren().clear();

        if (resultados.isEmpty()) {
            Label sinResultados = new Label("No se encontraron resultados para: \"" + texto + "\"");
            sinResultados.setStyle("-fx-padding: 10; -fx-font-style: italic;");
            resultadosBusqueda.getChildren().add(sinResultados);
        } else {
            for (CancionDTO cancion : resultados) {
                Button item = crearItemResultadoHorizontal(cancion);
                resultadosBusqueda.getChildren().add(item);
            }
        }

        resultadosBusqueda.setVisible(true);
        resultadosBusqueda.toFront();
    }



    private Button crearItemResultadoHorizontal(CancionDTO cancion) {
        String artista = cancion.getArtistas().isEmpty() ? "Desconocido" :
                cancion.getArtistas().get(0).getNombre();

        // Contenedor horizontal principal
        HBox contenido = new HBox(10);
        contenido.setAlignment(Pos.CENTER_LEFT);
        contenido.setPadding(new Insets(10));
        contenido.setPrefHeight(50);
        contenido.setPrefWidth(580); // Aumentamos ancho del contenido
        contenido.setMaxWidth(Double.MAX_VALUE);

        // Icono 🎵
        Label icono = new Label("🎵");
        icono.setStyle("-fx-font-size: 18px;");
        icono.setMinWidth(30);

        // Título + artista
        Label texto = new Label(cancion.getTitulo() + " — " + artista);
        texto.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        texto.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        texto.setWrapText(false); // Sin wrap
        texto.setEllipsisString("...");
        texto.setMaxWidth(Double.MAX_VALUE);
        texto.setMinWidth(450); // Forzamos más ancho
        HBox.setHgrow(texto, Priority.ALWAYS);

        contenido.getChildren().addAll(icono, texto);

        // Botón invisible que actúa como item
        Button btn = new Button();
        btn.setGraphic(contenido);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefWidth(600); // Muy importante: igual al VBox
        btn.setPrefHeight(50);
        btn.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #E0E0F8; -fx-padding: 0;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-padding: 0;"));

        btn.setOnAction(e -> {
            iniciarReproduccionDesdeBusqueda(cancion);
            ocultarResultadosBusqueda();
        });

        return btn;
    }


    private void configurarBusquedaPlaylists() {
        busquedaPlaylistsTimeline = new Timeline(new KeyFrame(Duration.millis(300), e -> realizarBusquedaPlaylists()));
        busquedaPlaylistsTimeline.setCycleCount(1);

        txtBuscarPlaylist.textProperty().addListener((observable, oldVal, newVal) -> {
            if (busquedaPlaylistsTimeline != null) busquedaPlaylistsTimeline.stop();
            if (newVal != null && !newVal.trim().isEmpty()) {
                busquedaPlaylistsTimeline.play();
            } else {
                ocultarResultadosBusquedaPlaylists();
            }
        });

        txtBuscarPlaylist.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                Timeline delay = new Timeline(new KeyFrame(Duration.millis(150), e -> ocultarResultadosBusquedaPlaylists()));
                delay.play();
            }
        });

        Platform.runLater(this::configurarContenedorResultadosPlaylists);
    }

    private void configurarContenedorResultadosPlaylists() {
        if (txtBuscarPlaylist.getScene() == null) return;

        BorderPane root = (BorderPane) txtBuscarPlaylist.getScene().getRoot();

        if (resultadosBusquedaPlaylists == null && root != null) {
            resultadosBusquedaPlaylists = new VBox();
            resultadosBusquedaPlaylists.setSpacing(2);
            resultadosBusquedaPlaylists.setPadding(new Insets(5));
            resultadosBusquedaPlaylists.setPrefWidth(420); // MISMO ANCHO DEL BOTÓN
            resultadosBusquedaPlaylists.setMaxWidth(420);
            resultadosBusquedaPlaylists.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-border-color: #ccc;" +
                            "-fx-border-radius: 6;" +
                            "-fx-background-radius: 6;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 8, 0, 0, 3);"
            );
            resultadosBusquedaPlaylists.setVisible(false);
            root.getChildren().add(resultadosBusquedaPlaylists);

            Platform.runLater(() -> {
                double x = txtBuscarPlaylist.localToScene(0, 0).getX();
                double y = txtBuscarPlaylist.localToScene(0, 0).getY() + txtBuscarPlaylist.getHeight();

                resultadosBusquedaPlaylists.setLayoutX(x + 15); // ligero desplazamiento visual
                resultadosBusquedaPlaylists.setLayoutY(y + 65); // para que no se superponga con el botón "+"
            });
        }
    }

    private void realizarBusquedaPlaylists() {
        if (txtBuscarPlaylist == null || todasLasPlaylists == null) return;

        String texto = txtBuscarPlaylist.getText().trim().toLowerCase();
        if (texto.isEmpty()) {
            ocultarResultadosBusquedaPlaylists();
            return;
        }

        List<PlaylistDTO> resultados = todasLasPlaylists.stream()
                .filter(p -> p.getTituloPlaylist().toLowerCase().contains(texto))
                .limit(5)
                .collect(Collectors.toList());

        Platform.runLater(() -> mostrarResultadosBusquedaPlaylists(resultados, texto));
    }

    private void mostrarResultadosBusquedaPlaylists(List<PlaylistDTO> resultados, String texto) {
        if (resultadosBusquedaPlaylists == null) return;

        resultadosBusquedaPlaylists.getChildren().clear();

        if (resultados.isEmpty()) {
            Label sinResultados = new Label("No se encontraron playlists con: \"" + texto + "\"");
            sinResultados.setStyle("-fx-padding: 10; -fx-font-style: italic;");
            resultadosBusquedaPlaylists.getChildren().add(sinResultados);
        } else {
            for (PlaylistDTO playlist : resultados) {
                Button item = crearItemResultadoPlaylist(playlist);
                resultadosBusquedaPlaylists.getChildren().add(item);
            }
        }

        resultadosBusquedaPlaylists.setVisible(true);
        resultadosBusquedaPlaylists.toFront();
    }

    private Button crearItemResultadoPlaylist(PlaylistDTO playlist) {
        // Contenedor horizontal del resultado
        HBox contenido = new HBox(10);
        contenido.setAlignment(Pos.CENTER_LEFT);
        contenido.setPadding(new Insets(10));
        contenido.setPrefHeight(50);
        contenido.setPrefWidth(400);
        contenido.setMaxWidth(Double.MAX_VALUE);

        // Ícono de playlist
        Label icono = new Label("📁");
        icono.setStyle("-fx-font-size: 18px;");
        icono.setMinWidth(30);

        // Título de la playlist
        Label nombre = new Label(playlist.getTituloPlaylist());
        nombre.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        nombre.setWrapText(false);
        nombre.setMaxWidth(Double.MAX_VALUE);
        nombre.setMinWidth(300);
        HBox.setHgrow(nombre, Priority.ALWAYS);

        contenido.getChildren().addAll(icono, nombre);

        // Botón contenedor
        Button btn = new Button();
        btn.setGraphic(contenido);
        btn.setPrefWidth(420);
        btn.setPrefHeight(50);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #F0F0F0; -fx-padding: 0;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-padding: 0;"));

        btn.setOnAction(e -> {
            mostrarCancionesDePlaylist(playlist);
            ocultarResultadosBusquedaPlaylists();
            txtBuscarPlaylist.clear();
        });

        return btn;
    }


    private void ocultarResultadosBusquedaPlaylists() {
        if (resultadosBusquedaPlaylists != null) {
            resultadosBusquedaPlaylists.setVisible(false);
        }
    }




    private void iniciarReproduccionDesdeBusqueda(CancionDTO cancionSeleccionada) {
        if (todasLasCanciones.isEmpty()) return;

        List<byte[]> archivosMp3 = todasLasCanciones.stream()
                .map(CancionDTO::getArchivoMP3)
                .collect(Collectors.toList());

        // Actualizar el reproductor con todas las canciones
        reproductor.cambiarPlaylist(archivosMp3);
        cancionesMostradas = todasLasCanciones; // Muy importante

        int indiceSeleccionado = todasLasCanciones.indexOf(cancionSeleccionada);
        if (indiceSeleccionado == -1) return;

        reproductor.getPlaylist().setIndiceActual(indiceSeleccionado);
        reproductor.reproducir();
        timeline.play();
        cambiarIconoPlay(false);

        // Actualizar la interfaz inferior (nombre, artista, imagen)
        lblNombreCancionActual.setText(cancionSeleccionada.getTitulo());
        lblArtistaActual.setText(cancionSeleccionada.getArtistas().stream()
                .map(a -> a.getNombre())
                .collect(Collectors.joining(", ")));

        if (cancionSeleccionada.getPortada() != null && cancionSeleccionada.getPortada().length > 0) {
            imgAlbumActual.setImage(new Image(new ByteArrayInputStream(cancionSeleccionada.getPortada())));
        } else {
            imgAlbumActual.setImage(portadaGenerica); // Usa imagen por defecto si no hay portada
        }
    }




    private void ocultarResultadosBusqueda() {
        if (resultadosBusqueda != null) {
            resultadosBusqueda.setVisible(false);
        }
    }





    /**
     * Muestra las canciones de una playlist seleccionada en la vista de detalle.
     * @param playlist La playlist seleccionada.
     */
    private void mostrarCancionesDePlaylist(PlaylistDTO playlist) {
        try {
            cancionesMostradas = playlistDAO.obtenerCancionesCompletasDePlaylist(playlist.getIdPlaylist());
            imgPlaylistHeader.setImage(playlist.getImagenPortada() != null ? new Image(new ByteArrayInputStream(playlist.getImagenPortada())) : portadaGenerica);
            lblPlaylistTituloHeader.setText(playlist.getTituloPlaylist());
            lblAutorHeader.setText("InkHarmony");
            tablaCanciones.getItems().setAll(cancionesMostradas);
            cambiarVista(true);
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Crea una tarjeta visual (VBox) para una playlist para mostrar en la cuadrícula.
     * @param playlist El DTO de la playlist.
     * @return Un VBox que representa la tarjeta de la playlist, con su evento de clic configurado.
     */
    private VBox crearPlaylistCard(PlaylistDTO playlist) {
        ImageView portadaView = new ImageView(playlist.getImagenPortada() != null ? new Image(new ByteArrayInputStream(playlist.getImagenPortada())) : portadaGenerica);
        portadaView.setFitHeight(180);
        portadaView.setFitWidth(180);
        Label tituloLabel = new Label(playlist.getTituloPlaylist());
        tituloLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        VBox card = new VBox(10, portadaView, tituloLabel);
        card.setCursor(Cursor.HAND);
        card.setOnMouseClicked(event -> mostrarCancionesDePlaylist(playlist));
        return card;
    }

    /**
     * Configura la ListView de la biblioteca para mostrar las playlists con su imagen y título.
     */
    private void configurarListViewPlaylists() {
        listViewPlaylists.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(PlaylistDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setGraphic(null);
                else {
                    ImageView img = new ImageView(item.getImagenPortada() != null ? new Image(new ByteArrayInputStream(item.getImagenPortada())) : portadaGenerica);
                    img.setFitHeight(40);
                    img.setFitWidth(40);
                    Label lbl = new Label(item.getTituloPlaylist());
                    lbl.setStyle("-fx-text-fill: #AFAFC7;");
                    HBox cellBox = new HBox(10, img, lbl);
                    cellBox.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(cellBox);
                }
            }
        });
        listViewPlaylists.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) mostrarCancionesDePlaylist(newVal);
        });
    }

    /**
     * Configura las columnas de la TableView de canciones, incluyendo cómo mostrar
     * la portada, datos y el evento de doble clic para reproducir.
     */
    private void configurarTablaCanciones() {
        colPortada.setCellValueFactory(cellData -> {
            ImageView imgView = new ImageView(cellData.getValue().getPortada() != null ? new Image(new ByteArrayInputStream(cellData.getValue().getPortada())) : portadaGenerica);
            imgView.setFitHeight(40);
            imgView.setFitWidth(40);
            return new SimpleObjectProperty<>(imgView);
        });
        colTitulo.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getTitulo()));
        colArtista.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getArtistas().stream().map(a -> a.getNombre()).collect(Collectors.joining(", "))));
        colDuracion.setCellValueFactory(cellData -> new SimpleObjectProperty<>(formatearTiempo(cellData.getValue().getDuracion())));
        tablaCanciones.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !tablaCanciones.getSelectionModel().isEmpty()) {
                iniciarReproduccion(tablaCanciones.getSelectionModel().getSelectedItem());
            }
        });
    }

    /**
     * Inicializa el Timeline para la actualización periódica de la barra de progreso.
     */
    private void inicializarTimeline() {
        timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> actualizarProgressBar()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        pgbProgresoCancion.setOnMousePressed(e -> usuarioArrastrando = true);
        pgbProgresoCancion.setOnMouseReleased(e -> {
            usuarioArrastrando = false;
            saltarAProgreso(pgbProgresoCancion.getValue());
        });
    }

    /**
     * Método llamado por el Timeline para actualizar la UI durante la reproducción.
     */
    private void actualizarProgressBar() {
        if (reproductor != null && reproductor.estaReproduciendo() && reproductor.getMediaPlayer() != null) {
            double tiempoActual = reproductor.getTiempoActual();
            double duracionTotal = reproductor.getMediaPlayer().getTotalDuration().toSeconds();
            if (!Double.isNaN(duracionTotal) && duracionTotal > 0 && !usuarioArrastrando) {
                pgbProgresoCancion.setValue(tiempoActual / duracionTotal);
            }
            actualizarTiempoLabel(tiempoActual, duracionTotal);
        }
        actualizarInfoCancionActual();
    }

    /**
     * Salta a un punto específico de la canción basado en el progreso del slider.
     * @param progreso El valor del slider, de 0.0 a 1.0.
     */
    private void saltarAProgreso(double progreso) {
        if (reproductor != null && reproductor.getMediaPlayer() != null) {
            double duracionTotal = reproductor.getMediaPlayer().getTotalDuration().toSeconds();
            if(!Double.isNaN(duracionTotal)) {
                reproductor.getMediaPlayer().seek(Duration.seconds(progreso * duracionTotal));
            }
        }
    }

    /**
     * Actualiza la etiqueta del tiempo en formato mm:ss.
     * @param segundosActuales El tiempo transcurrido.
     * @param segundosTotales La duración total.
     */
    private void actualizarTiempoLabel(double segundosActuales, double segundosTotales) {
        if (Double.isNaN(segundosTotales)) segundosTotales = 0.0;
        lblTiempo.setText(formatearTiempo(segundosActuales) + " / " + formatearTiempo(segundosTotales));
    }

    /**
     * Carga una nueva playlist en el reproductor y comienza a reproducir la canción seleccionada.
     * @param cancionSeleccionada La canción en la que se hizo doble clic.
     */
    private void iniciarReproduccion(CancionDTO cancionSeleccionada) {
        cancionesMostradas = tablaCanciones.getItems();
        if (cancionesMostradas.isEmpty()) return;
        List<byte[]> archivosMp3 = cancionesMostradas.stream().map(CancionDTO::getArchivoMP3).collect(Collectors.toList());
        reproductor.cambiarPlaylist(archivosMp3);
        int indiceSeleccionado = cancionesMostradas.indexOf(cancionSeleccionada);
        if (indiceSeleccionado == -1) return;
        reproductor.getPlaylist().setIndiceActual(indiceSeleccionado);
        reproductor.reproducir();
        timeline.play();
        cambiarIconoPlay(false);
    }

    /**
     * Maneja el clic en el botón "Atrás", regresando a la vista de cuadrícula de playlists.
     * @param event El evento de acción.
     */
    @FXML void clickAtras(ActionEvent event) { cargarYMostrarVistaPrincipal(); }

    /**
     * Maneja el clic en el botón "Anterior".
     * @param event El evento de acción.
     */
    @FXML void clickAnterior(ActionEvent event) { if (reproductor != null) { reproductor.anterior(); cambiarIconoPlay(false); }}

    /**
     * Maneja el clic en el botón "Siguiente".
     * @param event El evento de acción.
     */
    @FXML void clickSiguiente(ActionEvent event) { if (reproductor != null) { reproductor.siguiente(); cambiarIconoPlay(false); }}

    /**
     * Maneja el clic en el botón de búsqueda (actualmente es un placeholder).
     * @param event El evento de acción.
     */
    @FXML void clickBuscarCancion(ActionEvent event) { System.out.println("Búsqueda no implementada."); }

    /**
     * Maneja el clic en el botón de "+".
     * Permite redireccion a crear una nueva playlist.
     * @param event El evento de acción.
     */
    @FXML
    void  clickAgregarPlaylist(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/Playlist/frameNuevaPlaylist.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Crear Playlist");
            stage.showAndWait();
            cargarYMostrarVistaPrincipal(); // Recargar playlists después de crear una nueva
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void clickRecomendaciones(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/Recomendaciones/recomendaciones.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Recomendaciones");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Maneja el clic en el botón de "Reproducir/Pausar".
     * @param event El evento de acción.
     */
    @FXML
    void clickReproducir(ActionEvent event) {
        if (reproductor.getPlaylist() == null || reproductor.getPlaylist().getCanciones().isEmpty()) return;
        if (reproductor.estaReproduciendo()) {
            reproductor.pausar();
            timeline.stop();
            cambiarIconoPlay(true);
        } else {
            reproductor.reproducir();
            timeline.play();
            cambiarIconoPlay(false);
        }
    }

    /**
     * Actualiza la información de la canción que se muestra en la barra de reproducción inferior.
     */
    private void actualizarInfoCancionActual() {
        if (reproductor.getPlaylist() == null || reproductor.getPlaylist().getCanciones().isEmpty() || cancionesMostradas == null || cancionesMostradas.isEmpty()) return;
        int indiceActual = reproductor.getPlaylist().getIndiceActual();
        if (indiceActual == indiceCancionAnterior) return;
        if (indiceActual < 0 || indiceActual >= cancionesMostradas.size()) return;
        CancionDTO cancionActual = cancionesMostradas.get(indiceActual);
        lblNombreCancionActual.setText(cancionActual.getTitulo());
        lblArtistaActual.setText(cancionActual.getArtistas().stream().map(a -> a.getNombre()).collect(Collectors.joining(", ")));
        imgAlbumActual.setImage(cancionActual.getPortada() != null ? new Image(new ByteArrayInputStream(cancionActual.getPortada())) : portadaGenerica);
        indiceCancionAnterior = indiceActual;
    }

    /**
     * Formatea segundos a un string mm:ss.
     * @param segundos El tiempo en segundos a formatear.
     * @return El tiempo formateado como un String.
     */
    private String formatearTiempo(double segundos) {
        if (Double.isNaN(segundos)) return "00:00";
        return String.format("%02d:%02d", (int)(segundos / 60), (int)(segundos % 60));
    }

    /**
     * Cambia el icono del botón de reproducción.
     * @param aPlay true para mostrar el icono de 'play', false para mostrar el de 'pausa'.
     */
    private void cambiarIconoPlay(boolean aPlay) {
        imgPlayPause.setImage(new Image(getClass().getResourceAsStream("/UserInterface/Resources//img/" + (aPlay ? "boton-de-play.png" : "boton-de-pausa.png"))));
    }



    /**
     * Sincroniza visualmente la interfaz con la canción actual si ya se está reproduciendo.
     * Se llama al volver desde la vista expandida.
     */
    private void sincronizarInterfazConEstadoActual() {
        if (reproductor == null || reproductor.getPlaylist() == null || reproductor.getPlaylist().getCanciones() == null) return;

        int indiceActual = reproductor.getPlaylist().getIndiceActual();
        List<byte[]> canciones = reproductor.getPlaylist().getCanciones();

        if (indiceActual >= 0 && indiceActual < canciones.size() && cancionesMostradas != null && indiceActual < cancionesMostradas.size()) {
            CancionDTO cancionActual = cancionesMostradas.get(indiceActual);
            lblNombreCancionActual.setText(cancionActual.getTitulo());
            lblArtistaActual.setText(cancionActual.getArtistas().stream().map(a -> a.getNombre()).collect(Collectors.joining(", ")));
            imgAlbumActual.setImage(cancionActual.getPortada() != null ? new Image(new ByteArrayInputStream(cancionActual.getPortada())) : portadaGenerica);

            double duracion = 0;
            if (reproductor.getMediaPlayer() != null) {
                duracion = reproductor.getMediaPlayer().getTotalDuration().toSeconds();
                actualizarTiempoLabel(reproductor.getTiempoActual(), duracion);
            }

            cambiarIconoPlay(!reproductor.estaReproduciendo());
            timeline.play();
        }
    }


}
