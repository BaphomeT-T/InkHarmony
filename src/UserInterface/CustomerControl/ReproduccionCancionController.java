package UserInterface.CustomerControl;

import BusinessLogic.MotorReproduccion;
import BusinessLogic.ReproductorMP3;
import BusinessLogic.EstadoPausado;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Controlador para la interfaz de reproducción de canciones.
 * Esta clase maneja la interfaz de usuario para reproducir música,
 * incluyendo controles de reproducción, navegación y búsqueda de canciones.
 *
 * @author Grupo B
 * @version 1.0
 * @since 2025
 */
public class ReproduccionCancionController {

    private ReproductorMP3 reproductor;
    private Timer timerActualizacion;
    private boolean actualizandoProgress = false;
    private double duracionRealCancion = 0;

    /** Etiqueta que muestra el nombre del artista de la canción actual */
    @FXML
    private Label lblArtista;

    /** Etiqueta adicional para mostrar información del artista */
    @FXML
    private Label lblArtista1;

    /** Etiqueta que muestra el nombre de la canción actual */
    @FXML
    private Label lblNombreCancion;

    /** Etiqueta adicional para mostrar el nombre de la canción */
    @FXML
    private Label lblNombreCancion1;

    /** Etiqueta que muestra la duración o tiempo transcurrido de la canción */
    @FXML
    private Label lblTiempoCancion;

    /** Panel que contiene la imagen del álbum de la canción actual */
    @FXML
    private Pane panImageAlbum;

    /** Panel adicional para la imagen del álbum */
    @FXML
    private Pane panImageAlbum1;

    /** Barra de progreso que indica el avance de reproducción de la canción */
    @FXML
    private ProgressBar pgbProgresoCancion;

    /** Campo de texto para ingresar términos de búsqueda de canciones */
    @FXML
    private TextField txtBuscarCancion;

    /** Botón de reproducción/pausa */
    @FXML
    private Button btnReproducir;

    /** Imagen del botón de reproducción/pausa */
    @FXML
    private ImageView imgPlayPause;

    /**
     * Inicializa el controlador, cargando las canciones de prueba.
     */
    public void initialize() {
        List<byte[]> canciones = cargarCanciones();
        reproductor = ReproductorMP3.getInstancia(canciones);
        iniciarActualizacionProgressBar();
    }

    /**
     * Carga canciones desde rutas quemadas como byte[].
     */
    private List<byte[]> cargarCanciones() {
        List<byte[]> canciones = new ArrayList<>();
        try {
            // RUTAS QUEMADAS: cambia esto por tus rutas reales
            canciones.add(cargarArchivoComoBytes("C:/Users/Gabriel Del Valle/Downloads/Los Rodriguez - Para no olvidar.mp3"));
            canciones.add(cargarArchivoComoBytes("C:/Users/Gabriel Del Valle/Downloads/Ilan Amores - Bar La Perla.mp3"));
        } catch (Exception e) {
            System.out.println("Error al cargar canciones: " + e.getMessage());
        }
        return canciones;
    }

    /**
     * Lee un archivo de audio local y lo convierte en byte[].
     */
    private byte[] cargarArchivoComoBytes(String ruta) throws Exception {
        File archivo = new File(ruta);
        try (FileInputStream fis = new FileInputStream(archivo)) {
            byte[] datos = new byte[(int) archivo.length()];
            fis.read(datos);
            return datos;
        }
    }

    /**
     * Inicia la actualización automática del progress bar.
     * Se ejecuta cada 100ms para mostrar el progreso de reproducción.
     */
    private void iniciarActualizacionProgressBar() {
        if (timerActualizacion != null) {
            timerActualizacion.cancel();
        }

        timerActualizacion = new Timer();
        timerActualizacion.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    actualizarProgressBar();
                });
            }
        }, 0, 100); // Actualizar cada 100ms
    }

    /**
     * Actualiza el progress bar con el frame actual de la reproducción.
     */
    private void actualizarProgressBar() {
        if (reproductor != null && reproductor.getMotor() != null) {
            try {
                int frameActual = reproductor.getMotor().getPlayer().getLastPosition();
                double segundosActuales = frameActual / 26.0;

                // ✅ Si aún no tenemos duración, intentar mostrar el tiempo parcial
                if (duracionRealCancion > 0) {
                    double progreso = segundosActuales / duracionRealCancion;
                    progreso = Math.max(0.0, Math.min(1.0, progreso));
                    pgbProgresoCancion.setProgress(progreso);
                }

                // Siempre actualiza el tiempo visible, incluso si no hay duración aún
                actualizarTiempoCancion(segundosActuales, duracionRealCancion > 0 ? duracionRealCancion : 1);

            } catch (Exception e) {
                pgbProgresoCancion.setProgress(0.0);
                lblTiempoCancion.setText("00:00 / 00:00");
            }
        }
    }



    /**
     * Estima el total de frames basado en una duración aproximada.
     * Como no tenemos acceso al total real, usamos una estimación.
     */
    private int estimarFrameTotal() {
        // Estimación: 3 minutos = 180 segundos * 26 frames/segundo = 4680 frames
        // Esto es una aproximación, pero permite mostrar progreso
        return 4680; // Aproximadamente 3 minutos de música
    }

    /**
     * Actualiza la etiqueta de tiempo de la canción.
     */
    private void actualizarTiempoCancion(double segundosActuales, double segundosTotales) {
        try {
            int minutosActuales = (int) (segundosActuales / 60);
            int segundosRestantesActuales = (int) (segundosActuales % 60);
            int minutosTotales = (int) (segundosTotales / 60);
            int segundosRestantesTotales = (int) (segundosTotales % 60);

            String tiempoFormateado = String.format("%02d:%02d / %02d:%02d",
                    minutosActuales, segundosRestantesActuales,
                    minutosTotales, segundosRestantesTotales);

            lblTiempoCancion.setText(tiempoFormateado);
        } catch (Exception e) {
            lblTiempoCancion.setText("00:00 / 00:00");
        }
    }


    /**
     * Maneja el evento de clic en el botón "Anterior".
     * Cambia la reproducción a la canción anterior en la lista de reproducción.
     *
     * @param event El evento de acción generado por el clic del botón
     */
    @FXML
    void clickAnterior(ActionEvent event) {
        reproductor.anterior();
        reiniciarProgresoYTiempo();
    }


    /**
     * Maneja el evento de clic en el botón de búsqueda de canciones.
     * Permite al usuario buscar canciones específicas en la biblioteca musical.
     *
     * @param event El evento de acción generado por el clic del botón
     */
    @FXML
    void clickBuscarCancion(ActionEvent event) {
        System.out.println("Ingrese la cancion a buscar.");
    }

    /**
     * Maneja el evento de clic en el botón "Regresar".
     * Navega de vuelta a la página anterior en la aplicación.
     *
     * @param event El evento de acción generado por el clic del botón
     */
    @FXML
    void clickRegresarPagina(ActionEvent event) {
        System.out.println("Se ha regresado la pagina.");
    }

    /**
     * Maneja el evento de clic en el botón de reproducción/pausa.
     * Inicia o pausa la reproducción de la canción actual y cambia la imagen del botón.
     * BOTÓN ITERATIVO: Se puede usar n veces sin bloquearse.
     *
     * @param event El evento de acción generado por el clic del botón
     */
    @FXML
    void clickReproducir(ActionEvent event) {
        if (reproductor.estaReproduciendo()) {
            reproductor.pausar();
            cambiarAImagenPlay();
        } else {
            if (reproductor.getEstado() instanceof EstadoPausado) {
                reproductor.reanudar();
            } else {
                reproductor.reproducir();
                reiniciarProgresoYTiempo();
            }
            actualizarDuracionActual();
            cambiarAImagenPause();
        }
    }

    private void reiniciarProgresoYTiempo() {
        pgbProgresoCancion.setProgress(0.0);
        lblTiempoCancion.setText("00:00 / 00:00");
        duracionRealCancion = 0.0;

        // Asegura que la duración se calcule después de un breve delay
        // para dar tiempo al motor de cargar la nueva canción
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                reproductor.obtenerDuracionCancionActual(duracion -> {
                    Platform.runLater(() -> {
                        if (duracion > 0) {
                            duracionRealCancion = duracion;
                            actualizarTiempoCancion(0, duracionRealCancion);
                        }
                    });
                });
            }
        }, 500); // Esperar 500 ms para asegurar que la canción está lista
    }

    /**
     * Cambia la imagen del botón a play (cuando está pausado).
     * Usa boton-de-play.png específicamente.
     */
    private void cambiarAImagenPlay() {
        try {
            // Cargar la imagen PNG específica de play
            Image imagenPlay = new Image(getClass().getResourceAsStream("../Resources/img/boton-de-play.png"));
            imgPlayPause.setImage(imagenPlay);
            // Ajustar el tamaño para la imagen PNG
            imgPlayPause.setFitWidth(29.0);
            imgPlayPause.setFitHeight(26.0);
        } catch (Exception e) {
            // Fallback: usar la imagen PNG de play original
            Image imagenPlay = new Image(getClass().getResourceAsStream("../Resources/img/play.png"));
            imgPlayPause.setImage(imagenPlay);
            imgPlayPause.setFitWidth(29.0);
            imgPlayPause.setFitHeight(26.0);
        }
        // Mantener el estilo normal del botón (sin cambiar color)
        btnReproducir.setStyle("-fx-background-color: #070F2B; -fx-border-radius: 25px; -fx-background-radius: 25px;");
    }

    /**
     * Cambia la imagen del botón a pause (cuando está reproduciendo).
     * Usa boton-de-pausa.png específicamente.
     */
    private void cambiarAImagenPause() {
        try {
            // Cargar la imagen PNG específica de pause
            Image imagenPause = new Image(getClass().getResourceAsStream("../Resources/img/boton-de-pausa.png"));
            imgPlayPause.setImage(imagenPause);
            // Ajustar el tamaño para la imagen PNG
            imgPlayPause.setFitWidth(40.0);
            imgPlayPause.setFitHeight(35.0);
        } catch (Exception e) {
            // Fallback: usar la imagen PNG de pause original
            Image imagenPause = new Image(getClass().getResourceAsStream("../Resources/img/pause.png"));
            imgPlayPause.setImage(imagenPause);
            imgPlayPause.setFitWidth(35.0);
            imgPlayPause.setFitHeight(35.0);
        }
        // Mantener el estilo normal del botón (sin cambiar color)
        btnReproducir.setStyle("-fx-background-color: #070F2B; -fx-border-radius: 25px; -fx-background-radius: 25px;");
    }

    /**
     * Actualiza la duración actual de la canción en reproducción.
     * Se llama al iniciar la reproducción para establecer el tiempo correcto.
     */
    private void actualizarDuracionActual() {
        reproductor.obtenerDuracionCancionActual(duracion -> {
            Platform.runLater(() -> duracionRealCancion = duracion);
        });
    }

    /**
     * Maneja el evento de clic en el botón "Siguiente".
     * Cambia la reproducción a la siguiente canción en la lista de reproducción.
     *
     * @param event El evento de acción generado por el clic del botón
     */
    @FXML
    void clickSiguiente(ActionEvent event) {
        reproductor.siguiente();
        reproductor.reproducir();
        reiniciarProgresoYTiempo();
        cambiarAImagenPause();
    }

}
