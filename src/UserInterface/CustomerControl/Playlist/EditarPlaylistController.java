package UserInterface.CustomerControl.Playlist;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.application.Platform;
import BusinessLogic.Playlist;
import DataAccessComponent.DTO.PlaylistDTO;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.io.ByteArrayInputStream;

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
    private PlaylistDTO playlistActual;
    @FXML
    private ImageView imgRegresar;

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

    // MEJORAR EL MÉTODO validarCampos() para considerar cambios de imagen
    private void validarCampos() {
        // Habilitar/deshabilitar el botón según si hay título
        boolean tituloValido = !txtTitulo.getText().trim().isEmpty();
        btnActualizarPlaylist.setDisable(!tituloValido);

        // Verificar si hay cambios
        boolean hayCambios = huboCambios();

        // Cambiar el texto del botón según el estado
        if (tituloValido) {
            if (hayCambios) {
                btnActualizarPlaylist.setText("Guardar Cambios");
                btnActualizarPlaylist.setStyle("-fx-background-color: #9190C2; -fx-background-radius: 20; -fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold;");
            } else {
                btnActualizarPlaylist.setText("Sin Cambios");
                btnActualizarPlaylist.setStyle("-fx-background-color: #4CAF50; -fx-background-radius: 20; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
            }
        } else {
            btnActualizarPlaylist.setText("Título Requerido");
            btnActualizarPlaylist.setStyle("-fx-background-color: #6B6B6B; -fx-background-radius: 20; -fx-text-fill: #999999; -fx-font-size: 16px; -fx-font-weight: bold;");
        }
    }

    @FXML
    private void handleRegresar() {
        cerrarVentana();
    }
    @FXML
    private void handleRegresarImagen() {
        handleRegresar(); // Reutiliza la lógica que ya tienes
    }

    @FXML
    private void handleCerrar() {
        cerrarVentana();
    }

    // MODIFICAR EL MÉTODO handleSeleccionarPortada() para mejorar la experiencia
    @FXML
    private void handleSeleccionarPortada() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar nueva imagen de portada");

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
                // Validar que el archivo sea una imagen válida
                Image nuevaImagen = new Image(archivo.toURI().toString());

                // Verificar que la imagen se cargó correctamente
                if (nuevaImagen.isError()) {
                    mostrarAlerta("Error", "El archivo seleccionado no es una imagen válida.");
                    return;
                }

                // Verificar el tamaño del archivo (opcional: limitar a 5MB)
                long tamañoEnBytes = archivo.length();
                long tamañoMaximo = 5 * 1024 * 1024; // 5MB

                if (tamañoEnBytes > tamañoMaximo) {
                    mostrarAlerta("Error", "La imagen es demasiado grande. Por favor, selecciona una imagen menor a 5MB.");
                    return;
                }

                imgPortada.setImage(nuevaImagen);
                imagenSeleccionada = archivo;

                System.out.println("Nueva imagen de portada seleccionada: " + archivo.getName());
                System.out.println("Tamaño: " + (tamañoEnBytes / 1024) + " KB");

                // Validar campos para habilitar el botón de guardar
                validarCampos();

            } catch (Exception e) {
                System.out.println("Error al cargar la imagen: " + e.getMessage());
                mostrarAlerta("Error", "No se pudo cargar la imagen seleccionada: " + e.getMessage());
            }
        }
    }

    // MEJORAR EL MÉTODO handleActualizarPlaylist() con mejor manejo de errores
    @FXML
    private void handleActualizarPlaylist() {
        String titulo = txtTitulo.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        System.out.println("=== INICIANDO ACTUALIZACIÓN DE PLAYLIST ===");
        System.out.println("Título: " + titulo);
        System.out.println("Descripción: " + descripcion);
        System.out.println("Nueva imagen seleccionada: " + (imagenSeleccionada != null ? "Sí" : "No"));

        // Validación básica
        if (titulo.isEmpty()) {
            mostrarAlerta("Error", "Por favor, ingresa un título para la playlist.");
            return;
        }

        if (playlistActual == null) {
            mostrarAlerta("Error", "No hay playlist cargada para editar.");
            System.out.println("ERROR: playlistActual es null");
            return;
        }

        // Verificar si realmente hay cambios
        if (!huboCambios()) {
            mostrarAlerta("Información", "No se han realizado cambios en la playlist.");
            return;
        }

        System.out.println("Playlist actual ID: " + playlistActual.getIdPlaylist());
        System.out.println("Título original: " + playlistActual.getTituloPlaylist());

        try {
            // Usar BusinessLogic.Playlist para actualizar
            Playlist playlistLogic = new Playlist();

            // Actualizar los datos en el DTO
            playlistActual.setTituloPlaylist(titulo);
            playlistActual.setDescripcion(descripcion.isEmpty() ? null : descripcion);

            // Si hay nueva imagen, convertirla a bytes
            if (imagenSeleccionada != null) {
                System.out.println("Procesando nueva imagen: " + imagenSeleccionada.getName());
                try {
                    // Leer archivo de imagen y convertir a bytes
                    java.nio.file.Path path = imagenSeleccionada.toPath();
                    byte[] imagenBytes = java.nio.file.Files.readAllBytes(path);
                    playlistActual.setImagenPortada(imagenBytes);
                    System.out.println("Imagen convertida a bytes: " + imagenBytes.length + " bytes");
                } catch (Exception e) {
                    System.out.println("Error al procesar imagen: " + e.getMessage());
                    mostrarAlerta("Advertencia", "Se actualizará la playlist sin cambiar la imagen debido a un error al procesarla.");
                }
            } else {
                System.out.println("No hay nueva imagen seleccionada, manteniendo imagen actual");
            }

            System.out.println("Llamando a playlistLogic.actualizar()...");
            // Actualizar en la base de datos usando BusinessLogic
            boolean resultado = playlistLogic.actualizar(playlistActual);

            System.out.println("Resultado de actualización: " + resultado);

            if (resultado) {
                // Mostrar mensaje de éxito
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Éxito");
                alert.setHeaderText(null);

                String mensaje = "Playlist actualizada correctamente";
                if (imagenSeleccionada != null) {
                    mensaje += " con nueva imagen de portada";
                }

                alert.setContentText(mensaje);
                alert.showAndWait();

                System.out.println("Playlist actualizada exitosamente, cerrando ventana...");
                // Cerrar ventana automáticamente después del mensaje
                Platform.runLater(() -> cerrarVentana());
            } else {
                mostrarAlerta("Error", "No se pudo actualizar la playlist.");
                System.out.println("ERROR: No se pudo actualizar la playlist");
            }

        } catch (Exception e) {
            System.out.println("EXCEPCIÓN durante actualización: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "Error al actualizar playlist: " + e.getMessage());
        }
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

    // MEJORAR EL MÉTODO cargarImagenPorDefecto()
    private void cargarImagenPorDefecto() {
        try {
            // Intentar cargar ícono de cámara por defecto
            Image iconoCamara = new Image(getClass().getResourceAsStream("/UserInterface/Resources/img/CatalogoPlaylist/camara.png"));
            imgPortada.setImage(iconoCamara);
            System.out.println("Imagen por defecto (cámara) cargada");
        } catch (Exception e) {
            try {
                // Si no existe el ícono de cámara, intentar cargar imagen por defecto de playlist
                Image imagenDefecto = new Image(getClass().getResourceAsStream("/UserInterface/Resources/img/CatalogoPlaylist/simbolo-aplicacion.png"));
                imgPortada.setImage(imagenDefecto);
                System.out.println("Imagen por defecto (símbolo aplicación) cargada");
            } catch (Exception e2) {
                System.out.println("No se pudo cargar ninguna imagen por defecto");
                imgPortada.setImage(null);
            }
        }
    }

    // AGREGAR MÉTODO PARA RESTABLECER IMAGEN ORIGINAL
    @FXML
    private void handleRestablecerImagen() {
        if (playlistActual != null && playlistActual.getImagenPortada() != null) {
            try {
                // Restablecer a la imagen original de la playlist
                ByteArrayInputStream bis = new ByteArrayInputStream(playlistActual.getImagenPortada());
                Image imagenOriginal = new Image(bis);
                imgPortada.setImage(imagenOriginal);

                // Limpiar la selección de nueva imagen
                imagenSeleccionada = null;

                System.out.println("Imagen restablecida a la original");
            } catch (Exception e) {
                System.out.println("Error al restablecer imagen original: " + e.getMessage());
                cargarImagenPorDefecto();
            }
        } else {
            // Si no hay imagen original, cargar imagen por defecto
            cargarImagenPorDefecto();
            imagenSeleccionada = null;
        }
    }

    // MEJORAR EL MÉTODO huboCambios() para incluir imagen
    public boolean huboCambios() {
        String tituloActual = txtTitulo.getText().trim();
        String descripcionActual = txtDescripcion.getText().trim();

        boolean cambioTitulo = !tituloActual.equals(tituloOriginal != null ? tituloOriginal : "");
        boolean cambioDescripcion = !descripcionActual.equals(descripcionOriginal != null ? descripcionOriginal : "");
        boolean cambioImagen = imagenSeleccionada != null;

        System.out.println("=== VERIFICANDO CAMBIOS ===");
        System.out.println("Cambio título: " + cambioTitulo);
        System.out.println("Cambio descripción: " + cambioDescripcion);
        System.out.println("Cambio imagen: " + cambioImagen);

        return cambioTitulo || cambioDescripcion || cambioImagen;
    }
    /**
     * Método para establecer la playlist a editar
     * @param playlist PlaylistDTO con los datos de la playlist
     */
    public void setPlaylist(PlaylistDTO playlist) {
        System.out.println("=== CARGANDO PLAYLIST PARA EDITAR ===");
        this.playlistActual = playlist;
        
        if (playlist != null) {
            System.out.println("Playlist ID: " + playlist.getIdPlaylist());
            System.out.println("Título: " + playlist.getTituloPlaylist());
            System.out.println("Descripción: " + (playlist.getDescripcion() != null ? playlist.getDescripcion() : "null"));
            System.out.println("Tiene imagen: " + (playlist.getImagenPortada() != null ? "Sí (" + playlist.getImagenPortada().length + " bytes)" : "No"));
            
            // Cargar datos de la playlist en los controles
            txtTitulo.setText(playlist.getTituloPlaylist());
            txtDescripcion.setText(playlist.getDescripcion() != null ? playlist.getDescripcion() : "");
            
            // Guardar valores originales
            tituloOriginal = playlist.getTituloPlaylist();
            descripcionOriginal = playlist.getDescripcion();
            
            // Cargar imagen si existe
            if (playlist.getImagenPortada() != null) {
                try {
                    // Convertir bytes a imagen
                    ByteArrayInputStream bis = new ByteArrayInputStream(playlist.getImagenPortada());
                    Image imagen = new Image(bis);
                    imgPortada.setImage(imagen);
                    System.out.println("Imagen de portada cargada exitosamente");
                } catch (Exception e) {
                    System.out.println("Error al cargar imagen de portada: " + e.getMessage());
                    cargarImagenPorDefecto();
                }
            } else {
                System.out.println("No hay imagen de portada, cargando imagen por defecto");
                cargarImagenPorDefecto();
            }
            
            // Validar campos después de cargar
            validarCampos();
            System.out.println("Playlist cargada completamente para edición");
        } else {
            System.out.println("ERROR: Playlist recibida es null");
        }
    }
}