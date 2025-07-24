/*
|-----------------------------------------------|
| © 2025 EPN-FIS, Todos los derechos reservados |
| GR1SW                                         |
|-----------------------------------------------|
Autor: Sergio Rodríguez
Descripción: Controlador del catálogo de canciones.
*/

package UserInterface.CustomerControl.CatalogoCanciones;

// Importación de clases necesarias de las capas de lógica y datos
import BusinessLogic.Cancion;
import DataAccessComponent.DTO.CancionDTO;
import BusinessLogic.Genero;

// Importación de clases de JavaFX para interfaz gráfica
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

// Clase principal del controlador del catálogo de canciones
public class CatalogoCancionesController {

    // Referencia a la tabla que muestra las canciones
    @FXML
    private TableView<CancionDTO> tableCanciones;

    // Columnas de la tabla
    @FXML
    private TableColumn<CancionDTO, String> colGenero;

    @FXML
    private TableColumn<CancionDTO, String> colDuracion;

    @FXML
    private TableColumn<CancionDTO, Void> colAcciones;

    @FXML
    private TextField txtBuscar; // Campo de búsqueda

    // Instancia de la capa de lógica para obtener canciones
    private Cancion cancionBL = new Cancion();

    // Lista observable para cargar la tabla
    private ObservableList<CancionDTO> listaObservable;

    @FXML
    private TableColumn<CancionDTO, CancionDTO> colTituloConImagen; // Columna con imagen y título

    // Método que se ejecuta al iniciar el controlador
    @FXML
    public void initialize() {
        try {
            configurarTabla();      // Configura columnas de la tabla
            cargarCanciones();      // Carga canciones desde la base de datos
            configurarBusqueda();   // Prepara el filtro de búsqueda
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Configura el listener para el campo de búsqueda
    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarCanciones(newValue);
        });
    }

    // Filtra las canciones según el texto ingresado
    private void filtrarCanciones(String filtro) {
        if (filtro == null || filtro.isBlank()) {
            tableCanciones.setItems(listaObservable);
            agregarColumnaAcciones(); // Se asegura de mostrar los botones de acción
            return;
        }

        // Aplica filtro por título de la canción
        ObservableList<CancionDTO> filtrados = FXCollections.observableArrayList(
                listaObservable.stream()
                        .filter(cancion -> cancion.getTitulo().toLowerCase().contains(filtro.toLowerCase()))
                        .collect(Collectors.toList())
        );

        tableCanciones.setItems(filtrados);
        agregarColumnaAcciones();
    }

    // Configura las columnas de la tabla
    private void configurarTabla() {
        // Columna de géneros (pueden ser varios)
        colGenero.setCellValueFactory(cellData -> {
            List<Genero> generos = cellData.getValue().getGenero();
            String texto = generos.stream().map(Enum::name).collect(Collectors.joining(", "));
            return SimpleStringProperty.stringExpression(
                    Bindings.createStringBinding(() -> texto));
        });

        // Columna de duración
        colDuracion.setCellValueFactory(new PropertyValueFactory<>("duracion"));

        // Columna personalizada que incluye imagen y título de la canción
        colTituloConImagen.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));
        colTituloConImagen.setCellFactory(column -> new TableCell<>() {
            private final HBox contenedor = new HBox(10); // Contenedor con espacio entre elementos
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
                    if (cancion.getImagen() != null) {
                        try {
                            imageView.setImage(new Image(new ByteArrayInputStream(cancion.getImagen())));
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

        agregarColumnaAcciones();
    }

    // Carga una imagen por defecto si no hay imagen en la canción
    private Image loadDefaultImage() {
        try {
            return new Image(getClass().getResourceAsStream("/UserInterface/Resources/img/CatalogoCanciones/Lupa.png"), 40, 40, true, true);
        } catch (Exception e) {
            System.out.println("Imagen por defecto no encontrada.");
            return null;
        }
    }

    // Carga las canciones desde la base de datos usando la lógica de negocio
    private void cargarCanciones() throws Exception {
        List<CancionDTO> lista = cancionBL.buscarTodo(); // Consulta todas las canciones
        System.out.println("Canciones cargadas desde DAO:");
        lista.forEach(System.out::println); // Log de depuración
        listaObservable = FXCollections.observableArrayList(lista);
        tableCanciones.setItems(listaObservable);
    }

    // Agrega botones de acción (editar y eliminar) por cada fila de la tabla
    private void agregarColumnaAcciones() {
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar = new Button();
            private final Button btnEliminar = new Button();

            {
                // Se asignan íconos a los botones
                btnEditar.setGraphic(loadIcon("/UserInterface/Resources/img/CatalogoCanciones/subir.png"));
                btnEliminar.setGraphic(loadIcon("/UserInterface/Resources/img/CatalogoCanciones/camara.png"));

                btnEditar.setStyle("-fx-background-color: transparent;");
                btnEliminar.setStyle("-fx-background-color: transparent;");

                // Acción del botón Editar
                btnEditar.setOnAction(event -> {
                    CancionDTO cancion = getTableView().getItems().get(getIndex());
                    irAPantallaEditarCancion(cancion);
                });

                // Acción del botón Eliminar
                btnEliminar.setOnAction(event -> {
                    CancionDTO cancion = getTableView().getItems().get(getIndex());
                    irAPantallaEliminarCancion(cancion);
                });
            }

            private final HBox contenedor = new HBox(10, btnEditar, btnEliminar); // Contenedor de botones
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

    // Carga un ícono desde una ruta del proyecto
    private ImageView loadIcon(String path) {
        try {
            return new ImageView(new Image(getClass().getResourceAsStream(path), 18, 18, true, true));
        } catch (Exception e) {
            System.out.println("Imagen no encontrada: " + path);
            return new ImageView();
        }
    }

    // Abre la ventana para subir una nueva canción
    private void irAPantallaSubirCancion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/CatalogoCanciones/frameNuevaCancion.fxml"));
            Parent root = loader.load();

            SubirCancionesController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Subir Canción");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método ligado al botón "Subir canción" desde la vista
    @FXML
    private void handlePantallaSubirCancion() {
        irAPantallaSubirCancion();
    }

    // Abre la ventana para eliminar una canción
    private void irAPantallaEliminarCancion(CancionDTO cancion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/CatalogoCanciones/frameEliminarCancion.fxml"));
            Parent root = loader.load();

            EliminarCancionesController controller = loader.getController();
            controller.setCancion(cancion);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Eliminar Canción");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Abre la ventana para editar los datos de una canción seleccionada
    private void irAPantallaEditarCancion(CancionDTO cancionSeleccionada) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/CatalogoCanciones/frameEditarCancion.fxml"));
            Parent root = loader.load();

            EditarCancionesController controller = loader.getController();
            controller.setCancion(cancionSeleccionada);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Editar Canción");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
