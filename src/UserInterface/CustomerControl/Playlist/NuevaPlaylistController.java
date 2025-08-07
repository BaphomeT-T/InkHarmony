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
import BusinessLogic.Sesion;
import DataAccessComponent.DTO.PerfilDTO;
import DataAccessComponent.DAO.PerfilDAO;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.ArrayList;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import java.io.File;

public class NuevaPlaylistController implements Initializable {

    // Constantes
    private static final String RUTA_IMAGEN_DEFAULT = "/UserInterface/Resources/img/CatalogoPlaylist/camara.png";
    private static final String DESCRIPCION_DEFAULT = "Sin descripción";
    private static final String COLOR_BOTON_ACTIVO = "-fx-background-color: #9190C2; -fx-background-radius: 20; -fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold;";
    private static final String COLOR_BOTON_INACTIVO = "-fx-background-color: #6B6B6B; -fx-background-radius: 20; -fx-text-fill: #999999; -fx-font-size: 16px; -fx-font-weight: bold;";
    private static final String[] EXTENSIONES_IMAGEN = {"*.jpg", "*.jpeg", "*.png", "*.gif"};

    // Campos FXML
    @FXML private TextField txtTitulo;
    @FXML private TextArea txtDescripcion;
    @FXML private ImageView imgPortada;
    @FXML private Button btnCrear;
    @FXML private Button btnCerrar;
    @FXML private Button btnRegresar;
    @FXML private Button btnGuardarPlaylist;
    @FXML private Button btnSeleccionarPortada;
    @FXML private ImageView imgRegresar;

    // Variables de instancia
    private File imagenSeleccionada;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarEventos();
        cargarImagenPorDefecto();
    }

    private void configurarEventos() {
        configurarEventoSeleccionImagen();
        configurarValidacionTitulo();
    }

    private void configurarEventoSeleccionImagen() {
        imgPortada.setOnMouseClicked(event -> handleSeleccionarPortada());
    }

    private void configurarValidacionTitulo() {
        txtTitulo.textProperty().addListener((observable, oldValue, newValue) -> validarCampos());
    }

    private void validarCampos() {
        boolean esTituloValido = esTituloValido();
        actualizarEstadoBotonGuardar(esTituloValido);
    }

    private boolean esTituloValido() {
        return txtTitulo != null && !txtTitulo.getText().trim().isEmpty();
    }

    private void actualizarEstadoBotonGuardar(boolean habilitado) {
        if (btnGuardarPlaylist == null) return;

        btnGuardarPlaylist.setDisable(!habilitado);
        String estilo = habilitado ? COLOR_BOTON_ACTIVO : COLOR_BOTON_INACTIVO;
        btnGuardarPlaylist.setStyle(estilo);
    }

    @FXML
    private void handleCerrar() {
        cerrarVentana(btnCerrar);
    }

    @FXML
    private void handleRegresar() {
        cerrarVentana(imgRegresar);
    }

    @FXML
    private void handleRegresarImagen() {
        handleRegresar();
    }

    @FXML
    private void handleCrearPlaylist() {
        // Lógica para crear la playlist
    }

    @FXML
    private void handleSeleccionarPortada() {
        FileChooser fileChooser = crearFileChooser();
        Stage ventana = obtenerVentana(imgPortada);
        File archivo = fileChooser.showOpenDialog(ventana);

        if (archivo != null) {
            procesarImagenSeleccionada(archivo);
        }
    }

    @FXML
    private void handleGuardarPlaylist() {
        DatosPlaylist datos = obtenerDatosFormulario();

        if (!validarDatosPlaylist(datos)) {
            return;
        }

        try {
            // Validar sesión antes de crear la playlist
            if (!validarSesionActiva()) {
                return;
            }

            boolean resultado = crearPlaylist(datos);
            procesarResultadoCreacion(resultado);
        } catch (Exception e) {
            manejarErrorCreacion(e);
        }
    }

    /**
     * Valida que existe una sesión activa y un usuario autenticado
     * @return true si hay una sesión válida, false en caso contrario
     */
    private boolean validarSesionActiva() {
        try {
            Sesion sesion = Sesion.getSesion();
            PerfilDTO usuarioActual = sesion.obtenerUsuarioActual();
            
            if (usuarioActual == null) {
                mostrarAlerta("Error de Sesión", 
                    "No hay un usuario autenticado. Por favor, inicia sesión e intenta nuevamente.");
                return false;
            }
            
            return true;
        } catch (Exception e) {
            mostrarAlerta("Error de Sesión", 
                "Error al verificar la sesión del usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene el ID del usuario actual de la sesión.
     * Utiliza el correo para buscar el perfil completo si es necesario.
     * @return ID del usuario actual
     * @throws Exception si no hay usuario autenticado o error en la sesión
     */
    private int obtenerIdUsuarioActual() throws Exception {
        Sesion sesion = Sesion.getSesion();
        PerfilDTO usuarioActual = sesion.obtenerUsuarioActual();
        
        if (usuarioActual == null) {
            throw new Exception("No hay usuario autenticado en la sesión");
        }
        
        // Si el PerfilDTO ya tiene el ID disponible, utilizarlo directamente
        if (usuarioActual.getIdPerfil() > 0) {
            return usuarioActual.getIdPerfil();
        }
        
        // Si no tiene ID, buscar el perfil completo por correo usando PerfilDAO
        if (usuarioActual.getCorreo() != null && !usuarioActual.getCorreo().isEmpty()) {
            return obtenerIdPorCorreoConDAO(usuarioActual.getCorreo());
        }
        
        throw new Exception("No se pudo obtener el ID del usuario actual. Datos de sesión incompletos.");
    }

    /**
     * Obtiene el ID del usuario buscando el perfil completo por correo usando PerfilDAO
     * @param correo El correo del usuario
     * @return ID del usuario
     * @throws Exception si no se encuentra el usuario o hay error en la consulta
     */
    private int obtenerIdPorCorreoConDAO(String correo) throws Exception {
        try {
            PerfilDAO perfilDAO = new PerfilDAO();
            PerfilDTO perfilCompleto = perfilDAO.buscarPorEmail(correo);
            
            if (perfilCompleto == null) {
                throw new Exception("No se encontró usuario con el correo: " + correo);
            }
            
            // Verificar que el perfil tenga un ID válido
            if (perfilCompleto.getIdPerfil() <= 0) {
                throw new Exception("El perfil encontrado no tiene un ID válido");
            }
            
            return perfilCompleto.getIdPerfil();
        } catch (Exception e) {
            throw new Exception("Error al buscar usuario por correo: " + e.getMessage());
        }
    }

    // Métodos de utilidad
    private void cerrarVentana(Object fuente) {
        Stage stage = obtenerVentana(fuente);
        if (stage != null) {
            stage.close();
        }
    }

    private Stage obtenerVentana(Object fuente) {
        if (fuente instanceof Button) {
            return (Stage) ((Button) fuente).getScene().getWindow();
        } else if (fuente instanceof ImageView) {
            return (Stage) ((ImageView) fuente).getScene().getWindow();
        }
        return null;
    }

    private FileChooser crearFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen de portada");

        FileChooser.ExtensionFilter filtroImagen = new FileChooser.ExtensionFilter(
                "Archivos de imagen (*.jpg, *.jpeg, *.png, *.gif)",
                EXTENSIONES_IMAGEN
        );
        fileChooser.getExtensionFilters().add(filtroImagen);

        return fileChooser;
    }

    private void procesarImagenSeleccionada(File archivo) {
        try {
            Image nuevaImagen = new Image(archivo.toURI().toString());
            imgPortada.setImage(nuevaImagen);
            // Hacer que la imagen ocupe todo el contenedor
            imgPortada.setFitWidth(200.0);  // Tamaño completo del AnchorPane
            imgPortada.setFitHeight(200.0); // Tamaño completo del AnchorPane
            imgPortada.setPreserveRatio(false); // Permite deformación para llenar completamente

            imagenSeleccionada = archivo;
            System.out.println("Nueva imagen de portada seleccionada: " + archivo.getName());
        } catch (Exception e) {
            manejarErrorImagen(e);
        }
    }

    private void manejarErrorImagen(Exception e) {
        System.out.println("Error al cargar la imagen: " + e.getMessage());
        mostrarAlerta("Error", "No se pudo cargar la imagen seleccionada.");
    }

    private void cargarImagenPorDefecto() {
        try {
            Image iconoCamara = new Image(getClass().getResourceAsStream(RUTA_IMAGEN_DEFAULT));
            imgPortada.setImage(iconoCamara);
            // Mantener las propiedades originales para el ícono de cámara
            imgPortada.setFitWidth(120.0);
            imgPortada.setFitHeight(120.0);
            imgPortada.setPreserveRatio(true);
        } catch (Exception e) {
            System.out.println("No se pudo cargar el ícono de cámara por defecto");
            imgPortada.setImage(null);
        }
    }

    private DatosPlaylist obtenerDatosFormulario() {
        return new DatosPlaylist(
                txtTitulo.getText().trim(),
                txtDescripcion.getText().trim(),
                imagenSeleccionada
        );
    }

    private boolean validarDatosPlaylist(DatosPlaylist datos) {
        if (datos.getTitulo().isEmpty()) {
            mostrarAlerta("Error", "Por favor, ingresa un título para la playlist");
            return false;
        }
        return true;
    }

    private boolean crearPlaylist(DatosPlaylist datos) throws Exception {
        Playlist playlistLogic = new Playlist();
        byte[] imagenBytes = convertirImagenABytes(datos.getImagenArchivo());
        
        // Obtener el ID del usuario actual de la sesión
        int idUsuarioActual = obtenerIdUsuarioActual();

        return playlistLogic.registrar(
                datos.getTitulo(),
                datos.getDescripcionODefault(),
                idUsuarioActual, // Usar ID del usuario actual obtenido dinámicamente
                imagenBytes,
                new ArrayList<>()
        );
    }

    private byte[] convertirImagenABytes(File archivo) {
        if (archivo == null) {
            return null;
        }

        try {
            byte[] bytes = java.nio.file.Files.readAllBytes(archivo.toPath());
            System.out.println("Imagen convertida a bytes: " + bytes.length + " bytes");
            return bytes;
        } catch (Exception e) {
            System.out.println("Error al convertir imagen a bytes: " + e.getMessage());
            mostrarAlerta("Advertencia",
                    "Se creará la playlist sin imagen debido a un error al procesar la imagen seleccionada.");
            return null;
        }
    }

    private void procesarResultadoCreacion(boolean exito) {
        if (exito) {
            mostrarMensajeExito();
            Platform.runLater(this::handleRegresar);
        } else {
            mostrarAlerta("Error", "No se pudo crear la playlist");
        }
    }

    private void mostrarMensajeExito() {
        String mensaje = "Playlist creada correctamente";
        if (imagenSeleccionada != null) {
            mensaje += " con imagen de portada";
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void manejarErrorCreacion(Exception e) {
        mostrarAlerta("Error", "Error al crear playlist: " + e.getMessage());
        e.printStackTrace();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void limpiarCampos() {
        txtTitulo.clear();
        txtDescripcion.clear();
    }

    // Clase interna para encapsular datos de playlist
    private static class DatosPlaylist {
        private final String titulo;
        private final String descripcion;
        private final File imagenArchivo;

        public DatosPlaylist(String titulo, String descripcion, File imagenArchivo) {
            this.titulo = titulo;
            this.descripcion = descripcion;
            this.imagenArchivo = imagenArchivo;
        }

        public String getTitulo() {
            return titulo;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public String getDescripcionODefault() {
            return descripcion.isEmpty() ? DESCRIPCION_DEFAULT : descripcion;
        }

        public File getImagenArchivo() {
            return imagenArchivo;
        }
    }
}