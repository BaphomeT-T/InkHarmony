package UserInterface.CustomerControl.Recomendaciones;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class GeneroController {

    @FXML
    private Button btnBuscar;

    @FXML
    private TableColumn<?, ?> colAnio;

    @FXML
    private TableColumn<?, ?> colArtista;

    @FXML
    private TableColumn<?, ?> colDuracion;

    @FXML
    private TableColumn<?, ?> colImagen;

    @FXML
    private TableColumn<?, ?> colTitulo;

    @FXML
    private TableView<?> tablaCanciones;

    @FXML
    private TextField txtBuscarGenero;

    @FXML
    void buscarCancionesPorGenero(ActionEvent event) {

    }

}
