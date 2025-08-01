/*
package UserInterface.CustomerControl.Recomendaciones;

import BusinessLogic.ServicioRecomendaciones;
import BusinessLogic.Sesion;
import DataAccessComponent.DTO.CancionDTO;
import DataAccessComponent.DTO.UsuarioDTO;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.stream.Collectors;

public class PersonalizadasController {

    @FXML private Button btnActualizar;
    @FXML private TableView<CancionDTO> tablaCanciones;
    @FXML private TableColumn<CancionDTO,ImageView> colImagen;
    @FXML private TableColumn<CancionDTO,String>   colTitulo;
    @FXML private TableColumn<CancionDTO,String>   colArtista;
    @FXML private TableColumn<CancionDTO,Integer>  colAnio;
    @FXML private TableColumn<CancionDTO,String>   colDuracion;

    private final ServicioRecomendaciones servicio = new ServicioRecomendaciones();

    @FXML
    public void initialize() {
        configurarColumnas();
        tablaCanciones.setPlaceholder(
            new Label("Inicia sesión para obtener recomendaciones"));
        btnActualizar.setOnAction(e -> cargarRecomendaciones());
        cargarRecomendaciones();
    }

    private void cargarRecomendaciones() {
        UsuarioDTO usuario = (UsuarioDTO) Sesion.getSesion().obtenerUsuarioActual();
        if (usuario == null) {
            tablaCanciones.getItems().clear();
            return;
        }
        List<CancionDTO> lista = servicio.recomendarCanciones(usuario, null, false);
        tablaCanciones.setItems(FXCollections.observableArrayList(lista));
    }

    private void configurarColumnas() {
        colImagen.setCellValueFactory(d -> {
            byte[] portada = d.getValue().getPortada();
            if (portada != null) {
                ImageView img = new ImageView(
                        new Image(new ByteArrayInputStream(portada)));
                img.setFitWidth(60); img.setFitHeight(60);
                return new SimpleObjectProperty<>(img);
            }
            return new SimpleObjectProperty<>(null);
        });
        colTitulo .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTitulo()));
        colArtista.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getArtistas().stream()
                        .map(a -> a.getNombre())
                        .collect(Collectors.joining(", "))));
        colAnio   .setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getAnio()).asObject());
        colDuracion.setCellValueFactory(d -> {
            double s = d.getValue().getDuracion();
            return new SimpleStringProperty(String.format("%d:%02d",(int)s/60,(int)s%60));
        });
    }
}
*/
