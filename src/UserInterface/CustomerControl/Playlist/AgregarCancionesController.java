package UserInterface.CustomerControl.Playlist;

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
    @FXML private TableColumn<CancionDTO, String> colAnio;
    @FXML private TableColumn<CancionDTO, String> colDuracion;
    @FXML private TableColumn<CancionDTO, Void> colAgregar;
    @FXML private TextField txtBuscarCancion;
    @FXML private Button btnAgregarCanciones;

    private Playlist playlistDestino;
    private Cancion cancionBL = new Cancion();
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
        // Configurar columnas
        colPortada.setCellValueFactory(cellData -> {
            CancionDTO cancion = cellData.getValue();
            ImageView img = new ImageView();
            if (cancion.getPortada() != null) {
                try {
                    img.setImage(new Image(new ByteArrayInputStream(cancion.getPortada())));
                } catch (Exception e) {
                    img.setImage(new Image("/UserInterface/Resources/img/CatalogoCanciones/camara.png"));
                }
            } else {
                img.setImage(new Image("/UserInterface/Resources/img/CatalogoCanciones/camara.png"));
            }
            img.setFitHeight(40);
            img.setFitWidth(40);
            return new SimpleObjectProperty<>(img);
        });

        colTitulo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitulo()));
        colArtista.setCellValueFactory(cellData -> {
            String artistas = "";
            if (cellData.getValue().getArtistas() != null && !cellData.getValue().getArtistas().isEmpty()) {
                artistas = cellData.getValue().getArtistas().get(0).getNombre();
            }
            return new SimpleStringProperty(artistas);
        });
        colFecha.setCellValueFactory(cellData -> new SimpleStringProperty("20/06/2025")); // Placeholder
        colAnio.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getAnio())));
        colDuracion.setCellValueFactory(cellData -> {
            double duracion = cellData.getValue().getDuracion();
            int minutos = (int) (duracion / 60);
            int segundos = (int) (duracion % 60);
            return new SimpleStringProperty(String.format("%d:%02d", minutos, segundos));
        });

        // Columna de botones de agregar
        colAgregar.setCellFactory(param -> new TableCell<>() {
            private final Button btnAgregar = new Button("+");
            private final HBox hbox = new HBox(btnAgregar);
            
            {
                btnAgregar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 15;");
                btnAgregar.setPrefSize(30, 30);
                btnAgregar.setOnAction(event -> {
                    CancionDTO cancion = getTableView().getItems().get(getIndex());
                    if (cancionesSeleccionadas.contains(cancion)) {
                        cancionesSeleccionadas.remove(cancion);
                        btnAgregar.setText("+");
                        btnAgregar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 15;");
                    } else {
                        cancionesSeleccionadas.add(cancion);
                        btnAgregar.setText("✓");
                        btnAgregar.setStyle("-fx-background-color: #FF6B6B; -fx-text-fill: white; -fx-background-radius: 15;");
                    }
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
    }

    private void cargarCanciones() {
        try {
            List<CancionDTO> canciones = cancionBL.buscarTodo();
            listaObservable = FXCollections.observableArrayList(canciones);
            tablaCanciones.setItems(listaObservable);
        } catch (Exception e) {
            mostrarAlerta("Error al cargar canciones: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void configurarBusqueda() {
        txtBuscarCancion.textProperty().addListener((observable, oldValue, newValue) -> filtrarCanciones(newValue));
    }

    private void filtrarCanciones(String filtro) {
        if (filtro == null || filtro.isBlank()) {
            tablaCanciones.setItems(listaObservable);
            return;
        }
        
        ObservableList<CancionDTO> filtradas = FXCollections.observableArrayList();
        for (CancionDTO cancion : listaObservable) {
            if (cancion.getTitulo().toLowerCase().contains(filtro.toLowerCase()) ||
                (cancion.getArtistas() != null && !cancion.getArtistas().isEmpty() &&
                 cancion.getArtistas().get(0).getNombre().toLowerCase().contains(filtro.toLowerCase()))) {
                filtradas.add(cancion);
            }
        }
        tablaCanciones.setItems(filtradas);
    }

    @FXML
    private void agregarCancionesSeleccionadas() {
        if (cancionesSeleccionadas.isEmpty()) {
            mostrarAlerta("Selecciona al menos una canción", Alert.AlertType.WARNING);
            return;
        }

        if (playlistDestino == null) {
            mostrarAlerta("Error: No se ha especificado la playlist destino", Alert.AlertType.ERROR);
            return;
        }

        try {
            // Convertir CancionDTO a ElementoCancion y agregar a la playlist
            for (CancionDTO cancionDTO : cancionesSeleccionadas) {
                // Crear objeto Cancion desde CancionDTO
                Cancion cancion = new Cancion();
                // Crear ElementoCancion
                ElementoCancion elemento = new ElementoCancion(cancion);
                playlistDestino.agregar(elemento);
            }

            // Actualizar en la base de datos
            playlistDAO.actualizarPlaylist(playlistDestino);
            
            mostrarAlerta("Se agregaron " + cancionesSeleccionadas.size() + " canciones a la playlist", Alert.AlertType.INFORMATION);
            
            // Cerrar ventana
            cerrarVentana();
            
        } catch (Exception e) {
            mostrarAlerta("Error al agregar canciones: " + e.getMessage(), Alert.AlertType.ERROR);
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