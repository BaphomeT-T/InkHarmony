package UserInterface.CustomerControl.Playlist;
import UserInterface.CustomerControl.Playlist.EliminarPlaylistController;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    @FXML private Button btnVolumen;
    @FXML private Slider sliderVolumen;
    @FXML private Button btnExpandir;

    private ObservableList<PlaylistDTO> listPlaylistsData;
    private ObservableList<Object> listCancionesData;
    private PlaylistDTO playlistSeleccionada;

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
        if (playlistSeleccionada != null) {
            System.out.println("Reproduciendo playlist: " + playlistSeleccionada.getTituloPlaylist());
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

    @FXML
    private void handleAnterior() {
        System.out.println("Canción anterior");
    }

    @FXML
    private void handlePlayPause() {
        System.out.println("Play/Pause");
    }

    @FXML
    private void handleSiguiente() {
        System.out.println("Siguiente canción");
    }

    @FXML
    private void handleVolumen() {
        System.out.println("Control de volumen");
    }

    @FXML
    private void handleExpandir() {
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
}