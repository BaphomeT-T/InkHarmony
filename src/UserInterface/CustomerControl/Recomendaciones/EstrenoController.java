package UserInterface.CustomerControl.Recomendaciones;

import BusinessLogic.ServicioRecomendaciones;
import BusinessLogic.Sesion;
import DataAccessComponent.DTO.UsuarioDTO;
import DataAccessComponent.DTO.CancionDTO;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class EstrenoController {

    @FXML private TableView<CancionDTO> tablaCancionesPersonalizadas;

    @FXML private TableColumn<CancionDTO, ImageView> colImagen;
    @FXML private TableColumn<CancionDTO, String>    colTitulo;
    @FXML private TableColumn<CancionDTO, String>    colArtista;
    @FXML private TableColumn<CancionDTO, Integer>   colAnio;
    @FXML private TableColumn<CancionDTO, String>    colDuracion;
    @FXML private TableColumn<CancionDTO, String>    colFechaAgregacion;

    private final ServicioRecomendaciones servicio = new ServicioRecomendaciones();
    private final DateTimeFormatter fmtFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarEstrenos();
    }

    @FXML
    void actualizarEstrenos(ActionEvent e) {
        cargarEstrenos();
    }

    private void configurarColumnas() {
        colImagen.setCellValueFactory(datos -> {
            byte[] portada = datos.getValue().getPortada();
            if (portada != null) {
                Image img = new Image(new ByteArrayInputStream(portada));
                ImageView view = new ImageView(img);
                view.setFitWidth(60); view.setFitHeight(60);
                return new SimpleObjectProperty<>(view);
            }
            return new SimpleObjectProperty<>(null);
        });

        colTitulo .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTitulo()));
        colArtista.setCellValueFactory(d -> {
            String artistas = d.getValue().getArtistas()
                                .stream()
                                .map(a -> a.getNombre())
                                .collect(Collectors.joining(", "));
            return new SimpleStringProperty(artistas);
        });
        colAnio   .setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getAnio()).asObject());
        colDuracion.setCellValueFactory(d -> {
            double s = d.getValue().getDuracion();
            int min = (int) s / 60, seg = (int) s % 60;
            return new SimpleStringProperty(String.format("%d:%02d", min, seg));
        });
        colFechaAgregacion.setCellValueFactory(
            d -> new SimpleStringProperty(d.getValue().getFechaRegistro().format(fmtFecha)));
    }

    private void cargarEstrenos() {
        try {
            UsuarioDTO usuario = (UsuarioDTO) Sesion.getSesion().obtenerUsuarioActual();
            List<CancionDTO> lista = servicio.recomendarCanciones(usuario, null, true);
            ObservableList<CancionDTO> datos = FXCollections.observableArrayList(lista);
            tablaCancionesPersonalizadas.setItems(datos);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
