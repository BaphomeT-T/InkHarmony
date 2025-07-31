package UserInterface.CustomerControl.Playlist;

import BusinessLogic.Cancion;
import BusinessLogic.Playlist;
import BusinessLogic.PlaylistDAO;
import DataAccessComponent.DAO.CancionDAO;
import DataAccessComponent.DTO.CancionDTO;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.stage.Stage;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;

public class AgregarCancionesController {
    @FXML 
    private TableView<CancionDTO> tablaCanciones;
    @FXML private TableColumn<CancionDTO, ImageView> colPortada;
    @FXML private TableColumn<CancionDTO, String> colTitulo;
    @FXML private TableColumn<CancionDTO, String> colArtista;
    @FXML private TableColumn<CancionDTO, String> colFecha;
    @FXML private TableColumn<CancionDTO, Integer> colAnio;
    @FXML private TableColumn<CancionDTO, String> colDuracion;
    @FXML private TableColumn<CancionDTO, Void> colAgregar;
    @FXML private TextField txtBuscarCancion;
    @FXML private Button btnAgregarCanciones;

    private Playlist playlistDestino;
    private CancionDAO cancionDAO = new CancionDAO();
    private PlaylistDAO playlistDAO = new PlaylistDAO();
    private ObservableList<CancionDTO> listaObservable;
    private Set<CancionDTO> cancionesSeleccionadas = new HashSet<>();

    public void setPlaylistDestino(Playlist playlist) {
        this.playlistDestino = playlist;
    }

    @FXML
    public void initialize() {
        configurarTabla();
        cargarCanciones();
        configurarBusqueda();
    }

    private void configurarTabla() {
        colPortada.setCellValueFactory(cellData -> {
            CancionDTO cancion = cellData.getValue();
            if (cancion.getPortada() != null) {
                Image image = new Image(new ByteArrayInputStream(cancion.getPortada()));
                ImageView img = new ImageView(image);
                img.setFitHeight(40);
                img.setFitWidth(40);
                return new SimpleObjectProperty<>(img);
            } else {
                ImageView img = new ImageView(new Image("/UserInterface/Resources/img/something.png", 40, 40, true, true));
                return new SimpleObjectProperty<>(img);
            }
        });
        
        colTitulo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitulo()));
        colArtista.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getArtista()));
        colFecha.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFechaAgregacion()));
        colAnio.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getAnio()));
        colDuracion.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDuracion()));
        
        colAgregar.setCellFactory(param -> new TableCell<>() {
            private final Button btnAgregar = new Button("+");
            {
                btnAgregar.setOnAction(event -> {
                    CancionDTO cancion = getTableView().getItems().get(getIndex());
                    if (cancionesSeleccionadas.contains(cancion)) {
                        cancionesSeleccionadas.remove(cancion);
                        btnAgregar.setText("+");
                        btnAgregar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                    } else {
                        cancionesSeleccionadas.add(cancion);
                        btnAgregar.setText("✓");
                        btnAgregar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                    }
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    CancionDTO cancion = getTableView().getItems().get(getIndex());
                    if (cancionesSeleccionadas.contains(cancion)) {
                        btnAgregar.setText("✓");
                        btnAgregar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                    } else {
                        btnAgregar.setText("+");
                        btnAgregar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                    }
                    setGraphic(btnAgregar);
                }
            }
        });
    }

    private void cargarCanciones() {
        try {
            List<CancionDTO> canciones = cancionDAO.buscarTodo();
            listaObservable = FXCollections.observableArrayList(canciones);
            tablaCanciones.setItems(listaObservable);
        } catch (Exception e) {
            mostrarAlerta("Error al cargar las canciones: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void configurarBusqueda() {
        txtBuscarCancion.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isBlank()) {
                tablaCanciones.setItems(listaObservable);
            } else {
                ObservableList<CancionDTO> filtradas = FXCollections.observableArrayList();
                for (CancionDTO cancion : listaObservable) {
                    if (cancion.getTitulo().toLowerCase().contains(newValue.toLowerCase()) ||
                        cancion.getArtista().toLowerCase().contains(newValue.toLowerCase())) {
                        filtradas.add(cancion);
                    }
                }
                tablaCanciones.setItems(filtradas);
            }
        });
    }

    @FXML
    private void agregarCancionesSeleccionadas() {
        if (cancionesSeleccionadas.isEmpty()) {
            mostrarAlerta("Por favor selecciona al menos una canción", Alert.AlertType.WARNING);
            return;
        }
        
        if (playlistDestino == null) {
            mostrarAlerta("Error: No se ha especificado la playlist destino", Alert.AlertType.ERROR);
            return;
        }
        
        try {
            for (CancionDTO cancionDTO : cancionesSeleccionadas) {
                // Crear Cancion y agregarlo a la playlist
                Cancion elemento = new Cancion(cancionDTO);
                playlistDestino.agregar(elemento);
            }
            
            // Actualizar la playlist en la base de datos
            playlistDAO.actualizarPlaylist(playlistDestino);
            
            mostrarAlerta("Se agregaron " + cancionesSeleccionadas.size() + " canciones a la playlist", Alert.AlertType.INFORMATION);
            cerrarVentana();
            
        } catch (Exception e) {
            mostrarAlerta("Error al agregar las canciones: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnAgregarCanciones.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Agregar Canciones");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
} 