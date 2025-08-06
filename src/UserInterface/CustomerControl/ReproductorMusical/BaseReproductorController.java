package UserInterface.CustomerControl.ReproductorMusical;

import BusinessLogic.ReproductorMusical.EstadoPausado;
import BusinessLogic.ReproductorMusical.ReproductorMP3;
import DataAccessComponent.DAO.CancionDAO;
import DataAccessComponent.DTO.CancionDTO;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseReproductorController {

    protected ReproductorMP3 reproductor;
    protected Timeline timeline;
    protected double duracionRealCancion = 0;
    protected double tiempoActualSegundos = 0;
    protected List<CancionDTO> cancionesDTO;
    protected List<byte[]> cancionesByteArray;
    protected boolean usuarioArrastrando = false;

    @FXML protected Slider pgbProgresoCancion;
    @FXML protected Label lblTiempoCancion;
    @FXML protected Label lblArtista;
    @FXML protected Label lblNombreCancion;
    @FXML protected Pane panImageAlbum1;
    @FXML protected Button btnReproducir;
    @FXML protected ImageView imgPlayPause;
    @FXML protected Label lblNombreCancion1;
    @FXML protected Label lblArtista1;

    protected void inicializarBase(String iconPath) {
        cargarCanciones();
        inicializarTimeline();

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
                cambiarAImagenPause(iconPath);
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

    protected void cargarCanciones() {
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
                agregarCancionesLocales();
            }

            if (!cancionesByteArray.isEmpty()) {
                reproductor = ReproductorMP3.getInstancia(cancionesByteArray);
                mostrarInformacionCancion(cancionesDTO.get(0));
            }
        } catch (Exception e) {
            agregarCancionesLocales();
        }
    }

    protected void agregarCancionesLocales() {
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
                    cancionDTO.setDuracion(180);
                    cancionDTO.setAnio(2025);
                    cancionDTO.setFechaRegistro(java.time.LocalDateTime.now());
                    cancionDTO.setArchivoMP3(mp3Bytes);
                    cancionesDTO.add(cancionDTO);
                }
            }
        } catch (Exception ignored) {}
    }

    protected byte[] cargarArchivoComoBytes(String ruta) {
        try (FileInputStream fis = new FileInputStream(new File(ruta))) {
            byte[] datos = new byte[(int) new File(ruta).length()];
            fis.read(datos);
            return datos;
        } catch (Exception e) {
            return null;
        }
    }

    protected void inicializarTimeline() {
        timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> actualizarProgressBar()));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    protected void actualizarProgressBar() {
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

    protected void actualizarTiempoCancion(double segundosActuales, double segundosTotales) {
        String tiempoActual = formatearTiempo(segundosActuales);
        String tiempoTotal = formatearTiempo(segundosTotales > 0 ? segundosTotales : 0);
        lblTiempoCancion.setText(tiempoActual + " / " + tiempoTotal);
    }

    protected String formatearTiempo(double segundos) {
        int minutos = (int) (segundos / 60);
        int segundosRestantes = (int) (segundos % 60);
        return String.format("%02d:%02d", minutos, segundosRestantes);
    }

    @FXML
    protected void clickAnterior(ActionEvent event) {
        if (reproductor != null) reproductor.anterior();
    }

    @FXML
    protected void clickSiguiente(ActionEvent event) {
        if (reproductor != null) reproductor.siguiente();
    }

    protected void clickReproducir(ActionEvent event, String iconPath) {
        if (reproductor == null) return;
        if (reproductor.estaReproduciendo()) {
            reproductor.pausar();
            timeline.stop();
            cambiarAImagenPlay(iconPath);
        } else if (reproductor.getEstado() instanceof EstadoPausado) {
            reproductor.reanudar();
            timeline.play();
            cambiarAImagenPause(iconPath);
        } else {
            reproductor.reproducir();
            reiniciarProgresoYTiempo();
            actualizarDuracionActual();
            timeline.play();
            cambiarAImagenPause(iconPath);
        }
    }

    protected void reiniciarProgresoYTiempo() {
        timeline.stop();
        pgbProgresoCancion.setValue(0.0);
        tiempoActualSegundos = 0.0;
        duracionRealCancion = 0.0;
        lblTiempoCancion.setText("00:00 / 00:00");
        javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(Duration.millis(500));
        delay.setOnFinished(e -> actualizarDuracionActual());
        delay.play();
    }

    protected void actualizarDuracionActual() {
        reproductor.obtenerDuracionCancionActual(duracion -> {
            Platform.runLater(() -> {
                duracionRealCancion = duracion;
                actualizarTiempoCancion(tiempoActualSegundos, duracionRealCancion);
            });
        });
    }

    protected void actualizarInformacionCancionActual() {
        int indiceActual = reproductor.getPlaylist().getIndiceActual();
        if (indiceActual >= 0 && indiceActual < cancionesDTO.size()) {
            mostrarInformacionCancion(cancionesDTO.get(indiceActual));
        }
    }

    protected void cambiarAImagenPlay(String basePath) {
        try {
            imgPlayPause.setImage(new Image(getClass().getResourceAsStream(basePath + "boton-de-play.png")));
        } catch (Exception e) {
            imgPlayPause.setImage(new Image(getClass().getResourceAsStream(basePath + "play.png")));
        }
    }

    protected void cambiarAImagenPause(String basePath) {
        try {
            imgPlayPause.setImage(new Image(getClass().getResourceAsStream(basePath + "boton-de-pausa.png")));
        } catch (Exception e) {
            imgPlayPause.setImage(new Image(getClass().getResourceAsStream(basePath + "pause.png")));
        }
    }

    protected void mostrarInformacionCancion(CancionDTO cancion) {
        lblNombreCancion.setText(cancion.getTitulo());
        if (lblNombreCancion1 != null) {
            lblNombreCancion1.setText(cancion.getTitulo());
        }

        String artista = (cancion.getArtistas() != null && !cancion.getArtistas().isEmpty())
                ? cancion.getArtistas().get(0).getNombre()
                : "Desconocido";

        lblArtista.setText(artista);
        if (lblArtista1 != null) {
            lblArtista1.setText(artista);
        }

        duracionRealCancion = cancion.getDuracion();
        lblTiempoCancion.setText("00:00 / " + formatearTiempo(duracionRealCancion));

        mostrarPortadaCancion(cancion);
    }


    protected void mostrarPortadaCancion(CancionDTO cancion) {
        try {
            Image portada;
            if (cancion.getPortada() != null && cancion.getPortada().length > 0) {
                portada = new Image(new java.io.ByteArrayInputStream(cancion.getPortada()));
            } else {
                portada = new Image(getClass().getResourceAsStream("/UserInterface/Resources/ReproductorMusical/img/portada-generica.png"));
            }
            ImageView img = new ImageView(portada);
            img.setFitWidth(panImageAlbum1.getPrefWidth());
            img.setFitHeight(panImageAlbum1.getPrefHeight());
            img.setPreserveRatio(true);
            panImageAlbum1.getChildren().setAll(img);
        } catch (Exception e) {
            System.out.println("Error cargando portada");
        }
    }

    public void cleanup() {
        if (timeline != null) timeline.stop();
        if (reproductor != null) reproductor.detener();
    }

    @FXML
    protected void clickBuscarCancion(ActionEvent event) {
        System.out.println("Función de búsqueda por implementar");
    }

    @FXML
    protected void clickRegresarPagina(ActionEvent event) {
        System.out.println("Función de regresar a la página anterior por implementar");
    }

    @FXML
    protected void clickNuevaBiblioteca(ActionEvent event) {
        System.out.println("Función redireccionar interfaz para crear nueva biblioteca");
    }

    @FXML
    protected void clickBuscarBiblioteca(ActionEvent event) {
        System.out.printf("Funcion CRUD de playlyst");
    }


}
