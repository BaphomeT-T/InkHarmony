package UserInterface.CustomerControl.ReproductorMusical;

import BusinessLogic.ReproductorMP3;
import BusinessLogic.Sesion;
import BusinessLogic.Usuario;
import DataAccessComponent.DAO.PlaylistDAO;
import DataAccessComponent.DAO.UsuarioDAO;
import DataAccessComponent.DTO.CancionDTO;
import DataAccessComponent.DTO.PlaylistDTO;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.ByteArrayInputStream;
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

    private ReproductorMP3 reproductor;
    private PlaylistDAO playlistDAO;
    private List<PlaylistDTO> todasLasPlaylists;
    private List<CancionDTO> cancionesMostradas;
    private Timeline timeline;
    private boolean usuarioArrastrando = false;
    private Image portadaGenerica;
    private int indiceCancionAnterior = -1;

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

        cargarYMostrarVistaPrincipal();
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
}
