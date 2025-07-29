package UserInterface.CustomerControl.Playlist;

import BusinessLogic.Playlist;
import BusinessLogic.PlaylistDAO;
import BusinessLogic.ServicioValidacionPlaylist;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.text.html.ImageView;

public class EditarPlaylistController {
    @FXML private TextField txtTitulo;
    @FXML private TextField txtDescripcion;
    @FXML private ComboBox<String> comboTipo;
    @FXML private ImageView imgPortada;
    @FXML private Button btnSeleccionarImagen;
    @FXML private Button btnGuardar;

    private Playlist playlistOriginal;
    private File imagenSeleccionada;
    private ServicioValidacionPlaylist validador = new ServicioValidacionPlaylist();
    private PlaylistDAO playlistDAO = new PlaylistDAO();

    public void setPlaylist(Playlist playlist) {
        this.playlistOriginal = playlist;
        cargarDatosPlaylist();
    }

    @FXML
    public void initialize() {
        comboTipo.getItems().addAll("Pública", "Privada");
        comboTipo.setValue("Pública");
        
        btnSeleccionarImagen.setOnAction(e -> seleccionarImagen());
        btnGuardar.setOnAction(e -> guardarCambios());
        
        // Configurar validación en tiempo real
        txtTitulo.textProperty().addListener((observable, oldValue, newValue) -> validarCampo());
        txtDescripcion.textProperty().addListener((observable, oldValue, newValue) -> validarCampo());
    }

    private void cargarDatosPlaylist() {
        if (playlistOriginal != null) {
            txtTitulo.setText(playlistOriginal.getTitulo());
            txtDescripcion.setText(playlistOriginal.getDescripcion());
            
            // Cargar imagen de portada si existe
            if (playlistOriginal.getImagenPortada() != null) {
                // TODO: Convertir BufferedImage a ImageView
                // Por ahora usamos imagen por defecto
            }
            
            // Validar campos iniciales
            validarCampo();
        }
    }

    private void seleccionarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen de portada");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
        
        File file = fileChooser.showOpenDialog(btnSeleccionarImagen.getScene().getWindow());
        if (file != null) {
            try {
                imagenSeleccionada = file;
                Image image = new Image(file.toURI().toString());
                imgPortada.setImage(image);
            } catch (Exception e) {
                mostrarAlerta("Error al cargar la imagen: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void validarCampo() {
        String titulo = txtTitulo.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        
        boolean tituloValido = validador.validarTitulo(titulo);
        boolean descripcionValida = validador.validarDescripcion(descripcion);
        
        // Cambiar color de fondo según validación
        txtTitulo.setStyle(tituloValido ? 
            "-fx-background-color: #575a81; -fx-background-radius: 20; -fx-text-fill: #FFFFFF;" :
            "-fx-background-color: #8B0000; -fx-background-radius: 20; -fx-text-fill: #FFFFFF;");
            
        txtDescripcion.setStyle(descripcionValida ? 
            "-fx-background-color: #575a81; -fx-background-radius: 20; -fx-text-fill: #FFFFFF;" :
            "-fx-background-color: #8B0000; -fx-background-radius: 20; -fx-text-fill: #FFFFFF;");
    }

    @FXML
    private void guardarCambios() {
        String titulo = txtTitulo.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String tipo = comboTipo.getValue();
        
        // Validar campos
        if (!validador.validarTitulo(titulo)) {
            mostrarAlerta("El título debe tener entre 2 y 100 caracteres", Alert.AlertType.WARNING);
            txtTitulo.requestFocus();
            return;
        }
        
        if (!validador.validarDescripcion(descripcion)) {
            mostrarAlerta("La descripción debe tener entre 5 y 500 caracteres", Alert.AlertType.WARNING);
            txtDescripcion.requestFocus();
            return;
        }
        
        try {
            // Actualizar datos de la playlist
            playlistOriginal.setTitulo(titulo);
            playlistOriginal.setDescripcion(descripcion);
            
            // Asignar imagen si se seleccionó una nueva
            if (imagenSeleccionada != null) {
                BufferedImage bufferedImage = ImageIO.read(imagenSeleccionada);
                playlistOriginal.setImagenPortada(bufferedImage);
            }
            
            // Actualizar en la base de datos
            playlistDAO.actualizarPlaylist(playlistOriginal);
            
            mostrarAlerta("¡Playlist actualizada exitosamente!", Alert.AlertType.INFORMATION);
            
            // Cerrar ventana
            cerrarVentana();
            
        } catch (Exception e) {
            mostrarAlerta("Error al actualizar la playlist: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnGuardar.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Editar Playlist");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
} 