/*
|-----------------------------------------------|
| © 2025 EPN-FIS, Todos los derechos reservados |
| GR1SW                                         |
|-----------------------------------------------|
Autor: Grupo - A
Descripción: Controlador del catálogo de canciones.
*/

package UserInterface.CustomerControl.CatalogoCanciones;


import BusinessLogic.Cancion;
import DataAccessComponent.DTO.ArtistaDTO;
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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import javafx.event.ActionEvent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.lang.Thread;

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
    @FXML
    private Button cerrarButton;

    @FXML
    private void cerrarVentana() {
        try {
            // Obtener el Stage actual a partir del botón cerrar
            Stage stage = (Stage) cerrarButton.getScene().getWindow();

            // Cargar el nuevo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/AdminUserControl/home.fxml"));
            Parent root = loader.load();

            // Crear la nueva escena con la interfaz de administración
            Scene scene = new Scene(root);

            // Cambiar la escena del Stage
            stage.setScene(scene);
            stage.setTitle("Administración de Usuarios");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private TableColumn<CancionDTO, Void> colAcciones;


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
    @FXML
    private TableColumn<CancionDTO, String> colAnio;
    @FXML
    private TableColumn<CancionDTO, String> colArtista;

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
        try {
            configurarTabla();
            cargarCanciones();
            configurarBusqueda();
        } catch (Exception e) {
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

    private void configurarTabla() {
        System.out.println("=== CONFIGURANDO TABLA ===");

        // Política de ajuste de columna
        tableCanciones.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Deshabilitar reordenamiento de columnas
        colTituloConImagen.setReorderable(false);
        colGenero.setReorderable(false);
        colDuracion.setReorderable(false);
        colAcciones.setReorderable(false);

        colArtista.setCellValueFactory(cellData -> {
            List<ArtistaDTO> artistas = cellData.getValue().getArtistas();
            String nombres = artistas.stream()
                    .map(ArtistaDTO::getNombre)
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(nombres);
        });



        // Columna de géneros
        colGenero.setCellValueFactory(cellData -> {
            List<Genero> generos = cellData.getValue().getGeneros();
            String texto = generos.stream().map(Enum::name).collect(Collectors.joining(", "));
            return SimpleStringProperty.stringExpression(Bindings.createStringBinding(() -> texto));
        });

        // Año
        colAnio.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getAnio())));

        // Duración
        colDuracion.setCellValueFactory(new PropertyValueFactory<>("duracion"));


        // Columna personalizada: imagen + título
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
        // Columna de acciones
        agregarColumnaAcciones();
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

    private void agregarColumnaAcciones() {
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar = new Button();
            private final Button btnEliminar = new Button();

            {
                btnEditar.setGraphic(loadIcon("/UserInterface/Resources/img/CatalogoCanciones/tuerca.png"));
                btnEliminar.setGraphic(loadIcon("/UserInterface/Resources/img/CatalogoCanciones/tacho.png"));

                btnEditar.setStyle("-fx-background-color: transparent;");
                btnEliminar.setStyle("-fx-background-color: transparent;");

                btnEditar.setOnAction(event -> {
                    CancionDTO cancion = getTableView().getItems().get(getIndex());
                    irAPantallaEditarCancion(cancion);
                });

                btnEliminar.setOnAction(event -> {
                    CancionDTO cancion = getTableView().getItems().get(getIndex());
                    irAPantallaEliminarCancion(cancion);
                });
            }

            private final HBox contenedor = new HBox(10, btnEditar, btnEliminar);

            {
                contenedor.setPadding(new Insets(5));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : contenedor);
            }
        });
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
            stage.setMaximized(false);
            
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
        try {
            txtBuscar.clear();
            cargarCanciones();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Acción asociada al botón "Subir canción".
     */
    @FXML
    private void handlePantallaSubirCancion() {
        irAPantallaSubirCancion();
    }
    



    /**
     * Abre la ventana para confirmar la eliminación de una canción.
     *
     * @param cancion Canción que se desea eliminar.
     */
/*    private void irAPantallaEliminarCancion(CancionDTO cancion) {
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
    }*/

    private void irAPantallaEliminarCancion(CancionDTO cancion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/CatalogoCanciones/frameEliminarCancion.fxml"));
            Parent root = loader.load();

            EliminarCancionesController controller = loader.getController();
            controller.setCancion(cancion);
            controller.setCatalogoController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Eliminar Canción");
            stage.setMaximized(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void irAPantallaEditarCancion(CancionDTO cancion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/CatalogoCanciones/frameEditarCancion.fxml"));
            Parent root = loader.load();

            EditarCancionesController controller = loader.getController();
            controller.setCancion(cancion);
            controller.setCatalogoController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Editar Canción");
            stage.setMaximized(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
