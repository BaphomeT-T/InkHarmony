package UserInterface.CustomerControl.Playlist;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.application.Platform;
import BusinessLogic.Playlist;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.ArrayList;
// AGREGAR ESTAS IMPORTACIONES AL INICIO DEL ARCHIVO
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import java.io.File;

public class NuevaPlaylistController implements Initializable {
    @FXML
    private TextField txtTitulo;
    @FXML
    private TextArea txtDescripcion;
    @FXML
    private ImageView imgPortada;
    @FXML
    private Button btnCrear;
    @FXML
    private Button btnCerrar;
    @FXML
    private Button btnRegresar;
    @FXML
    private Button btnGuardarPlaylist;
    @FXML
    private Button btnSeleccionarPortada;
    @FXML
    private File imagenSeleccionada;
    @FXML
    private ImageView imgRegresar;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarEventos();
        cargarImagenPorDefecto();
    }

    // AGREGAR ESTE MÉTODO
    private void configurarEventos() {
        // Hacer que el área de imagen sea clickeable para seleccionar imagen
        imgPortada.setOnMouseClicked(event -> handleSeleccionarPortada());

        // Validación en tiempo real del título
        txtTitulo.textProperty().addListener((observable, oldValue, newValue) -> {
            validarCampos();
        });
    }

    // AGREGAR ESTE MÉTODO
    private void validarCampos() {
        boolean tituloValido = !txtTitulo.getText().trim().isEmpty();

        if (btnGuardarPlaylist != null) {
            btnGuardarPlaylist.setDisable(!tituloValido);

            if (tituloValido) {
                btnGuardarPlaylist.setStyle("-fx-background-color: #9190C2; -fx-background-radius: 20; -fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold;");
            } else {
                btnGuardarPlaylist.setStyle("-fx-background-color: #6B6B6B; -fx-background-radius: 20; -fx-text-fill: #999999; -fx-font-size: 16px; -fx-font-weight: bold;");
            }
        }
    }



    @FXML
    private void handleCerrar() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleRegresar() {
        Stage stage = (Stage) imgRegresar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCrearPlaylist() {
        // Lógica para crear la playlist
    }

    // MODIFICAR EL MÉTODO handleSeleccionarPortada()
    @FXML
    private void handleSeleccionarPortada() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen de portada");

        // Filtros para archivos de imagen
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
                "Archivos de imagen (*.jpg, *.jpeg, *.png, *.gif)",
                "*.jpg", "*.jpeg", "*.png", "*.gif"
        );
        fileChooser.getExtensionFilters().add(imageFilter);

        // Obtener la ventana actual
        Stage stage = (Stage) imgPortada.getScene().getWindow();

        // Mostrar el selector de archivos
        File archivo = fileChooser.showOpenDialog(stage);

        if (archivo != null) {
            try {
                // Cargar y mostrar la imagen seleccionada
                Image nuevaImagen = new Image(archivo.toURI().toString());
                imgPortada.setImage(nuevaImagen);
                imagenSeleccionada = archivo;

                System.out.println("Nueva imagen de portada seleccionada: " + archivo.getName());
            } catch (Exception e) {
                System.out.println("Error al cargar la imagen: " + e.getMessage());
                mostrarAlerta("Error", "No se pudo cargar la imagen seleccionada.");
            }
        }
    }

    // AGREGAR ESTE MÉTODO

    private void cargarImagenPorDefecto() {
        try {
            // Cargar ícono de cámara por defecto
            Image iconoCamara = new Image(getClass().getResourceAsStream("/UserInterface/Resources/img/CatalogoPlaylist/camara.png"));
            imgPortada.setImage(iconoCamara);
        } catch (Exception e) {
            System.out.println("No se pudo cargar el ícono de cámara por defecto");
            // Si no existe la imagen por defecto, crear una imagen simple
            imgPortada.setImage(null);
        }
    }

    // MODIFICAR EL MÉTODO handleGuardarPlaylist()
    @FXML
    private void handleGuardarPlaylist() {
        // Obtener datos del formulario
        String titulo = txtTitulo.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        // Validar campos básicos
        if (titulo.isEmpty()) {
            mostrarAlerta("Error", "Por favor, ingresa un título para la playlist");
            return;
        }

        try {
            // Usar la lógica de BusinessLogic.Playlist
            Playlist playlistLogic = new Playlist();

            // Convertir imagen a bytes si hay una seleccionada
            byte[] imagenBytes = null;
            if (imagenSeleccionada != null) {
                try {
                    java.nio.file.Path path = imagenSeleccionada.toPath();
                    imagenBytes = java.nio.file.Files.readAllBytes(path);
                    System.out.println("Imagen convertida a bytes: " + imagenBytes.length + " bytes");
                } catch (Exception e) {
                    System.out.println("Error al convertir imagen a bytes: " + e.getMessage());
                    mostrarAlerta("Advertencia", "Se creará la playlist sin imagen debido a un error al procesar la imagen seleccionada.");
                }
            }

            // Crear playlist
            boolean resultado = playlistLogic.registrar(
                    titulo,
                    descripcion.isEmpty() ? "Sin descripción" : descripcion,
                    1,  // ID del propietario (por defecto)
                    imagenBytes,  // Imagen convertida a bytes
                    new ArrayList<>()  // Lista vacía de canciones
            );

            if (resultado) {
                // Mostrar mensaje de éxito
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Éxito");
                alert.setHeaderText(null);
                alert.setContentText("Playlist creada correctamente" +
                        (imagenBytes != null ? " con imagen de portada" : ""));
                alert.showAndWait();

                // Cerrar ventana automáticamente después del mensaje
                Platform.runLater(() -> handleRegresar());
            } else {
                mostrarAlerta("Error", "No se pudo crear la playlist");
            }

        } catch (Exception e) {
            mostrarAlerta("Error", "Error al crear playlist: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * Muestra una alerta con el mensaje especificado
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void handleRegresarImagen() {
        handleRegresar(); // Reutiliza la lógica que ya tienes
    }
    
    /**
     * Limpia los campos del formulario
     */
    private void limpiarCampos() {
        txtTitulo.clear();
        txtDescripcion.clear();
    }


}
