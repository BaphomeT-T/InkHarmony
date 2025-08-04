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

    // === COMPONENTES DE LA INTERFAZ ===
    
    @FXML
    private TableView<CancionDTO> tableCanciones;
    
    @FXML
    private TableColumn<CancionDTO, Boolean> colSeleccionar;
    
    @FXML
    private TableColumn<CancionDTO, CancionDTO> colTituloConImagen;
    
    @FXML
    private TableColumn<CancionDTO, String> colArtista;
    
    @FXML
    private TableColumn<CancionDTO, String> colGenero;
    
    @FXML
    private TableColumn<CancionDTO, String> colAnio;
    
    @FXML
    private TableColumn<CancionDTO, String> colDuracion;
    
    @FXML
    private TableColumn<CancionDTO, CancionDTO> colAcciones;
    
    @FXML
    private TextField txtBuscar;
    
    @FXML
    private Label lblNombrePlaylist;
    
    @FXML
    private Label lblCancionesSeleccionadas;
    
    @FXML
    private Button btnAgregarSeleccionadas;
    
    @FXML
    private Button btnAgregarTodas;
    
    @FXML
    private Button btnCerrar;

    @FXML
    private Button btnRegresar;

    // === LÓGICA DE NEGOCIO ===
    
    private Cancion cancionBL = new Cancion();
    private Playlist playlistBL = new Playlist();
    
    // === DATOS ===
    
    private ObservableList<CancionDTO> listaObservable;
    private List<CancionDTO> cancionesSeleccionadas = new ArrayList<>();

    /**
     * Inicializa el controlador configurando la tabla y cargando las canciones.
     */
    @FXML
    public void initialize() {
        try {
            configurarTabla();
            cargarCanciones();
            configurarBusqueda();
            actualizarContadorSeleccionadas();
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al inicializar: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Establece la playlist a la cual se van a agregar canciones.
     */
    public void setPlaylist(PlaylistDTO playlist) {
        if (playlist != null) {
            lblNombrePlaylist.setText("Playlist: " + playlist.getTituloPlaylist());
            try {
                // Cargar la playlist usando buscarPorId
                PlaylistDTO playlistCargada = playlistBL.buscarPorId(playlist.getIdPlaylist());
                if (playlistCargada != null) {
                    System.out.println("Playlist cargada: " + playlistCargada.getTituloPlaylist());
                }
            } catch (Exception e) {
                mostrarAlerta("Error", "Error al cargar playlist: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Configura la tabla de canciones con todas las columnas necesarias.
     */
    private void configurarTabla() {
        System.out.println("=== CONFIGURANDO TABLA AGREGAR CANCIONES ===");

        // Política de ajuste de columna
        tableCanciones.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Deshabilitar reordenamiento de columnas
        colSeleccionar.setReorderable(false);
        colTituloConImagen.setReorderable(false);
        colArtista.setReorderable(false);
        colGenero.setReorderable(false);
        colAnio.setReorderable(false);
        colDuracion.setReorderable(false);
        colAcciones.setReorderable(false);

        // Configurar columna de selección (checkbox) - simplificado
        colSeleccionar.setCellValueFactory(cellData -> {
            CancionDTO cancion = cellData.getValue();
            javafx.beans.property.BooleanProperty selected = new javafx.beans.property.SimpleBooleanProperty(cancionesSeleccionadas.contains(cancion));
            
            // Listener para manejar cambios de selección
            selected.addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) {
                    if (!cancionesSeleccionadas.contains(cancion)) {
                        cancionesSeleccionadas.add(cancion);
                    }
                } else {
                    cancionesSeleccionadas.remove(cancion);
                }
                actualizarContadorSeleccionadas();
                tableCanciones.refresh(); // Refrescar para mostrar cambios visuales
            });
            
            return selected;
        });
        
        colSeleccionar.setCellFactory(CheckBoxTableCell.forTableColumn(colSeleccionar));
        colSeleccionar.setEditable(true);
        tableCanciones.setEditable(true);
        
        // Configurar selección de filas para mejor visibilidad
        tableCanciones.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
        tableCanciones.setRowFactory(tv -> {
            TableRow<CancionDTO> row = new TableRow<>();
            row.itemProperty().addListener((obs, previousCancion, currentCancion) -> {
                if (currentCancion != null) {
                    if (cancionesSeleccionadas.contains(currentCancion)) {
                        row.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                    } else {
                        row.setStyle("");
                    }
                }
            });
            return row;
        });

        // Configurar columna de artista
        colArtista.setCellValueFactory(cellData -> {
            List<ArtistaDTO> artistas = cellData.getValue().getArtistas();
            String nombres = artistas.stream()
                    .map(ArtistaDTO::getNombre)
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(nombres);
        });

        // Configurar columna de géneros
        colGenero.setCellValueFactory(cellData -> {
            List<Genero> generos = cellData.getValue().getGeneros();
            String texto = generos.stream().map(Enum::name).collect(Collectors.joining(", "));
            return new SimpleStringProperty(texto);
        });

        // Configurar columna de año
        colAnio.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.valueOf(cellData.getValue().getAnio())));

        // Configurar columna de duración
        colDuracion.setCellValueFactory(new PropertyValueFactory<>("duracion"));

        // Configurar columna personalizada: imagen + título
        colTituloConImagen.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));
        colTituloConImagen.setCellFactory(column -> new TableCell<CancionDTO, CancionDTO>() {
            private final HBox contenedor = new HBox(10);
            private final ImageView imageView = new ImageView();
            private final Label labelTitulo = new Label();

            {
                imageView.setFitHeight(40);
                imageView.setFitWidth(40);
                imageView.setPreserveRatio(true);
                contenedor.getChildren().addAll(imageView, labelTitulo);
                contenedor.setPadding(new Insets(5));
            }

            @Override
            protected void updateItem(CancionDTO cancion, boolean empty) {
                super.updateItem(cancion, empty);
                if (empty || cancion == null) {
                    setGraphic(null);
                } else {
                    labelTitulo.setText(cancion.getTitulo());
                    
                    // Cargar imagen de la canción
                    if (cancion.getPortada() != null) {
                        try {
                            ByteArrayInputStream inputStream = new ByteArrayInputStream(cancion.getPortada());
                            Image imagen = new Image(inputStream);
                            imageView.setImage(imagen);
                        } catch (Exception e) {
                            cargarImagenPorDefecto();
                        }
                    } else {
                        cargarImagenPorDefecto();
                    }
                    
                    setGraphic(contenedor);
                }
            }

            private void cargarImagenPorDefecto() {
                try {
                    Image imagenPorDefecto = new Image(getClass().getResourceAsStream("/UserInterface/Resources/img/CatalogoCanciones/cancion-default.png"));
                    imageView.setImage(imagenPorDefecto);
                } catch (Exception e) {
                    // Si no existe imagen por defecto, crear una imagen vacía
                    imageView.setImage(null);
                }
            }
        });

        // Configurar columna de acciones (botón +)
        agregarColumnaAcciones();
    }

    /**
     * Agrega la columna de acciones con botón + para agregar canciones individuales.
     */
    private void agregarColumnaAcciones() {
        colAcciones.setCellFactory(col -> new TableCell<CancionDTO, CancionDTO>() {
            private final Button btnAgregar = new Button("+");

            {
                btnAgregar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 15; -fx-font-size: 14px; -fx-font-weight: bold;");
                btnAgregar.setPrefSize(30, 30);
                
                btnAgregar.setOnAction(event -> {
                    CancionDTO cancion = getTableView().getItems().get(getIndex());
                    agregarCancionIndividual(cancion);
                });
            }

            @Override
            protected void updateItem(CancionDTO cancion, boolean empty) {
                super.updateItem(cancion, empty);
                if (empty || cancion == null) {
                    setGraphic(null);
                } else {
                    setGraphic(btnAgregar);
                }
            }
        });
    }

    /**
     * Carga todas las canciones disponibles en la base de datos.
     */
    private void cargarCanciones() throws Exception {
        System.out.println("Cargando canciones disponibles...");
        List<CancionDTO> lista = cancionBL.buscarTodo();
        System.out.println("Canciones cargadas: " + lista.size());
        
        listaObservable = FXCollections.observableArrayList(lista);
        tableCanciones.setItems(listaObservable);
    }

    /**
     * Configura la búsqueda en tiempo real.
     */
    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarCanciones(newValue);
        });
    }

    /**
     * Filtra las canciones según el texto de búsqueda.
     */
    private void filtrarCanciones(String filtro) {
        if (filtro == null || filtro.isBlank()) {
            tableCanciones.setItems(listaObservable);
            return;
        }

        ObservableList<CancionDTO> filtrados = FXCollections.observableArrayList(
            listaObservable.stream()
                .filter(cancion -> cancion.getTitulo().toLowerCase().contains(filtro.toLowerCase()) ||
                                 cancion.getArtistas().stream().anyMatch(artista -> 
                                     artista.getNombre().toLowerCase().contains(filtro.toLowerCase())))
                .collect(Collectors.toList())
        );

        tableCanciones.setItems(filtrados);
    }

    /**
     * Maneja la búsqueda desde el campo de texto.
     */
    @FXML
    private void handleBuscar() {
        // Ya manejado por el listener en configurarBusqueda()
    }

    /**
     * Agrega una canción individual a la playlist.
     */
    private void agregarCancionIndividual(CancionDTO cancion) {
        try {
            boolean agregada = playlistBL.agregarCancion(cancion.getIdCancion());
            
            if (agregada) {
                mostrarAlerta("Éxito", 
                    "Canción '" + cancion.getTitulo() + "' agregada a la playlist correctamente.", 
                    Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("Información", 
                    "La canción '" + cancion.getTitulo() + "' ya existe en la playlist.", 
                    Alert.AlertType.WARNING);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", 
                "Error al agregar canción: " + e.getMessage(), 
                Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Maneja el evento de agregar canciones seleccionadas.
     */
    @FXML
    private void handleAgregarSeleccionadas() {
        agregarCancionesSeleccionadas();
    }

    /**
     * Maneja el evento de agregar todas las canciones seleccionadas.
     */
    @FXML
    private void handleAgregarTodasSeleccionadas() {
        agregarCancionesSeleccionadas();
    }

    /**
     * Agrega todas las canciones que están seleccionadas.
     */
    private void agregarCancionesSeleccionadas() {
        if (cancionesSeleccionadas.isEmpty()) {
            mostrarAlerta("Información", 
                "No hay canciones seleccionadas para agregar.", 
                Alert.AlertType.INFORMATION);
            return;
        }

        int exitosas = 0;
        int duplicadas = 0;
        int errores = 0;

        for (CancionDTO cancion : cancionesSeleccionadas) {
            try {
                boolean agregada = playlistBL.agregarCancion(cancion.getIdCancion());
                if (agregada) {
                    exitosas++;
                } else {
                    duplicadas++;
                }
            } catch (Exception e) {
                errores++;
                System.err.println("Error al agregar canción " + cancion.getTitulo() + ": " + e.getMessage());
            }
        }

        // Mostrar resumen
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Proceso completado:\n");
        mensaje.append("• Canciones agregadas: ").append(exitosas).append("\n");
        if (duplicadas > 0) {
            mensaje.append("• Canciones ya existentes: ").append(duplicadas).append("\n");
        }
        if (errores > 0) {
            mensaje.append("• Errores: ").append(errores).append("\n");
        }

        Alert.AlertType tipo = errores > 0 ? Alert.AlertType.WARNING : Alert.AlertType.INFORMATION;
        mostrarAlerta("Resultado", mensaje.toString(), tipo);

        // Limpiar selección
        cancionesSeleccionadas.clear();
        actualizarContadorSeleccionadas();
        tableCanciones.refresh();
    }

    /**
     * Actualiza el contador de canciones seleccionadas.
     */
    private void actualizarContadorSeleccionadas() {
        lblCancionesSeleccionadas.setText(cancionesSeleccionadas.size() + " canciones seleccionadas");
    }

    /**
     * Cierra la ventana.
     */
    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }
    /**
     * Regresa a la ventana anterior cerrando la ventana actual.
     */
    @FXML
    private void regresarVentana() {
        try {
            // Obtener el stage actual desde cualquier componente de la interfaz
            Stage stage = (Stage) btnRegresar.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            System.err.println("Error al cerrar la ventana: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * Muestra una alerta con el mensaje especificado.
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
