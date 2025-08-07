package UserInterface.CustomerControl.Playlist;
import BusinessLogic.ReproductorMP3;
import BusinessLogic.EstadoPausado;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Duration;
import BusinessLogic.Playlist;
import DataAccessComponent.DTO.PlaylistDTO;
import DataAccessComponent.DTO.CancionDTO;
import DataAccessComponent.DAO.CancionDAO;
import DataAccessComponent.DAO.PlaylistDAO;
import DataAccessComponent.DAO.UsuarioDAO;
import BusinessLogic.Sesion;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
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
    @FXML private Button btnRecomendaciones;
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
    @FXML private Slider pgbProgresoCancion; // Corregido el nombre
    @FXML private Label lblTiempoTotal;
    @FXML private Label lblCancionActual;
    @FXML private Label lblArtistaActual;
    @FXML private ImageView imgCancionActual;
    @FXML private Button btnVolumen;
    @FXML private Slider sliderVolumen;
    @FXML private Button btnExpandir;
    @FXML private AnchorPane anchorBarraReproduccion; // Panel completo del reproductor (corregido el nombre)

    private ObservableList<PlaylistDTO> listPlaylistsData;
    private ObservableList<Object> listCancionesData;
    private PlaylistDTO playlistSeleccionada;
    
    // Variables para el reproductor de playlist
    private ReproductorMP3 reproductor;
    private Timeline timeline;
    private PlaylistDTO playlistReproduciendose;
    private List<CancionDTO> cancionesReproduciendose;
    private boolean usuarioArrastrando = false;
    private double duracionRealCancion = 0;
    private double tiempoActualSegundos = 0;
    private Image imagenPlay;
    private Image imagenPause;
    private Image imagenPortadaGenerica;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializar listas
        listPlaylistsData = FXCollections.observableArrayList();
        listCancionesData = FXCollections.observableArrayList();

        listPlaylists.setItems(listPlaylistsData);
        tableCanciones.setItems(listCancionesData);

        // Inicializar reproductor y sus componentes
        inicializarReproductor();

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

        // Configurar doble clic en tabla para reproducir canción específica
        tableCanciones.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !tableCanciones.getSelectionModel().isEmpty()) {
                Object cancionSeleccionada = tableCanciones.getSelectionModel().getSelectedItem();
                reproducirCancionSeleccionada(cancionSeleccionada);
            }
        });

        // Configurar búsqueda de playlists
        txtBuscarPlaylist.textProperty().addListener((observable, oldValue, newValue) ->
                filtrarPlaylists(newValue));

        // Cargar playlists al inicializar
        cargarPlaylists();

        // Crear playlists recomendadas del momento
        crearPlaylistsRecomendadas();

        // Mostrar catálogo principal al inicio
        mostrarCatalogoPrincipal();
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
        // Configurar columna de número
        colNumero.setCellValueFactory(cellData -> {
            int index = tableCanciones.getItems().indexOf(cellData.getValue()) + 1;
            return new javafx.beans.property.SimpleIntegerProperty(index).asObject();
        });

        // Configurar columna de título
        colTitulo.setCellValueFactory(cellData -> {
            Object cancion = cellData.getValue();
            if (cancion instanceof CancionDTO) {
                return new javafx.beans.property.SimpleStringProperty(((CancionDTO) cancion).getTitulo());
            }
            return new javafx.beans.property.SimpleStringProperty(cancion.toString());
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

        // Configurar columna de fecha de agregación
        colFechaAgregacion.setCellValueFactory(cellData -> {
            // Por ahora devolver fecha actual, luego se puede personalizar
            return new javafx.beans.property.SimpleStringProperty(
                java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            );
        });

        // Configurar columna de año
        colAnio.setCellValueFactory(cellData -> {
            Object cancion = cellData.getValue();
            if (cancion instanceof CancionDTO) {
                return new javafx.beans.property.SimpleIntegerProperty(((CancionDTO) cancion).getAnio()).asObject();
            }
            return new javafx.beans.property.SimpleIntegerProperty(2024).asObject();
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

        // Cargar imagen de portada
        try {
            if (playlist.getImagenPortada() != null && playlist.getImagenPortada().length > 0) {
                InputStream imageStream = new ByteArrayInputStream(playlist.getImagenPortada());
                imgPortadaPlaylistCanciones.setImage(new Image(imageStream));
            } else {
                imgPortadaPlaylistCanciones.setImage(
                        new Image("/UserInterface/Resources/img/CatalogoPlaylist/simbolo-aplicacion.png")
                );
            }
        } catch (Exception e) {
            imgPortadaPlaylistCanciones.setImage(
                    new Image("/UserInterface/Resources/img/CatalogoPlaylist/simbolo-aplicacion.png")
            );
            e.printStackTrace(); // Ayuda a depurar si falla la carga
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
        try {
            // Cerrar sesión actual
            Sesion sesion = Sesion.getSesion();
            sesion.cerrarSesion(); // Limpia la sesión
            
            // Abrir ventana de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/AdminUserControl/login.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.setTitle("InkHarmony - Login");
            loginStage.setMinWidth(1280);
            loginStage.setMinHeight(680);
            loginStage.show();
            
            // Cerrar ventana actual
            Stage currentStage = (Stage) btnLogo.getScene().getWindow();
            currentStage.close();
            
        } catch (Exception e) {
            System.out.println("Error al cerrar sesión: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegresar() {
        // Regresar a la vista principal del catálogo en lugar de cerrar
        mostrarCatalogoPrincipal();
    }
@FXML
private void handleRecomendaciones() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/Recomendaciones/recomendaciones.fxml"));
        Parent root = loader.load();

        // Obtener el controlador de recomendaciones
        UserInterface.CustomerControl.Recomendaciones.RecomendacionesController controller = loader.getController();

        // Si hay una canción activa, pásala
        if (cancionesReproduciendose != null && !cancionesReproduciendose.isEmpty() && reproductor != null) {
            int indiceActual = reproductor.getPlaylist() != null ? reproductor.getPlaylist().getIndiceActual() : 0;
            //pasar el id de la canción no de la playlist
            controller.setCanciones(cancionesReproduciendose);
            controller.setReproduccionActual(cancionesReproduciendose, indiceActual);
        }

        Stage loginStage = new Stage();
        loginStage.setScene(new Scene(root));
        loginStage.setTitle("Recomendaciones - InkHarmony");
        loginStage.setMinWidth(1280);
        loginStage.setMinHeight(680);
        loginStage.show();

        // Cerrar ventana actual
        Stage currentStage = (Stage) btnLogo.getScene().getWindow();
        currentStage.close();

    } catch (Exception e) {
        System.out.println("Error al abrir recomendaciones: " + e.getMessage());
        e.printStackTrace();
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

    /**
     * Reproduce una canción específica seleccionada de la tabla
     */
    private void reproducirCancionSeleccionada(Object cancionSeleccionada) {
        if (cancionSeleccionada instanceof CancionDTO && playlistSeleccionada != null) {
            try {
                CancionDTO cancion = (CancionDTO) cancionSeleccionada;
                
                // Cargar todas las canciones de la playlist
                PlaylistDAO playlistDAO = new PlaylistDAO();
                List<CancionDTO> todasLasCanciones = playlistDAO.obtenerCancionesCompletasDePlaylist(playlistSeleccionada.getIdPlaylist());
                
                if (todasLasCanciones != null && !todasLasCanciones.isEmpty()) {
                    // Encontrar el índice de la canción seleccionada
                    int indiceCancion = -1;
                    for (int i = 0; i < todasLasCanciones.size(); i++) {
                        if (todasLasCanciones.get(i).getIdCancion() == cancion.getIdCancion()) {
                            indiceCancion = i;
                            break;
                        }
                    }
                    
                    if (indiceCancion >= 0) {
                        iniciarReproduccionDesdeCancion(playlistSeleccionada, todasLasCanciones, indiceCancion);
                    } else {
                        mostrarAlerta("Error", "No se encontró la canción en la playlist", Alert.AlertType.ERROR);
                    }
                } else {
                    mostrarAlerta("Error", "No se pudieron cargar las canciones de la playlist", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                System.err.println("Error al reproducir canción específica: " + e.getMessage());
                mostrarAlerta("Error", "Error al reproducir la canción: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Formatea tiempo en segundos a formato mm:ss
     */
    private String formatearTiempo(double segundos) {
        if (Double.isNaN(segundos)) return "00:00";
        int minutos = (int) (segundos / 60);
        int segundosRestantes = (int) (segundos % 60);
        return String.format("%02d:%02d", minutos, segundosRestantes);
    }

    @FXML
    private void handlePlayAll() {
        System.out.println("=== DEBUG handlePlayAll ===");
        System.out.println("playlistSeleccionada: " + playlistSeleccionada);
        
        if (playlistSeleccionada != null) {
            System.out.println("Playlist seleccionada: " + playlistSeleccionada.getTituloPlaylist());
            System.out.println("ID de playlist: " + playlistSeleccionada.getIdPlaylist());
            
            try {
                // Cargar las canciones de la playlist desde la base de datos
                PlaylistDAO playlistDAO = new PlaylistDAO();
                List<CancionDTO> canciones = playlistDAO.obtenerCancionesCompletasDePlaylist(playlistSeleccionada.getIdPlaylist());
                
                System.out.println("Canciones cargadas: " + (canciones != null ? canciones.size() : "null"));
                
                if (canciones != null && !canciones.isEmpty()) {
                    iniciarReproduccionPlaylist(playlistSeleccionada, canciones);
                } else {
                    mostrarAlerta("Información", "La playlist no tiene canciones para reproducir", Alert.AlertType.INFORMATION);
                }
            } catch (Exception e) {
                System.err.println("Error al reproducir playlist: " + e.getMessage());
                e.printStackTrace();
                mostrarAlerta("Error", "Error al iniciar la reproducción: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            System.out.println("playlistSeleccionada es NULL");
            mostrarAlerta("Advertencia", "Por favor, selecciona una playlist para reproducir", Alert.AlertType.WARNING);
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
        seleccionarCancion(cancion);
    }

    // =============== MÉTODOS DEL REPRODUCTOR ===============

    /**
     * Inicializa el reproductor de playlist
     */
    private void inicializarReproductor() {
        try {
            // Cargar imágenes
            imagenPlay = new Image(getClass().getResourceAsStream("/UserInterface/Resources/img/boton-de-play.png"));
            imagenPause = new Image(getClass().getResourceAsStream("/UserInterface/Resources/img/boton-de-pausa.png"));
            imagenPortadaGenerica = new Image(getClass().getResourceAsStream("/UserInterface/Resources/img/portada-generica.jpg"));
        } catch (Exception e) {
            System.err.println("Error cargando imágenes del reproductor: " + e.getMessage());
        }

        // Inicializar reproductor como singleton
        reproductor = ReproductorMP3.getInstancia(new ArrayList<>());

        // Configurar Timeline para actualización de progreso
        timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> actualizarProgressBarPlaylist()));
        timeline.setCycleCount(Timeline.INDEFINITE);

        // Configurar slider de tiempo (progreso)
        if (pgbProgresoCancion != null) {
            pgbProgresoCancion.setOnMousePressed(e -> usuarioArrastrando = true);
            pgbProgresoCancion.setOnMouseReleased(e -> {
                usuarioArrastrando = false;
                saltarAProgreso(pgbProgresoCancion.getValue());
            });
        }

        // Configurar slider de volumen
        if (sliderVolumen != null) {
            sliderVolumen.setValue(0.5);
            sliderVolumen.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (reproductor != null && reproductor.getMediaPlayer() != null) {
                    reproductor.getMediaPlayer().setVolume(newVal.doubleValue());
                }
            });
        }

        // Configurar callback para cambio de canción
        if (reproductor != null) {
            reproductor.setOnSongChange(() -> {
                Platform.runLater(() -> {
                    actualizarInformacionCancionReproductor();
                    reiniciarProgresoYTiempo();
                    cambiarIconoPlayPause(false);
                    timeline.play();
                });
            });
        }

        // Ocultar el panel reproductor inicialmente
        ocultarReproductor();
    }

    /**
     * Inicia la reproducción de una playlist completa
     */
    private void iniciarReproduccionPlaylist(PlaylistDTO playlist, List<CancionDTO> canciones) {
        if (playlist == null || canciones == null || canciones.isEmpty()) {
            System.out.println("No se puede reproducir: playlist o canciones nulas/vacías");
            return;
        }

        this.playlistReproduciendose = playlist;
        this.cancionesReproduciendose = new ArrayList<>(canciones);

        // Convertir canciones a byte arrays
        List<byte[]> archivosMp3 = canciones.stream()
                .map(CancionDTO::getArchivoMP3)
                .filter(archivo -> archivo != null)
                .collect(Collectors.toList());

        if (archivosMp3.isEmpty()) {
            mostrarAlerta("Error", "No hay archivos MP3 válidos en la playlist", Alert.AlertType.ERROR);
            return;
        }

        // Cambiar playlist en el reproductor
        reproductor.cambiarPlaylist(archivosMp3);
        reproductor.reproducir();

        // Mostrar reproductor y actualizar información
        mostrarReproductor();
        actualizarInformacionCancionReproductor();
        timeline.play();
        cambiarIconoPlayPause(false);

        System.out.println("Iniciando reproducción de playlist: " + playlist.getTituloPlaylist());
    }

    /**
     * Inicia reproducción desde una canción específica
     */
    private void iniciarReproduccionDesdeCancion(PlaylistDTO playlist, List<CancionDTO> canciones, int indiceCancion) {
        iniciarReproduccionPlaylist(playlist, canciones);
        
        if (indiceCancion >= 0 && indiceCancion < canciones.size()) {
            reproductor.getPlaylist().setIndiceActual(indiceCancion);
            reproductor.reproducir();
            actualizarInformacionCancionReproductor();
        }
    }

    /**
     * Actualiza la barra de progreso del reproductor
     */
    private void actualizarProgressBarPlaylist() {
        if (reproductor != null && reproductor.estaReproduciendo()) {
            tiempoActualSegundos = reproductor.getTiempoActual();
            if (duracionRealCancion > 0 && !usuarioArrastrando && pgbProgresoCancion != null) {
                double progreso = Math.max(0.0, Math.min(1.0, tiempoActualSegundos / duracionRealCancion));
                pgbProgresoCancion.setValue(progreso);
            }
            actualizarLabelsTime(tiempoActualSegundos, duracionRealCancion);
        } else if (reproductor != null && reproductor.estaPausado()) {
            actualizarLabelsTime(tiempoActualSegundos, duracionRealCancion);
        }
    }

    /**
     * Salta a un punto específico de la canción
     */
    private void saltarAProgreso(double progreso) {
        if (duracionRealCancion > 0 && reproductor != null && reproductor.getMediaPlayer() != null) {
            double segundos = progreso * duracionRealCancion;
            reproductor.getMediaPlayer().seek(Duration.seconds(segundos));
            tiempoActualSegundos = segundos;
            actualizarLabelsTime(tiempoActualSegundos, duracionRealCancion);
        }
    }

    /**
     * Actualiza los labels de tiempo
     */
    private void actualizarLabelsTime(double segundosActuales, double segundosTotales) {
        if (lblTiempoActual != null) {
            lblTiempoActual.setText(formatearTiempo(segundosActuales));
        }
        if (lblTiempoTotal != null) {
            lblTiempoTotal.setText(formatearTiempo(segundosTotales > 0 ? segundosTotales : 0));
        }
    }

    /**
     * Actualiza la información de la canción en el reproductor
     */
    public void actualizarInformacionCancionReproductor() {
        if (reproductor == null || cancionesReproduciendose == null || cancionesReproduciendose.isEmpty()) return;

        int indiceActual = reproductor.getPlaylist().getIndiceActual();
        if (indiceActual >= 0 && indiceActual < cancionesReproduciendose.size()) {
            CancionDTO cancionActual = cancionesReproduciendose.get(indiceActual);
            mostrarInformacionCancionReproductor(cancionActual);
        }
    }

    /**
     * Muestra información de la canción en el reproductor
     */
    private void mostrarInformacionCancionReproductor(CancionDTO cancion) {
        if (cancion == null) return;

        // Actualizar nombre de canción
        if (lblCancionActual != null) {
            lblCancionActual.setText(cancion.getTitulo());
        }

        // Actualizar artista
        if (lblArtistaActual != null) {
            if (cancion.getArtistas() != null && !cancion.getArtistas().isEmpty()) {
                String artista = cancion.getArtistas().stream()
                        .map(a -> a.getNombre())
                        .collect(Collectors.joining(", "));
                lblArtistaActual.setText(artista);
            } else {
                lblArtistaActual.setText("Artista Desconocido");
            }
        }

        // Actualizar imagen
        if (imgCancionActual != null) {
            try {
                Image portada;
                if (cancion.getPortada() != null && cancion.getPortada().length > 0) {
                    portada = new Image(new ByteArrayInputStream(cancion.getPortada()));
                } else {
                    portada = imagenPortadaGenerica;
                }
                imgCancionActual.setImage(portada);
            } catch (Exception e) {
                imgCancionActual.setImage(imagenPortadaGenerica);
            }
        }

        // Actualizar duración
        duracionRealCancion = cancion.getDuracion();
        actualizarLabelsTime(0, duracionRealCancion);
    }

    /**
     * Reinicia progreso cuando cambia la canción
     */
    private void reiniciarProgresoYTiempo() {
        timeline.stop();
        if (pgbProgresoCancion != null) {
            pgbProgresoCancion.setValue(0.0);
        }
        tiempoActualSegundos = 0.0;
        duracionRealCancion = 0.0;
        actualizarLabelsTime(0, 0);
        
        // Delay para obtener duración real
        javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(Duration.millis(500));
        delay.setOnFinished(e -> actualizarDuracionActual());
        delay.play();
    }

    /**
     * Obtiene duración real de la canción actual
     */
    private void actualizarDuracionActual() {
        if (reproductor == null) return;
        
        reproductor.obtenerDuracionCancionActual(duracion -> {
            Platform.runLater(() -> {
                this.duracionRealCancion = duracion;
                actualizarLabelsTime(tiempoActualSegundos, duracionRealCancion);
            });
        });
    }

    /**
     * Cambia el icono del botón play/pause
     */
    private void cambiarIconoPlayPause(boolean aPlay) {
        if (btnPlayPause == null) return;
        
        if (aPlay && imagenPlay != null) {
            ImageView iconView = new ImageView(imagenPlay);
            iconView.setFitHeight(24);
            iconView.setFitWidth(24);
            btnPlayPause.setGraphic(iconView);
        } else if (!aPlay && imagenPause != null) {
            ImageView iconView = new ImageView(imagenPause);
            iconView.setFitHeight(24);
            iconView.setFitWidth(24);
            btnPlayPause.setGraphic(iconView);
        }
    }

    /**
     * Muestra el panel del reproductor
     */
    public void mostrarReproductor() {
        if (anchorBarraReproduccion != null) {
            anchorBarraReproduccion.setVisible(true);
            anchorBarraReproduccion.setManaged(true);
        }
    }

    /**
     * Oculta el panel del reproductor
     */
    private void ocultarReproductor() {
        if (anchorBarraReproduccion != null) {
            anchorBarraReproduccion.setVisible(false);
            anchorBarraReproduccion.setManaged(false);
        }
    }

    @FXML
    private void handleAnterior() {
        if (reproductor != null) {
            reproductor.anterior();
        }
    }

    @FXML
    private void handlePlayPause() {
        if (reproductor == null) return;

        if (reproductor.estaReproduciendo()) {
            reproductor.pausar();
            timeline.stop();
            cambiarIconoPlayPause(true);
        } else {
            reproductor.reproducir();
            if (reproductor.getEstado() instanceof EstadoPausado) {
                reproductor.reanudar();
            }
            actualizarDuracionActual();
            timeline.play();
            cambiarIconoPlayPause(false);
        }
    }

    @FXML
    private void handleSiguiente() {
        if (reproductor != null) {
            reproductor.siguiente();
        }
    }

    @FXML
    private void handleVolumen() {
        System.out.println("Control de volumen");
    }

    @FXML
    private void handleExpandir() {
        //abrir el reproductor para la cancion actual
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/ReproductorMusical/reproduccionCancion.fxml"));

            Parent root = loader.load();
            // Obtener el controlador y pasarle la lista y el índice actual
            UserInterface.CustomerControl.ReproductorMusical.ReproduccionCancionController controller = loader.getController();
            int indiceActual = reproductor.getPlaylist().getIndiceActual();
            controller.setDatosReproduccion(cancionesReproduciendose, indiceActual);
            Stage stage = new Stage();
            stage.setTitle("Reproductor Musical");
            stage.setScene(new Scene(root, 1200, 800));
            stage.setResizable(true);
            stage.setMinWidth(1000);
            stage.setMinHeight(700);
            stage.centerOnScreen();
            stage.setOnHidden(e -> {
                System.out.println("Reproductor cerrado, recargando playlists...");
                cargarPlaylists();
            });
            stage.show();

            // Cerrar la ventana actual
            Stage currentStage = (Stage) btnExpandir.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            mostrarAlerta("Error", "Error al abrir el reproductor: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }

        System.out.println("Expandir reproductor");
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
            // Obtener el ID del usuario actual
            int idUsuarioActual = obtenerIdUsuarioActual();
            if (idUsuarioActual <= 0) {
                System.err.println("No se pudo obtener el ID del usuario actual para filtrar");
                return;
            }

            // Obtener solo las playlists del usuario actual
            PlaylistDAO playlistDAO = new PlaylistDAO();
            List<PlaylistDTO> todasLasPlaylists = playlistDAO.obtenerPlaylistPorUsuario(idUsuarioActual);

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
            // Obtener el ID del usuario actual
            int idUsuarioActual = obtenerIdUsuarioActual();
            if (idUsuarioActual <= 0) {
                System.err.println("No se pudo obtener el ID del usuario actual");
                mostrarAlerta("Error", "No se pudo identificar al usuario actual. Por favor, inicia sesión nuevamente.", Alert.AlertType.ERROR);
                return;
            }

            // Usar el método que filtra por usuario
            PlaylistDAO playlistDAO = new PlaylistDAO();
            List<PlaylistDTO> playlists = playlistDAO.obtenerPlaylistPorUsuario(idUsuarioActual);

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
     * Obtiene el ID del usuario actualmente logueado
     * @return ID del usuario actual, o -1 si no se puede obtener
     */
    private int obtenerIdUsuarioActual() {
        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            String correoUsuario = Sesion.getSesion().obtenerUsuarioActual().getCorreo();
            int idUsuario = usuarioDAO.obtenerIdUsuarioPorCorreo(correoUsuario);
            
            if (idUsuario <= 0) {
                System.err.println("No se pudo obtener un ID válido para el usuario con correo: " + correoUsuario);
            }
            
            return idUsuario;
        } catch (Exception e) {
            System.err.println("Error al obtener ID del usuario actual: " + e.getMessage());
            e.printStackTrace();
            return -1;
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
     * Método de limpieza al cerrar la ventana
     */
    public void cleanup() {
        if (timeline != null) {
            timeline.stop();
        }
        if (reproductor != null) {
            reproductor.detener();
        }
    }

    public void setReproduccionActual(java.util.List<DataAccessComponent.DTO.CancionDTO> canciones, int indiceActual) {
        this.cancionesReproduciendose = canciones;
        if (reproductor != null && reproductor.getPlaylist() != null) {
            reproductor.getPlaylist().setIndiceActual(indiceActual);
        }
        actualizarInformacionCancionReproductor();
    }
}