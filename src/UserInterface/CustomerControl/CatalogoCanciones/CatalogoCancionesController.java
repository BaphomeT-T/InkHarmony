/*
|-----------------------------------------------|
| © 2025 EPN-FIS, Todos los derechos reservados |
| GR1SW                                         |
|-----------------------------------------------|
Autor: Sergio Rodríguez
Descripción: Controlador del catálogo de canciones.
*/

package UserInterface.CustomerControl.CatalogoCanciones;


import BusinessLogic.Cancion;
import DataAccessComponent.DTO.CancionDTO;
import BusinessLogic.Genero;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase CatalogoCancionesController que gestiona la interfaz gráfica del catálogo de canciones.
 *
 * Permite visualizar, filtrar, editar y eliminar canciones del sistema InkHarmony. La clase se
 * comunica con la capa de lógica de negocio (Cancion) y manipula datos representados por objetos DTO.
 * Utiliza JavaFX para construir una experiencia interactiva con elementos visuales como tablas, imágenes y botones.
 *
 * @author Sergio Rodríguez
 * @version 1.0
 * @since 18-07-2025
 */
public class CatalogoCancionesController {

    // Referencia a la tabla principal que muestra las canciones
    @FXML
    private TableView<CancionDTO> tableCanciones;

    // Columnas de la tabla
    @FXML
    private TableColumn<CancionDTO, String> colGenero;
    @FXML
    private TableColumn<CancionDTO, String> colDuracion;
    @FXML
    private TableColumn<CancionDTO, CancionDTO> colTituloConImagen;
    
    // Botones de acción
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnEliminar;

    // Campo de texto para búsqueda de canciones por título
    @FXML
    private TextField txtBuscar;

    // Instancia de la capa de lógica de negocio para manejo de canciones
    private Cancion cancionBL = new Cancion();

    // Lista observable que se vincula a la tabla para actualización dinámica
    private ObservableList<CancionDTO> listaObservable;

    /**
     * Inicializa el controlador configurando la tabla, botones, búsqueda y cargando datos.
     */
    @FXML
    public void initialize() {
        System.out.println("=== INICIALIZANDO CATÁLOGO DE CANCIONES ===");
        
        // Verificar que los botones estén conectados
        System.out.println("Verificando conexión de botones...");
        if (btnEditar != null) {
            System.out.println("btnEditar conectado correctamente");
        } else {
            System.out.println("ERROR: btnEditar es null");
        }
        
        if (btnEliminar != null) {
            System.out.println("btnEliminar conectado correctamente");
        } else {
            System.out.println("ERROR: btnEliminar es null");
        }
        
        try {
            configurarTabla();
            configurarBotones();
            cargarCanciones();
            configurarBusqueda();
            System.out.println("=== INICIALIZACIÓN COMPLETADA ===");
        } catch (Exception e) {
            System.out.println("Error en inicialización: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Configura el campo de búsqueda para filtrar canciones en tiempo real.
     */
    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarCanciones(newValue);
        });
    }

    /**
     * Filtra la lista de canciones según el texto ingresado en el campo de búsqueda.
     *
     * @param filtro Texto para buscar coincidencias en los títulos de las canciones.
     */
    private void filtrarCanciones(String filtro) {
        if (filtro == null || filtro.isBlank()) {
            tableCanciones.setItems(listaObservable);
            return;
        }

        ObservableList<CancionDTO> filtrados = FXCollections.observableArrayList(
                listaObservable.stream()
                        .filter(cancion -> cancion.getTitulo().toLowerCase().contains(filtro.toLowerCase()))
                        .collect(Collectors.toList())
        );

        tableCanciones.setItems(filtrados);
    }

    /**
     * Configura las columnas de la tabla con título, imagen, géneros y duración.
     */
    private void configurarTabla() {
        System.out.println("=== CONFIGURANDO TABLA ===");
        
        // Deshabilitar reordenamiento de columnas
        tableCanciones.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colTituloConImagen.setReorderable(false);
        colGenero.setReorderable(false);
        colDuracion.setReorderable(false);
        
        // Configurar selección de filas
        tableCanciones.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        // Listener para habilitar/deshabilitar botones según selección
        tableCanciones.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean haySeleccion = newSelection != null;
            btnEditar.setDisable(!haySeleccion);
            btnEliminar.setDisable(!haySeleccion);
            
            if (haySeleccion) {
                System.out.println("Canción seleccionada: " + newSelection.getTitulo());
                System.out.println("Botones habilitados: Editar=" + !btnEditar.isDisabled() + ", Eliminar=" + !btnEliminar.isDisabled());
            } else {
                System.out.println("Ninguna canción seleccionada");
                System.out.println("Botones deshabilitados: Editar=" + btnEditar.isDisabled() + ", Eliminar=" + btnEliminar.isDisabled());
            }
        });
        
        // Columna de géneros (puede haber más de uno)
        colGenero.setCellValueFactory(cellData -> {
            List<Genero> generos = cellData.getValue().getGeneros();
            String texto = generos.stream().map(Enum::name).collect(Collectors.joining(", "));
            return SimpleStringProperty.stringExpression(Bindings.createStringBinding(() -> texto));
        });

        // Columna de duración
        colDuracion.setCellValueFactory(new PropertyValueFactory<>("duracion"));

        // Columna personalizada que muestra imagen y título de la canción
        colTituloConImagen.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));
        colTituloConImagen.setCellFactory(column -> new TableCell<>() {
            private final HBox contenedor = new HBox(10);
            private final ImageView imageView = new ImageView();
            private final Label label = new Label();

            {
                imageView.setFitHeight(40);
                imageView.setFitWidth(40);
                contenedor.getChildren().addAll(imageView, label);
            }

            @Override
            protected void updateItem(CancionDTO cancion, boolean empty) {
                super.updateItem(cancion, empty);
                if (empty || cancion == null) {
                    setGraphic(null);
                } else {
                    if (cancion.getPortada() != null) {
                        try {
                            imageView.setImage(new Image(new ByteArrayInputStream(cancion.getPortada())));
                        } catch (Exception e) {
                            imageView.setImage(loadDefaultImage());
                        }
                    } else {
                        imageView.setImage(loadDefaultImage());
                    }
                    label.setText(cancion.getTitulo());
                    setGraphic(contenedor);
                }
            }
        });
    }

    /**
     * Carga una imagen por defecto si no existe portada en la canción.
     *
     * @return Objeto Image con la imagen por defecto o null si no se encuentra.
     */
    private Image loadDefaultImage() {
        try {
            return new Image(getClass().getResourceAsStream("/UserInterface/Resources/img/CatalogoCanciones/Lupa.png"), 40, 40, true, true);
        } catch (Exception e) {
            System.out.println("Imagen por defecto no encontrada.");
            return null;
        }
    }

    /**
     * Carga las canciones desde la base de datos y las asigna a la tabla observable.
     */
    private void cargarCanciones() throws Exception {
        System.out.println("Obteniendo canciones desde la base de datos...");
        List<CancionDTO> lista = cancionBL.buscarTodo();
        System.out.println("Canciones cargadas desde DAO (" + lista.size() + " canciones):");
        lista.forEach(cancion -> System.out.println("  - " + cancion.getTitulo() + " (" + cancion.getAnio() + ")"));
        
        // Crear nueva lista observable
        listaObservable = FXCollections.observableArrayList(lista);
        
        // Actualizar la tabla
        tableCanciones.setItems(listaObservable);
        
        System.out.println("Tabla actualizada con " + listaObservable.size() + " canciones");
    }

    /**
     * Configura los botones de acción para que estén deshabilitados inicialmente.
     */
    private void configurarBotones() {
        System.out.println("=== CONFIGURANDO BOTONES DE ACCIÓN ===");
        
        // Deshabilitar botones inicialmente
        btnEditar.setDisable(true);
        btnEliminar.setDisable(true);
        
        // Configurar estilos adicionales
        btnEditar.setStyle("-fx-background-color: #9190C2; -fx-background-radius: 10; -fx-cursor: hand; -fx-text-fill: white;");
        btnEliminar.setStyle("-fx-background-color: #9190C2; -fx-background-radius: 10; -fx-cursor: hand; -fx-text-fill: white;");
        
        // Agregar tooltips
        btnEditar.setTooltip(new Tooltip("Selecciona una canción para editar"));
        btnEliminar.setTooltip(new Tooltip("Selecciona una canción para eliminar"));
        
        // Configurar focus traversable
        btnEditar.setFocusTraversable(false);
        btnEliminar.setFocusTraversable(false);
        
        System.out.println("Botones configurados - deshabilitados hasta seleccionar canción");
        System.out.println("Color de ambos botones: #9190C2 (violeta claro)");
    }

    /**
     * Carga un icono desde una ruta específica del proyecto.
     *
     * @param path Ruta del recurso.
     * @return ImageView con el icono o uno vacío si no se encontró.
     */
    private ImageView loadIcon(String path) {
        try {
            Image image = new Image(getClass().getResourceAsStream(path), 18, 18, true, true);
            if (image.isError()) {
                System.out.println("Error al cargar imagen: " + path);
                return new ImageView();
            }
            return new ImageView(image);
        } catch (Exception e) {
            System.out.println("Imagen no encontrada: " + path);
            return new ImageView();
        }
    }

    /**
     * Abre la ventana para subir una nueva canción.
     */
    private void irAPantallaSubirCancion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/CatalogoCanciones/frameNuevaCancion.fxml"));
            Parent root = loader.load();
            SubirCancionesController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Subir Canción");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            
            // Configurar el controlador para que actualice el catálogo cuando se registre una canción
            controller.setCatalogoController(this);
            
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método público para refrescar la tabla de canciones.
     * Se llama desde otros controladores cuando se registra, edita o elimina una canción.
     */
    public void refrescarTabla() {
        System.out.println("=== REFRESCANDO TABLA DE CANCIONES ===");
        
        // Asegurar que se ejecute en el hilo de la interfaz de usuario
        javafx.application.Platform.runLater(() -> {
            try {
                System.out.println("Cargando canciones desde la base de datos...");
                cargarCanciones();
                System.out.println("Tabla refrescada exitosamente");
                
                // Forzar la actualización visual de la tabla
                tableCanciones.refresh();
                
            } catch (Exception e) {
                System.out.println("Error al refrescar tabla: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Acción asociada al botón "Subir canción".
     */
    @FXML
    private void handlePantallaSubirCancion() {
        irAPantallaSubirCancion();
    }
    
    /**
     * Maneja el click en el botón "Editar Canción".
     */
    @FXML
    private void handleEditarCancion() {
        System.out.println("=== BOTÓN EDITAR CANCIÓN CLICKEADO ===");
        System.out.println("Estado del botón Editar: " + (btnEditar.isDisabled() ? "DESHABILITADO" : "HABILITADO"));
        System.out.println("Método handleEditarCancion ejecutándose...");
        
        CancionDTO cancionSeleccionada = tableCanciones.getSelectionModel().getSelectedItem();
        
        if (cancionSeleccionada != null) {
            System.out.println("Editando canción: " + cancionSeleccionada.getTitulo());
            System.out.println("Llamando a irAPantallaEditarCancion...");
            irAPantallaEditarCancion(cancionSeleccionada);
        } else {
            System.out.println("No hay canción seleccionada para editar");
        }
    }
    
    /**
     * Maneja el click en el botón "Eliminar Canción".
     */
    @FXML
    private void handleEliminarCancion() {
        System.out.println("=== BOTÓN ELIMINAR CANCIÓN CLICKEADO ===");
        System.out.println("Estado del botón Eliminar: " + (btnEliminar.isDisabled() ? "DESHABILITADO" : "HABILITADO"));
        
        CancionDTO cancionSeleccionada = tableCanciones.getSelectionModel().getSelectedItem();
        
        if (cancionSeleccionada != null) {
            System.out.println("Eliminando canción: " + cancionSeleccionada.getTitulo());
            irAPantallaEliminarCancion(cancionSeleccionada);
        } else {
            System.out.println("No hay canción seleccionada para eliminar");
        }
    }
    


    /**
     * Abre la ventana para confirmar la eliminación de una canción.
     *
     * @param cancion Canción que se desea eliminar.
     */
    private void irAPantallaEliminarCancion(CancionDTO cancion) {
        System.out.println("=== ABRIENDO VENTANA ELIMINAR CANCIÓN ===");
        System.out.println("Canción a eliminar: " + cancion.getTitulo());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/CatalogoCanciones/frameEliminarCancion.fxml"));
            Parent root = loader.load();
            EliminarCancionesController controller = loader.getController();
            controller.setCancion(cancion);
            controller.setCatalogoController(this); // Pasar referencia para actualizar el catálogo

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Eliminar Canción");
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre la ventana para editar la información de una canción existente.
     *
     * @param cancionSeleccionada Canción que será editada.
     */
    private void irAPantallaEditarCancion(CancionDTO cancionSeleccionada) {
        System.out.println("=== ABRIENDO VENTANA EDITAR CANCIÓN ===");
        System.out.println("Canción a editar: " + cancionSeleccionada.getTitulo());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/CatalogoCanciones/frameEditarCancion.fxml"));
            Parent root = loader.load();
            EditarCancionesController controller = loader.getController();
            controller.setCancion(cancionSeleccionada);
            controller.setCatalogoController(this); // Pasar referencia para actualizar el catálogo

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Editar Canción");
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
