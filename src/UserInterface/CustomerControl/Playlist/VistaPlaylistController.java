/*
package UserInterface.CustomerControl.Playlist;

import BusinessLogic.ElementoCancion;
import BusinessLogic.Playlist;
import BusinessLogic.PlaylistDAO;
import java.io.IOException;
import java.util.List;
import javax.swing.table.TableColumn;
import javax.swing.text.TableView;
import javax.swing.text.TableView.TableCell;
import javax.swing.text.html.ImageView;

import com.sun.scenario.effect.impl.ImagePool;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;

public class VistaPlaylistController {
    @FXML
    private ImageView imgPortadaPlaylist;
    @FXML private Label lblTituloPlaylist;
    @FXML private Label lblInfoPlaylist;
    @FXML private Label lblDescripcion;
    @FXML private Button btnAgregarMas;
    @FXML private Button btnPlayAll;
    @FXML private TableView<ElementoCancion> tablaCancionesPlaylist;
    @FXML private TableColumn<ElementoCancion, ImageView> colPortadaCancion;
    @FXML private TableColumn<ElementoCancion, String> colTituloCancion;
    @FXML private TableColumn<ElementoCancion, String> colArtistaCancion;
    @FXML private TableColumn<ElementoCancion, String> colFechaAgregado;
    @FXML private TableColumn<ElementoCancion, Integer> colAnioCancion;
    @FXML private TableColumn<ElementoCancion, String> colDuracionCancion;
    @FXML private TableColumn<ElementoCancion, Void> colAccionesCancion;
    @FXML private VBox vboxPlaylistVacia;
    @FXML private Button btnAgregarPrimeraCancion;

    private Playlist playlist;
    private PlaylistDAO playlistDAO = new PlaylistDAO();
    private ObservableList<ElementoCancion> listaObservable;

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
        cargarInformacionPlaylist();
        cargarCancionesPlaylist();
        actualizarVistaPlaylistVacia();
    }

    @FXML
    public void initialize() {
        configurarTabla();
        configurarBotones();
    }

    private void configurarTabla() {
        colPortadaCancion.setCellValueFactory(cellData -> {
            // Placeholder para imagen de canción
            ImageView img = new ImageView(new Image("/UserInterface/Resources/img/something.png", 40, 40, true, true));
            return new SimpleObjectProperty<>(img);
        });
        
        colTituloCancion.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitulo()));
        colArtistaCancion.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getArtista()));
        colFechaAgregado.setCellValueFactory(cellData -> new SimpleStringProperty("20/06/2025")); // Placeholder
        colAnioCancion.setCellValueFactory(cellData -> new SimpleObjectProperty<>(2025)); // Placeholder
        colDuracionCancion.setCellValueFactory(cellData -> new SimpleStringProperty("3:45")); // Placeholder
        
        colAccionesCancion.setCellFactory(param -> new TableCell<>() {
            private final Button btnQuitar = new Button("Quitar");
            {
                btnQuitar.setOnAction(event -> {
                    ElementoCancion elemento = getTableView().getItems().get(getIndex());
                    quitarCancion(elemento);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnQuitar);
            }
        });
    }

    private void configurarBotones() {
        btnAgregarMas.setOnAction(e -> agregarCanciones());
        btnPlayAll.setOnAction(e -> reproducirTodas());
        btnAgregarPrimeraCancion.setOnAction(e -> agregarCanciones());
    }

    private void cargarInformacionPlaylist() {
        if (playlist != null) {
            lblTituloPlaylist.setText(playlist.getTitulo());
            lblDescripcion.setText(playlist.getDescripcion());
            
            // Cargar imagen de portada si existe
            if (playlist.getImagenPortada() != null) {
                // TODO: Convertir BufferedImage a ImageView
                // Por ahora usamos imagen por defecto
            }
            
            actualizarInformacionPlaylist();
        }
    }

    private void cargarCancionesPlaylist() {
        if (playlist != null) {
            List<ElementoCancion> elementos = playlist.getComponentes();
            listaObservable = FXCollections.observableArrayList(elementos);
            tablaCancionesPlaylist.setItems(listaObservable);
        }
    }

    private void actualizarInformacionPlaylist() {
        if (playlist != null) {
            int cantidadCanciones = playlist.calcularCantidadCanciones();
            double duracionTotal = playlist.obtenerDuracion();
            int horas = (int) (duracionTotal / 3600);
            int minutos = (int) ((duracionTotal % 3600) / 60);
            
            lblInfoPlaylist.setText(String.format("%d canciones • %dh %dm", cantidadCanciones, horas, minutos));
        }
    }

    private void actualizarVistaPlaylistVacia() {
        if (playlist != null) {
            boolean playlistVacia = playlist.calcularCantidadCanciones() == 0;
            tablaCancionesPlaylist.setVisible(!playlistVacia);
            vboxPlaylistVacia.setVisible(playlistVacia);
        }
    }

    @FXML
    private void agregarCanciones() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/Playlist/frameAgregarCanciones.fxml"));
            Parent root = loader.load();
            
            AgregarCancionesController controller = loader.getController();
            controller.setPlaylistDestino(playlist);
            
            Stage stage = new Stage();
            stage.setTitle("Agregar Canciones a: " + playlist.getTitulo());
            stage.setScene(new Scene(root));
            stage.show();
            
            // Recargar la playlist después de cerrar la ventana
            stage.setOnCloseRequest(e -> {
                cargarCancionesPlaylist();
                actualizarInformacionPlaylist();
                actualizarVistaPlaylistVacia();
            });
        } catch (IOException e) {
            mostrarAlerta("Error al abrir la ventana de agregar canciones: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void quitarCancion(ElementoCancion elemento) {
        if (playlist != null && elemento != null) {
            try {
                playlist.eliminar(elemento);
                playlistDAO.actualizarPlaylist(playlist);
                cargarCancionesPlaylist();
                actualizarInformacionPlaylist();
                actualizarVistaPlaylistVacia();
                mostrarAlerta("Canción removida de la playlist", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                mostrarAlerta("Error al remover la canción: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    private void reproducirTodas() {
        // TODO: Implementar reproducción de todas las canciones
        mostrarAlerta("Función de reproducción en desarrollo", Alert.AlertType.INFORMATION);
    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Vista de Playlist");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
} */
