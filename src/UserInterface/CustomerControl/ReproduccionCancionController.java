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
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import DataAccessComponent.DAO.CancionDAO;
import DataAccessComponent.DTO.CancionDTO;
import DataAccessComponent.DTO.ArtistaDTO;

/**
 * Controlador para la interfaz de reproducción de canciones.
 * Esta clase maneja la interfaz de usuario para reproducir música,
 * incluyendo controles de reproducción, navegación y búsqueda de canciones.
 *
 * @author Grupo B
 * @version 1.0
 * @since 2025
 */
public class ReproduccionCancionController implements Initializable {

    private ReproductorMP3 reproductor;
    private Timeline timeline; // Cambiado de Timer a Timeline para mejor integración con JavaFX
    private boolean actualizandoProgress = false;
    private double duracionRealCancion = 0;
    private double tiempoActualSegundos = 0; // Nueva variable para el tiempo actual

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            CancionDAO cancionDAO = new CancionDAO();
            CancionDTO cancion = cancionDAO.buscarPorId(1);
            mostrarInformacionCancion(cancion);
        } catch (Exception e) {
            System.out.println("Error al cargar la canción: " + e.getMessage());
        }

        inicializarTimeline(); // Cambiado de iniciarActualizacionProgressBar
    }

    /**
     * Inicializa el Timeline de JavaFX para actualizar el progreso cada 100ms.
     * Reemplaza el Timer de Java por mejor integración con JavaFX.
     */
    private void inicializarTimeline() {
        timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> actualizarProgressBar()));
        timeline.setCycleCount(Timeline.INDEFINITE);
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
     * Actualiza el progress bar con el frame actual de la reproducción.
     * Ahora maneja correctamente el tiempo actual vs tiempo total.
     */
    private void actualizarProgressBar() {
        if (reproductor != null && reproductor.getMotor() != null && reproductor.estaReproduciendo()) {
            try {
                int frameActual = reproductor.getMotor().getPlayer().getLastPosition();
                tiempoActualSegundos = frameActual / 26.0; // Actualizar tiempo actual

                // Actualizar barra de progreso si tenemos duración total
                if (duracionRealCancion > 0) {
                    double progreso = tiempoActualSegundos / duracionRealCancion;
                    progreso = Math.max(0.0, Math.min(1.0, progreso));
                    pgbProgresoCancion.setProgress(progreso);
                }

                // Actualizar etiqueta de tiempo (tiempo_actual / duracion_total)
                actualizarTiempoCancion(tiempoActualSegundos, duracionRealCancion);

                // Verificar si la canción ha terminado
                if (tiempoActualSegundos >= duracionRealCancion && duracionRealCancion > 0) {
                    onCancionTerminada();
                }

            } catch (Exception e) {
                pgbProgresoCancion.setProgress(0.0);
                lblTiempoCancion.setText("00:00 / 00:00");
            }
        } else if (!reproductor.estaReproduciendo()) {
            // Si está pausado, mantener el tiempo actual pero no avanzar
            actualizarTiempoCancion(tiempoActualSegundos, duracionRealCancion);
        }
    }

    /**
     * Maneja el evento cuando una canción termina de reproducirse.
     */
    private void onCancionTerminada() {
        timeline.stop();
        cambiarAImagenPlay();
        // Opcional: avanzar automáticamente a la siguiente canción
        // clickSiguiente(null);
    }

    /**
     * Actualiza la etiqueta de tiempo de la canción con formato MM:SS / MM:SS.
     * Ahora muestra correctamente tiempo_actual / duracion_total.
     */
    private void actualizarTiempoCancion(double segundosActuales, double segundosTotales) {
        try {
            String tiempoActual = formatearTiempo(segundosActuales);
            String tiempoTotal = formatearTiempo(segundosTotales > 0 ? segundosTotales : 0);

            lblTiempoCancion.setText(tiempoActual + " / " + tiempoTotal);
        } catch (Exception e) {
            lblTiempoCancion.setText("00:00 / 00:00");
        }
    }

    /**
     * Formatea segundos a formato MM:SS.
     */
    private String formatearTiempo(double segundos) {
        int minutos = (int) (segundos / 60);
        int segundosRestantes = (int) (segundos % 60);
        return String.format("%02d:%02d", minutos, segundosRestantes);
    }

    /**
     * Maneja el evento de clic en el botón "Anterior".
     */
    @FXML
    void clickAnterior(ActionEvent event) {
        reproductor.anterior();
        reiniciarProgresoYTiempo();
    }

    /**
     * Maneja el evento de clic en el botón de búsqueda de canciones.
     */
    @FXML
    void clickBuscarCancion(ActionEvent event) {
        System.out.println("Ingrese la cancion a buscar.");
    }

    /**
     * Maneja el evento de clic en el botón "Regresar".
     */
    @FXML
    void clickRegresarPagina(ActionEvent event) {
        // Detener timeline al salir
        if (timeline != null) {
            timeline.stop();
        }
        System.out.println("Se ha regresado la pagina.");
    }

    /**
     * Maneja el evento de clic en el botón de reproducción/pausa.
     * Ahora controla correctamente el Timeline.
     */
    @FXML
    void clickReproducir(ActionEvent event) {
        if (reproductor.estaReproduciendo()) {
            // Pausar reproducción y timeline
            reproductor.pausar();
            timeline.stop();
            cambiarAImagenPlay();
        } else {
            if (reproductor.getEstado() instanceof EstadoPausado) {
                // Reanudar reproducción y timeline
                reproductor.reanudar();
                timeline.play();
            } else {
                // Iniciar nueva reproducción
                reproductor.reproducir();
                reiniciarProgresoYTiempo();
                timeline.play();
            }
            actualizarDuracionActual();
            cambiarAImagenPause();
        }
    }

    /**
     * Reinicia el progreso y tiempo cuando se cambia de canción.
     */
    private void reiniciarProgresoYTiempo() {
        timeline.stop(); // Detener timeline actual
        pgbProgresoCancion.setProgress(0.0);
        tiempoActualSegundos = 0.0; // Resetear tiempo actual
        duracionRealCancion = 0.0;
        lblTiempoCancion.setText("00:00 / 00:00");

        // Obtener duración de la nueva canción después de un breve delay
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
        }, 500);
    }

    /**
     * Cambia la imagen del botón a play (cuando está pausado).
     */
    private void cambiarAImagenPlay() {
        try {
            Image imagenPlay = new Image(getClass().getResourceAsStream("../Resources/img/boton-de-play.png"));
            imgPlayPause.setImage(imagenPlay);
            imgPlayPause.setFitWidth(29.0);
            imgPlayPause.setFitHeight(26.0);
        } catch (Exception e) {
            Image imagenPlay = new Image(getClass().getResourceAsStream("../Resources/img/play.png"));
            imgPlayPause.setImage(imagenPlay);
            imgPlayPause.setFitWidth(29.0);
            imgPlayPause.setFitHeight(26.0);
        }
        btnReproducir.setStyle("-fx-background-color: #070F2B; -fx-border-radius: 25px; -fx-background-radius: 25px;");
    }

    /**
     * Cambia la imagen del botón a pause (cuando está reproduciendo).
     */
    private void cambiarAImagenPause() {
        try {
            Image imagenPause = new Image(getClass().getResourceAsStream("../Resources/img/boton-de-pausa.png"));
            imgPlayPause.setImage(imagenPause);
            imgPlayPause.setFitWidth(40.0);
            imgPlayPause.setFitHeight(35.0);
        } catch (Exception e) {
            Image imagenPause = new Image(getClass().getResourceAsStream("../Resources/img/pause.png"));
            imgPlayPause.setImage(imagenPause);
            imgPlayPause.setFitWidth(35.0);
            imgPlayPause.setFitHeight(35.0);
        }
        btnReproducir.setStyle("-fx-background-color: #070F2B; -fx-border-radius: 25px; -fx-background-radius: 25px;");
    }

    /**
     * Actualiza la duración actual de la canción en reproducción.
     */
    private void actualizarDuracionActual() {
        reproductor.obtenerDuracionCancionActual(duracion -> {
            Platform.runLater(() -> duracionRealCancion = duracion);
        });
    }

    /**
     * Maneja el evento de clic en el botón "Siguiente".
     */
    @FXML
    void clickSiguiente(ActionEvent event) {
        reproductor.siguiente();
        reproductor.reproducir();
        reiniciarProgresoYTiempo();
        timeline.play(); // Iniciar timeline para nueva canción
        cambiarAImagenPause();
    }

    /**
     * Muestra la información de una canción en la interfaz.
     */
    public void mostrarInformacionCancion(CancionDTO cancion) {
        if (cancion != null) {
            // Mostrar título
            lblNombreCancion.setText(cancion.getTitulo());
            lblNombreCancion1.setText(cancion.getTitulo());

            // Mostrar artista
            if (!cancion.getArtistas().isEmpty()) {
                String nombreArtista = cancion.getArtistas().get(0).getNombre();
                lblArtista.setText(nombreArtista);
                lblArtista1.setText(nombreArtista);
            } else {
                lblArtista.setText("Desconocido");
                lblArtista1.setText("Desconocido");
            }

            // Mostrar duración en formato mm:ss
            double duracion = cancion.getDuracion();
            String duracionFormateada = formatearTiempo(duracion);
            lblTiempoCancion.setText("00:00 / " + duracionFormateada);

            // Actualizar variable de duración para la barra de progreso
            duracionRealCancion = duracion;
            tiempoActualSegundos = 0.0; // Inicializar tiempo actual

            // Mostrar portada de la canción
            mostrarPortadaCancion(cancion);
        }
    }

    /**
     * Muestra la portada de la canción en ambos paneles.
     * Si la canción no tiene portada (null), muestra una imagen genérica.
     */
    private void mostrarPortadaCancion(CancionDTO cancion) {
        try {
            Image portada;

            // Verificar si la canción tiene portada (como byte[])
            if (cancion.getPortada() != null && cancion.getPortada().length > 0) {
                // Cargar portada específica de la canción desde bytes
                java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(cancion.getPortada());
                portada = new Image(bis);
            } else {
                // Cargar imagen genérica desde recursos
                portada = new Image(getClass().getResourceAsStream("../Resources/img/portada-generica.png"));

                // Si no encuentra la imagen en recursos, intentar con otras extensiones comunes
                if (portada.isError()) {
                    portada = new Image(getClass().getResourceAsStream("../Resources/img/portada-generica.jpg"));
                }
                if (portada.isError()) {
                    portada = new Image(getClass().getResourceAsStream("../Resources/img/default-cover.png"));
                }
                if (portada.isError()) {
                    portada = new Image(getClass().getResourceAsStream("../Resources/img/no-image.png"));
                }
                if (portada.isError()) {
                    portada = new Image(getClass().getResourceAsStream("../Resources/img/album-cover.png"));
                }
            }

            // Configurar imagen para el panel principal (grande)
            if (!portada.isError()) {
                ImageView imageViewGrande = new ImageView(portada);
                imageViewGrande.setFitWidth(panImageAlbum.getPrefWidth());
                imageViewGrande.setFitHeight(panImageAlbum.getPrefHeight());
                imageViewGrande.setPreserveRatio(true);
                imageViewGrande.setSmooth(true); // Suavizado para mejor calidad

                // Limpiar panel y agregar imagen
                panImageAlbum.getChildren().clear();
                panImageAlbum.getChildren().add(imageViewGrande);

                // Configurar imagen para el panel pequeño (reproductor inferior)
                ImageView imageViewPequeno = new ImageView(portada);
                imageViewPequeno.setFitWidth(panImageAlbum1.getPrefWidth());
                imageViewPequeno.setFitHeight(panImageAlbum1.getPrefHeight());
                imageViewPequeno.setPreserveRatio(true);
                imageViewPequeno.setSmooth(true);

                panImageAlbum1.getChildren().clear();
                panImageAlbum1.getChildren().add(imageViewPequeno);
            } else {
                // Si todas las imágenes fallan, mostrar un color de fondo
                mostrarPortadaPorDefecto();
            }

        } catch (Exception e) {
            System.out.println("Error al cargar portada: " + e.getMessage());
            mostrarPortadaPorDefecto();
        }
    }

    /**
     * Muestra una portada por defecto cuando no se puede cargar ninguna imagen.
     * Simplemente cambia el color de fondo de los paneles.
     */
    private void mostrarPortadaPorDefecto() {
        // Limpiar paneles
        panImageAlbum.getChildren().clear();
        panImageAlbum1.getChildren().clear();

        // Cambiar estilo para mostrar un color de fondo por defecto
        panImageAlbum.setStyle("-fx-background-color: #9290C2; -fx-border-color: #070F2B; -fx-border-width: 2px;");
        panImageAlbum1.setStyle("-fx-background-color: #9290C2; -fx-border-color: #070F2B; -fx-border-width: 2px;");

        // Opcional: agregar un texto o ícono indicando "Sin portada"
        Label lblSinPortada = new Label("♪");
        lblSinPortada.setStyle("-fx-text-fill: white; -fx-font-size: 48px;");
        lblSinPortada.setLayoutX(panImageAlbum.getPrefWidth() / 2 - 15);
        lblSinPortada.setLayoutY(panImageAlbum.getPrefHeight() / 2 - 25);
        panImageAlbum.getChildren().add(lblSinPortada);

        Label lblSinPortadaPequeno = new Label("♪");
        lblSinPortadaPequeno.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");
        lblSinPortadaPequeno.setLayoutX(panImageAlbum1.getPrefWidth() / 2 - 8);
        lblSinPortadaPequeno.setLayoutY(panImageAlbum1.getPrefHeight() / 2 - 10);
        panImageAlbum1.getChildren().add(lblSinPortadaPequeno);
    }

    /**
     * Método de limpieza para liberar recursos al cerrar la ventana.
     */
    public void cleanup() {
        if (timeline != null) {
            timeline.stop();
        }
        if (reproductor != null) {
            reproductor.detener();
        }
    }
}
