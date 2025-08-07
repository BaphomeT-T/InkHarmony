package UserInterface.CustomerControl.Recomendaciones;

import BusinessLogic.*;
import DataAccessComponent.DTO.CancionDTO;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class BarraReproduccionController implements Initializable {

    /* ───── FXML ───── */
    @FXML
    private HBox barraReproduccion;
    @FXML
    private ImageView imgCancionActual,
            imgAnterior, // nuevos fx:id para iconos
            imgSiguiente;
    @FXML
    private Label lblNombreCancion, lblNombreArtista,
            lblTiempoActual, lblTiempoTotal;
    @FXML
    private Button btnAnterior, btnPlayPause, btnSiguiente, btnVolumen;
    @FXML
    private Slider pgbProgresoCancion, sliderVolumen;

    /* ───── estado ───── */
    private final ReproductorMP3 reproductor = ReproductorMP3.getInstancia(new ArrayList<>());
    private List<CancionDTO> canciones = List.of();
    private Timeline timeline;
    private double duracion = 0, posActual = 0;
    private boolean arrastrando = false;

    private Image iconPlay, iconPause, coverDefault;

    /* ==================================================== */

    @Override
    public void initialize(URL u, ResourceBundle r) {
        cargarImagenes();
        configurarSliders();
        configurarTimeline();
        ocultar();

        reproductor.setOnSongChange(() -> Platform.runLater(() -> {
            actualizarInfo();
            reiniciar();
            setIcono(false);
            timeline.play();
        }));
    }

    /* ===== PUBLIC API ===== */

    public void reproducir(List<CancionDTO> lista, int indice) {
        if (lista == null || lista.isEmpty())
            return;

        canciones = List.copyOf(lista);

        List<byte[]> mp3 = lista.stream()
                .map(CancionDTO::getArchivoMP3)
                .filter(Objects::nonNull).toList();
        reproductor.cambiarPlaylist(mp3);
        reproductor.getPlaylist().setIndiceActual(indice);
        reproductor.reproducir();

        mostrar();
        actualizarInfo();
        timeline.play();
        setIcono(false);
    }

    public void mostrarSiActiva(List<CancionDTO> lista) {
        if (reproductor.estaReproduciendo() || reproductor.estaPausado()) {
            canciones = lista;
            mostrar();
            actualizarInfo();
            actualizarDur();
            timeline.play();
            setIcono(!reproductor.estaReproduciendo());
        }
    }

    /* ===== botones ===== */

    @FXML
    private void clickAnterior() {
        reproductor.anterior();
    }

    @FXML
    private void clickSiguiente() {
        reproductor.siguiente();
    }

    @FXML
    private void clickPlayPause() {
        if (reproductor.estaReproduciendo()) {
            reproductor.pausar();
            timeline.stop();
            setIcono(true);
        } else {
            reproductor.reproducir();
            if (reproductor.getEstado() instanceof EstadoPausado)
                reproductor.reanudar();
            actualizarDur();
            timeline.play();
            setIcono(false);
        }
    }

    @FXML
    private void clickVolumen() {
        var mp = reproductor.getMediaPlayer();
        if (mp == null)
            return;
        mp.setMute(!mp.isMute());
        btnVolumen.setText(mp.isMute() ? "🔇" : "🔊");
        // al mutear, mantenemos la posición del slider sin moverla
    }

    /* ===== helpers ===== */

    private void cargarImagenes() {
        iconPlay = new Image(getClass().getResource("/UserInterface/Resources/img/boton-de-play.png").toExternalForm());
        iconPause = new Image(
                getClass().getResource("/UserInterface/Resources/img/boton-de-pausa.png").toExternalForm());

        imgAnterior.setImage(
                new Image(getClass().getResource("/UserInterface/Resources/img/pista-anterior.png").toExternalForm()));
        imgSiguiente.setImage(
                new Image(getClass().getResource("/UserInterface/Resources/img/siguiente-pista.png").toExternalForm()));
    }

    private void configurarSliders() {

        /* ─── volumen ─── */
        sliderVolumen.valueProperty().addListener((o, ov, nv) -> {
            if (reproductor.getMediaPlayer() != null)
                reproductor.getMediaPlayer().setVolume(nv.doubleValue());
        });

        /* ─── progreso ─── */
        // 1. clic directo sobre la pista
        pgbProgresoCancion.setOnMousePressed(e -> {
            double prog = e.getX() / pgbProgresoCancion.getWidth(); // 0‒1
            prog = Math.max(0, Math.min(1, prog));
            pgbProgresoCancion.setValue(prog);
            saltarA(prog); // ← mueve la canción
            arrastrando = true; // evita “rebote” mientras arrastras
        });

        // 2. arrastre
        pgbProgresoCancion.valueChangingProperty().addListener((o, was, nw) -> {
            arrastrando = nw;
            if (!nw) { // sueltas el ratón
                saltarA(pgbProgresoCancion.getValue());
            }
        });
    }

    private void configurarTimeline() {
        timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> refreshProgress()));
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    private void refreshProgress() {
        if (reproductor.getMediaPlayer() == null)
            return;
        posActual = reproductor.getTiempoActual();
        if (duracion > 0 && !arrastrando)
            pgbProgresoCancion.setValue(Math.max(0, Math.min(1, posActual / duracion)));
        lblTiempoActual.setText(fmt(posActual));
    }

    private void saltarA(double progreso) {
        if (duracion == 0 || reproductor.getMediaPlayer() == null)
            return;
        double segundos = progreso * duracion;
        reproductor.getMediaPlayer().seek(Duration.seconds(segundos));
        posActual = segundos;
        lblTiempoActual.setText(fmt(posActual));
    }

    private void actualizarInfo() {
        int i = reproductor.getPlaylist().getIndiceActual();
        if (i < 0 || i >= canciones.size())
            return;
        var c = canciones.get(i);
        lblNombreCancion.setText(c.getTitulo());
        lblNombreArtista.setText(c.getArtistas() == null || c.getArtistas().isEmpty()
                ? "Artista desconocido"
                : c.getArtistas().stream().map(a -> a.getNombre()).collect(Collectors.joining(", ")));
        Image cover;
        try {
            cover = c.getPortada() != null && c.getPortada().length > 0
                    ? new Image(new ByteArrayInputStream(c.getPortada()))
                    : coverDefault;
        } catch (Exception ex) {
            cover = coverDefault;
        }
        imgCancionActual.setImage(cover);
        duracion = c.getDuracion();
        lblTiempoTotal.setText(fmt(duracion));
    }

    private void reiniciar() {
        timeline.stop();
        pgbProgresoCancion.setValue(0);
        posActual = 0;
        duracion = 0;
        lblTiempoActual.setText("00:00");
        lblTiempoTotal.setText("00:00");
    }

    private void actualizarDur() {
        reproductor.obtenerDuracionCancionActual(d -> Platform.runLater(() -> {
            duracion = d;
            lblTiempoTotal.setText(fmt(d));
        }));
    }

    private static String fmt(double s) {
        int m = (int) (s / 60), seg = (int) (s % 60);
        return "%02d:%02d".formatted(m, seg);
    }

    private void setIcono(boolean play) {
        ImageView iv = new ImageView(play ? iconPlay : iconPause);
        iv.setFitHeight(26);
        iv.setFitWidth(26);
        btnPlayPause.setGraphic(iv);
    }

    /* visibilidad */
    public void mostrar() {
        barraReproduccion.setVisible(true);
    }

    public void ocultar() {
        barraReproduccion.setVisible(false);
    }
    
    public void actualizarUIDesdeReproductor() {
        ReproductorMP3 reproductor = ReproductorMP3.getInstancia(null);
        if (reproductor == null || reproductor.getPlaylist() == null)
            return;

        int indiceActual = reproductor.getPlaylist().getIndiceActual();
        // Usa la lista que guardaste al abrir la ventana
        List<CancionDTO> cancionesActuales = this.canciones;
        if (cancionesActuales == null || cancionesActuales.isEmpty()) return;

        if (indiceActual >= 0 && indiceActual < cancionesActuales.size()) {
            CancionDTO cancionActual = cancionesActuales.get(indiceActual);
            lblNombreCancion.setText(cancionActual.getTitulo());
            lblNombreArtista.setText(
                cancionActual.getArtistas() == null || cancionActual.getArtistas().isEmpty()
                    ? "Artista desconocido"
                    : cancionActual.getArtistas().stream().map(a -> a.getNombre()).collect(Collectors.joining(", "))
            );
            Image cover;
            try {
                cover = cancionActual.getPortada() != null && cancionActual.getPortada().length > 0
                        ? new Image(new ByteArrayInputStream(cancionActual.getPortada()))
                        : coverDefault;
            } catch (Exception ex) {
                cover = coverDefault;
            }
            imgCancionActual.setImage(cover);
            duracion = cancionActual.getDuracion();
            lblTiempoTotal.setText(fmt(duracion));
        }
    }
public void setCanciones(List<CancionDTO> canciones) {
    if (canciones != null && !canciones.isEmpty()) {
        this.canciones = List.copyOf(canciones);
    }
}
}
