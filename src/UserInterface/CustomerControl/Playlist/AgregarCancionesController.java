
/*
|-----------------------------------------------|
| © 2025 EPN-FIS, Todos los derechos reservados |
| GR1SW                                         |
|-----------------------------------------------|
Autor: Grupo - C
Descripción: Controlador para agregar canciones a una playlist.
*/

package UserInterface.CustomerControl.Playlist;

import BusinessLogic.Cancion;
import BusinessLogic.Playlist;
import DataAccessComponent.DTO.ArtistaDTO;
import DataAccessComponent.DTO.CancionDTO;
import DataAccessComponent.DTO.PlaylistDTO;
import BusinessLogic.Genero;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador para la interfaz de agregar canciones a una playlist.
 *
 * Permite visualizar todas las canciones disponibles en la base de datos y
 * agregarlas a la playlist seleccionada usando BusinessLogic.Playlist.
 *
 * @author Grupo - A
 */
public class AgregarCancionesController {

    // === CONSTANTES ===
    private static final String IMAGEN_DEFAULT_PATH = "/UserInterface/Resources/img/CatalogoCanciones/cancion-default.png";
    private static final String ESTILO_FILA_SELECCIONADA = "-fx-background-color: #4CAF50; -fx-text-fill: white;";
    private static final String ESTILO_FILA_NORMAL = "";
    private static final String ESTILO_BOTON_AGREGAR = "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 15; -fx-font-size: 14px; -fx-font-weight: bold;";
    private static final int TAMAÑO_IMAGEN = 40;
    private static final int TAMAÑO_BOTON = 30;
    private static final int ESPACIADO_CONTENEDOR = 10;
    private static final int PADDING_CONTENEDOR = 5;

    // === COMPONENTES DE LA INTERFAZ ===
    @FXML private TableView<CancionDTO> tableCanciones;
    @FXML private TableColumn<CancionDTO, Boolean> colSeleccionar;
    @FXML private TableColumn<CancionDTO, CancionDTO> colTituloConImagen;
    @FXML private TableColumn<CancionDTO, String> colArtista;
    @FXML private TableColumn<CancionDTO, String> colGenero;
    @FXML private TableColumn<CancionDTO, String> colAnio;
    @FXML private TableColumn<CancionDTO, String> colDuracion;
    @FXML private TableColumn<CancionDTO, CancionDTO> colAcciones;
    @FXML private TextField txtBuscar;
    @FXML private Label lblNombrePlaylist;
    @FXML private Label lblCancionesSeleccionadas;
    @FXML private Button btnAgregarSeleccionadas;
    @FXML private Button btnAgregarTodas;
    @FXML private Button btnCerrar;
    @FXML private Button btnRegresar;

    // === LÓGICA DE NEGOCIO ===
    private final Cancion cancionBL = new Cancion();
    private final Playlist playlistBL = new Playlist();

    // === DATOS ===
    private ObservableList<CancionDTO> listaObservable;
    private final List<CancionDTO> cancionesSeleccionadas = new ArrayList<>();

    /**
     * Inicializa el controlador configurando la tabla y cargando las canciones.
     */
    @FXML
    public void initialize() {
        try {
            initializeComponents();
        } catch (Exception e) {
            handleInitializationError(e);
        }
    }

    /**
     * Establece la playlist a la cual se van a agregar canciones.
     */
    public void setPlaylist(PlaylistDTO playlist) {
        if (isValidPlaylist(playlist)) {
            setupPlaylistLabel(playlist);
            loadPlaylistData(playlist);
        }
    }

    // === MÉTODOS DE INICIALIZACIÓN ===

    private void initializeComponents() throws Exception {
        configurarTabla();
        cargarCanciones();
        configurarBusqueda();
        actualizarContadorSeleccionadas();
    }

    private void handleInitializationError(Exception e) {
        mostrarAlerta("Error", "Error al inicializar: " + e.getMessage(), Alert.AlertType.ERROR);
        e.printStackTrace();
    }

    private boolean isValidPlaylist(PlaylistDTO playlist) {
        return playlist != null;
    }

    private void setupPlaylistLabel(PlaylistDTO playlist) {
        lblNombrePlaylist.setText("Playlist: " + playlist.getTituloPlaylist());
    }

    private void loadPlaylistData(PlaylistDTO playlist) {
        try {
            PlaylistDTO playlistCargada = playlistBL.buscarPorId(playlist.getIdPlaylist());
            if (playlistCargada != null) {
                System.out.println("Playlist cargada: " + playlistCargada.getTituloPlaylist());
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar playlist: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // === CONFIGURACIÓN DE LA TABLA ===

    private void configurarTabla() {
        System.out.println("=== CONFIGURANDO TABLA AGREGAR CANCIONES ===");

        setupTableProperties();
        setupColumnProperties();
        setupTableColumns();
        setupRowFactory();
    }

    private void setupTableProperties() {
        tableCanciones.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableCanciones.setEditable(true);
        tableCanciones.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
    }

    private void setupColumnProperties() {
        disableColumnReordering();
    }

    private void disableColumnReordering() {
        colSeleccionar.setReorderable(false);
        colTituloConImagen.setReorderable(false);
        colArtista.setReorderable(false);
        colGenero.setReorderable(false);
        colAnio.setReorderable(false);
        colDuracion.setReorderable(false);
        colAcciones.setReorderable(false);
    }

    private void setupTableColumns() {
        setupSelectionColumn();
        setupArtistColumn();
        setupGenreColumn();
        setupYearColumn();
        setupDurationColumn();
        setupTitleWithImageColumn();
        setupActionsColumn();
    }

    private void setupSelectionColumn() {
        colSeleccionar.setCellValueFactory(this::createSelectionCellValue);
        colSeleccionar.setCellFactory(CheckBoxTableCell.forTableColumn(colSeleccionar));
        colSeleccionar.setEditable(true);
    }

    private BooleanProperty createSelectionCellValue(TableColumn.CellDataFeatures<CancionDTO, Boolean> cellData) {
        CancionDTO cancion = cellData.getValue();
        BooleanProperty selected = new SimpleBooleanProperty(cancionesSeleccionadas.contains(cancion));

        selected.addListener((obs, wasSelected, isSelected) -> {
            handleSelectionChange(cancion, isSelected);
        });

        return selected;
    }

    private void handleSelectionChange(CancionDTO cancion, boolean isSelected) {
        if (isSelected) {
            addToSelection(cancion);
        } else {
            removeFromSelection(cancion);
        }
        actualizarContadorSeleccionadas();
        tableCanciones.refresh();
    }

    private void addToSelection(CancionDTO cancion) {
        if (!cancionesSeleccionadas.contains(cancion)) {
            cancionesSeleccionadas.add(cancion);
        }
    }

    private void removeFromSelection(CancionDTO cancion) {
        cancionesSeleccionadas.remove(cancion);
    }

    private void setupRowFactory() {
        tableCanciones.setRowFactory(tv -> createCustomTableRow());
    }

    private TableRow<CancionDTO> createCustomTableRow() {
        TableRow<CancionDTO> row = new TableRow<>();
        row.itemProperty().addListener((obs, previousCancion, currentCancion) -> {
            updateRowStyle(row, currentCancion);
        });
        return row;
    }

    private void updateRowStyle(TableRow<CancionDTO> row, CancionDTO cancion) {
        if (cancion != null) {
            String style = cancionesSeleccionadas.contains(cancion) ?
                    ESTILO_FILA_SELECCIONADA : ESTILO_FILA_NORMAL;
            row.setStyle(style);
        }
    }

    private void setupArtistColumn() {
        colArtista.setCellValueFactory(cellData -> {
            String artistNames = extractArtistNames(cellData.getValue().getArtistas());
            return new SimpleStringProperty(artistNames);
        });
    }

    private String extractArtistNames(List<ArtistaDTO> artistas) {
        return artistas.stream()
                .map(ArtistaDTO::getNombre)
                .collect(Collectors.joining(", "));
    }

    private void setupGenreColumn() {
        colGenero.setCellValueFactory(cellData -> {
            String genreNames = extractGenreNames(cellData.getValue().getGeneros());
            return new SimpleStringProperty(genreNames);
        });
    }

    private String extractGenreNames(List<Genero> generos) {
        return generos.stream()
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }

    private void setupYearColumn() {
        colAnio.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getAnio())));
    }

    private void setupDurationColumn() {
        colDuracion.setCellValueFactory(new PropertyValueFactory<>("duracion"));
    }

    private void setupTitleWithImageColumn() {
        colTituloConImagen.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));
        colTituloConImagen.setCellFactory(column -> new TitleWithImageCell());
    }

    private void setupActionsColumn() {
        colAcciones.setCellFactory(col -> new ActionButtonCell());
    }

    // === CLASES INTERNAS PARA CELDAS PERSONALIZADAS ===

    private class TitleWithImageCell extends TableCell<CancionDTO, CancionDTO> {
        private final HBox contenedor = new HBox(ESPACIADO_CONTENEDOR);
        private final ImageView imageView = new ImageView();
        private final Label labelTitulo = new Label();

        public TitleWithImageCell() {
            setupImageView();
            setupContainer();
        }

        private void setupImageView() {
            imageView.setFitHeight(TAMAÑO_IMAGEN);
            imageView.setFitWidth(TAMAÑO_IMAGEN);
            imageView.setPreserveRatio(true);
        }

        private void setupContainer() {
            contenedor.getChildren().addAll(imageView, labelTitulo);
            contenedor.setPadding(new Insets(PADDING_CONTENEDOR));
        }

        @Override
        protected void updateItem(CancionDTO cancion, boolean empty) {
            super.updateItem(cancion, empty);

            if (empty || cancion == null) {
                setGraphic(null);
            } else {
                updateCellContent(cancion);
                setGraphic(contenedor);
            }
        }

        private void updateCellContent(CancionDTO cancion) {
            labelTitulo.setText(cancion.getTitulo());
            loadSongImage(cancion);
        }

        private void loadSongImage(CancionDTO cancion) {
            if (cancion.getPortada() != null) {
                loadImageFromBytes(cancion.getPortada());
            } else {
                cargarImagenPorDefecto();
            }
        }

        private void loadImageFromBytes(byte[] imageData) {
            try {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
                Image imagen = new Image(inputStream);
                imageView.setImage(imagen);
            } catch (Exception e) {
                cargarImagenPorDefecto();
            }
        }

        private void cargarImagenPorDefecto() {
            try {
                Image imagenPorDefecto = new Image(getClass().getResourceAsStream(IMAGEN_DEFAULT_PATH));
                imageView.setImage(imagenPorDefecto);
            } catch (Exception e) {
                imageView.setImage(null);
            }
        }
    }

    private class ActionButtonCell extends TableCell<CancionDTO, CancionDTO> {
        private final Button btnAgregar = new Button("+");

        public ActionButtonCell() {
            setupButton();
        }

        private void setupButton() {
            btnAgregar.setStyle(ESTILO_BOTON_AGREGAR);
            btnAgregar.setPrefSize(TAMAÑO_BOTON, TAMAÑO_BOTON);
            btnAgregar.setOnAction(event -> handleAddButtonClick());
        }

        private void handleAddButtonClick() {
            CancionDTO cancion = getTableView().getItems().get(getIndex());
            agregarCancionIndividual(cancion);
        }

        @Override
        protected void updateItem(CancionDTO cancion, boolean empty) {
            super.updateItem(cancion, empty);
            setGraphic(empty || cancion == null ? null : btnAgregar);
        }
    }

    // === CARGA Y FILTRADO DE DATOS ===

    private void cargarCanciones() throws Exception {
        System.out.println("Cargando canciones disponibles...");
        List<CancionDTO> lista = cancionBL.buscarTodo();
        System.out.println("Canciones cargadas: " + lista.size());

        listaObservable = FXCollections.observableArrayList(lista);
        tableCanciones.setItems(listaObservable);
    }

    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarCanciones(newValue);
        });
    }

    private void filtrarCanciones(String filtro) {
        if (isEmptyFilter(filtro)) {
            tableCanciones.setItems(listaObservable);
            return;
        }

        ObservableList<CancionDTO> filtrados = createFilteredList(filtro);
        tableCanciones.setItems(filtrados);
    }

    private boolean isEmptyFilter(String filtro) {
        return filtro == null || filtro.isBlank();
    }

    private ObservableList<CancionDTO> createFilteredList(String filtro) {
        return FXCollections.observableArrayList(
                listaObservable.stream()
                        .filter(cancion -> matchesFilter(cancion, filtro))
                        .collect(Collectors.toList())
        );
    }

    private boolean matchesFilter(CancionDTO cancion, String filtro) {
        String filterLower = filtro.toLowerCase();
        return cancion.getTitulo().toLowerCase().contains(filterLower) ||
                cancion.getArtistas().stream().anyMatch(artista ->
                        artista.getNombre().toLowerCase().contains(filterLower));
    }

    // === MANEJO DE EVENTOS ===

    @FXML
    private void handleBuscar() {
        // Ya manejado por el listener en configurarBusqueda()
    }

    @FXML
    private void handleAgregarSeleccionadas() {
        agregarCancionesSeleccionadas();
    }

    @FXML
    private void handleAgregarTodasSeleccionadas() {
        agregarCancionesSeleccionadas();
    }

    @FXML
    private void cerrarVentana() {
        closeCurrentWindow(btnCerrar);
    }

    @FXML
    private void regresarVentana() {
        try {
            closeCurrentWindow(btnRegresar);
        } catch (Exception e) {
            handleWindowCloseError(e);
        }
    }

    // === LÓGICA DE AGREGAR CANCIONES ===

    private void agregarCancionIndividual(CancionDTO cancion) {
        try {
            boolean agregada = playlistBL.agregarCancion(cancion.getIdCancion());
            handleIndividualAddResult(cancion, agregada);
        } catch (Exception e) {
            handleAddSongError(cancion, e);
        }
    }

    private void handleIndividualAddResult(CancionDTO cancion, boolean agregada) {
        if (agregada) {
            showSuccessMessage(cancion);
        } else {
            showDuplicateMessage(cancion);
        }
    }

    private void showSuccessMessage(CancionDTO cancion) {
        mostrarAlerta("Éxito",
                "Canción '" + cancion.getTitulo() + "' agregada a la playlist correctamente.",
                Alert.AlertType.INFORMATION);
    }

    private void showDuplicateMessage(CancionDTO cancion) {
        mostrarAlerta("Información",
                "La canción '" + cancion.getTitulo() + "' ya existe en la playlist.",
                Alert.AlertType.WARNING);
    }

    private void handleAddSongError(CancionDTO cancion, Exception e) {
        mostrarAlerta("Error",
                "Error al agregar canción: " + e.getMessage(),
                Alert.AlertType.ERROR);
        e.printStackTrace();
    }

    private void agregarCancionesSeleccionadas() {
        if (noHayCancionesSeleccionadas()) {
            showNoSelectionMessage();
            return;
        }

        ProcessResult result = processSelectedSongs();
        showProcessResult(result);
        clearSelection();
    }

    private boolean noHayCancionesSeleccionadas() {
        return cancionesSeleccionadas.isEmpty();
    }

    private void showNoSelectionMessage() {
        mostrarAlerta("Información",
                "No hay canciones seleccionadas para agregar.",
                Alert.AlertType.INFORMATION);
    }

    private ProcessResult processSelectedSongs() {
        ProcessResult result = new ProcessResult();

        for (CancionDTO cancion : cancionesSeleccionadas) {
            processSingleSong(cancion, result);
        }

        return result;
    }

    private void processSingleSong(CancionDTO cancion, ProcessResult result) {
        try {
            boolean agregada = playlistBL.agregarCancion(cancion.getIdCancion());
            if (agregada) {
                result.incrementExitosas();
            } else {
                result.incrementDuplicadas();
            }
        } catch (Exception e) {
            result.incrementErrores();
            System.err.println("Error al agregar canción " + cancion.getTitulo() + ": " + e.getMessage());
        }
    }

    private void showProcessResult(ProcessResult result) {
        String mensaje = buildResultMessage(result);
        Alert.AlertType tipo = result.hasErrors() ? Alert.AlertType.WARNING : Alert.AlertType.INFORMATION;
        mostrarAlerta("Resultado", mensaje, tipo);
    }

    private String buildResultMessage(ProcessResult result) {
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Proceso completado:\n");
        mensaje.append("• Canciones agregadas: ").append(result.getExitosas()).append("\n");

        if (result.getDuplicadas() > 0) {
            mensaje.append("• Canciones ya existentes: ").append(result.getDuplicadas()).append("\n");
        }

        if (result.getErrores() > 0) {
            mensaje.append("• Errores: ").append(result.getErrores()).append("\n");
        }

        return mensaje.toString();
    }

    private void clearSelection() {
        cancionesSeleccionadas.clear();
        actualizarContadorSeleccionadas();
        tableCanciones.refresh();
    }

    // === MÉTODOS AUXILIARES ===

    private void actualizarContadorSeleccionadas() {
        lblCancionesSeleccionadas.setText(cancionesSeleccionadas.size() + " canciones seleccionadas");
    }

    private void closeCurrentWindow(Button sourceButton) {
        Stage stage = (Stage) sourceButton.getScene().getWindow();
        stage.close();
    }

    private void handleWindowCloseError(Exception e) {
        System.err.println("Error al cerrar la ventana: " + e.getMessage());
        e.printStackTrace();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // === CLASE AUXILIAR PARA RESULTADOS DE PROCESAMIENTO ===

    private static class ProcessResult {
        private int exitosas = 0;
        private int duplicadas = 0;
        private int errores = 0;

        public void incrementExitosas() { exitosas++; }
        public void incrementDuplicadas() { duplicadas++; }
        public void incrementErrores() { errores++; }

        public int getExitosas() { return exitosas; }
        public int getDuplicadas() { return duplicadas; }
        public int getErrores() { return errores; }

        public boolean hasErrors() { return errores > 0; }
    }
}