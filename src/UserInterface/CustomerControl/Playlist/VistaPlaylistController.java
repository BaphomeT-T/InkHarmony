package UserInterface.CustomerControl.Playlist;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.table.TableColumn;
import javax.swing.text.TableView;
import javax.swing.text.TableView.TableCell;
import javax.swing.text.html.ImageView;

public class VistaPlaylistController {
    @FXML private ImageView imgPortadaPlaylist;
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
    @FXML private TableColumn<ElementoCancion, String> colAnioCancion;
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
    }

    @FXML
    public void initialize() {
        configurarTabla();
        configurarBotones();
    }

    private void configurarTabla() {
        colPortadaCancion.setCellValueFactory(cellData -> {
            ElementoCancion elemento = cellData.getValue();
            ImageView img = new ImageView();
            if (elemento.getCancion().getPortada() != null) {
                try {
                    img.setImage(new Image(new ByteArrayInputStream(elemento.getCancion().getPortada())));
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

        colTituloCancion.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCancion().getTitulo()));
        colArtistaCancion.setCellValueFactory(cellData -> {
            String artistas = "";
            if (cellData.getValue().getCancion().getArtistas() != null && !cellData.getValue().getCancion().getArtistas().isEmpty()) {
                artistas = cellData.getValue().getCancion().getArtistas().get(0).getNombre();
            }
            return new SimpleStringProperty(artistas);
        });
        colFechaAgregado.setCellValueFactory(cellData -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            return new SimpleStringProperty(sdf.format(cellData.getValue().getFechaAgregado()));
        });
        colAnioCancion.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getCancion().getAnio())));
        colDuracionCancion.setCellValueFactory(cellData -> {
            double duracion = cellData.getValue().getCancion().getDuracion();
            int minutos = (int) (duracion / 60);
            int segundos = (int) (duracion % 60);
            return new SimpleStringProperty(String.format("%d:%02d", minutos, segundos));
        });

        // Columna de acciones (quitar canción)
        colAccionesCancion.setCellFactory(param -> new TableCell<>() {
            private final Button btnQuitar = new Button("Quitar");
            private final HBox hbox = new HBox(btnQuitar);
            
            {
                btnQuitar.setStyle("-fx-background-color: #FF6B6B; -fx-text-fill: white; -fx-background-radius: 10;");
                btnQuitar.setOnAction(event -> quitarCancion(getTableView().getItems().get(getIndex())));
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
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
            
            // Cargar portada si existe
            if (playlist.getImagenPortada() != null) {
                // TODO: Convertir BufferedImage a ImageView
            }
            
            actualizarInformacionPlaylist();
        }
    }

    private void cargarCancionesPlaylist() {
        if (playlist != null) {
            List<ElementoCancion> elementos = playlist.getComponentes();
            listaObservable = FXCollections.observableArrayList();
            
            for (ElementoCancion elemento : elementos) {
                if (elemento instanceof ElementoCancion) {
                    listaObservable.add(elemento);
                }
            }
            
            tablaCancionesPlaylist.setItems(listaObservable);
            actualizarVistaPlaylistVacia();
        }
    }

    private void actualizarInformacionPlaylist() {
        if (playlist != null) {
            int cantidadCanciones = playlist.calcularCantidadCanciones();
            double duracionTotal = playlist.obtenerDuracion();
            int horas = (int) (duracionTotal / 3600);
            int minutos = (int) ((duracionTotal % 3600) / 60);
            
            String info = String.format("%d canciones • %dh %dm", cantidadCanciones, horas, minutos);
            lblInfoPlaylist.setText(info);
        }
    }

    private void actualizarVistaPlaylistVacia() {
        boolean estaVacia = listaObservable.isEmpty();
        tablaCancionesPlaylist.setVisible(!estaVacia);
        vboxPlaylistVacia.setVisible(estaVacia);
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
            });
            
        } catch (IOException e) {
            mostrarAlerta("Error al abrir la pantalla de agregar canciones: " + e.getMessage(), Alert.AlertType.ERROR);
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
                
                mostrarAlerta("Canción removida de la playlist", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                mostrarAlerta("Error al quitar la canción: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    private void reproducirTodas() {
        if (listaObservable.isEmpty()) {
            mostrarAlerta("La playlist está vacía", Alert.AlertType.WARNING);
            return;
        }
        
        // TODO: Implementar reproducción de todas las canciones
        mostrarAlerta("Reproduciendo todas las canciones...", Alert.AlertType.INFORMATION);
    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Playlist");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
} 