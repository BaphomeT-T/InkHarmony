package UserInterface.CustomerControl.Recomendaciones;

import BusinessLogic.ServicioRecomendaciones;
import BusinessLogic.Sesion;
import BusinessLogic.Genero;
import DataAccessComponent.DTO.CancionDTO;
import DataAccessComponent.DTO.UsuarioDTO;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.stream.Collectors;

public class GeneroController {

    @FXML private Button btnBuscar;
    @FXML private TextField txtBuscarGenero;

    @FXML private TableView<CancionDTO> tablaCanciones;
    @FXML private TableColumn<CancionDTO, ImageView> colImagen;
    @FXML private TableColumn<CancionDTO, String>    colTitulo;
    @FXML private TableColumn<CancionDTO, String>    colArtista;
    @FXML private TableColumn<CancionDTO, Integer>   colAnio;
    @FXML private TableColumn<CancionDTO, String>    colDuracion;

    private final ServicioRecomendaciones servicio = new ServicioRecomendaciones();

    @FXML
    public void initialize() {
        configurarColumnas();

        tablaCanciones.setPlaceholder(new Label("Ingresa un género para buscar"));

        btnBuscar.setOnAction(this::buscarCancionesPorGenero);

        txtBuscarGenero.setOnKeyPressed(ev -> {
            if (ev.getCode() == KeyCode.ENTER) {
                buscarCancionesPorGenero(null);
            }
        });
    }

    @FXML
    void buscarCancionesPorGenero(ActionEvent e) {
        String texto = txtBuscarGenero.getText().trim().toUpperCase();
        if (texto.isBlank()) {
            tablaCanciones.getItems().clear();
            return;
        }

        Genero genero;
        try {
            genero = Genero.valueOf(texto);
        } catch (IllegalArgumentException ex) {
            tablaCanciones.setPlaceholder(new Label("Género no válido: " + texto));
            tablaCanciones.getItems().clear();
            return;
        }

        UsuarioDTO usuario = (UsuarioDTO) Sesion.getSesion().obtenerUsuarioActual();
        List<CancionDTO> lista = servicio.recomendarCanciones(usuario, genero, false);
        tablaCanciones.setItems(FXCollections.observableArrayList(lista));
    }

    private void configurarColumnas() {
        colImagen.setCellValueFactory(datos -> {
            byte[] portada = datos.getValue().getPortada();
            if (portada != null) {
                ImageView view = new ImageView(new Image(new ByteArrayInputStream(portada)));
                view.setFitWidth(60); view.setFitHeight(60);
                return new SimpleObjectProperty<>(view);
            }
            return new SimpleObjectProperty<>(null);
        });

        colTitulo .setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTitulo()));
        colArtista.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getArtistas().stream()
                         .map(a -> a.getNombre())
                         .collect(Collectors.joining(", "))
        ));
        colAnio   .setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getAnio()).asObject());
        colDuracion.setCellValueFactory(d -> {
            double s = d.getValue().getDuracion();
            return new SimpleStringProperty(String.format("%d:%02d", (int) s / 60, (int) s % 60));
        });
    }
}
