package UserInterface.CustomerControl.Playlist;

import BusinessLogic.ElementoCancion;
import BusinessLogic.Playlist;
import BusinessLogic.PlaylistDAO;
import DataAccessComponent.DAO.CancionDAO;
import DataAccessComponent.DTO.CancionDTO;
import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.table.TableColumn;
import javax.swing.text.TableView;
import javax.swing.text.TableView.TableCell;
import javax.swing.text.html.ImageView;

public class AgregarCancionesController {
    @FXML private TableView<CancionDTO> tablaCanciones;
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
                // Crear ElementoCancion y agregarlo a la playlist
                ElementoCancion elemento = new ElementoCancion(cancionDTO);
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