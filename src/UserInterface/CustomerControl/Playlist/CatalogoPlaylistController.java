package UserInterface.CustomerControl.Playlist;
import UserInterface.CustomerControl.Playlist.EliminarPlaylistController;
import BusinessLogic.utilities.VolumeController;
import javafx.scene.control.ButtonBar;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.scene.Cursor;
import javafx.util.Duration;
import BusinessLogic.Playlist;
import DataAccessComponent.DTO.PlaylistDTO;
import DataAccessComponent.DTO.CancionDTO;
import DataAccessComponent.DAO.CancionDAO;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.InputStream;


/**
 * Controlador para el catálogo de playlists con diseño tipo Spotify.
 * Gestiona la interfaz de usuario para mostrar, buscar y administrar playlists.
 */
public class CatalogoPlaylistController implements Initializable {

    // Panel superior - Búsqueda principal
    @FXML private TextField txtBusquedaPrincipal;
    @FXML private Button btnBuscar;
    @FXML private Button btnRegresar;
    @FXML private Button btnLogo;

    // Panel izquierdo - Biblioteca
    @FXML private TextField txtBuscarPlaylist;
    @FXML private ListView<PlaylistDTO> listPlaylists;
    @FXML private Button btnAgregarPlaylist;
    @FXML private Button btnCrear;
    @FXML private VBox vboxCrearPrimera;

    // Vistas principales
    @FXML private VBox vboxCatalogoPrincipal;
    @FXML private VBox vboxPlaylistVacia;
    @FXML private VBox vboxPlaylistConCanciones;

    // Catálogo principal
    @FXML private Button btnFiltroMusica;
    @FXML private GridPane gridPlaylistsMomento1;
    @FXML private GridPane gridPlaylistsMomento2;

    // Playlist vacía
    @FXML private Label lblNombrePlaylistVacia;
    @FXML private Label lblDescripcionPlaylistVacia;
    @FXML private ImageView imgPortadaPlaylistVacia;
    @FXML private Button btnAgregarCancionesVacia;
    @FXML private Button btnEditarPlaylistVacia;

    // Playlist con canciones
    @FXML private Label lblNombrePlaylistCanciones;
    @FXML private Label lblDescripcionPlaylistCanciones;
    @FXML private Label lblCantidadCanciones;
    @FXML private Label lblDuracionTotal;
    @FXML private Label lblSeparador1;
    @FXML private Label lblSeparador2;
    @FXML private ImageView imgPortadaPlaylistCanciones;
    @FXML private Button btnPlayAll;
    @FXML private Button btnAgregarCanciones;
    @FXML private Button btnEditarPlaylist;

    // Tabla de canciones
    @FXML private TableView<Object> tableCanciones;
    @FXML private TableColumn<Object, Integer> colNumero;
    @FXML private TableColumn<Object, String> colTitulo;
    @FXML private TableColumn<Object, String> colArtista;
    @FXML private TableColumn<Object, String> colFechaAgregacion;
    @FXML private TableColumn<Object, Integer> colAnio;
    @FXML private TableColumn<Object, String> colDuracion;
    @FXML private TableColumn<Object, Void> colAcciones;

    // Panel inferior - Reproductor
    @FXML private Button btnAnterior;
    @FXML private Button btnPlayPause;
    @FXML private Button btnSiguiente;
    @FXML private Label lblTiempoActual;
    @FXML private Slider sliderTiempo;
    @FXML private Label lblTiempoTotal;
    @FXML private Label lblCancionActual;
    @FXML private Label lblArtistaActual;
    @FXML private ImageView imgCancionActual;
    @FXML private Pane panImageAlbum1;
    @FXML private ProgressBar pgbProgresoCancion;
    @FXML private Button btnVolumen;
    @FXML private Slider sliderVolumen;
    @FXML private Button btnExpandir;
    @FXML private AnchorPane anchorBarraReproduccion;

    private ObservableList<PlaylistDTO> listPlaylistsData;
    private ObservableList<Object> listCancionesData;
    private PlaylistDTO playlistSeleccionada;
    private BusinessLogic.ReproductorMP3 reproductorActivo;
    
    // Control de la barra de progreso
    private Timeline timelineProgreso;
    private double duracionCancionActual = 210.0; // duración típica de 3.5 minutos en segundos
    private boolean barraReproduccionVisible = false;
    private long tiempoInicioReproduccion = 0; // Para calcular tiempo transcurrido
    // Control de volumen
    private double volumenAnterior = 0.7; // Guardar volumen antes de silenciar
    private boolean silenciado = false;
    private boolean pausado = false;
    
    // Control del slider de tiempo
    private boolean arrastrando = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializar listas
        listPlaylistsData = FXCollections.observableArrayList();
        listCancionesData = FXCollections.observableArrayList();

        listPlaylists.setItems(listPlaylistsData);
        tableCanciones.setItems(listCancionesData);

        // Configurar columnas de la tabla de canciones
        configurarTablaCancion();

        // Configurar cell factory para el ListView de playlists con menú de tres puntos
        configurarCellFactoryPlaylists();

        // Configurar eventos
        listPlaylists.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    playlistSeleccionada = newValue;
                    mostrarDetallesPlaylist(newValue);
                });

        tableCanciones.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> seleccionarCancion(newValue));

        // Configurar búsqueda de playlists
        txtBuscarPlaylist.textProperty().addListener((observable, oldValue, newValue) ->
                filtrarPlaylists(newValue));

        // Cargar playlists al inicializar
        cargarPlaylists();

        // Configurar control de volumen
        configurarControlVolumen();
        
        // Configurar slider de tiempo interactivo (como YouTube)
        configurarSliderTiempo();
        
        // Configurar botón de play/pause inicial
        if (btnPlayPause != null) {
            btnPlayPause.setText("▶");
            btnPlayPause.setStyle(
                "-fx-font-size: 20px; " +
                "-fx-text-fill: white; " +
                "-fx-background-color: transparent; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 5px;"
            );
            // Limpiar cualquier tooltip o contenido adicional
            btnPlayPause.setTooltip(null);
            btnPlayPause.setGraphic(null);
            System.out.println("Botón play/pause inicializado limpio");
        }

        // Crear playlists recomendadas del momento
        crearPlaylistsRecomendadas();

        // Mostrar catálogo principal al inicio
        mostrarCatalogoPrincipal();
        
        // Debug: Verificar elementos FXML
        System.out.println("Inicialización completada - Verificando elementos FXML:");
        System.out.println("lblTiempoActual: " + (lblTiempoActual != null ? "OK" : "NULL"));
        System.out.println("lblTiempoTotal: " + (lblTiempoTotal != null ? "OK" : "NULL"));
        System.out.println("sliderVolumen: " + (sliderVolumen != null ? "OK" : "NULL"));
        System.out.println("pgbProgresoCancion: " + (pgbProgresoCancion != null ? "OK" : "NULL"));
        System.out.println("btnVolumen: " + (btnVolumen != null ? "OK" : "NULL"));
        
        // Forzar inicialización de controles
        forzarInicializacionControles();
    }

    /**
     * Fuerza la inicialización de todos los controles de reproducción
     */
    private void forzarInicializacionControles() {
        // Inicializar tiempos con valores por defecto
        if (lblTiempoActual != null) {
            lblTiempoActual.setText("0:00");
            lblTiempoActual.setVisible(true);
            System.out.println("lblTiempoActual inicializado y visible");
        }
        
        if (lblTiempoTotal != null) {
            lblTiempoTotal.setText("3:30");
            lblTiempoTotal.setVisible(true);
            System.out.println("lblTiempoTotal inicializado y visible");
        }
        
        // Configurar botón de volumen inicial
        if (btnVolumen != null) {
            btnVolumen.setText("🔊");
            btnVolumen.setVisible(true);
            System.out.println("btnVolumen inicializado y visible");
        }
        
        // Asegurar que el slider esté visible y funcional
        if (sliderVolumen != null) {
            sliderVolumen.setVisible(true);
            sliderVolumen.setDisable(false);
            System.out.println("sliderVolumen visible y habilitado");
        }
    }

    /**
     * Configura el cell factory para el ListView de playlists
     */
    private void configurarCellFactoryPlaylists() {
        listPlaylists.setCellFactory(listView -> new ListCell<PlaylistDTO>() {
            private Button btnMenu = new Button("⋯");

            @Override
            protected void updateItem(PlaylistDTO playlist, boolean empty) {
                super.updateItem(playlist, empty);

                if (empty || playlist == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Crear contenedor horizontal
                    HBox container = new HBox(10);
                    container.setAlignment(Pos.CENTER_LEFT);
                    container.setStyle("-fx-padding: 5px;");

                    // Crear label con el título de la playlist
                    Label lblTitulo = new Label(playlist.getTituloPlaylist());
                    lblTitulo.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                    lblTitulo.setMaxWidth(Double.MAX_VALUE);
                    HBox.setHgrow(lblTitulo, Priority.ALWAYS);

                    // Configurar botón de menú con 3 puntos horizontales
                    btnMenu.setText("⋯");
                    btnMenu.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-cursor: hand; -fx-font-weight: bold; -fx-text-fill: #AFAFC7;");

                    // Crear menú contextual
                    ContextMenu contextMenu = new ContextMenu();

                    MenuItem editarItem = new MenuItem("Editar");
                    editarItem.setOnAction(e -> CatalogoPlaylistController.this.abrirEditarPlaylist(playlist));

                    MenuItem borrarItem = new MenuItem("Borrar");
                    // CAMBIO AQUÍ: Ahora llama al método que abre la ventana de eliminar
                    borrarItem.setOnAction(e -> CatalogoPlaylistController.this.abrirEliminarPlaylist(playlist));

                    contextMenu.getItems().addAll(editarItem, borrarItem);

                    // Configurar evento del botón para mostrar menú
                    btnMenu.setOnAction(e -> {
                        contextMenu.show(btnMenu, javafx.geometry.Side.BOTTOM, 0, 0);
                    });

                    container.getChildren().addAll(lblTitulo, btnMenu);
                    setText(null);
                    setGraphic(container);
                }
            }
        });
    }

    /**
     * Configura las columnas de la tabla de canciones
     */
    private void configurarTablaCancion() {
        // Aplicar estilos CSS personalizados a la tabla
        tableCanciones.setRowFactory(tv -> {
            TableRow<Object> row = new TableRow<>();
            row.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
            
            row.setOnMouseEntered(e -> {
                if (!row.isEmpty()) {
                    row.setStyle("-fx-background-color: #5A5A80; -fx-text-fill: white;");
                }
            });
            
            row.setOnMouseExited(e -> {
                if (!row.isEmpty()) {
                    row.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
                }
            });
            
            // Configurar doble clic para reproducir canción
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    Object cancionSeleccionada = row.getItem();
                    if (cancionSeleccionada != null) {
                        reproducirCancionSeleccionada(cancionSeleccionada);
                    }
                }
            });
            
            return row;
        });
        
        // También configurar la tabla para Enter
        tableCanciones.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                Object cancionSeleccionada = tableCanciones.getSelectionModel().getSelectedItem();
                if (cancionSeleccionada != null) {
                    reproducirCancionSeleccionada(cancionSeleccionada);
                }
            }
        });

        // Configurar columna de número
        colNumero.setCellValueFactory(cellData -> {
            int index = tableCanciones.getItems().indexOf(cellData.getValue()) + 1;
            return new javafx.beans.property.SimpleIntegerProperty(index).asObject();
        });
        
        colNumero.setCellFactory(col -> {
            TableCell<Object, Integer> cell = new TableCell<Object, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(String.valueOf(item));
                    }
                    setStyle("-fx-text-fill: #AFAFC7; -fx-alignment: CENTER; -fx-background-color: transparent;");
                }
            };
            return cell;
        });

        // Configurar columna de título
        colTitulo.setCellValueFactory(cellData -> {
            Object cancion = cellData.getValue();
            if (cancion instanceof CancionDTO) {
                return new javafx.beans.property.SimpleStringProperty(((CancionDTO) cancion).getTitulo());
            }
            return new javafx.beans.property.SimpleStringProperty(cancion.toString());
        });
        
        colTitulo.setCellFactory(col -> {
            TableCell<Object, String> cell = new TableCell<Object, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item);
                    }
                    setStyle("-fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-background-color: transparent; -fx-padding: 0 0 0 10px;");
                }
            };
            return cell;
        });

        // Configurar columna de artista
        colArtista.setCellValueFactory(cellData -> {
            Object cancion = cellData.getValue();
            if (cancion instanceof CancionDTO) {
                List<DataAccessComponent.DTO.ArtistaDTO> artistas = ((CancionDTO) cancion).getArtistas();
                if (artistas != null && !artistas.isEmpty()) {
                    return new javafx.beans.property.SimpleStringProperty(artistas.get(0).getNombre());
                }
                return new javafx.beans.property.SimpleStringProperty("Artista Desconocido");
            }
            return new javafx.beans.property.SimpleStringProperty("Artista Desconocido");
        });
        
        colArtista.setCellFactory(col -> {
            TableCell<Object, String> cell = new TableCell<Object, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item);
                    }
                    setStyle("-fx-text-fill: #AFAFC7; -fx-alignment: CENTER_LEFT; -fx-background-color: transparent; -fx-padding: 0 0 0 10px;");
                }
            };
            return cell;
        });

        // Configurar columna de fecha de agregación
        colFechaAgregacion.setCellValueFactory(cellData -> {
            // Por ahora devolver fecha actual, luego se puede personalizar
            return new javafx.beans.property.SimpleStringProperty(
                    java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            );
        });
        
        colFechaAgregacion.setCellFactory(col -> {
            TableCell<Object, String> cell = new TableCell<Object, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item);
                    }
                    setStyle("-fx-text-fill: #AFAFC7; -fx-alignment: CENTER; -fx-background-color: transparent;");
                }
            };
            return cell;
        });

        // Configurar columna de año
        colAnio.setCellValueFactory(cellData -> {
            Object cancion = cellData.getValue();
            if (cancion instanceof CancionDTO) {
                return new javafx.beans.property.SimpleIntegerProperty(((CancionDTO) cancion).getAnio()).asObject();
            }
            return new javafx.beans.property.SimpleIntegerProperty(2024).asObject();
        });
        
        colAnio.setCellFactory(col -> {
            TableCell<Object, Integer> cell = new TableCell<Object, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(String.valueOf(item));
                    }
                    setStyle("-fx-text-fill: #AFAFC7; -fx-alignment: CENTER; -fx-background-color: transparent;");
                }
            };
            return cell;
        });

        // Configurar columna de duración
        colDuracion.setCellValueFactory(cellData -> {
            Object cancion = cellData.getValue();
            if (cancion instanceof CancionDTO) {
                double duracionDouble = ((CancionDTO) cancion).getDuracion();
                int duracion = (int) duracionDouble;
                int minutos = duracion / 60;
                int segundos = duracion % 60;
                return new javafx.beans.property.SimpleStringProperty(
                        String.format("%d:%02d", minutos, segundos)
                );
            }
            return new javafx.beans.property.SimpleStringProperty("3:30");
        });
        
        colDuracion.setCellFactory(col -> {
            TableCell<Object, String> cell = new TableCell<Object, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item);
                    }
                    setStyle("-fx-text-fill: #AFAFC7; -fx-alignment: CENTER; -fx-background-color: transparent;");
                }
            };
            return cell;
        });

        // Configurar columna de acciones con botón de eliminar
        colAcciones.setCellFactory(col -> new TableCell<Object, Void>() {
            private final Button btnEliminar = new Button("−");

            {
                btnEliminar.setStyle(
                        "-fx-background-color: transparent; " +
                                "-fx-text-fill: #ff6b6b; " +
                                "-fx-font-size: 16px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-cursor: hand; " +
                                "-fx-padding: 5px 10px;"
                );

                btnEliminar.setOnMouseEntered(e ->
                        btnEliminar.setStyle(
                                "-fx-background-color: #ff6b6b; " +
                                        "-fx-text-fill: white; " +
                                        "-fx-font-size: 16px; " +
                                        "-fx-font-weight: bold; " +
                                        "-fx-cursor: hand; " +
                                        "-fx-padding: 5px 10px; " +
                                        "-fx-background-radius: 15px;"
                        )
                );

                btnEliminar.setOnMouseExited(e ->
                        btnEliminar.setStyle(
                                "-fx-background-color: transparent; " +
                                        "-fx-text-fill: #ff6b6b; " +
                                        "-fx-font-size: 16px; " +
                                        "-fx-font-weight: bold; " +
                                        "-fx-cursor: hand; " +
                                        "-fx-padding: 5px 10px;"
                        )
                );

                btnEliminar.setOnAction(e -> {
                    Object cancion = getTableRow().getItem();
                    if (cancion != null) {
                        CatalogoPlaylistController.this.eliminarCancionDePlaylist(cancion);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnEliminar);
                }
            }
        });
    }

    /**
     * Elimina una canción de la playlist actual (no de la base de datos)
     */
    private void eliminarCancionDePlaylist(Object cancion) {
        try {
            if (playlistSeleccionada == null) {
                System.out.println("No hay playlist seleccionada");
                return;
            }

            // Mostrar confirmación
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Eliminar Canción");
            alert.setHeaderText("¿Quitar canción de la playlist?");

            final String nombreCancion;
            final int idCancion;

            if (cancion instanceof CancionDTO) {
                nombreCancion = ((CancionDTO) cancion).getTitulo();
                idCancion = ((CancionDTO) cancion).getIdCancion();
            } else {
                nombreCancion = cancion.toString();
                idCancion = -1; // Para canciones de ejemplo
            }

            alert.setContentText("¿Estás seguro de que quieres quitar \"" + nombreCancion + "\" de la playlist \"" + playlistSeleccionada.getTituloPlaylist() + "\"?\n\n(La canción no se eliminará de tu biblioteca)");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        // Eliminar de la tabla de la interfaz
                        listCancionesData.remove(cancion);

                        // Eliminar de la playlist en la base de datos (solo si es una canción real)
                        if (idCancion != -1) {
                            Playlist playlistLogic = new Playlist();

                            // Configurar la playlist en el objeto de lógica de negocio
                            playlistLogic.setPlaylistDTO(playlistSeleccionada);

                            boolean eliminado = playlistLogic.eliminarCancion(idCancion);

                            if (eliminado) {
                                System.out.println("Canción eliminada de la playlist en BD: " + nombreCancion);

                                // Actualizar la playlist seleccionada
                                actualizarPlaylistSeleccionada();
                            } else {
                                System.out.println("Error al eliminar canción de la playlist en BD");
                                // Volver a agregar a la tabla si falló en BD
                                listCancionesData.add(cancion);
                                mostrarAlerta("Error", "No se pudo quitar la canción de la playlist", Alert.AlertType.ERROR);
                                return;
                            }
                        }

                        // Actualizar información de la playlist
                        actualizarInfoPlaylist();

                        System.out.println("Canción quitada de la playlist: " + nombreCancion);

                    } catch (Exception e) {
                        System.out.println("Error al eliminar canción de la playlist: " + e.getMessage());
                        e.printStackTrace();
                        // Volver a agregar a la tabla si hubo error
                        if (!listCancionesData.contains(cancion)) {
                            listCancionesData.add(cancion);
                        }
                        mostrarAlerta("Error", "Error al quitar la canción de la playlist: " + e.getMessage(), Alert.AlertType.ERROR);
                    }
                }
            });

        } catch (Exception e) {
            System.out.println("Error al eliminar canción: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "Error inesperado: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Actualiza la playlist seleccionada desde la base de datos
     */
    private void actualizarPlaylistSeleccionada() {
        try {
            if (playlistSeleccionada != null) {
                Playlist playlistLogic = new Playlist();
                PlaylistDTO playlistActualizada = playlistLogic.buscarPorId(playlistSeleccionada.getIdPlaylist());
                if (playlistActualizada != null) {
                    playlistSeleccionada = playlistActualizada;
                }
            }
        } catch (Exception e) {
            System.out.println("Error al actualizar playlist seleccionada: " + e.getMessage());
        }
    }

    /**
     * Actualiza la información de cantidad y duración de la playlist
     */
    private void actualizarInfoPlaylist() {
        if (playlistSeleccionada == null) return;

        int numCanciones = listCancionesData.size();

        // Actualizar labels según la vista actual
        if (vboxPlaylistConCanciones.isVisible()) {
            lblCantidadCanciones.setText(numCanciones + (numCanciones == 1 ? " canción" : " canciones"));
            lblDuracionTotal.setText("~" + (numCanciones * 3) + " min");
        }

        // Si no quedan canciones, cambiar a vista vacía
        if (numCanciones == 0) {
            mostrarPlaylistVacia(playlistSeleccionada);
        }
    }

    /**
     * Abre la ventana de eliminar playlist
     */
    private void abrirEliminarPlaylist(PlaylistDTO playlist) {
        try {
            System.out.println("=== ABRIENDO VENTANA ELIMINAR PLAYLIST ===");
            System.out.println("Playlist a eliminar: " + playlist.getTituloPlaylist());

            // Cargar la playlist completa desde la base de datos para asegurar que tiene todos los datos
            Playlist playlistLogic = new Playlist();
            PlaylistDTO playlistCompleta = playlistLogic.buscarPorId(playlist.getIdPlaylist());

            if (playlistCompleta == null) {
                mostrarAlerta("Error", "No se pudo cargar la información completa de la playlist", Alert.AlertType.ERROR);
                return;
            }

            // Cargar el FXML de la ventana eliminar
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/Playlist/frameEliminarPlaylist.fxml"));
            Parent root = loader.load();

            // Obtener el controlador y pasarle la playlist
            EliminarPlaylistController controller = loader.getController();
            controller.setPlaylist(playlistCompleta);

            // Crear y configurar la ventana
            Stage stage = new Stage();
            stage.setTitle("Eliminar Playlist - " + playlistCompleta.getTituloPlaylist());
// CAMBIAR ESTAS DIMENSIONES:
            stage.setScene(new Scene(root, 1200, 800)); // Era 800x600, ahora 1200x800
            stage.setResizable(true); // Permitir redimensionar
            stage.setMinWidth(1000);  // Ancho mínimo
            stage.setMinHeight(700);  // Alto mínimo
            stage.centerOnScreen();

            // O VERSIÓN MÁS SIMPLE (solo verifica si hay playlist seleccionada):
            stage.setOnHidden(e -> {
                System.out.println("Ventana de eliminar cerrada, recargando playlists...");
                cargarPlaylists();
                // Si había una playlist seleccionada, regresar al catálogo principal por seguridad
                if (playlistSeleccionada != null) {
                    mostrarCatalogoPrincipal();
                }
            });

            System.out.println("Mostrando ventana de eliminar playlist...");
            stage.show();

        } catch (Exception e) {
            System.out.println("ERROR al abrir ventana eliminar playlist: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "Error al abrir la ventana de eliminar playlist: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Crea las tarjetas de playlists recomendadas
     */
    private void crearPlaylistsRecomendadas() {
        // Datos de ejemplo para playlists del momento
        String[][] playlistsData = {
                {"no tiene sentido", "Beéle", "/UserInterface/Resources/img/CatalogoPlaylist/beele.png"},
                {"Rock That Body", "Black Eyed Peas", "/UserInterface/Resources/img/CatalogoPlaylist/blackeyedpeas.png"},
                {"Stay", "Misdemeanor", "/UserInterface/Resources/img/CatalogoPlaylist/stay.png"},
                {"Blinding Lights", "The Weeknd", "/UserInterface/Resources/img/CatalogoPlaylist/weeknd.png"},
                {"Somebody Else", "The 1975", "/UserInterface/Resources/img/CatalogoPlaylist/the1975.png"},
                {"Die with the Smile", "Bruno Mars & Lady Gaga", "/UserInterface/Resources/img/CatalogoPlaylist/brunomars.png"},
                {"As it was", "Harry Styles", "/UserInterface/Resources/img/CatalogoPlaylist/harrystyles.jpg"},
                {"Wildflower", "Billie Eilish", "/UserInterface/Resources/img/CatalogoPlaylist/billieeilish.png"}
        };

        // Limpiar grids
        gridPlaylistsMomento1.getChildren().clear();
        gridPlaylistsMomento2.getChildren().clear();

        // Agregar primeras 4 playlists al primer grid
        for (int i = 0; i < 4 && i < playlistsData.length; i++) {
            VBox tarjeta = crearTarjetaPlaylist(playlistsData[i][0], playlistsData[i][1], playlistsData[i][2]);
            gridPlaylistsMomento1.add(tarjeta, i, 0);
        }

        // Agregar siguientes 4 playlists al segundo grid
        for (int i = 4; i < 8 && i < playlistsData.length; i++) {
            VBox tarjeta = crearTarjetaPlaylist(playlistsData[i][0], playlistsData[i][1], playlistsData[i][2]);
            gridPlaylistsMomento2.add(tarjeta, i - 4, 0);
        }
    }

    /**
     * Crea una tarjeta de playlist recomendada
     * VERSIÓN MODIFICADA para que la imagen llene todo el espacio
     */
    private VBox crearTarjetaPlaylist(String titulo, String artista, String imagePath) {
        VBox tarjeta = new VBox(10);
        tarjeta.setAlignment(Pos.TOP_LEFT);
        tarjeta.setStyle("-fx-cursor: hand;");
        tarjeta.setPrefWidth(150);

        // Imagen de la playlist
        VBox contenedorImagen = new VBox();
        contenedorImagen.setAlignment(Pos.CENTER);
        contenedorImagen.setStyle("-fx-background-color: #201D4E; -fx-background-radius: 10px;");
        contenedorImagen.setPrefWidth(150);
        contenedorImagen.setPrefHeight(150);

        ImageView imagen = new ImageView();

        // CAMBIOS AQUÍ - Para que la imagen llene todo el espacio:
        imagen.setFitHeight(150);
        imagen.setFitWidth(150);
        imagen.setPreserveRatio(false); // CLAVE: false para llenar completamente
        imagen.setSmooth(true); // Mejor calidad visual

        // Aplicar bordes redondeados a la imagen
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(150, 150);
        clip.setArcWidth(10);
        clip.setArcHeight(10);
        imagen.setClip(clip);

        try {
            imagen.setImage(new Image(imagePath));
        } catch (Exception e) {
            // Imagen por defecto si no se encuentra la imagen
            imagen.setImage(new Image("/UserInterface/Resources/img/CatalogoPlaylist/playlist-default.png"));
        }

        contenedorImagen.getChildren().add(imagen);

        // Título de la canción
        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        lblTitulo.setWrapText(true);
        lblTitulo.setMaxWidth(150);

        // Artista
        Label lblArtista = new Label(artista);
        lblArtista.setStyle("-fx-text-fill: #AFAFC7; -fx-font-size: 12px;");
        lblArtista.setWrapText(true);
        lblArtista.setMaxWidth(150);

        tarjeta.getChildren().addAll(contenedorImagen, lblTitulo, lblArtista);

        // Efecto hover
        tarjeta.setOnMouseEntered(e -> tarjeta.setStyle("-fx-cursor: hand; -fx-opacity: 0.8;"));
        tarjeta.setOnMouseExited(e -> tarjeta.setStyle("-fx-cursor: hand; -fx-opacity: 1.0;"));

        return tarjeta;
    }
    // =============== MÉTODOS DE NAVEGACIÓN ENTRE VISTAS ===============

    /**
     * Muestra la vista principal del catálogo
     */
    private void mostrarCatalogoPrincipal() {
        vboxCatalogoPrincipal.setVisible(true);
        vboxPlaylistVacia.setVisible(false);
        vboxPlaylistConCanciones.setVisible(false);

        // Limpiar selección de playlist
        listPlaylists.getSelectionModel().clearSelection();
        playlistSeleccionada = null;
    }

    /**
     * Muestra la vista de playlist vacía
     * MODIFICADO para que la imagen llene todo el cuadrito
     */
    private void mostrarPlaylistVacia(PlaylistDTO playlist) {
        vboxCatalogoPrincipal.setVisible(false);
        vboxPlaylistVacia.setVisible(true);
        vboxPlaylistConCanciones.setVisible(false);

        // Actualizar información de la playlist vacía
        lblNombrePlaylistVacia.setText(playlist.getTituloPlaylist());
        lblDescripcionPlaylistVacia.setText(
                playlist.getDescripcion() != null ? playlist.getDescripcion() : "Sin descripción"
        );

        // CONFIGURAR EL IMAGEVIEW PARA LLENAR TODO EL ESPACIO
        imgPortadaPlaylistVacia.setPreserveRatio(false); // CLAVE: false para llenar completamente
        imgPortadaPlaylistVacia.setSmooth(true); // Mejor calidad visual

        // Cargar imagen de portada
        try {
            if (playlist.getImagenPortada() != null && playlist.getImagenPortada().length > 0) {
                InputStream imageStream = new ByteArrayInputStream(playlist.getImagenPortada());
                Image imagen = new Image(imageStream);
                imgPortadaPlaylistVacia.setImage(imagen);
                System.out.println("Imagen de playlist cargada: " + imagen.getWidth() + "x" + imagen.getHeight());
            } else {
                Image imagenDefecto = new Image("/UserInterface/Resources/img/CatalogoPlaylist/simbolo-aplicacion.png");
                imgPortadaPlaylistVacia.setImage(imagenDefecto);
                System.out.println("Cargando imagen por defecto");
            }
        } catch (Exception e) {
            Image imagenDefecto = new Image("/UserInterface/Resources/img/CatalogoPlaylist/simbolo-aplicacion.png");
            imgPortadaPlaylistVacia.setImage(imagenDefecto);
            System.out.println("Error cargando imagen, usando por defecto: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * Muestra la vista de playlist con canciones
     */
    private void mostrarPlaylistConCanciones(PlaylistDTO playlist) {
        vboxCatalogoPrincipal.setVisible(false);
        vboxPlaylistVacia.setVisible(false);
        vboxPlaylistConCanciones.setVisible(true);

        // Actualizar información de la playlist con canciones
        lblNombrePlaylistCanciones.setText(playlist.getTituloPlaylist());
        lblDescripcionPlaylistCanciones.setText(
                playlist.getDescripcion() != null ? playlist.getDescripcion() : "Sin descripción"
        );

        // AGREGAR CONFIGURACIÓN PARA LLENAR TODO EL ESPACIO (igual que en playlist vacía)
        imgPortadaPlaylistCanciones.setPreserveRatio(false); // CLAVE: false para llenar completamente
        imgPortadaPlaylistCanciones.setSmooth(true); // Mejor calidad visual

        // Cargar imagen de portada
        try {
            if (playlist.getImagenPortada() != null && playlist.getImagenPortada().length > 0) {
                InputStream imageStream = new ByteArrayInputStream(playlist.getImagenPortada());
                Image imagen = new Image(imageStream);
                imgPortadaPlaylistCanciones.setImage(imagen);
                System.out.println("Imagen de playlist con canciones cargada: " + imagen.getWidth() + "x" + imagen.getHeight());
            } else {
                Image imagenDefecto = new Image("/UserInterface/Resources/img/CatalogoPlaylist/simbolo-aplicacion.png");
                imgPortadaPlaylistCanciones.setImage(imagenDefecto);
                System.out.println("Cargando imagen por defecto para playlist con canciones");
            }
        } catch (Exception e) {
            Image imagenDefecto = new Image("/UserInterface/Resources/img/CatalogoPlaylist/simbolo-aplicacion.png");
            imgPortadaPlaylistCanciones.setImage(imagenDefecto);
            System.out.println("Error cargando imagen para playlist con canciones, usando por defecto: " + e.getMessage());
            e.printStackTrace();
        }

        // Actualizar información de canciones
        int numCanciones = playlist.getCancionesIds() != null ? playlist.getCancionesIds().size() : 0;
        lblCantidadCanciones.setText(numCanciones + (numCanciones == 1 ? " canción" : " canciones"));
        lblDuracionTotal.setText("~" + (numCanciones * 3) + " min");

        // Cargar canciones en la tabla
        cargarCancionesEnTabla(playlist);
    }

    // =============== MÉTODOS DE MANEJO DE EVENTOS ===============

    @FXML
    private void handleBusquedaPrincipal() {
        String busqueda = txtBusquedaPrincipal.getText();
        System.out.println("Búsqueda principal: " + busqueda);
    }

    @FXML
    private void handleBuscar() {
        handleBusquedaPrincipal();
    }

    @FXML
    private void handleLogo() {
        // Limpiar selección actual de playlist
        listPlaylists.getSelectionModel().clearSelection();
        playlistSeleccionada = null;

        // Limpiar datos de canciones
        listCancionesData.clear();

        // Mostrar la vista del catálogo principal
        mostrarCatalogoPrincipal();

        System.out.println("Regresando al catálogo principal desde el logo");
    }

    @FXML
    private void handleRegresar() {
        try {
            Stage currentStage = (Stage) btnRegresar.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            System.out.println("Error al regresar: " + e.getMessage());
        }
    }

    @FXML
    private void handleBuscarPlaylist() {
        // No es necesario implementar aquí, se maneja con el listener del textProperty
    }

    @FXML
    private void handleAgregarPlaylist() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/Playlist/frameNuevaPlaylist.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Nueva Playlist");
            stage.setScene(new Scene(root, 1200, 800));
            stage.setResizable(true);
            stage.setMinWidth(1000);
            stage.setMinHeight(700);
            stage.centerOnScreen();

            // Cuando se cierre la ventana, recargar playlists
            stage.setOnHidden(e -> cargarPlaylists());

            stage.show();
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al abrir nueva playlist: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSeleccionarPlaylist() {
        PlaylistDTO playlist = listPlaylists.getSelectionModel().getSelectedItem();
        if (playlist != null) {
            mostrarDetallesPlaylist(playlist);
        }
    }

    @FXML
    private void handlePlayAll() {
        if (playlistSeleccionada == null) {
            mostrarAlerta("Advertencia", "Por favor, selecciona una playlist para reproducir.", Alert.AlertType.WARNING);
            return;
        }
        
        try {
            System.out.println("Intentando reproducir playlist: " + playlistSeleccionada.getTituloPlaylist());
            
            Playlist playlist = new Playlist();
            playlist.setPlaylistDTO(playlistSeleccionada);
            
            // Verificar si la playlist tiene canciones
            if (playlistSeleccionada.getCancionesIds() == null || playlistSeleccionada.getCancionesIds().isEmpty()) {
                mostrarAlerta("Advertencia", "La playlist está vacía. Agrega canciones antes de reproducir.", Alert.AlertType.WARNING);
                return;
            }
            
            System.out.println("Playlist tiene " + playlistSeleccionada.getCancionesIds().size() + " canciones");
            
            // Obtener la primera canción para obtener su duración real
            try {
                CancionDAO cancionDAO = new CancionDAO();
                
                // Obtener el índice actual del reproductor (debería ser 0 al iniciar)
                int indiceInicial = reproductorActivo != null ? reproductorActivo.getPlaylist().getIndiceActual() : 0;
                
                CancionDTO primeraCancion = cancionDAO.buscarPorId(playlistSeleccionada.getCancionesIds().get(indiceInicial));
                if (primeraCancion != null) {
                    duracionCancionActual = primeraCancion.getDuracion();
                    // Actualizar slider de tiempo para la nueva duración
                    actualizarSliderTiempo();
                    System.out.println("=== INICIANDO REPRODUCCIÓN ===");
                    System.out.println("Canción inicial: " + primeraCancion.getTitulo());
                    System.out.println("Duración real: " + duracionCancionActual + " segundos (" + formatearTiempo(duracionCancionActual) + ")");
                } else {
                    duracionCancionActual = 210.0; // Valor por defecto
                    actualizarSliderTiempo();
                    System.out.println("No se pudo obtener duración de la primera canción, usando valor por defecto: 210 segundos");
                }
            } catch (Exception e) {
                duracionCancionActual = 210.0; // Valor por defecto en caso de error
                actualizarSliderTiempo();
                System.out.println("Error al obtener duración de la primera canción, usando valor por defecto: " + e.getMessage());
            }
            
            // Obtener los bytes de las canciones para crear/actualizar el reproductor
            List<byte[]> cancionesBytes = playlist.obtenerCancionesParaReproduccion();
            
            // SIEMPRE actualizar las canciones del reproductor con la nueva playlist
            if (reproductorActivo != null) {
                // Detener reproducción actual antes de cambiar las canciones
                reproductorActivo.detener();
                if (timelineProgreso != null) {
                    timelineProgreso.stop();
                }
                
                // Actualizar las canciones usando GestorPlaylist
                reproductorActivo.getPlaylist().setCanciones(cancionesBytes);
                System.out.println("=== PLAYLIST CAMBIADA USANDO GestorPlaylist.setCanciones() ===");
                System.out.println("Reproductor detenido y canciones actualizadas");
                System.out.println("Nueva playlist: " + playlistSeleccionada.getTituloPlaylist());
                System.out.println("Número de canciones cargadas: " + cancionesBytes.size());
                System.out.println("Índice reiniciado a: " + reproductorActivo.getPlaylist().getIndiceActual());
            } else {
                // Si no existe reproductor, crear uno nuevo
                reproductorActivo = BusinessLogic.ReproductorMP3.getInstancia(cancionesBytes);
                System.out.println("=== NUEVO REPRODUCTOR CREADO ===");
                System.out.println("Playlist: " + playlistSeleccionada.getTituloPlaylist());
                System.out.println("Número de canciones: " + cancionesBytes.size());
            }
            
            // Reproducir la playlist actualizada
            playlist.reproducir();
            
            // Inicializar inmediatamente los tiempos con la duración real
            if (lblTiempoActual != null) {
                lblTiempoActual.setText("0:00");
                System.out.println("Tiempo actual inicializado: 0:00");
            }
            if (lblTiempoTotal != null) {
                lblTiempoTotal.setText(formatearTiempo(duracionCancionActual));
                System.out.println("Tiempo total inicializado: " + formatearTiempo(duracionCancionActual));
            }
            
            // Iniciar la barra de progreso
            iniciarBarraProgreso();
            
            // Actualizar el botón de play/pause a PAUSE
            actualizarIconoPlayPause("⏸", "PAUSE");
            
            // Actualizar la interfaz del reproductor con el nombre de la primera canción
            try {
                CancionDAO cancionDAO = new CancionDAO();
                
                // Usar el mismo índice que el reproductor
                int indiceActual = reproductorActivo != null ? reproductorActivo.getPlaylist().getIndiceActual() : 0;
                
                CancionDTO cancionActual = cancionDAO.buscarPorId(playlistSeleccionada.getCancionesIds().get(indiceActual));
                if (cancionActual != null) {
                    String nombreArtista = "Artista Desconocido";
                    if (cancionActual.getArtistas() != null && !cancionActual.getArtistas().isEmpty()) {
                        nombreArtista = cancionActual.getArtistas().get(0).getNombre();
                    }
                    actualizarReproductor(cancionActual.getTitulo(), nombreArtista);
                    System.out.println("Reproductor actualizado: " + cancionActual.getTitulo() + " - " + nombreArtista);
                } else {
                    actualizarReproductor(playlistSeleccionada.getTituloPlaylist(), "Playlist");
                    System.out.println("No se pudo cargar información de la canción, usando nombre de playlist");
                }
            } catch (Exception e) {
                actualizarReproductor(playlistSeleccionada.getTituloPlaylist(), "Playlist");
                System.out.println("Error al obtener información de la canción: " + e.getMessage());
            }
            
            System.out.println("Playlist cargada en el reproductor correctamente");
            
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo reproducir la playlist: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAgregarCanciones() {
        if (playlistSeleccionada == null) {
            mostrarAlerta("Advertencia", "Por favor, selecciona una playlist para agregar canciones.", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/Playlist/frameAgregarCanciones.fxml"));
            Parent root = loader.load();

            // Obtener el controlador y pasarle la playlist
            AgregarCancionesController controller = loader.getController();
            controller.setPlaylist(playlistSeleccionada);

            Stage stage = new Stage();
            stage.setTitle("Agregar Canciones - " + playlistSeleccionada.getTituloPlaylist());
            stage.setScene(new Scene(root, 1200, 650));
            stage.setResizable(true);
            stage.setMinWidth(1000);
            stage.setMinHeight(600);
            stage.centerOnScreen();

            // Cuando se cierre la ventana, recargar playlists
            stage.setOnHidden(e -> {
                cargarPlaylists();
                // Actualizar la vista actual de la playlist
                if (playlistSeleccionada != null) {
                    mostrarDetallesPlaylist(playlistSeleccionada);
                }
            });

            stage.show();
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al abrir pantalla de agregar canciones: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditarPlaylist() {
        if (playlistSeleccionada != null) {
            abrirEditarPlaylist(playlistSeleccionada);
        }
    }

    @FXML
    private void handleSeleccionarCancion() {
        Object cancion = tableCanciones.getSelectionModel().getSelectedItem();
        if (cancion != null) {
            reproducirCancionSeleccionada(cancion);
        }
    }
    
    /**
     * Reproduce una canción específica seleccionada de la tabla
     */
    private void reproducirCancionSeleccionada(Object cancion) {
        try {
            if (playlistSeleccionada == null) {
                mostrarAlerta("Error", "No hay playlist seleccionada", Alert.AlertType.ERROR);
                return;
            }
            
            // Encontrar el índice de la canción seleccionada
            int indiceSelecionado = tableCanciones.getSelectionModel().getSelectedIndex();
            
            if (indiceSelecionado >= 0 && cancion instanceof CancionDTO) {
                CancionDTO cancionDTO = (CancionDTO) cancion;
                
                System.out.println("=== REPRODUCIENDO CANCIÓN SELECCIONADA ===");
                System.out.println("Canción: " + cancionDTO.getTitulo());
                System.out.println("Índice en tabla: " + indiceSelecionado);
                
                // Detener reproductor actual si existe
                if (reproductorActivo != null) {
                    try {
                        reproductorActivo.detener();
                        if (timelineProgreso != null) {
                            timelineProgreso.stop();
                        }
                        System.out.println("Reproductor anterior detenido");
                    } catch (Exception e) {
                        System.out.println("Error al detener reproductor anterior: " + e.getMessage());
                    }
                }
                
                // Crear nueva playlist desde el inicio
                Playlist playlist = new Playlist();
                playlist.setPlaylistDTO(playlistSeleccionada);
                
                // Obtener las canciones para reproducción
                List<byte[]> cancionesBytes = playlist.obtenerCancionesParaReproduccion();
                
                if (cancionesBytes == null || cancionesBytes.isEmpty()) {
                    mostrarAlerta("Error", "No se pudieron cargar las canciones para reproducción", Alert.AlertType.ERROR);
                    return;
                }
                
                // Verificar que el índice sea válido
                if (indiceSelecionado >= cancionesBytes.size()) {
                    mostrarAlerta("Error", "Índice de canción fuera de rango", Alert.AlertType.ERROR);
                    return;
                }
                
                // Usar GestorPlaylist.setCanciones para actualizar el reproductor existente
                if (reproductorActivo != null) {
                    // Actualizar las canciones usando el método del GestorPlaylist
                    reproductorActivo.getPlaylist().setCanciones(cancionesBytes);
                    System.out.println("=== CANCIONES ACTUALIZADAS CON GestorPlaylist.setCanciones() ===");
                    System.out.println("Playlist: " + playlistSeleccionada.getTituloPlaylist());
                    System.out.println("Total canciones cargadas: " + cancionesBytes.size());
                    System.out.println("Índice reiniciado a: " + reproductorActivo.getPlaylist().getIndiceActual());
                } else {
                    // Si no existe reproductor, crear uno nuevo
                    reproductorActivo = BusinessLogic.ReproductorMP3.getInstancia(cancionesBytes);
                    System.out.println("=== NUEVO REPRODUCTOR CREADO PARA CANCIÓN ESPECÍFICA ===");
                }
                
                // Ahora establecer el índice de la canción seleccionada
                if (reproductorActivo.getPlaylist() != null) {
                    reproductorActivo.getPlaylist().setIndiceActual(indiceSelecionado);
                    System.out.println("Índice establecido en reproductor: " + indiceSelecionado);
                } else {
                    System.out.println("ERROR: getPlaylist() devolvió null");
                    mostrarAlerta("Error", "Error al configurar la playlist en el reproductor", Alert.AlertType.ERROR);
                    return;
                }
                
                // Obtener la duración real de la canción seleccionada
                duracionCancionActual = cancionDTO.getDuracion();
                System.out.println("Duración de la canción: " + duracionCancionActual + " segundos");
                
                // Actualizar el slider con la nueva duración
                actualizarSliderTiempo();
                
                // Actualizar información del reproductor ANTES de iniciar
                String nombreArtista = "Artista Desconocido";
                if (cancionDTO.getArtistas() != null && !cancionDTO.getArtistas().isEmpty()) {
                    nombreArtista = cancionDTO.getArtistas().get(0).getNombre();
                }
                
                actualizarReproductor(cancionDTO.getTitulo(), nombreArtista);
                
                // Inicializar tiempos en la interfaz
                if (lblTiempoActual != null) {
                    lblTiempoActual.setText("0:00");
                }
                if (lblTiempoTotal != null) {
                    lblTiempoTotal.setText(formatearTiempo(duracionCancionActual));
                }
                
                // Mostrar la barra de reproducción ANTES de iniciar
                mostrarBarraReproduccion();
                
                // Configurar la barra de progreso
                configurarBarraProgreso();
                
                // Iniciar reproducción
                reproductorActivo.reproducir();
                
                // Actualizar el botón de play/pause a PAUSE
                actualizarIconoPlayPause("⏸", "PAUSE");
                
                // Iniciar el timeline de progreso
                reiniciarTiempoReproduccion();
                if (timelineProgreso != null) {
                    timelineProgreso.play();
                }
                
                System.out.println("=== REPRODUCCIÓN INICIADA EXITOSAMENTE ===");
                System.out.println("Canción: " + cancionDTO.getTitulo());
                System.out.println("Artista: " + nombreArtista);
                System.out.println("Duración: " + formatearTiempo(duracionCancionActual));
                System.out.println("Estado del reproductor: " + reproductorActivo.getEstado().getClass().getSimpleName());
                
            } else {
                System.out.println("Error: Canción no válida o índice incorrecto");
                System.out.println("Índice: " + indiceSelecionado);
                System.out.println("Tipo de objeto: " + (cancion != null ? cancion.getClass().getSimpleName() : "null"));
                mostrarAlerta("Error", "No se pudo reproducir la canción seleccionada", Alert.AlertType.ERROR);
            }
            
        } catch (Exception e) {
            System.out.println("=== ERROR EN REPRODUCCIÓN DE CANCIÓN SELECCIONADA ===");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            
            // Limpiar estado en caso de error
            if (timelineProgreso != null) {
                timelineProgreso.stop();
            }
            if (pgbProgresoCancion != null) {
                pgbProgresoCancion.setProgress(0.0);
            }
            
            mostrarAlerta("Error", "Error al reproducir la canción: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // =============== MÉTODOS DEL REPRODUCTOR ===============

    @FXML
    private void handleAnterior() {
        try {
            if (reproductorActivo == null) {
                mostrarAlerta("Advertencia", "No hay ninguna playlist en reproducción.", Alert.AlertType.WARNING);
                return;
            }
            
            reproductorActivo.anterior();
            
            // Actualizar duración de la nueva canción
            actualizarDuracionCancionActual();
            
            // Reiniciar la barra de progreso para la nueva canción
            reiniciarTiempoReproduccion();
            pgbProgresoCancion.setProgress(0.0);
            
            // Mantener el estado del botón según el estado del reproductor
            if (reproductorActivo.getEstado() instanceof BusinessLogic.EstadoReproduciendo) {
                actualizarIconoPlayPause("⏸", "PAUSE");
            } else {
                actualizarIconoPlayPause("▶", "PLAY");
            }
            
            System.out.println("Cambiando a canción anterior");
            
        } catch (Exception e) {
            System.err.println("Error al cambiar a canción anterior: " + e.getMessage());
            mostrarAlerta("Error", "Error al cambiar a la canción anterior: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handlePlayPause() {
        try {
            if (reproductorActivo == null) {
                mostrarAlerta("Advertencia", "No hay ninguna playlist en reproducción. Presiona el botón ▶ en una playlist primero.", Alert.AlertType.WARNING);
                return;
            }
            
            // Debug: mostrar estado actual
            String estadoActual = reproductorActivo.getEstado().getClass().getSimpleName();
            System.out.println("Estado actual del reproductor: " + estadoActual);
            
            // Verificar el estado actual y alternar entre reproducir/pausar/reanudar
            if (reproductorActivo.getEstado() instanceof BusinessLogic.EstadoReproduciendo) {
                reproductorActivo.pausar();
                // Pausar la barra de progreso y guardar tiempo transcurrido
                if (timelineProgreso != null) {
                    timelineProgreso.pause();
                }
                pausado = true;
                
                // Cambiar ícono a PLAY cuando está pausado
                actualizarIconoPlayPause("▶", "PLAY");
                
                System.out.println("Música pausada");
                
            } else if (reproductorActivo.getEstado() instanceof BusinessLogic.EstadoPausado) {
                reproductorActivo.reanudar();
                // Reanudar la barra de progreso y ajustar tiempo de inicio
                if (timelineProgreso != null) {
                    // Ajustar el tiempo de inicio para compensar el tiempo pausado
                    long tiempoActual = System.currentTimeMillis();
                    double progresoActual = pgbProgresoCancion.getProgress();
                    double tiempoTranscurridoAntes = progresoActual * duracionCancionActual * 1000; // en milisegundos
                    tiempoInicioReproduccion = tiempoActual - (long)tiempoTranscurridoAntes;
                    
                    timelineProgreso.play();
                }
                pausado = false;
                
                // Cambiar ícono a PAUSE cuando está reproduciendo
                actualizarIconoPlayPause("⏸", "PAUSE");
                
                System.out.println("Música reanudada");
                
            } else {
                // Si está detenido, iniciar reproducción
                reproductorActivo.reproducir();
                // Iniciar la barra de progreso
                iniciarBarraProgreso();
                
                // Cambiar ícono a PAUSE cuando empieza a reproducir
                actualizarIconoPlayPause("⏸", "PAUSE");
                
                System.out.println("Música iniciada");
            }
            
            // Debug: mostrar nuevo estado
            String nuevoEstado = reproductorActivo.getEstado().getClass().getSimpleName();
            System.out.println("Nuevo estado del reproductor: " + nuevoEstado);
            
        } catch (Exception e) {
            System.err.println("Error en play/pause: " + e.getMessage());
            mostrarAlerta("Error", "Error al controlar la reproducción: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSiguiente() {
        try {
            if (reproductorActivo == null) {
                mostrarAlerta("Advertencia", "No hay ninguna playlist en reproducción.", Alert.AlertType.WARNING);
                return;
            }
            
            reproductorActivo.siguiente();
            
            // Actualizar duración de la nueva canción
            actualizarDuracionCancionActual();
            
            // Reiniciar la barra de progreso para la nueva canción
            reiniciarTiempoReproduccion();
            pgbProgresoCancion.setProgress(0.0);
            
            // Mantener el estado del botón según el estado del reproductor
            if (reproductorActivo.getEstado() instanceof BusinessLogic.EstadoReproduciendo) {
                actualizarIconoPlayPause("⏸", "PAUSE");
            } else {
                actualizarIconoPlayPause("▶", "PLAY");
            }
            
            System.out.println("Cambiando a siguiente canción");
            
        } catch (Exception e) {
            System.err.println("Error al cambiar a siguiente canción: " + e.getMessage());
            mostrarAlerta("Error", "Error al cambiar a la siguiente canción: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleVolumen() {
        try {
            VolumeController volumeController = VolumeController.getInstance();
            
            if (sliderVolumen != null) {
                if (!silenciado) {
                    // Silenciar: guardar volumen actual y poner a 0
                    volumenAnterior = sliderVolumen.getValue();
                    sliderVolumen.setValue(0.0);
                    silenciado = true;
                    
                    // Aplicar silencio al sistema si está disponible
                    if (volumeController.isAvailable()) {
                        volumeController.setVolume(0.0f);
                    }
                    
                    System.out.println("🔇 Audio silenciado (volumen anterior: " + Math.round(volumenAnterior * 100) + "%)");
                } else {
                    // Restaurar volumen anterior
                    if (volumenAnterior <= 0.0) {
                        volumenAnterior = 0.5; // Volumen por defecto si no había volumen anterior
                    }
                    sliderVolumen.setValue(volumenAnterior);
                    silenciado = false;
                    
                    // Restaurar volumen en el sistema si está disponible
                    if (volumeController.isAvailable()) {
                        volumeController.setVolume((float) volumenAnterior);
                    }
                    
                    System.out.println("🔊 Audio restaurado a " + Math.round(volumenAnterior * 100) + "%");
                }
                
                // Forzar actualización visual
                aplicarVolumen(sliderVolumen.getValue());
            } else {
                System.out.println("ERROR: sliderVolumen es null en handleVolumen");
            }
        } catch (Exception e) {
            System.out.println("Error en handleVolumen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleExpandir() {
        System.out.println("Expandir reproductor");
    }

    /**
     * Actualiza la duración de la canción actual cuando se cambia de canción
     */
    private void actualizarDuracionCancionActual() {
        try {
            if (playlistSeleccionada != null && reproductorActivo != null) {
                // Obtener el índice real de la canción actual del reproductor
                int indiceCancionActual = reproductorActivo.getPlaylist().getIndiceActual();
                
                List<Integer> cancionesIds = playlistSeleccionada.getCancionesIds();
                if (cancionesIds != null && indiceCancionActual < cancionesIds.size()) {
                    
                    CancionDAO cancionDAO = new CancionDAO();
                    CancionDTO cancionActual = cancionDAO.buscarPorId(cancionesIds.get(indiceCancionActual));
                    
                    if (cancionActual != null) {
                        double nuevaDuracion = cancionActual.getDuracion();
                        duracionCancionActual = nuevaDuracion;
                        
                        // Actualizar el slider con la nueva duración
                        actualizarSliderTiempo();
                        
                        // Actualizar el tiempo total en la interfaz
                        if (lblTiempoTotal != null) {
                            lblTiempoTotal.setText(formatearTiempo(duracionCancionActual));
                        }
                        
                        // Actualizar información del reproductor
                        String nombreArtista = "Artista Desconocido";
                        if (cancionActual.getArtistas() != null && !cancionActual.getArtistas().isEmpty()) {
                            nombreArtista = cancionActual.getArtistas().get(0).getNombre();
                        }
                        actualizarReproductor(cancionActual.getTitulo(), nombreArtista);
                        
                        System.out.println("=== CANCIÓN ACTUALIZADA ===");
                        System.out.println("Índice: " + indiceCancionActual);
                        System.out.println("Canción: " + cancionActual.getTitulo());
                        System.out.println("Duración: " + duracionCancionActual + " segundos (" + formatearTiempo(duracionCancionActual) + ")");
                        System.out.println("Artista: " + nombreArtista);
                    } else {
                        duracionCancionActual = 210.0; // Valor por defecto
                        System.out.println("No se pudo obtener la canción con ID: " + cancionesIds.get(indiceCancionActual));
                    }
                } else {
                    System.out.println("Índice fuera de rango: " + indiceCancionActual + " / " + (cancionesIds != null ? cancionesIds.size() : 0));
                }
            }
        } catch (Exception e) {
            duracionCancionActual = 210.0; // Valor por defecto en caso de error
            System.out.println("Error al actualizar duración de canción: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // =============== MÉTODOS DE LÓGICA DE NEGOCIO ===============

    /**
     * Muestra los detalles de una playlist seleccionada
     */
    private void mostrarDetallesPlaylist(PlaylistDTO playlist) {
        if (playlist == null) {
            mostrarCatalogoPrincipal();
            return;
        }

        // Verificar si la playlist tiene canciones
        if (playlist.getCancionesIds() == null || playlist.getCancionesIds().isEmpty()) {
            mostrarPlaylistVacia(playlist);
        } else {
            mostrarPlaylistConCanciones(playlist);
        }
    }

    /**
     * Carga las canciones en la tabla desde la base de datos
     */
    private void cargarCancionesEnTabla(PlaylistDTO playlist) {
        try {
            // Limpiar tabla actual
            listCancionesData.clear();

            if (playlist.getCancionesIds() != null && !playlist.getCancionesIds().isEmpty()) {
                // Cargar las canciones reales desde la base de datos
                CancionDAO cancionDAO = new CancionDAO();

                for (Integer idCancion : playlist.getCancionesIds()) {
                    try {
                        CancionDTO cancion = cancionDAO.buscarPorId(idCancion);
                        if (cancion != null) {
                            listCancionesData.add(cancion);
                            System.out.println("Canción cargada: " + cancion.getTitulo());
                        } else {
                            System.out.println("No se encontró la canción con ID: " + idCancion);
                        }
                    } catch (Exception e) {
                        System.out.println("Error al cargar canción con ID " + idCancion + ": " + e.getMessage());
                        // Agregar placeholder para canciones que no se pueden cargar
                        listCancionesData.add("Error al cargar canción (ID: " + idCancion + ")");
                    }
                }

                System.out.println("Total de canciones cargadas en la tabla: " + listCancionesData.size());
            } else {
                System.out.println("La playlist no tiene canciones o la lista de IDs es nula");
            }

        } catch (Exception e) {
            System.out.println("Error al cargar canciones en la tabla: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Filtra las playlists según el texto de búsqueda
     */
    private void filtrarPlaylists(String filtro) {
        if (filtro == null || filtro.trim().isEmpty()) {
            cargarPlaylists();
            return;
        }

        try {
            Playlist playlistLogic = new Playlist();
            List<PlaylistDTO> todasLasPlaylists = playlistLogic.buscarTodo();

            listPlaylistsData.clear();

            for (PlaylistDTO playlist : todasLasPlaylists) {
                if (playlist.getTituloPlaylist().toLowerCase().contains(filtro.toLowerCase())) {
                    listPlaylistsData.add(playlist);
                }
            }

            if (listPlaylistsData.isEmpty()) {
                Label noResultsLabel = new Label("No se encontraron playlists\ncon ese nombre");
                noResultsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #AFAFC7; -fx-text-alignment: center;");
                listPlaylists.setPlaceholder(noResultsLabel);
            } else {
                listPlaylists.setPlaceholder(null);
            }

        } catch (Exception e) {
            System.out.println("Error al filtrar playlists: " + e.getMessage());
        }
    }

    /**
     * Abre la pantalla de editar playlist
     */
    private void abrirEditarPlaylist(PlaylistDTO playlist) {
        try {
            Playlist playlistLogic = new Playlist();
            PlaylistDTO playlistCompleta = playlistLogic.buscarPorId(playlist.getIdPlaylist());

            if (playlistCompleta == null) {
                mostrarAlerta("Error", "No se pudo cargar la playlist", Alert.AlertType.ERROR);
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/Playlist/frameEditarPlaylist.fxml"));
            Parent root = loader.load();

            EditarPlaylistController controller = loader.getController();
            controller.setPlaylist(playlistCompleta);

            Stage stage = new Stage();
            stage.setTitle("Editar Playlist - " + playlistCompleta.getTituloPlaylist());
            stage.setScene(new Scene(root, 1200, 800));
            stage.setResizable(true);
            stage.setMinWidth(1000);
            stage.setMinHeight(700);
            stage.centerOnScreen();

            stage.setOnHidden(e -> cargarPlaylists());

            stage.show();
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al abrir editar playlist: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }


    /**
     * Método original de borrar playlist (puedes mantenerlo como respaldo o eliminarlo)
     * Ahora se usa abrirEliminarPlaylist() en su lugar
     */
    private void borrarPlaylistDirecto(PlaylistDTO playlist) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Estás seguro de que quieres eliminar esta playlist?");
        confirmacion.setContentText("Playlist: " + playlist.getTituloPlaylist() + "\n\nEsta acción no se puede deshacer.");

        ButtonType btnSi = new ButtonType("Sí, eliminar");
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmacion.getButtonTypes().setAll(btnSi, btnCancelar);

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == btnSi) {
                try {
                    Playlist playlistLogic = new Playlist();
                    boolean resultado = playlistLogic.eliminar(playlist.getIdPlaylist());

                    if (resultado) {
                        Alert exito = new Alert(Alert.AlertType.INFORMATION);
                        exito.setTitle("Playlist eliminada");
                        exito.setHeaderText(null);
                        exito.setContentText("La playlist '" + playlist.getTituloPlaylist() + "' ha sido eliminada exitosamente.");
                        exito.showAndWait();

                        cargarPlaylists();
                        mostrarCatalogoPrincipal();
                    } else {
                        mostrarAlerta("Error", "No se pudo eliminar la playlist.", Alert.AlertType.ERROR);
                    }
                } catch (Exception e) {
                    mostrarAlerta("Error", "Error al eliminar playlist: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    /**
     * Selecciona una canción para reproducir
     */
    private void seleccionarCancion(Object cancion) {
        if (cancion != null) {
            lblCancionActual.setText(cancion.toString());
            lblArtistaActual.setText("Artista de ejemplo");
            System.out.println("Canción seleccionada: " + cancion.toString());
        }
    }

    /**
     * Carga las playlists del usuario desde la base de datos
     */
    private void cargarPlaylists() {
        try {
            Playlist playlistLogic = new Playlist();
            List<PlaylistDTO> playlists = playlistLogic.buscarTodo();

            listPlaylistsData.clear();

            if (playlists.isEmpty()) {
                vboxCrearPrimera.setVisible(true);
                listPlaylists.setVisible(false);
            } else {
                vboxCrearPrimera.setVisible(false);
                listPlaylists.setVisible(true);

                for (PlaylistDTO playlist : playlists) {
                    listPlaylistsData.add(playlist);
                }
                listPlaylists.setPlaceholder(null);
            }

        } catch (Exception e) {
            vboxCrearPrimera.setVisible(false);
            listPlaylists.setVisible(true);
            listPlaylistsData.clear();

            Label errorLabel = new Label("Error al cargar playlists:\n" + e.getMessage());
            errorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #AFAFC7; -fx-text-alignment: center;");
            listPlaylists.setPlaceholder(errorLabel);
            System.out.println("Error al cargar playlists: " + e.getMessage());
        }
    }

    /**
     * Método para mostrar alertas
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Actualiza la información del reproductor en la barra inferior
     */
    private void actualizarReproductor(String nombreCancion, String artista) {
        if (lblCancionActual != null) {
            lblCancionActual.setText(nombreCancion);
        }
        if (lblArtistaActual != null) {
            lblArtistaActual.setText(artista);
        }
    }
    
    /**
     * Configura la barra de progreso para la reproducción
     */
    private void configurarBarraProgreso() {
        // Detener timeline anterior si existe
        if (timelineProgreso != null) {
            timelineProgreso.stop();
        }
        
        // Crear timeline para actualizar la barra de progreso con alta frecuencia
        // Actualizar cada 50ms para mayor fluidez (como YouTube)
        timelineProgreso = new Timeline(
            new KeyFrame(Duration.millis(50), e -> actualizarProgreso())
        );
        timelineProgreso.setCycleCount(Timeline.INDEFINITE);
        
        // Inicializar la barra de progreso
        pgbProgresoCancion.setProgress(0.0);
    }
    
    /**
     * Actualiza el progreso de la barra durante la reproducción
     */
    private void actualizarProgreso() {
        try {
            if (reproductorActivo != null && reproductorActivo.getEstado() instanceof BusinessLogic.EstadoReproduciendo) {
                // Calcular progreso basado en tiempo real transcurrido
                long tiempoActual = System.currentTimeMillis();
                long tiempoTranscurrido = tiempoActual - tiempoInicioReproduccion;
                
                // Convertir a segundos
                double segundosTranscurridos = tiempoTranscurrido / 1000.0;
                
                // Validar que tenemos una duración válida
                if (duracionCancionActual <= 0) {
                    System.out.println("Duración inválida, deteniendo progreso");
                    return;
                }
                
                // Calcular progreso como porcentaje
                double progreso = segundosTranscurridos / duracionCancionActual;
                
                // Asegurar que no exceda 1.0
                progreso = Math.min(1.0, Math.max(0.0, progreso));
                
                // Actualizar la barra de progreso
                if (pgbProgresoCancion != null) {
                    pgbProgresoCancion.setProgress(progreso);
                }
                
                // Actualizar el slider de tiempo (solo si el usuario no está arrastrando)
                if (sliderTiempo != null && !arrastrando) {
                    double porcentajeSlider = progreso * 100.0;
                    sliderTiempo.setValue(porcentajeSlider);
                }
                
                // Actualizar el tiempo actual mostrado
                actualizarTiempoActual(segundosTranscurridos);
                
                // Si la canción llegó al final, simular cambio a siguiente
                if (progreso >= 1.0) {
                    // Esperar un poco antes de cambiar para evitar loops infinitos
                    if (tiempoTranscurrido > (duracionCancionActual * 1000) + 500) { // 500ms de buffer
                        System.out.println("Canción terminó, actualizando a la siguiente...");
                        
                        try {
                            // Actualizar duración de la nueva canción
                            actualizarDuracionCancionActual();
                            
                            // Reiniciar para la siguiente canción
                            reiniciarTiempoReproduccion();
                            if (pgbProgresoCancion != null) {
                                pgbProgresoCancion.setProgress(0.0);
                            }
                        } catch (Exception e) {
                            System.out.println("Error al cambiar a siguiente canción automáticamente: " + e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error en actualizarProgreso: " + e.getMessage());
            // No detener el timeline por un error menor, solo registrar
        }
    }
    
    /**
     * Reinicia el tiempo de inicio de reproducción para una nueva canción
     */
    private void reiniciarTiempoReproduccion() {
        tiempoInicioReproduccion = System.currentTimeMillis();
        pausado = false;
        // Actualizar el tiempo total al iniciar una nueva canción
        actualizarTiempoTotal();
    }
    
    /**
     * Formatea segundos a formato MM:SS
     */
    private String formatearTiempo(double segundos) {
        int minutos = (int) segundos / 60;
        int segs = (int) segundos % 60;
        return String.format("%d:%02d", minutos, segs);
    }
    
    /**
     * Actualiza el tiempo total mostrado en la interfaz
     */
    private void actualizarTiempoTotal() {
        if (lblTiempoTotal != null) {
            String tiempoFormateado = formatearTiempo(duracionCancionActual);
            lblTiempoTotal.setText(tiempoFormateado);
            System.out.println("Tiempo total actualizado a: " + tiempoFormateado);
        } else {
            System.out.println("ERROR: lblTiempoTotal es null");
        }
    }
    
    /**
     * Actualiza el tiempo actual transcurrido en la interfaz
     */
    private void actualizarTiempoActual(double segundosTranscurridos) {
        if (lblTiempoActual != null) {
            String tiempoFormateado = formatearTiempo(segundosTranscurridos);
            lblTiempoActual.setText(tiempoFormateado);
            // Debug - mostrar cada 5 segundos
            if (((int)segundosTranscurridos) % 5 == 0) {
                System.out.println("Tiempo actual: " + tiempoFormateado);
            }
        } else {
            System.out.println("ERROR: lblTiempoActual es null");
        }
    }
    
    /**
     * Configura el control de volumen
     */
    private void configurarControlVolumen() {
        System.out.println("Configurando control de volumen...");
        
        if (sliderVolumen != null) {
            System.out.println("Slider de volumen encontrado - configurando...");
            
            // Configurar rango del slider (0.0 a 1.0)
            sliderVolumen.setMin(0.0);
            sliderVolumen.setMax(1.0);
            sliderVolumen.setValue(0.7); // Volumen inicial al 70%
            volumenAnterior = 0.7; // Guardar volumen inicial
            
            // Aplicar volumen inicial
            aplicarVolumen(0.7);
            
            // Agregar listener para cambios en el volumen
            sliderVolumen.valueProperty().addListener((observable, oldValue, newValue) -> {
                double volumen = newValue.doubleValue();
                aplicarVolumen(volumen);
                // Actualizar volumen anterior para el botón de mute
                if (volumen > 0.0) {
                    volumenAnterior = volumen;
                }
                System.out.println("Volumen cambiado a: " + Math.round(volumen * 100) + "%");
            });
            
            // Configurar estilo visual del slider
            sliderVolumen.setStyle(
                "-fx-control-inner-background: #070F2B;" +
                "-fx-accent: #FF0000;" +
                "-fx-track-color: #AFAFC7;"
            );
            
            System.out.println("Configuración de volumen completada");
        } else {
            System.out.println("ERROR: sliderVolumen es null");
        }
    }
    
    /**
     * Aplica el volumen especificado al reproductor
     */
    private void aplicarVolumen(double volumen) {
        try {
            // Actualizar el estado visual del botón de volumen
            if (btnVolumen != null) {
                if (volumen == 0.0) {
                    btnVolumen.setText("🔇"); // Icono mudo
                    btnVolumen.setStyle("-fx-background-color: transparent; -fx-text-fill: #FF6666;"); // Rojo para mudo
                    silenciado = true;
                } else if (volumen <= 0.3) {
                    btnVolumen.setText("�"); // Volumen bajo
                    btnVolumen.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
                    silenciado = false;
                } else {
                    btnVolumen.setText("�"); // Volumen alto
                    btnVolumen.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
                    silenciado = false;
                }
            }
            
            // Intentar aplicar volumen al sistema usando el controlador de volumen
            VolumeController volumeController = VolumeController.getInstance();
            if (volumeController.isAvailable()) {
                volumeController.setVolume((float) volumen);
                System.out.println("✅ Volumen aplicado al sistema: " + Math.round(volumen * 100) + "%");
            } else {
                System.out.println("⚠️ Control de volumen del sistema no disponible - usando simulación");
                System.out.println("Volumen aplicado (simulado): " + Math.round(volumen * 100) + "%");
            }
            
            // Debug del volumen aplicado
            if (volumen == 0.0) {
                System.out.println("🔇 Audio silenciado");
            } else if (volumen <= 0.3) {
                System.out.println("🔉 Volumen bajo: " + Math.round(volumen * 100) + "%");
            } else if (volumen <= 0.7) {
                System.out.println("🔊 Volumen medio: " + Math.round(volumen * 100) + "%");
            } else {
                System.out.println("🔊 Volumen alto: " + Math.round(volumen * 100) + "%");
            }
            
            // Guardar el volumen actual para persistencia
            if (volumen > 0.0) {
                volumenAnterior = volumen;
            }
            
        } catch (Exception e) {
            System.out.println("Error al aplicar volumen: " + e.getMessage());
        }
    }
    
    /**
     * Configura el slider de tiempo para navegación interactiva (como YouTube)
     */
    private void configurarSliderTiempo() {
        System.out.println("Configurando slider de tiempo interactivo...");
        
        if (sliderTiempo != null) {
            // Configurar rango inicial del slider (se actualizará con cada canción)
            sliderTiempo.setMin(0.0);
            sliderTiempo.setMax(100.0); // Se actualizará con la duración real
            sliderTiempo.setValue(0.0);
            
            // Eventos para detectar cuando el usuario empieza a arrastrar o hace clic
            sliderTiempo.setOnMousePressed(event -> {
                arrastrando = true;
                System.out.println("🎯 Usuario interactuó con slider");
                
                // Si es un clic simple (no un arrastre), hacer seek inmediatamente
                if (reproductorActivo != null) {
                    double nuevaPosicion = sliderTiempo.getValue();
                    double tiempoObjetivo = (nuevaPosicion / 100.0) * duracionCancionActual;
                    
                    System.out.println("🎯 DEBUG CLICK SEEK:");
                    System.out.println("  Posición slider: " + nuevaPosicion + "%");
                    System.out.println("  Duración total: " + duracionCancionActual + "s");
                    System.out.println("  Tiempo objetivo: " + String.format("%.1f", tiempoObjetivo) + "s");
                    
                    try {
                        // PASO 1: Detener timeline para evitar interferencias
                        if (timelineProgreso != null) {
                            timelineProgreso.pause();
                        }
                        
                        // PASO 2: Detener reproductor
                        reproductorActivo.detener();
                        
                        // PASO 3: Actualizar tiempo de inicio simulando el seek
                        long tiempoActual = System.currentTimeMillis();
                        tiempoInicioReproduccion = tiempoActual - (long)(tiempoObjetivo * 1000);
                        
                        // PASO 4: Actualizar visualización inmediatamente
                        if (pgbProgresoCancion != null) {
                            pgbProgresoCancion.setProgress(nuevaPosicion / 100.0);
                        }
                        actualizarTiempoActual(tiempoObjetivo);
                        
                        // PASO 5: Reiniciar reproducción
                        reproductorActivo.reproducir();
                        
                        // PASO 6: Reanudar timeline después de un breve delay
                        Platform.runLater(() -> {
                            if (timelineProgreso != null) {
                                timelineProgreso.play();
                            }
                        });
                        
                        System.out.println("✅ Seek básico completado a: " + String.format("%.1f", tiempoObjetivo) + "s");
                        
                    } catch (Exception e) {
                        System.err.println("❌ Error al hacer seek en clic: " + e.getMessage());
                    }
                }
            });
            
            // Cuando el usuario suelta el slider
            sliderTiempo.setOnMouseReleased(event -> {
                if (arrastrando && reproductorActivo != null) {
                    double nuevaPosicion = sliderTiempo.getValue();
                    double tiempoObjetivo = (nuevaPosicion / 100.0) * duracionCancionActual;
                    
                    System.out.println("🎯 DEBUG DRAG SEEK:");
                    System.out.println("  Posición slider: " + nuevaPosicion + "%");
                    System.out.println("  Duración total: " + duracionCancionActual + "s");
                    System.out.println("  Tiempo objetivo: " + String.format("%.1f", tiempoObjetivo) + "s");
                    
                    try {
                        // PASO 1: Detener timeline para evitar interferencias
                        if (timelineProgreso != null) {
                            timelineProgreso.pause();
                        }
                        
                        // PASO 2: Detener reproductor
                        reproductorActivo.detener();
                        
                        // PASO 3: Actualizar el tiempo de inicio para simular el seek
                        long tiempoActual = System.currentTimeMillis();
                        tiempoInicioReproduccion = tiempoActual - (long)(tiempoObjetivo * 1000);
                        
                        // PASO 4: Actualizar visualización inmediatamente
                        if (pgbProgresoCancion != null) {
                            pgbProgresoCancion.setProgress(nuevaPosicion / 100.0);
                        }
                        actualizarTiempoActual(tiempoObjetivo);
                        
                        // PASO 5: Reiniciar reproducción
                        reproductorActivo.reproducir();
                        
                        // PASO 6: Reanudar timeline después de un breve delay
                        Platform.runLater(() -> {
                            if (timelineProgreso != null) {
                                timelineProgreso.play();
                            }
                        });
                        
                        System.out.println("✅ Drag seek completado a: " + String.format("%.1f", tiempoObjetivo) + "s");
                        
                    } catch (Exception e) {
                        System.err.println("❌ Error al hacer seek: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                arrastrando = false;
            });
            
            // Listener para cambios en el valor (durante el arrastre)
            sliderTiempo.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (arrastrando) {
                    // Durante el arrastre, actualizar solo la visualización del tiempo
                    double tiempoObjetivo = (newValue.doubleValue() / 100.0) * duracionCancionActual;
                    actualizarTiempoActual(tiempoObjetivo);
                    
                    // También actualizar la barra de progreso para sincronización visual
                    if (pgbProgresoCancion != null) {
                        pgbProgresoCancion.setProgress(newValue.doubleValue() / 100.0);
                    }
                }
            });
            
            // Estilo del slider (similar a YouTube)
            sliderTiempo.setStyle(
                "-fx-control-inner-background: #AFAFC7;" +
                "-fx-accent: #FF0000;" +
                "-fx-track-color: #4A4A4A;" +
                "-fx-thumb-color: #FF0000;"
            );
            
            System.out.println("✅ Slider de tiempo configurado correctamente");
        } else {
            System.out.println("❌ ERROR: sliderTiempo es null");
        }
    }
    
    /**
     * Actualiza el slider de tiempo cuando cambia la duración de la canción
     */
    private void actualizarSliderTiempo() {
        if (sliderTiempo != null) {
            // Reiniciar el slider para la nueva canción
            sliderTiempo.setValue(0.0);
            sliderTiempo.setMax(100.0); // Siempre usamos porcentajes (0-100)
            
            System.out.println("🎵 Slider de tiempo actualizado para nueva canción (duración: " + 
                             formatearTiempo(duracionCancionActual) + ")");
        }
    }
    
    /**
     * Inicia la animación de la barra de progreso
     */
    private void iniciarBarraProgreso() {
        // Mostrar la barra de reproducción
        mostrarBarraReproduccion();
        
        // Inicializar el tiempo de inicio
        reiniciarTiempoReproduccion();
        
        // Inicializar los labels de tiempo
        actualizarTiempoTotal();
        actualizarTiempoActual(0.0);
        
        // Configurar y iniciar la animación
        configurarBarraProgreso();
        if (timelineProgreso != null) {
            timelineProgreso.play();
            System.out.println("Barra de progreso iniciada - Timeline activado");
        }
        
        // Debug de estado inicial
        System.out.println("=== INICIO BARRA PROGRESO ===");
        System.out.println("Duración canción: " + duracionCancionActual + " segundos");
        System.out.println("lblTiempoActual: " + (lblTiempoActual != null ? lblTiempoActual.getText() : "NULL"));
        System.out.println("lblTiempoTotal: " + (lblTiempoTotal != null ? lblTiempoTotal.getText() : "NULL"));
    }
    
    /**
     * Detiene la animación de la barra de progreso
     */
    private void detenerBarraProgreso() {
        if (timelineProgreso != null) {
            timelineProgreso.stop();
        }
        pgbProgresoCancion.setProgress(0.0);
        
        // Ocultar la barra de reproducción cuando se detiene
        ocultarBarraReproduccion();
    }
    
    /**
     * Muestra la barra de reproducción
     */
    private void mostrarBarraReproduccion() {
        if (anchorBarraReproduccion != null) {
            anchorBarraReproduccion.setVisible(true);
            anchorBarraReproduccion.setManaged(true);
            barraReproduccionVisible = true;
            
            // Asegurar que todos los controles estén visibles
            if (lblTiempoActual != null) {
                lblTiempoActual.setVisible(true);
                lblTiempoActual.setText("0:00");
            }
            if (lblTiempoTotal != null) {
                lblTiempoTotal.setVisible(true);
                lblTiempoTotal.setText(formatearTiempo(duracionCancionActual));
            }
            if (sliderVolumen != null) {
                sliderVolumen.setVisible(true);
                sliderVolumen.setDisable(false);
            }
            if (btnVolumen != null) {
                btnVolumen.setVisible(true);
            }
            
            System.out.println("Barra de reproducción mostrada con todos los controles");
            
            // Configurar la barra de progreso para ser interactiva
            configurarBarraInteractiva();
        }
    }
    
    /**
     * Oculta la barra de reproducción
     */
    private void ocultarBarraReproduccion() {
        if (anchorBarraReproduccion != null) {
            anchorBarraReproduccion.setVisible(false);
            anchorBarraReproduccion.setManaged(false);
            barraReproduccionVisible = false;
            
            // Detener la barra de progreso
            detenerBarraProgreso();
        }
    }
    
    /**
     * Configura la barra de progreso para ser interactiva
     */
    private void configurarBarraInteractiva() {
        if (pgbProgresoCancion != null) {
            // Permitir hacer clic en la barra para cambiar la posición
            pgbProgresoCancion.setOnMouseClicked(event -> {
                if (reproductorActivo != null) {
                    // Calcular la nueva posición basada en donde se hizo clic
                    double width = pgbProgresoCancion.getWidth();
                    double clickX = event.getX();
                    double newProgress = clickX / width;
                    
                    // Asegurar que el progreso esté entre 0 y 1
                    newProgress = Math.max(0.0, Math.min(1.0, newProgress));
                    
                    // Calcular el nuevo tiempo objetivo
                    double tiempoObjetivo = newProgress * duracionCancionActual; // en segundos
                    
                    // Actualizar el tiempo de inicio para reflejar la nueva posición
                    long tiempoActual = System.currentTimeMillis();
                    tiempoInicioReproduccion = tiempoActual - (long)(tiempoObjetivo * 1000);
                    
                    // Actualizar la barra de progreso inmediatamente
                    pgbProgresoCancion.setProgress(newProgress);
                    
                    System.out.println("Posición cambiada a: " + (newProgress * 100) + "% (" + 
                                     String.format("%.1f", tiempoObjetivo) + " segundos)");
                }
            });
            
            // Cambiar cursor cuando se pase sobre la barra
            pgbProgresoCancion.setOnMouseEntered(event -> {
                pgbProgresoCancion.getScene().setCursor(Cursor.HAND);
            });
            
            pgbProgresoCancion.setOnMouseExited(event -> {
                pgbProgresoCancion.getScene().setCursor(Cursor.DEFAULT);
            });
        }
    }
    
    /**
     * Actualiza el ícono del botón play/pause con el estilo apropiado
     */
    private void actualizarIconoPlayPause(String icono, String descripcion) {
        if (btnPlayPause != null) {
            btnPlayPause.setText(icono);
            btnPlayPause.setStyle(
                "-fx-font-size: 20px; " +
                "-fx-text-fill: white; " +
                "-fx-background-color: transparent; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 5px;"
            );
            // Limpiar cualquier elemento adicional que pueda mostrar texto
            btnPlayPause.setTooltip(null);
            btnPlayPause.setGraphic(null);
            System.out.println("🎵 Botón actualizado: " + descripcion + " (" + icono + ")");
        }
    }
}