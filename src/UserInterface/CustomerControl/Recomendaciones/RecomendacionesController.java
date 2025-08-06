package UserInterface.CustomerControl.Recomendaciones;

import BusinessLogic.*;
import DataAccessComponent.DTO.CancionDTO;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controla la única pantalla de Recomendaciones.
 * - Cada botón actúa como “toggle” → aplica o quita su filtro.
 * - Los <TextField> de artista / género filtran en vivo al escribir.
 */
public class RecomendacionesController {

    // ---------- estilos ----------
    private static final String BASE_STYLE = "-fx-background-color: #5A5A80; -fx-text-fill: white; -fx-background-radius: 40; -fx-padding: 20 40;";
    private static final String PRESSED_STYLE = "-fx-background-color: #9190C2; -fx-text-fill: white; -fx-background-radius: 40; -fx-padding: 20 40;";

    // ---------- estado de filtros ----------
    private boolean filtroGenero = false;
    private boolean filtroArtista = false;
    private boolean filtroPrefer = false;
    private boolean filtroEstrenos = false;

    // ---------- servicios y datos ----------
    private final ServicioRecomendaciones servicio = new ServicioRecomendaciones();
    private final ObservableList<CancionDTO> datos = FXCollections.observableArrayList();

    // ---------- FXML ----------
    @FXML
    private Button btnLimpiarFiltros, btnGenero, btnArtista, btnPersonalizadas,
            btnEstrenos, cerrarButton;
    @FXML
    private TextField txtBuscarGenero, txtBuscarArtista;
    @FXML
    private HBox filtrosBox;
    @FXML
    private Label mensajeBienvenida, mensajeSelecciona;
    @FXML
    private TableView<CancionDTO> tablaCanciones;
    @FXML
    private TableColumn<CancionDTO, CancionDTO> colTituloConImagen;
    @FXML
    private TableColumn<CancionDTO, String> colArtista, colGenero, colAnio, colDuracion;
    @FXML
    private TableColumn<CancionDTO, Void> colPlay;
    @FXML
    private StackPane contentPane;

    private TableHeaderRow headerRow;

    // ---------- init ----------
    @FXML
    private void initialize() {
        configurarColumnas();
        configurarBusquedasEnVivo();
        tablaCanciones.setItems(datos);
        tablaCanciones.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        configurarAnchuraDinamica();

        // ─── localizar la fila de encabezado ───
        Platform.runLater(() -> {
            headerRow = (TableHeaderRow) tablaCanciones.lookup("TableHeaderRow");
            if (headerRow != null) { // al inicio la tabla está vacía
                headerRow.setVisible(false);
                headerRow.setManaged(false);
            }
        });

        // arranque: UI limpia
        filtrosBox.setVisible(false);
        filtrosBox.setManaged(false);
        txtBuscarGenero.setVisible(false);
        txtBuscarGenero.setManaged(false);
        txtBuscarArtista.setVisible(false);
        txtBuscarArtista.setManaged(false);
    }

    // ---------- handlers de botones ----------
    @FXML
    private void handlePorGenero() {
        toggleFiltroGenero();
    }

    @FXML
    private void handlePorArtista() {
        toggleFiltroArtista();
    }

    @FXML
    private void handlePersonalizadas() {
        toggleFiltroPreferencias();
    }

    @FXML
    private void handleEstrenos() {
        toggleFiltroEstrenos();
    }

    @FXML
    private void handleLimpiarFiltros() {
        filtroGenero = filtroArtista = filtroPrefer = filtroEstrenos = false;
        txtBuscarGenero.clear();
        txtBuscarArtista.clear();
        actualizarEstilosBotones();
        actualizarVisibilidadCampos();
        refrescarTabla();
    }

    @FXML
    private void cerrarVentana(ActionEvent e) {
        ((Button) e.getSource()).getScene().getWindow().hide();
    }

    // ---------- lógica de toggles ----------
    private void toggleFiltroGenero() {
        filtroGenero = !filtroGenero;
        if (!filtroGenero) txtBuscarGenero.clear();
        actualizarEstilosBotones();
        actualizarVisibilidadCampos();
        refrescarTabla();
    }

    private void toggleFiltroArtista() {
        filtroArtista = !filtroArtista;
        if (!filtroArtista) txtBuscarArtista.clear();
        actualizarEstilosBotones();
        actualizarVisibilidadCampos();
        refrescarTabla();
    }

    private void toggleFiltroPreferencias() {
        filtroPrefer = !filtroPrefer;
        actualizarEstilosBotones();
        actualizarVisibilidadCampos();
        refrescarTabla();
    }

    private void toggleFiltroEstrenos() {
        filtroEstrenos = !filtroEstrenos;
        actualizarEstilosBotones();
        actualizarVisibilidadCampos();
        refrescarTabla();
    }

    private void actualizarEstilosBotones() {
        btnGenero.setStyle(filtroGenero ? PRESSED_STYLE : BASE_STYLE);
        btnArtista.setStyle(filtroArtista ? PRESSED_STYLE : BASE_STYLE);
        btnPersonalizadas.setStyle(filtroPrefer ? PRESSED_STYLE : BASE_STYLE);
        btnEstrenos.setStyle(filtroEstrenos ? PRESSED_STYLE : BASE_STYLE);
    }

    private void actualizarVisibilidadCampos() {
        boolean mostrarBox = filtroGenero || filtroArtista || filtroPrefer || filtroEstrenos;
        filtrosBox.setVisible(mostrarBox);
        filtrosBox.setManaged(mostrarBox);

        txtBuscarGenero.setVisible(filtroGenero);
        txtBuscarGenero.setManaged(filtroGenero);
        txtBuscarArtista.setVisible(filtroArtista);
        txtBuscarArtista.setManaged(filtroArtista);
    }

    // ---------- busca / refresca ----------
    private void configurarBusquedasEnVivo() {
        txtBuscarGenero.textProperty().addListener((obs, oldVal, newVal) -> refrescarTabla());
        txtBuscarArtista.textProperty().addListener((obs, oldVal, newVal) -> refrescarTabla());
    }

    private void configurarAnchuraDinamica() {
        // Esperamos a que el Scene y el Stage existan
        Platform.runLater(() -> {
            Stage stage = (Stage) contentPane.getScene().getWindow();

            // 1ª vez: parte en 900 px
            contentPane.setPrefWidth(975);

            // Cada vez que el usuario maximice / restaure
            stage.maximizedProperty().addListener((obs, wasMax, isMax) -> {
                contentPane.setPrefWidth(isMax ? 1242 : 975);
            });
        });
    }

    /** Llena la tabla según los filtros activos y el texto escrito. */
    private void refrescarTabla() {

        /* ---------- A. Sin filtros ---------- */
        if (!mostrarFiltrosActivos()) {
            datos.clear();
            actualizarHeader(); // ← NUEVO
            mensajeBienvenida.setVisible(true);
            mensajeSelecciona.setVisible(true);
            mensajeSelecciona.setText("Selecciona uno o más filtros");
            return;
        }

        /* ---------- B. Falta texto ---------- */
        String textoGenero = filtroGenero ? txtBuscarGenero.getText().trim() : "";
        String textoArtista = filtroArtista ? txtBuscarArtista.getText().trim() : "";

        boolean generoPend = filtroGenero && textoGenero.isBlank();
        boolean artistaPend = filtroArtista && textoArtista.isBlank();
        boolean faltaTextoEnTodos = (generoPend || artistaPend)
                && !(filtroGenero && !generoPend)
                && !(filtroArtista && !artistaPend)
                && !(filtroPrefer || filtroEstrenos);

        if (faltaTextoEnTodos) {
            datos.clear();
            actualizarHeader(); // ← NUEVO
            mensajeBienvenida.setVisible(false);
            mensajeSelecciona.setVisible(true);
            mensajeSelecciona.setText(
                    generoPend && artistaPend ? "Ingresa un género o artista"
                            : generoPend ? "Ingresa un género"
                                    : "Ingresa un artista");
            return;
        }

        /* ---------- C. Consulta al servicio ---------- */
        List<CancionDTO> base = servicio.recomendar(
                filtroPrefer, null,
                filtroArtista ? textoArtista : null,
                filtroEstrenos);

        List<CancionDTO> resultado = base;
        if (filtroGenero && !textoGenero.isBlank()) {
            String patron = textoGenero.toLowerCase();
            resultado = base.stream()
                    .filter(c -> c.getGeneros() != null &&
                            c.getGeneros().stream()
                                    .anyMatch(g -> g.name().toLowerCase()
                                            .contains(patron)))
                    .toList();
        }

        datos.setAll(resultado);
        actualizarHeader();

        if (resultado.isEmpty()) {
            mensajeBienvenida.setVisible(false);
            mensajeSelecciona.setVisible(true);
            mensajeSelecciona.setText("No hay coincidencias");
        } else {
            mensajeBienvenida.setVisible(false);
            mensajeSelecciona.setVisible(false);
        }
    }

    private void actualizarHeader() {
        if (headerRow != null) {
            boolean hayDatos = !datos.isEmpty();
            headerRow.setVisible(hayDatos);
            headerRow.setManaged(hayDatos);
        }
    }

    /** Devuelve true si al menos un filtro está ON. */
    private boolean mostrarFiltrosActivos() {
        return filtroGenero || filtroArtista || filtroPrefer || filtroEstrenos;
    }

    // ---------- columnas ----------
    private void configurarColumnas() {

        // ── columna TÍTULO + PORTADA ──
        colTituloConImagen.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue()));

        colTituloConImagen.setCellFactory(tc -> new TableCell<>() {

            private final ImageView portada = new ImageView();
            private final Label titulo = new Label();
            private final HBox box = new HBox(10, portada, titulo);

            {
                portada.setFitWidth(40);
                portada.setFitHeight(40);
                titulo.setStyle("-fx-font-weight: bold;");

                box.setAlignment(Pos.CENTER_LEFT);

                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setAlignment(Pos.CENTER_LEFT);
            }

            @Override
            protected void updateItem(CancionDTO c, boolean empty) {
                super.updateItem(c, empty);
                if (empty || c == null) {
                    setGraphic(null);
                } else {
                    if (c.getPortada() != null)
                        portada.setImage(new Image(new ByteArrayInputStream(c.getPortada())));
                    titulo.setText(c.getTitulo());
                    setGraphic(box);
                }
            }
        });

        // ── ARTISTA(S) ──
        colArtista.setCellValueFactory(cd -> Bindings.createStringBinding(() -> cd.getValue().getArtistas().stream()
                .map(a -> a.getNombre())
                .collect(Collectors.joining(", "))));

        // ── GÉNERO(S) ──
        colGenero.setCellValueFactory(cd -> Bindings.createStringBinding(() -> cd.getValue().getGeneros().stream()
                .map(Enum::name)
                .collect(Collectors.joining(", "))));

        // ── AÑO ──
        colAnio.setCellValueFactory(cd -> Bindings.createStringBinding(
                () -> String.valueOf(cd.getValue().getAnio())));

        // ── DURACIÓN ──
        colDuracion.setCellValueFactory(cd -> Bindings.createStringBinding(
                () -> cd.getValue().getDuracion() + " s"));

        // ── PLAY ──
        colPlay.setCellFactory(col -> new TableCell<>() {

            private final Button btnPlay = new Button();

            {
                Image img = new Image(getClass().getResourceAsStream(
                        "/UserInterface/Resources/img/play.png"), 11, 11, true, true);
                btnPlay.setGraphic(new ImageView(img));
                btnPlay.setStyle("""
                            -fx-background-color: #9190C2;
                            -fx-background-radius: 20;
                            -fx-cursor: hand;
                        """);
                btnPlay.setOnAction(e -> {
                });
            }

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : btnPlay);
                setStyle("-fx-alignment:center;");
            }
        });
    }
}