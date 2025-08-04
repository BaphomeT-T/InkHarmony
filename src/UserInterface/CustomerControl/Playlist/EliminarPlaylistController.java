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

/**
 * Controlador para la interfaz de eliminar playlists.
 *
 * Permite visualizar los datos de una playlist seleccionada y confirmar su eliminación.
 * Los campos se muestran de solo lectura para evitar edición accidental.
 *
 * @author Grupo - C
 */
public class EliminarPlaylistController implements Initializable {

    @FXML
    private TextField txtTitulo;

    @FXML
    private TextArea txtDescripcion;

    @FXML
    private ImageView imgPortada;

    @FXML
    private Button btnEliminar;

    @FXML
    private Button btnCerrar;

    @FXML
    private Button btnRegresar;

    @FXML
    private ImageView imgRegresar;

    // Playlist actual a eliminar
    private PlaylistDTO playlistActual;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurar campos como solo lectura
        configurarCamposLectura();

        // Configurar el ImageView
        configurarImageView();

        // Establecer imagen por defecto
        establecerImagenPorDefecto();
    }

    /**
     * Configura los campos de texto como solo lectura
     */
    private void configurarCamposLectura() {
        // Hacer campos no editables
        txtTitulo.setEditable(false);
        txtDescripcion.setEditable(false);

        // Cambiar estilo para indicar que son solo lectura
        txtTitulo.setStyle("-fx-background-color: #7A7A9D; -fx-background-radius: 15; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 0 15 0 15; -fx-opacity: 0.8;");
        txtDescripcion.setStyle("-fx-background-color: #7A7A9D; -fx-background-radius: 15; -fx-text-fill: white; -fx-padding: 15; -fx-control-inner-background: #7A7A9D; -fx-opacity: 0.8;");

        // Hacer que no se puedan seleccionar con tab
        txtTitulo.setFocusTraversable(false);
        txtDescripcion.setFocusTraversable(false);
    }

    /**
     * Configura el ImageView
     */
    private void configurarImageView() {
        // Asegurar que el ImageView mantenga las proporciones adecuadas
        imgPortada.setPreserveRatio(false);
        imgPortada.setSmooth(true);

        // Remover interactividad del ImageView
        imgPortada.setOnMouseClicked(null);
        imgPortada.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);");
    }

    @FXML
    private void handleCerrar() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleRegresar() {
        Stage stage = (Stage) btnRegresar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleEliminarPlaylist() {
        if (playlistActual == null) {
            mostrarAlerta("Error", "No hay playlist seleccionada para eliminar.", Alert.AlertType.ERROR);
            return;
        }

        // Crear alerta de confirmación personalizada
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Estás seguro de que quieres eliminar esta playlist?");
        confirmacion.setContentText(
                "Playlist: " + playlistActual.getTituloPlaylist() +
                        "\n\nEsta acción eliminará permanentemente la playlist y todas sus canciones asociadas." +
                        "\n\n⚠️ Esta acción no se puede deshacer.");

        // Personalizar botones
        ButtonType btnEliminar = new ButtonType("Sí, eliminar playlist", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmacion.getButtonTypes().setAll(btnEliminar, btnCancelar);

        // Cambiar el estilo de la alerta para que sea más visible
        confirmacion.getDialogPane().setStyle("-fx-background-color: #1B1A55;");

        // Mostrar confirmación y procesar respuesta
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == btnEliminar) {
                eliminarPlaylist();
            }
        });
    }

    /**
     * Ejecuta la eliminación de la playlist
     */
    private void eliminarPlaylist() {
        try {
            System.out.println("=== INICIANDO ELIMINACIÓN DE PLAYLIST ===");
            System.out.println("Playlist ID: " + playlistActual.getIdPlaylist());
            System.out.println("Título: " + playlistActual.getTituloPlaylist());

            // Usar BusinessLogic.Playlist para eliminar
            Playlist playlistLogic = new Playlist();
            boolean resultado = playlistLogic.eliminar(playlistActual.getIdPlaylist());

            System.out.println("Resultado de eliminación: " + resultado);

            if (resultado) {
                // Mostrar mensaje de éxito
                Alert exito = new Alert(Alert.AlertType.INFORMATION);
                exito.setTitle("Playlist eliminada");
                exito.setHeaderText("✅ Eliminación exitosa");
                exito.setContentText("La playlist '" + playlistActual.getTituloPlaylist() + "' ha sido eliminada exitosamente del sistema.");

                // Personalizar el estilo del diálogo de éxito
                exito.getDialogPane().setStyle("-fx-background-color: #1B1A55;");

                exito.showAndWait();

                System.out.println("Playlist eliminada exitosamente, cerrando ventana...");

                // Cerrar ventana automáticamente después del mensaje
                Platform.runLater(() -> handleRegresar());

            } else {
                mostrarAlerta("Error", "No se pudo eliminar la playlist. Puede que ya haya sido eliminada o que ocurriera un error en el sistema.", Alert.AlertType.ERROR);
                System.out.println("ERROR: No se pudo eliminar la playlist");
            }

        } catch (Exception e) {
            System.out.println("EXCEPCIÓN durante eliminación: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "Error al eliminar playlist: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Establece la imagen por defecto (ícono de playlist genérico)
     */
    private void establecerImagenPorDefecto() {
        try {
            // Intentar cargar la imagen por defecto del catálogo
            Image imagenPorDefecto = new Image(getClass().getResourceAsStream(
                    "/UserInterface/Resources/img/CatalogoPlaylist/simbolo-aplicacion.png"));

            if (imagenPorDefecto.isError()) {
                // Si falla, intentar con playlist-default.png
                imagenPorDefecto = new Image(getClass().getResourceAsStream(
                        "/UserInterface/Resources/img/CatalogoPlaylist/playlist-default.png"));
            }

            if (!imagenPorDefecto.isError()) {
                imgPortada.setImage(imagenPorDefecto);
                System.out.println("Imagen por defecto cargada exitosamente");
            } else {
                System.out.println("Error al cargar imagen por defecto, creando placeholder");
                crearImagenPlaceholder();
            }

        } catch (Exception e) {
            System.err.println("No se pudo cargar la imagen por defecto de playlist: " + e.getMessage());
            e.printStackTrace();
            // Crear una imagen de placeholder si no se puede cargar
            crearImagenPlaceholder();
        }
    }



    private void crearImagenPlaceholder() {
        try {
            javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas(300, 300);
            javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();

            // Fondo con gradiente (simular el estilo de Spotify)
            javafx.scene.paint.LinearGradient gradient = new javafx.scene.paint.LinearGradient(
                    0, 0, 1, 1, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
                    new javafx.scene.paint.Stop(0, javafx.scene.paint.Color.web("#1B1A55")),
                    new javafx.scene.paint.Stop(1, javafx.scene.paint.Color.web("#201D4E"))
            );
            gc.setFill(gradient);
            gc.fillRect(0, 0, 300, 300);

            // Dibujar un ícono musical simple
            gc.setFill(javafx.scene.paint.Color.web("#AFAFC7"));
            gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 40));

            // Símbolo musical ♪
            gc.fillText("♪", 130, 140);

            // Texto descriptivo
            gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.NORMAL, 16));
            gc.fillText("Playlist", 110, 170);
            gc.fillText("sin imagen", 95, 190);

            // Convertir canvas a imagen
            javafx.scene.SnapshotParameters params = new javafx.scene.SnapshotParameters();
            params.setFill(javafx.scene.paint.Color.TRANSPARENT);
            Image placeholder = canvas.snapshot(params, null);

            imgPortada.setImage(placeholder);
            System.out.println("Imagen placeholder creada exitosamente");

        } catch (Exception e) {
            System.err.println("No se pudo crear imagen placeholder: " + e.getMessage());
            e.printStackTrace();

            // Como último recurso, crear una imagen sólida simple
            try {
                javafx.scene.canvas.Canvas simpleCanvas = new javafx.scene.canvas.Canvas(300, 300);
                javafx.scene.canvas.GraphicsContext simpleGc = simpleCanvas.getGraphicsContext2D();

                simpleGc.setFill(javafx.scene.paint.Color.web("#201D4E"));
                simpleGc.fillRect(0, 0, 300, 300);

                simpleGc.setFill(javafx.scene.paint.Color.WHITE);
                simpleGc.setFont(javafx.scene.text.Font.font(18));
                simpleGc.fillText("Sin imagen", 100, 150);

                javafx.scene.SnapshotParameters simpleParams = new javafx.scene.SnapshotParameters();
                Image simpleImage = simpleCanvas.snapshot(simpleParams, null);
                imgPortada.setImage(simpleImage);

            } catch (Exception finalE) {
                System.err.println("Error crítico creando imagen: " + finalE.getMessage());
                imgPortada.setImage(null);
            }
        }
    }

    /**
     * Muestra una alerta con el mensaje especificado
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        // Personalizar estilo según el tipo
        if (tipo == Alert.AlertType.ERROR) {
            alert.getDialogPane().setStyle("-fx-background-color: #1B1A55;");
        }

        alert.showAndWait();
    }

    @FXML
    private void handleRegresarImagen() {
        Stage stage = (Stage) imgRegresar.getScene().getWindow();
        stage.close();
    }

    /**
     * Método público para establecer la playlist a eliminar
     * @param playlist PlaylistDTO con los datos de la playlist a eliminar
     */
    public void setPlaylist(PlaylistDTO playlist) {
        System.out.println("=== CARGANDO PLAYLIST PARA ELIMINAR ===");
        this.playlistActual = playlist;

        if (playlist != null) {
            System.out.println("Playlist ID: " + playlist.getIdPlaylist());
            System.out.println("Título: " + playlist.getTituloPlaylist());
            System.out.println("Descripción: " + (playlist.getDescripcion() != null ? playlist.getDescripcion() : "null"));
            System.out.println("Tiene imagen: " + (playlist.getImagenPortada() != null ? "Sí (" + playlist.getImagenPortada().length + " bytes)" : "No"));

            // Cargar datos de la playlist en los controles (solo lectura)
            txtTitulo.setText(playlist.getTituloPlaylist());
            txtDescripcion.setText(playlist.getDescripcion() != null ? playlist.getDescripcion() : "Sin descripción");

            // Cargar imagen si existe
            if (playlist.getImagenPortada() != null && playlist.getImagenPortada().length > 0) {
                try {
                    // Convertir bytes a imagen
                    ByteArrayInputStream bis = new ByteArrayInputStream(playlist.getImagenPortada());
                    Image imagen = new Image(bis);

                    if (!imagen.isError()) {
                        imgPortada.setImage(imagen);
                        System.out.println("Imagen de portada cargada exitosamente");
                        System.out.println("Dimensiones imagen: " + imagen.getWidth() + "x" + imagen.getHeight());
                    } else {
                        System.out.println("Error en la imagen cargada desde bytes");
                        establecerImagenPorDefecto();
                    }
                } catch (Exception e) {
                    System.out.println("Error al cargar imagen de portada: " + e.getMessage());
                    e.printStackTrace();
                    establecerImagenPorDefecto();
                }
            } else {
                System.out.println("No hay imagen de portada, cargando imagen por defecto");
                establecerImagenPorDefecto();
            }

            System.out.println("Playlist cargada completamente para eliminación");
        } else {
            System.out.println("ERROR: Playlist recibida es null");
        }
    }
}