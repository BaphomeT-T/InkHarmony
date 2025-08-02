package UserInterface.CustomerControl;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador para el catálogo de playlists.
 * Gestiona la interfaz de usuario para mostrar, buscar y administrar playlists.
 */
public class CatalogoPlaylistController implements Initializable {

    // Panel izquierdo - Biblioteca
    @FXML
    private TextField txtBuscarPlaylist;
    
    @FXML
    private ListView<Object> listPlaylists;
    
    @FXML
    private Button btnAgregarPlaylist;

    // Panel central - Detalles de la playlist
    @FXML
    private Label lblNombrePlaylist;
    
    @FXML
    private Label lblInfoPlaylist;
    
    @FXML
    private ImageView imgPortadaPlaylist;
    
    @FXML
    private Button btnPlayAll;
    
    @FXML
    private Button btnAgregarMas;

    // Tabla de canciones
    @FXML
    private TableView<Object> tableCanciones;
    
    @FXML
    private TableColumn<Object, String> colTitulo;
    
    @FXML
    private TableColumn<Object, String> colArtista;
    
    @FXML
    private TableColumn<Object, String> colFechaAgregacion;
    
    @FXML
    private TableColumn<Object, Integer> colAnio;
    
    @FXML
    private TableColumn<Object, String> colDuracion;

    // Panel inferior - Reproductor
    @FXML
    private Button btnAnterior;
    
    @FXML
    private Button btnPlayPause;
    
    @FXML
    private Button btnSiguiente;
    
    @FXML
    private Label lblTiempoActual;
    
    @FXML
    private Slider sliderTiempo;
    
    @FXML
    private Label lblTiempoTotal;
    
    @FXML
    private Label lblCancionActual;
    
    @FXML
    private ImageView imgCancionActual;

    private ObservableList<Object> listPlaylistsData;
    private ObservableList<Object> listCancionesData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializar listas
        listPlaylistsData = FXCollections.observableArrayList();
        listCancionesData = FXCollections.observableArrayList();
        
        listPlaylists.setItems(listPlaylistsData);
        tableCanciones.setItems(listCancionesData);
        
        // Configurar eventos
        listPlaylists.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> mostrarDetallesPlaylist(newValue));
        
        tableCanciones.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> seleccionarCancion(newValue));
    }

    @FXML
    private void handleBuscarPlaylist() {
        // Lógica de búsqueda de playlists
    }

    @FXML
    private void handleAgregarPlaylist() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../GUI/NuevaPlaylist.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Nueva Playlist");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initOwner(btnAgregarPlaylist.getScene().getWindow());
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePlayAll() {
        // Reproducir todas las canciones de la playlist
    }

    @FXML
    private void handleAgregarMas() {
        // Agregar más canciones a la playlist
    }

    @FXML
    private void handleAnterior() {
        // Canción anterior
    }

    @FXML
    private void handlePlayPause() {
        // Play/Pause
    }

    @FXML
    private void handleSiguiente() {
        // Siguiente canción
    }

    private void mostrarDetallesPlaylist(Object playlist) {
        // Mostrar información de la playlist seleccionada
        if (playlist != null) {
            // Cargar canciones de la playlist
            cargarCancionesPlaylist(playlist);
        }
    }

    private void seleccionarCancion(Object cancion) {
        // Actualizar información de la canción en el reproductor
        if (cancion != null) {
            // Actualizar labels del reproductor
        }
    }

    private void cargarPlaylists() {
        // Cargar playlists desde la base de datos
    }

    private void cargarCancionesPlaylist(Object playlist) {
        // Cargar canciones de la playlist seleccionada
    }
}
