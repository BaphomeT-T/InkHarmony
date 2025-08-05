package UserInterface.CustomerControl.Playlist;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.application.Platform;
import BusinessLogic.Playlist;
import DataAccessComponent.DTO.PlaylistDTO;
import java.net.URL;
import java.util.ResourceBundle;
import java.io.ByteArrayInputStream;
import java.util.Optional;

/**
 * Controlador para la interfaz de eliminar playlists.
 *
 * Permite visualizar los datos de una playlist seleccionada y confirmar su eliminación.
 * Los campos se muestran de solo lectura para evitar edición accidental.
 *
 * @author Grupo - C
 */
public class EliminarPlaylistController implements Initializable {

    // Constantes de configuración
    private static final String ESTILO_CAMPO_LECTURA = "-fx-background-color: #7A7A9D; -fx-background-radius: 15; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 0 15 0 15; -fx-opacity: 0.8;";
    private static final String ESTILO_TEXTAREA_LECTURA = "-fx-background-color: #7A7A9D; -fx-background-radius: 15; -fx-text-fill: white; -fx-padding: 15; -fx-control-inner-background: #7A7A9D; -fx-opacity: 0.8;";
    private static final String ESTILO_DIALOGO = "-fx-background-color: #1B1A55;";
    private static final String RUTA_IMAGEN_SIMBOLO = "/UserInterface/Resources/img/CatalogoPlaylist/simbolo-aplicacion.png";
    private static final String RUTA_IMAGEN_DEFAULT = "/UserInterface/Resources/img/CatalogoPlaylist/playlist-default.png";

    // Constantes de diseño
    private static final String COLOR_FONDO_PLAYLIST = "#1B1A55";
    private static final String COLOR_FONDO_DEGRADADO = "#201D4E";
    private static final String COLOR_TEXTO_ICONO = "#AFAFC7";
    private static final int CANVAS_DIMENSIONS = 300;
    private static final int FONT_SIZE_ICONO = 40;
    private static final int FONT_SIZE_TEXTO = 16;

    // Componentes FXML
    @FXML private TextField txtTitulo;
    @FXML private TextArea txtDescripcion;
    @FXML private ImageView imgPortada;
    @FXML private Button btnEliminar;
    @FXML private Button btnCerrar;
    @FXML private Button btnRegresar;
    @FXML private ImageView imgRegresar;

    private PlaylistDTO playlistActual;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarInterfaz();
        establecerImagenPorDefecto();
    }

    /**
     * Configura todos los elementos de la interfaz
     */
    private void configurarInterfaz() {
        configurarCamposLectura();
        configurarImageView();
    }

    /**
     * Configura los campos de texto como solo lectura
     */
    private void configurarCamposLectura() {
        txtTitulo.setEditable(false);
        txtDescripcion.setEditable(false);

        txtTitulo.setStyle(ESTILO_CAMPO_LECTURA);
        txtDescripcion.setStyle(ESTILO_TEXTAREA_LECTURA);

        txtTitulo.setFocusTraversable(false);
        txtDescripcion.setFocusTraversable(false);
    }

    /**
     * Configura el ImageView de la portada
     */
    private void configurarImageView() {
        imgPortada.setPreserveRatio(false);
        imgPortada.setSmooth(true);
        imgPortada.setOnMouseClicked(null);
        imgPortada.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);");
    }

    // Event Handlers
    @FXML
    private void handleCerrar() {
        cerrarVentana();
    }

    @FXML
    private void handleRegresar() {
        cerrarVentana();
    }

    @FXML
    private void handleRegresarImagen() {
        cerrarVentana();
    }

    /**
     * Cierra la ventana actual de forma segura
     */
    private void cerrarVentana() {
        try {
            Stage stage = obtenerStageActual();
            if (stage != null) {
                stage.close();
            }
        } catch (Exception e) {
            System.err.println("Error al cerrar ventana: " + e.getMessage());
        }
    }

    /**
     * Obtiene el Stage actual de forma segura
     */
    private Stage obtenerStageActual() {
        if (btnCerrar != null && btnCerrar.getScene() != null) {
            return (Stage) btnCerrar.getScene().getWindow();
        }
        return null;
    }

    @FXML
    private void handleEliminarPlaylist() {
        if (!esPlaylistValida()) {
            mostrarError("No hay playlist seleccionada para eliminar.");
            return;
        }

        Optional<ButtonType> confirmacion = mostrarDialogoConfirmacion();
        if (confirmacion.isPresent() && esConfirmacionPositiva(confirmacion.get())) {
            procesarEliminacion();
        }
    }

    /**
     * Valida si hay una playlist seleccionada
     */
    private boolean esPlaylistValida() {
        return playlistActual != null;
    }

    /**
     * Muestra el diálogo de confirmación de eliminación
     */
    private Optional<ButtonType> mostrarDialogoConfirmacion() {
        Alert confirmacion = crearDialogoConfirmacion();
        return confirmacion.showAndWait();
    }

    /**
     * Crea el diálogo de confirmación
     */
    private Alert crearDialogoConfirmacion() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);

        configurarContenidoDialogo(confirmacion);
        configurarBotonesDialogo(confirmacion);

        return confirmacion;
    }

    /**
     * Configura el contenido del diálogo de confirmación
     */
    private void configurarContenidoDialogo(Alert confirmacion) {
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Estás seguro de que quieres eliminar esta playlist?");
        confirmacion.setContentText(construirMensajeConfirmacion());
        confirmacion.getDialogPane().setStyle(ESTILO_DIALOGO);
    }

    /**
     * Construye el mensaje de confirmación personalizado
     */
    private String construirMensajeConfirmacion() {
        return String.format(
                "Playlist: %s\n\n" +
                        "Esta acción eliminará permanentemente la playlist y todas sus canciones asociadas.\n\n" +
                        "⚠️ Esta acción no se puede deshacer.",
                playlistActual.getTituloPlaylist()
        );
    }

    /**
     * Configura los botones del diálogo
     */
    private void configurarBotonesDialogo(Alert confirmacion) {
        ButtonType btnEliminar = new ButtonType("Sí, eliminar playlist", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmacion.getButtonTypes().setAll(btnEliminar, btnCancelar);
    }

    /**
     * Verifica si la respuesta es positiva
     */
    private boolean esConfirmacionPositiva(ButtonType response) {
        return response.getButtonData() == ButtonBar.ButtonData.OK_DONE;
    }

    /**
     * Procesa la eliminación de la playlist
     */
    private void procesarEliminacion() {
        try {
            registrarInicioEliminacion();

            boolean eliminacionExitosa = ejecutarEliminacion();

            if (eliminacionExitosa) {
                manejarEliminacionExitosa();
            } else {
                mostrarError("No se pudo eliminar la playlist. Puede que ya haya sido eliminada o que ocurriera un error en el sistema.");
            }

        } catch (Exception e) {
            manejarErrorEliminacion(e);
        }
    }

    /**
     * Registra el inicio del proceso de eliminación
     */
    private void registrarInicioEliminacion() {
        System.out.println("=== INICIANDO ELIMINACIÓN DE PLAYLIST ===");
        System.out.println("Playlist ID: " + playlistActual.getIdPlaylist());
        System.out.println("Título: " + playlistActual.getTituloPlaylist());
    }

    /**
     * Ejecuta la eliminación en la capa de lógica de negocio
     */
    private boolean ejecutarEliminacion() throws Exception {
        Playlist playlistLogic = new Playlist();
        boolean resultado = playlistLogic.eliminar(playlistActual.getIdPlaylist());
        System.out.println("Resultado de eliminación: " + resultado);
        return resultado;
    }

    /**
     * Maneja el caso de eliminación exitosa
     */
    private void manejarEliminacionExitosa() {
        System.out.println("Playlist eliminada exitosamente");

        Alert alertaExito = crearAlertaExito();
        alertaExito.showAndWait();

        // Cerrar ventana inmediatamente después del diálogo
        cerrarVentanaInmediatamente();
    }

    /**
     * Crea la alerta de éxito
     */
    private Alert crearAlertaExito() {
        Alert exito = new Alert(Alert.AlertType.INFORMATION);
        exito.setTitle("Playlist eliminada");
        exito.setHeaderText("✅ Eliminación exitosa");
        exito.setContentText(String.format(
                "La playlist '%s' ha sido eliminada exitosamente del sistema.",
                playlistActual.getTituloPlaylist()
        ));
        exito.getDialogPane().setStyle(ESTILO_DIALOGO);
        return exito;
    }

    /**
     * Cierra la ventana de forma inmediata y segura
     */
    private void cerrarVentanaInmediatamente() {
        Platform.runLater(() -> {
            try {
                Stage stage = obtenerStageActual();
                if (stage != null && stage.isShowing()) {
                    System.out.println("Cerrando ventana...");
                    stage.close();
                    System.out.println("Ventana cerrada exitosamente");
                }
            } catch (Exception e) {
                System.err.println("Error al cerrar ventana después de eliminación: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Maneja errores durante la eliminación
     */
    private void manejarErrorEliminacion(Exception e) {
        System.err.println("EXCEPCIÓN durante eliminación: " + e.getMessage());
        e.printStackTrace();
        mostrarError("Error al eliminar playlist: " + e.getMessage());
    }

    // Métodos de manejo de imágenes

    /**
     * Establece la imagen por defecto
     */
    private void establecerImagenPorDefecto() {
        try {
            Image imagen = cargarImagenPorDefecto();

            if (esImagenValida(imagen)) {
                aplicarImagen(imagen);
            } else {
                crearImagenPlaceholder();
            }

        } catch (Exception e) {
            manejarErrorImagen(e);
        }
    }

    /**
     * Carga la imagen por defecto desde recursos
     */
    private Image cargarImagenPorDefecto() {
        Image imagen = new Image(getClass().getResourceAsStream(RUTA_IMAGEN_SIMBOLO));

        if (imagen.isError()) {
            imagen = new Image(getClass().getResourceAsStream(RUTA_IMAGEN_DEFAULT));
        }

        return imagen;
    }

    /**
     * Valida si la imagen es correcta
     */
    private boolean esImagenValida(Image imagen) {
        return imagen != null && !imagen.isError();
    }

    /**
     * Aplica la imagen al ImageView
     */
    private void aplicarImagen(Image imagen) {
        imgPortada.setImage(imagen);
        System.out.println("Imagen cargada exitosamente");
    }

    /**
     * Maneja errores al cargar imagen
     */
    private void manejarErrorImagen(Exception e) {
        System.err.println("Error al cargar imagen: " + e.getMessage());
        crearImagenPlaceholder();
    }

    /**
     * Crea una imagen placeholder programáticamente
     */
    private void crearImagenPlaceholder() {
        try {
            Image placeholder = generarImagenPlaceholder();
            imgPortada.setImage(placeholder);
            System.out.println("Imagen placeholder creada");

        } catch (Exception e) {
            System.err.println("Error creando placeholder: " + e.getMessage());
            imgPortada.setImage(null);
        }
    }

    /**
     * Genera la imagen placeholder con canvas
     */
    private Image generarImagenPlaceholder() {
        javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas(CANVAS_DIMENSIONS, CANVAS_DIMENSIONS);
        javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();

        dibujarFondoGradiente(gc);
        dibujarIconoMusical(gc);
        dibujarTextoDescriptivo(gc);

        return crearImagenDesdeCanvas(canvas);
    }

    /**
     * Dibuja el fondo con gradiente
     */
    private void dibujarFondoGradiente(javafx.scene.canvas.GraphicsContext gc) {
        javafx.scene.paint.LinearGradient gradient = new javafx.scene.paint.LinearGradient(
                0, 0, 1, 1, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
                new javafx.scene.paint.Stop(0, javafx.scene.paint.Color.web(COLOR_FONDO_PLAYLIST)),
                new javafx.scene.paint.Stop(1, javafx.scene.paint.Color.web(COLOR_FONDO_DEGRADADO))
        );
        gc.setFill(gradient);
        gc.fillRect(0, 0, CANVAS_DIMENSIONS, CANVAS_DIMENSIONS);
    }

    /**
     * Dibuja el icono musical
     */
    private void dibujarIconoMusical(javafx.scene.canvas.GraphicsContext gc) {
        gc.setFill(javafx.scene.paint.Color.web(COLOR_TEXTO_ICONO));
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, FONT_SIZE_ICONO));
        gc.fillText("♪", 130, 140);
    }

    /**
     * Dibuja el texto descriptivo
     */
    private void dibujarTextoDescriptivo(javafx.scene.canvas.GraphicsContext gc) {
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.NORMAL, FONT_SIZE_TEXTO));
        gc.fillText("Playlist", 110, 170);
        gc.fillText("sin imagen", 95, 190);
    }

    /**
     * Crea imagen desde canvas
     */
    private Image crearImagenDesdeCanvas(javafx.scene.canvas.Canvas canvas) {
        javafx.scene.SnapshotParameters params = new javafx.scene.SnapshotParameters();
        params.setFill(javafx.scene.paint.Color.TRANSPARENT);
        return canvas.snapshot(params, null);
    }

    // Métodos de utilidad para alertas

    /**
     * Muestra un mensaje de error
     */
    private void mostrarError(String mensaje) {
        mostrarAlerta("Error", mensaje, Alert.AlertType.ERROR);
    }

    /**
     * Muestra una alerta genérica
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        if (tipo == Alert.AlertType.ERROR) {
            alert.getDialogPane().setStyle(ESTILO_DIALOGO);
        }

        alert.showAndWait();
    }

    // Métodos públicos

    /**
     * Establece la playlist a eliminar
     */
    public void setPlaylist(PlaylistDTO playlist) {
        registrarCargaPlaylist(playlist);
        this.playlistActual = playlist;

        if (playlist != null) {
            cargarDatosPlaylist(playlist);
            cargarImagenPlaylist(playlist);
            System.out.println("Playlist cargada para eliminación");
        } else {
            System.err.println("ERROR: Playlist recibida es null");
        }
    }

    /**
     * Registra información de la playlist cargada
     */
    private void registrarCargaPlaylist(PlaylistDTO playlist) {
        System.out.println("=== CARGANDO PLAYLIST PARA ELIMINAR ===");
        if (playlist != null) {
            System.out.println("ID: " + playlist.getIdPlaylist());
            System.out.println("Título: " + playlist.getTituloPlaylist());
            System.out.println("Descripción: " + obtenerDescripcionSegura(playlist));
            System.out.println("Imagen: " + evaluarImagen(playlist));
        }
    }

    /**
     * Obtiene la descripción de forma segura
     */
    private String obtenerDescripcionSegura(PlaylistDTO playlist) {
        return playlist.getDescripcion() != null ? playlist.getDescripcion() : "Sin descripción";
    }

    /**
     * Evalúa si la playlist tiene imagen
     */
    private String evaluarImagen(PlaylistDTO playlist) {
        return playlist.getImagenPortada() != null
                ? "Sí (" + playlist.getImagenPortada().length + " bytes)"
                : "No";
    }

    /**
     * Carga los datos de la playlist en los campos
     */
    private void cargarDatosPlaylist(PlaylistDTO playlist) {
        txtTitulo.setText(playlist.getTituloPlaylist());
        txtDescripcion.setText(obtenerDescripcionSegura(playlist));
    }

    /**
     * Carga la imagen de la playlist
     */
    private void cargarImagenPlaylist(PlaylistDTO playlist) {
        if (tieneImagen(playlist)) {
            cargarImagenDesdeBytes(playlist.getImagenPortada());
        } else {
            establecerImagenPorDefecto();
        }
    }

    /**
     * Verifica si la playlist tiene imagen
     */
    private boolean tieneImagen(PlaylistDTO playlist) {
        return playlist.getImagenPortada() != null && playlist.getImagenPortada().length > 0;
    }

    /**
     * Carga imagen desde array de bytes
     */
    private void cargarImagenDesdeBytes(byte[] imagenBytes) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(imagenBytes);
            Image imagen = new Image(bis);

            if (esImagenValida(imagen)) {
                imgPortada.setImage(imagen);
                System.out.println("Imagen de portada cargada - " + imagen.getWidth() + "x" + imagen.getHeight());
            } else {
                establecerImagenPorDefecto();
            }

        } catch (Exception e) {
            System.err.println("Error cargando imagen: " + e.getMessage());
            establecerImagenPorDefecto();
        }
    }
}