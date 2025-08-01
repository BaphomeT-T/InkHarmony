package UserInterface.CustomerControl.CatalogoCanciones;

import BusinessLogic.Cancion;
import BusinessLogic.Genero;
import BusinessLogic.ServicioValidacionCancion;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

public class SubirCancionesController {
    // Componentes de la interfaz gráfica
    @FXML private ImageView caratulaImageView;
    @FXML private Label letraLabel;
    @FXML private TextArea letraTextArea;
    @FXML private Button cerrarButton;
    @FXML private MenuButton generoMenuButton;
    @FXML private Label generoLabel;
    @FXML private Label tituloLabel;
    @FXML private TextField tituloTextField;
    @FXML private TextField artistaTextField;
    @FXML private TextField anioTextField;
    @FXML private TextField duracionTextField;
    @FXML private Label mensajeTituloLabel;
    @FXML private Button publicarButton;
    @FXML private Button buscarArtistaButton;
    @FXML private Button seleccionarArchivoButton;
    @FXML private Label seleccionarLabel;

    private List<Genero> generosSeleccionados = new ArrayList<>(); // Lista de géneros seleccionados
    
    // Referencia al controlador del catálogo para actualizar la tabla
    private CatalogoCancionesController catalogoController;

    // Método para manejar la publicación de la canción
    @FXML
    void publicar(ActionEvent event) {
        // Obtener los valores de los campos
        String titulo = this.tituloTextField.getText();
        String artista = this.artistaTextField.getText();
        String anio = this.anioTextField.getText();
        String duracion = this.duracionTextField.getText();
        String letra = this.letraTextArea.getText();

        // Validar que todos los campos estén completos
        if (!titulo.isBlank() && !artista.isBlank() && !anio.isBlank() && !duracion.isBlank() &&
                !letra.isBlank() && this.caratulaImageView.getImage() != null) {

            // Obtener los géneros seleccionados del menú
            List<Genero> generosSeleccionados = this.generoMenuButton.getItems().stream()
                    .filter(item -> item instanceof CheckMenuItem && ((CheckMenuItem)item).isSelected())
                    .map(item -> (Genero)item.getUserData())
                    .toList();

            // Validar que al menos un género esté seleccionado
            if (generosSeleccionados.isEmpty()) {
                this.mostrarAlerta("Debes seleccionar al menos un género musical.");
                return;
            }

            byte[] imagenBytes = null;

            try {
                // Convertir la imagen a bytes para almacenamiento
                URI uri = new URI(this.caratulaImageView.getImage().getUrl());
                Path path = Paths.get(uri);
                imagenBytes = Files.readAllBytes(path);
            } catch (Exception e) {
                this.mostrarAlerta("Error al procesar la carátula.");
                return;
            }

            // Validar que el título sea único
            ServicioValidacionCancion validador = new ServicioValidacionCancion();
            if (!validador.validarTitulo(titulo)) {
                this.mostrarAlerta("El título de la canción ya existe.");
            } else {
                try {
                    // Registrar la canción en el sistema
                    Cancion cancionLogic = new Cancion();
                    boolean exito = cancionLogic.registrar(
                            titulo,
                            anio,
                            duracion,
                            generosSeleccionados,
                            letra,
                            imagenBytes
                    );

                    if (exito) {
                        this.mostrarExito("Canción subida con éxito.");
                        
                        // Actualizar el catálogo si existe la referencia
                        if (this.catalogoController != null) {
                            this.catalogoController.refrescarTabla();
                        }
                        
                        this.cerrarVentana(); // Cerrar la ventana después de subir exitosamente
                    } else {
                        this.mostrarAlerta("No se pudo subir la canción.");
                    }
                } catch (Exception e) {
                    this.mostrarAlerta("Error al subir la canción: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            this.mostrarAlerta("Completa todos los campos antes de publicar.");
        }
    }

    // Muestra un mensaje de éxito
    private void mostrarExito(String mensaje) {
        Alert alerta = new Alert(AlertType.INFORMATION);
        alerta.setTitle("Éxito");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // Limpia todos los campos del formulario
    private void limpiarCampos() {
        this.tituloTextField.clear();
        this.artistaTextField.clear();
        this.anioTextField.clear();
        this.duracionTextField.clear();
        this.letraTextArea.clear();
        // Restablecer la imagen por defecto
        this.caratulaImageView.setImage(new Image(
                Objects.requireNonNull(this.getClass().getResourceAsStream(
                        "/UserInterface/Resources/img/CatalogoCanciones/caratula_default.png"
                ))
        ));
        this.mensajeTituloLabel.setText("");
        this.generoMenuButton.setText("Seleccione");

        // Desmarcar todos los géneros seleccionados
        for(MenuItem item : this.generoMenuButton.getItems()) {
            if (item instanceof CheckMenuItem checkItem) {
                checkItem.setSelected(false);
            }
        }
    }

    // Método para seleccionar una carátula desde el sistema de archivos
    @FXML
    public void seleccionarCaratula(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar carátula de la canción");
        // Filtro para mostrar solo imágenes
        FileChooser.ExtensionFilter extFilterImagenes = new FileChooser.ExtensionFilter(
                "Imágenes (*.png, *.jpg, *.jpeg)",
                new String[]{"*.png", "*.jpg", "*.jpeg"}
        );
        fileChooser.getExtensionFilters().add(extFilterImagenes);
        File archivoSeleccionado = fileChooser.showOpenDialog(null);

        if (archivoSeleccionado != null) {
            try {
                // Cargar la imagen seleccionada
                Image imagen = new Image(archivoSeleccionado.toURI().toString());
                // Validar las dimensiones de la imagen
                if (imagen.getWidth() == 264.0 && imagen.getHeight() == 264.0) {
                    this.caratulaImageView.setImage(imagen);
                                    System.out.println("Caratula cargada correctamente: " +
                            archivoSeleccionado.getAbsolutePath());
                } else {
                    this.mostrarAlerta("La carátula debe tener exactamente 264x264 píxeles.");
                }
            } catch (Exception e) {
                this.mostrarAlerta("Error al cargar la carátula.");
            }
        }
    }

    // Muestra una alerta con el mensaje especificado
    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(AlertType.WARNING);
        alerta.setTitle("Advertencia");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // Método para cerrar la ventana actual
    @FXML
    void cerrarVentana() {
        Stage stage = (Stage)this.cerrarButton.getScene().getWindow();
        stage.close();
    }

    // Método para buscar artista
    @FXML
    void buscarArtista(ActionEvent event) {
        this.mostrarAlerta("Funcionalidad de búsqueda de artistas no implementada aún.");
    }

    // Método para seleccionar archivo de audio
    @FXML
    void seleccionarArchivo(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo de audio");
        // Filtro para mostrar solo archivos de audio
        FileChooser.ExtensionFilter extFilterAudio = new FileChooser.ExtensionFilter(
                "Archivos de audio (*.mp3, *.wav, *.flac)",
                new String[]{"*.mp3", "*.wav", "*.flac"}
        );
        fileChooser.getExtensionFilters().add(extFilterAudio);
        File archivoSeleccionado = fileChooser.showOpenDialog(null);

        if (archivoSeleccionado != null) {
            System.out.println("Archivo de audio seleccionado: " + archivoSeleccionado.getAbsolutePath());
            this.mostrarExito("Archivo de audio seleccionado: " + archivoSeleccionado.getName());
        }
    }

    // Método de inicialización del controlador
    @FXML
    public void initialize() {
        // Configurar los géneros musicales en el menú
        for(Genero genero : Genero.values()) {
            CheckMenuItem item = new CheckMenuItem(this.formatearGenero(genero));
            item.setUserData(genero);
            item.setOnAction(e -> {
                e.consume();
                this.actualizarTextoMenuButton(); // Actualizar el texto del botón al seleccionar
            });
            this.generoMenuButton.getItems().add(item);
        }

        // Listener para validar el título en tiempo real
        this.tituloTextField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText != null && !newText.trim().isEmpty()) {
                ServicioValidacionCancion servicioValidacionCancion = new ServicioValidacionCancion();
                boolean esUnico = servicioValidacionCancion.validarTitulo(newText);
                if (!esUnico) {
                    this.mensajeTituloLabel.setText("El título de la canción ya está en uso");
                    this.mensajeTituloLabel.setStyle("-fx-text-fill: red;");
                } else {
                    this.mensajeTituloLabel.setText("");
                }
            } else {
                this.mensajeTituloLabel.setText("");
            }
        });

        // Configurar recorte circular para la imagen de la carátula
        Rectangle clip = new Rectangle(306.0, 264.0);
        clip.setArcWidth(30.0);
        clip.setArcHeight(30.0);
        this.caratulaImageView.setClip(clip);
    }

    // Actualiza el texto del botón de géneros con los seleccionados
    private void actualizarTextoMenuButton() {
        List<String> seleccionados = this.generoMenuButton.getItems().stream()
                .filter(item -> item instanceof CheckMenuItem && ((CheckMenuItem)item).isSelected())
                .map(item -> ((CheckMenuItem)item).getText())
                .toList();

        this.generoMenuButton.setText(seleccionados.isEmpty() ? "Seleccione" : String.join(", ", seleccionados));
    }

    // Formatea el nombre del género para mostrarlo correctamente
    private String formatearGenero(Genero genero) {
        String nombre = genero.name().replace('_', ' ').toLowerCase();
        String[] palabras = nombre.split(" ");
        StringBuilder resultado = new StringBuilder();

        for(String palabra : palabras) {
            if (!palabra.isEmpty()) {
                // Capitalizar la primera letra de cada palabra
                resultado.append(Character.toUpperCase(palabra.charAt(0)))
                        .append(palabra.substring(1))
                        .append(" ");
            }
        }

        return resultado.toString().trim();
    }
    
    /**
     * Establece la referencia al controlador del catálogo para poder actualizar la tabla
     * cuando se registre una nueva canción.
     * 
     * @param catalogoController Referencia al controlador del catálogo
     */
    public void setCatalogoController(CatalogoCancionesController catalogoController) {
        this.catalogoController = catalogoController;
    }
}