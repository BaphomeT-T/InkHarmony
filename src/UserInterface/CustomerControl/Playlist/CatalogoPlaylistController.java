package UserInterface.CustomerControl.Playlist;

import BusinessLogic.Playlist;
import BusinessLogic.PlaylistDAO;
import java.io.IOException;
import java.util.List;
import javax.swing.table.TableColumn;
import javax.swing.text.TableView;
import javax.swing.text.TableView.TableCell;
import javax.swing.text.html.ImageView;

public class CatalogoPlaylistController {
    @FXML
    private TableView<Playlist> tablaPlaylists;
    @FXML
    private TableColumn<Playlist, ImageView> colPortada;
    @FXML
    private TableColumn<Playlist, String> colNombre;
    @FXML
    private TableColumn<Playlist, String> colDescripcion;
    @FXML
    private TableColumn<Playlist, Integer> colCanciones;
    @FXML
    private TableColumn<Playlist, String> colDuracion;
    @FXML
    private TableColumn<Playlist, Void> colAcciones;
    @FXML
    private TextField txtBuscarPlaylist;

    private PlaylistDAO playlistDAO = new PlaylistDAO();
    private ObservableList<Playlist> listaObservable;

    @FXML
    public void initialize() {
        configurarTabla();
        cargarPlaylists();
        configurarBusqueda();
    }

    private void configurarTabla() {
        colPortada.setCellValueFactory(cellData -> {
            // Aquí deberías convertir la imagen de portada a ImageView
            // Placeholder temporal:
            ImageView img = new ImageView(new Image("/UserInterface/Resources/img/something.png", 40, 40, true, true));
            return new SimpleObjectProperty<>(img);
        });
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitulo()));
        colDescripcion.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescripcion()));
        colCanciones.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().calcularCantidadCanciones()));
        colDuracion.setCellValueFactory(cellData -> new SimpleStringProperty("0h 0m")); // Puedes calcular duración real
        // Acciones (editar, eliminar, ver)
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnVer = new Button("Ver");
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox hbox = new HBox(5, btnVer, btnEditar, btnEliminar);
            {
                btnVer.setOnAction(event -> verPlaylist(getTableView().getItems().get(getIndex())));
                btnEditar.setOnAction(event -> editarPlaylist(getTableView().getItems().get(getIndex())));
                btnEliminar.setOnAction(event -> eliminarPlaylist(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
    }

    private void cargarPlaylists() {
        List<Playlist> playlists = playlistDAO.buscarPlaylist();
        listaObservable = FXCollections.observableArrayList(playlists);
        tablaPlaylists.setItems(listaObservable);
    }

    private void configurarBusqueda() {
        txtBuscarPlaylist.textProperty().addListener((observable, oldValue, newValue) -> filtrarPlaylists(newValue));
    }

    private void filtrarPlaylists(String filtro) {
        if (filtro == null || filtro.isBlank()) {
            tablaPlaylists.setItems(listaObservable);
            return;
        }
        ObservableList<Playlist> filtradas = FXCollections.observableArrayList();
        for (Playlist p : listaObservable) {
            if (p.getTitulo().toLowerCase().contains(filtro.toLowerCase())) {
                filtradas.add(p);
            }
        }
        tablaPlaylists.setItems(filtradas);
    }

    @FXML
    private void handleNuevaPlaylist() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/Playlist/frameNuevaPlaylist.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Nueva Playlist");
            stage.setScene(new Scene(root));
            stage.show();
            
            // Recargar la lista después de crear una nueva playlist
            stage.setOnCloseRequest(e -> cargarPlaylists());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void verPlaylist(Playlist playlist) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/Playlist/frameVistaPlaylist.fxml"));
            Parent root = loader.load();
            
            VistaPlaylistController controller = loader.getController();
            controller.setPlaylist(playlist);
            
            Stage stage = new Stage();
            stage.setTitle("Vista de Playlist: " + playlist.getTitulo());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void editarPlaylist(Playlist playlist) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/Playlist/frameEditarPlaylist.fxml"));
            Parent root = loader.load();
            
            EditarPlaylistController controller = loader.getController();
            controller.setPlaylist(playlist);
            
            Stage stage = new Stage();
            stage.setTitle("Editar Playlist: " + playlist.getTitulo());
            stage.setScene(new Scene(root));
            stage.show();
            
            // Recargar la lista después de editar
            stage.setOnCloseRequest(e -> cargarPlaylists());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void eliminarPlaylist(Playlist playlist) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/Playlist/frameEliminarPlaylist.fxml"));
            Parent root = loader.load();
            
            EliminarPlaylistController controller = loader.getController();
            controller.setPlaylist(playlist);
            controller.setOnEliminacionExitosa(this::cargarPlaylists); // Callback para recargar lista
            
            Stage stage = new Stage();
            stage.setTitle("Eliminar Playlist: " + playlist.getTitulo());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 