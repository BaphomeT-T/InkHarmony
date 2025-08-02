package UserInterface.CustomerControl.CatalagoPlaylist;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class EditarPlaylistController implements Initializable {

    @FXML
    private TextField txtTitulo;

    @FXML
    private TextArea txtDescripcion;

    @FXML
    private ImageView imgPortada;

    @FXML
    private Button btnRegresar;

    @FXML
    private Button btnCerrar;

    @FXML
    private Button btnActualizarPlaylist;

    private File imagenSeleccionada;
    private String tituloOriginal;
    private String descripcionOriginal;
    private String rutaImagenOriginal;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarEventos();
    }

    private void configurarEventos() {
        // Validación en tiempo real de los campos
        txtTitulo.textProperty().addListener((observable, oldValue, newValue) -> {
            validarCampos();
        });

        txtDescripcion.textProperty().addListener((observable, oldValue, newValue) -> {
            validarCampos();
        });

        // Hacer que el área de imagen sea clickeable para seleccionar imagen
        imgPortada.setOnMouseClicked(event -> handleSeleccionarPortada());
    }

    private void validarCampos() {
        // Habilitar/deshabilitar el botón según si hay título
        boolean tituloValido = !txtTitulo.getText().trim().isEmpty();
        btnActualizarPlaylist.setDisable(!tituloValido);

        // Cambiar el texto del botón según el estado
        if (tituloValido) {
            btnActualizarPlaylist.setText("Actualizar");
            btnActualizarPlaylist.setStyle("-fx-background-color: #9190C2; -fx-background-radius: 20; -fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold;");
        } else {
            btnActualizarPlaylist.setText("Actualizar");
            btnActualizarPlaylist.setStyle("-fx-background-color: #6B6B6B; -fx-background-radius: 20; -fx-text-fill: #999999; -fx-font-size: 16px; -fx-font-weight: bold;");
        }
    }

    @FXML
    private void handleRegresar() {
        cerrarVentana();
    }

    @FXML
    private void handleCerrar() {
        cerrarVentana();
    }

    @FXML
    private void handleSeleccionarPortada() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar nueva imagen de portada");

        // Filtros para archivos de imagen
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
                "Archivos de imagen", ".jpg", ".jpeg", ".png", ".gif"
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

    @FXML
    private void handleActualizarPlaylist() {
        String titulo = txtTitulo.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        // Validación básica
        if (titulo.isEmpty()) {
            mostrarAlerta("Error", "Por favor, ingresa un título para la playlist.");
            return;
        }

        // Aquí iría la lógica para actualizar en la base de datos
        System.out.println("=== ACTUALIZANDO PLAYLIST ===");
        System.out.println("Título original: " + tituloOriginal + " -> Nuevo: " + titulo);
        System.out.println("Descripción original: " + (descripcionOriginal == null || descripcionOriginal.isEmpty() ? "Sin descripción" : descripcionOriginal)
                + " -> Nueva: " + (descripcion.isEmpty() ? "Sin descripción" : descripcion));

        if (imagenSeleccionada != null) {
            System.out.println("Nueva imagen: " + imagenSeleccionada.getName());
        } else {
            System.out.println("Manteniendo imagen original");
        }

        // Mostrar mensaje de éxito
        mostrarAlerta("Éxito", "Playlist '" + titulo + "' actualizada exitosamente.");

        // Cerrar ventana después de actualizar
        cerrarVentana();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnRegresar.getScene().getWindow();
        stage.close();
    }

    // Método público para cargar los datos existentes de la playlist
    public void cargarDatosPlaylist(String titulo, String descripcion, String rutaImagen) {
        // Guardar datos originales
        this.tituloOriginal = titulo;
        this.descripcionOriginal = descripcion;
        this.rutaImagenOriginal = rutaImagen;

        // Cargar datos en los campos
        if (titulo != null) {
            txtTitulo.setText(titulo);
        }

        if (descripcion != null) {
            txtDescripcion.setText(descripcion);
        }

        // Cargar imagen existente
        cargarImagenExistente(rutaImagen);

        // Validar campos después de cargar datos
        validarCampos();
    }

    private void cargarImagenExistente(String rutaImagen) {
        try {
            if (rutaImagen != null && !rutaImagen.isEmpty()) {
                // Si hay una ruta de imagen, cargarla
                Image imagenExistente;

                // Verificar si es una ruta de archivo local o un recurso
                if (rutaImagen.startsWith("file:") || new File(rutaImagen).exists()) {
                    imagenExistente = new Image(rutaImagen);
                } else {
                    // Asumir que es un recurso en el classpath
                    imagenExistente = new Image(getClass().getResourceAsStream(rutaImagen));
                }

                imgPortada.setImage(imagenExistente);
                System.out.println("Imagen de playlist cargada: " + rutaImagen);

            } else {
                // Si no hay imagen, cargar imagen por defecto
                cargarImagenPorDefecto();
            }
        } catch (Exception e) {
            System.out.println("Error al cargar imagen existente: " + e.getMessage());
            cargarImagenPorDefecto();
        }
    }

    private void cargarImagenPorDefecto() {
        try {
            // Cargar ícono de cámara por defecto
            Image iconoCamara = new Image(getClass().getResourceAsStream("/UserInterface/Resources/img/CatalogoPlaylist/camara.png"));
            imgPortada.setImage(iconoCamara);
        } catch (Exception e) {
            System.out.println("No se pudo cargar el ícono de cámara por defecto");
        }
    }

    // Método para obtener información sobre cambios realizados
    public boolean huboCambios() {
        String tituloActual = txtTitulo.getText().trim();
        String descripcionActual = txtDescripcion.getText().trim();

        boolean cambioTitulo = !tituloActual.equals(tituloOriginal != null ? tituloOriginal : "");
        boolean cambioDescripcion = !descripcionActual.equals(descripcionOriginal != null ? descripcionOriginal : "");
        boolean cambioImagen = imagenSeleccionada != null;

        return cambioTitulo || cambioDescripcion || cambioImagen;
    }
}