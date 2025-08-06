package UserInterface.CustomerControl.ReproductorMusical;

import BusinessLogic.ReproductorMusical.ReproductorMP3;
import BusinessLogic.ReproductorMusical.EstadoPausado;
import DataAccessComponent.DAO.CancionDAO;
import DataAccessComponent.DTO.CancionDTO;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controlador unificado para la reproducción de canciones.
 * Combina la carga desde base de datos, actualización de labels y portadas,
 * control de play/pause y actualización de barra de progreso.
 */
public class ReproduccionCancionController implements Initializable {

    private ReproductorMP3 reproductor;
    private Timeline timeline;
    private double duracionRealCancion = 0;
    private double tiempoActualSegundos = 0;
    private List<CancionDTO> cancionesDTO;
    private List<byte[]> cancionesByteArray;
    @FXML
    private Slider pgbProgresoCancion;

    private boolean usuarioArrastrando = false;

    @FXML private Label lblArtista;
    @FXML private Label lblArtista1;
    @FXML private Label lblNombreCancion;
    @FXML private Label lblNombreCancion1;
    @FXML private Label lblTiempoCancion;
    @FXML private Pane panImageAlbum;
    @FXML private Pane panImageAlbum1;
    @FXML private TextField txtBuscarCancion;
    @FXML private Button btnReproducir;
    @FXML private ImageView imgPlayPause;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarCanciones();
        inicializarTimeline();

        // Detectar cuando el usuario empieza a arrastrar
        pgbProgresoCancion.setOnMousePressed(e -> usuarioArrastrando = true);
        pgbProgresoCancion.setOnMouseReleased(e -> {
            usuarioArrastrando = false;
            double progreso = pgbProgresoCancion.getValue();
            saltarAProgreso(progreso);
        });

        reproductor.setOnSongChange(() -> {
            Platform.runLater(() -> {
                actualizarInformacionCancionActual();
                reiniciarProgresoYTiempo();
                cambiarAImagenPause();
                timeline.play();
            });
        });
    }

    private void saltarAProgreso(double progreso) {
        if (duracionRealCancion > 0 && reproductor != null) {
            double segundos = progreso * duracionRealCancion;
            reproductor.getMediaPlayer().seek(Duration.seconds(segundos));
            tiempoActualSegundos = segundos;
            actualizarTiempoCancion(tiempoActualSegundos, duracionRealCancion);
        }
    }

    /*
    private void cargarCancionesDesdeBD() {
        try {
            CancionDAO cancionDAO = new CancionDAO();
            cancionesDTO = cancionDAO.buscarTodo();
            cancionesByteArray = new ArrayList<>();

            for (CancionDTO cancion : cancionesDTO) {
                if (cancion.getArchivoMP3() != null) {
                    cancionesByteArray.add(cancion.getArchivoMP3());
                }
            }

            if (!cancionesByteArray.isEmpty()) {
                reproductor = ReproductorMP3.getInstancia(cancionesByteArray);
                mostrarInformacionCancion(cancionesDTO.get(0));
            } else {
                System.out.println("No hay canciones en la base de datos");
            }

        } catch (Exception e) {
            System.err.println("Error al cargar canciones: " + e.getMessage());
        }
    }*/

    /* Metodo para cargar canciones locales ya que hay poquitas en la bd */
    private void cargarCanciones() {
        try {
            CancionDAO cancionDAO = new CancionDAO();
            cancionesDTO = cancionDAO.buscarTodo();
            cancionesByteArray = new ArrayList<>();

            for (CancionDTO cancion : cancionesDTO) {
                if (cancion.getArchivoMP3() != null) {
                    cancionesByteArray.add(cancion.getArchivoMP3());
                }
            }

            if (cancionesDTO.size() <= 1) {
                System.out.println(" Pocas canciones en BD, agregando locales...");
                agregarCancionesLocales();
            }

            if (!cancionesByteArray.isEmpty()) {
                reproductor = ReproductorMP3.getInstancia(cancionesByteArray);
                mostrarInformacionCancion(cancionesDTO.get(0));
            } else {
                System.out.println(" No hay canciones disponibles.");
            }

        } catch (Exception e) {
            System.err.println("Error al cargar canciones: " + e.getMessage());
            agregarCancionesLocales();
        }
    }

    private void agregarCancionesLocales() {
        try {
            String[] rutasLocales = {
                    "C:/Users/Gabriel Del Valle/Downloads/Los Rodriguez - Para no olvidar.mp3",
                    "C:/Users/Gabriel Del Valle/Downloads/Ilan Amores - Bar La Perla.mp3"
            };

            for (String ruta : rutasLocales) {
                byte[] mp3Bytes = cargarArchivoComoBytes(ruta);
                if (mp3Bytes != null) {
                    cancionesByteArray.add(mp3Bytes);

                    CancionDTO cancionDTO = new CancionDTO();
                    cancionDTO.setTitulo(new File(ruta).getName());
                    cancionDTO.setDuracion(180); // Duración estimada en segundos
                    cancionDTO.setAnio(2025);
                    cancionDTO.setFechaRegistro(java.time.LocalDateTime.now());
                    cancionDTO.setArchivoMP3(mp3Bytes);
                    cancionDTO.setPortada(null); // Si tienes portada, cargarla aquí

                    cancionesDTO.add(cancionDTO);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al agregar canciones locales: " + e.getMessage());
        }
    }

    private byte[] cargarArchivoComoBytes(String ruta) {
        try (FileInputStream fis = new FileInputStream(new File(ruta))) {
            byte[] datos = new byte[(int) new File(ruta).length()];
            fis.read(datos);
            return datos;
        } catch (Exception e) {
            System.err.println("Error al leer archivo local: " + ruta + " -> " + e.getMessage());
            return null;
        }
    }







    private void inicializarTimeline() {
        timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> actualizarProgressBar()));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    private void actualizarProgressBar() {
        if (reproductor != null && reproductor.estaReproduciendo()) {
            tiempoActualSegundos = reproductor.getTiempoActual();

            if (duracionRealCancion > 0 && !usuarioArrastrando) {
                double progreso = Math.max(0.0, Math.min(1.0, tiempoActualSegundos / duracionRealCancion));
                pgbProgresoCancion.setValue(progreso);
            }

            actualizarTiempoCancion(tiempoActualSegundos, duracionRealCancion);
        } else if (reproductor != null && reproductor.estaPausado()) {
            actualizarTiempoCancion(tiempoActualSegundos, duracionRealCancion);
        }
    }


    private void actualizarTiempoCancion(double segundosActuales, double segundosTotales) {
        String tiempoActual = formatearTiempo(segundosActuales);
        String tiempoTotal = formatearTiempo(segundosTotales > 0 ? segundosTotales : 0);
        lblTiempoCancion.setText(tiempoActual + " / " + tiempoTotal);
    }

    private String formatearTiempo(double segundos) {
        int minutos = (int) (segundos / 60);
        int segundosRestantes = (int) (segundos % 60);
        return String.format("%02d:%02d", minutos, segundosRestantes);
    }

    @FXML
    void clickAnterior(ActionEvent event) {
        if (reproductor != null) {
            reproductor.anterior(); // Esto dispara notificarCambioCancion()
        }
    }

    @FXML
    void clickSiguiente(ActionEvent event) {
        if (reproductor != null) {
            reproductor.siguiente(); // Esto dispara notificarCambioCancion()
        }
    }







    @FXML
    void clickReproducir(ActionEvent event) {
        if (reproductor == null) {
            System.out.println("No hay reproductor inicializado. Verifica la conexión a la base de datos o carga de canciones.");
            return;
        }
        if (reproductor.estaReproduciendo()) {
            reproductor.pausar();
            timeline.stop();
            cambiarAImagenPlay();
        } else if (reproductor.getEstado() instanceof EstadoPausado) {
            reproductor.reanudar();
            timeline.play();
            cambiarAImagenPause();
        } else {
            reproductor.reproducir();
            reiniciarProgresoYTiempo();
            actualizarDuracionActual();
            timeline.play();
            cambiarAImagenPause();
        }
    }

    @FXML
    void clickBuscarCancion(ActionEvent event) {
        System.out.println("Función de búsqueda por implementar");
    }

    private void reiniciarProgresoYTiempo() {
        timeline.stop();
        pgbProgresoCancion.setValue(0.0); // Ahora es Slider
        tiempoActualSegundos = 0.0;
        duracionRealCancion = 0.0;
        lblTiempoCancion.setText("00:00 / 00:00");

        // Esperar 500 ms antes de pedir duración para asegurar que el reproductor la tenga lista
        javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(Duration.millis(500));
        delay.setOnFinished(e -> actualizarDuracionActual());
        delay.play();
    }





    private void actualizarDuracionActual() {
        reproductor.obtenerDuracionCancionActual(duracion -> {
            Platform.runLater(() -> {
                duracionRealCancion = duracion;
                actualizarTiempoCancion(tiempoActualSegundos, duracionRealCancion);
            });
        });
    }

    private void actualizarInformacionCancionActual() {
        int indiceActual = reproductor.getPlaylist().getIndiceActual();
        if (indiceActual >= 0 && indiceActual < cancionesDTO.size()) {
            mostrarInformacionCancion(cancionesDTO.get(indiceActual));
        }
    }

    private void cambiarAImagenPlay() {
        try {
            Image img = new Image(getClass().getResourceAsStream("/UserInterface/Resources/ReproductorMusical/img/boton-de-play.png"));
            imgPlayPause.setImage(img);
            imgPlayPause.setFitWidth(29.0);
            imgPlayPause.setFitHeight(26.0);
        } catch (Exception e) {
            imgPlayPause.setImage(new Image(getClass().getResourceAsStream("/UserInterface/Resources/ReproductorMusical/img/play.png")));
        }
    }

    private void cambiarAImagenPause() {
        try {
            Image img = new Image(getClass().getResourceAsStream("/UserInterface/Resources/ReproductorMusical/img/boton-de-pausa.png"));
            imgPlayPause.setImage(img);
            imgPlayPause.setFitWidth(40.0);
            imgPlayPause.setFitHeight(35.0);
        } catch (Exception e) {
            imgPlayPause.setImage(new Image(getClass().getResourceAsStream("/UserInterface/Resources/ReproductorMusical/img/pause.png")));
        }
    }

    public void mostrarInformacionCancion(CancionDTO cancion) {
        lblNombreCancion.setText(cancion.getTitulo());
        lblNombreCancion1.setText(cancion.getTitulo());
        if (cancion.getArtistas() != null && !cancion.getArtistas().isEmpty()) {
            String artista = cancion.getArtistas().get(0).getNombre();
            lblArtista.setText(artista);
            lblArtista1.setText(artista);
        } else {
            lblArtista.setText("Desconocido");
            lblArtista1.setText("Desconocido");
        }
        duracionRealCancion = cancion.getDuracion();
        lblTiempoCancion.setText("00:00 / " + formatearTiempo(duracionRealCancion));
        mostrarPortadaCancion(cancion);
    }

    private void mostrarPortadaCancion(CancionDTO cancion) {
        try {
            Image portada;
            if (cancion.getPortada() != null && cancion.getPortada().length > 0) {
                portada = new Image(new java.io.ByteArrayInputStream(cancion.getPortada()));
            } else {
                portada = new Image(getClass().getResourceAsStream("/UserInterface/Resources/ReproductorMusical/img/portada-generica.png"));
            }
            ImageView grande = new ImageView(portada);
            grande.setFitWidth(panImageAlbum.getPrefWidth());
            grande.setFitHeight(panImageAlbum.getPrefHeight());
            grande.setPreserveRatio(true);

            panImageAlbum.getChildren().setAll(grande);

            ImageView pequeno = new ImageView(portada);
            pequeno.setFitWidth(panImageAlbum1.getPrefWidth());
            pequeno.setFitHeight(panImageAlbum1.getPrefHeight());
            pequeno.setPreserveRatio(true);

            panImageAlbum1.getChildren().setAll(pequeno);

        } catch (Exception e) {
            System.out.println("Error cargando portada");
        }
    }

    public void cleanup() {
        if (timeline != null) timeline.stop();
        if (reproductor != null) reproductor.detener();
    }
}
