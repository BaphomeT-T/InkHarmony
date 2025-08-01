/*
package UserInterface.CustomerControl.Playlist;

import BusinessLogic.Playlist;
import BusinessLogic.PlaylistDAO;
import BusinessLogic.ServicioValidacionPlaylist;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.text.html.ImageView;

public class NuevaPlaylistController {
    @FXML private TextField txtTitulo;
    @FXML private TextField txtDescripcion;
    @FXML private ComboBox<String> comboTipo;
    @FXML private ImageView imgPortada;
    @FXML private Button btnSeleccionarImagen;
    @FXML private Button btnCrear;

    private File imagenSeleccionada;
    private ServicioValidacionPlaylist validador = new ServicioValidacionPlaylist();
    private PlaylistDAO playlistDAO = new PlaylistDAO();

    @FXML
    public void initialize() {
        comboTipo.getItems().addAll("Pública", "Privada");
        comboTipo.setValue("Pública"); // Valor por defecto
        
        btnSeleccionarImagen.setOnAction(e -> seleccionarImagen());
        btnCrear.setOnAction(e -> crearPlaylist());
        
        // Configurar validación en tiempo real
        txtTitulo.textProperty().addListener((observable, oldValue, newValue) -> validarCampo());
        txtDescripcion.textProperty().addListener((observable, oldValue, newValue) -> validarCampo());
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

    private void crearPlaylist() {
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
        
        // Crear perfil de usuario temporal (en una app real esto vendría del login)
        Perfil propietario = new Perfil("Usuario Actual", "usuario@example.com");
        
        try {
            // Crear la playlist
            Playlist playlist = new Playlist(titulo, descripcion, propietario);
            
            // Asignar imagen si se seleccionó una
            if (imagenSeleccionada != null) {
                BufferedImage bufferedImage = ImageIO.read(imagenSeleccionada);
                playlist.setImagenPortada(bufferedImage);
            }
            
            // Registrar en la base de datos
            playlistDAO.registrarPlaylist(playlist);
            
            mostrarAlerta("¡Playlist creada exitosamente!", Alert.AlertType.INFORMATION);
            
            // Limpiar campos y cerrar ventana
            limpiarCampos();
            cerrarVentana();
            
        } catch (Exception e) {
            mostrarAlerta("Error al crear la playlist: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void limpiarCampos() {
        txtTitulo.clear();
        txtDescripcion.clear();
        comboTipo.setValue("Pública");
        imgPortada.setImage(new Image("/UserInterface/Resources/img/CatalogoCanciones/camara.png"));
        imagenSeleccionada = null;
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCrear.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Nueva Playlist");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
} */
