package UserInterface.CustomerControl.RecomendacionesControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

public class PlaylistController {

    @FXML
    private Button btnBuscar;

    @FXML
    private TableColumn<?, ?> colAnio;

    @FXML
    private TableColumn<?, ?> colArtista;

    @FXML
    private TableColumn<?, ?> colDuracion;

    @FXML
    private TableColumn<?, ?> colTitulo;

    @FXML
    private FlowPane contenedorPlaylists;

    @FXML
    private TableView<?> tablaCanciones;

    @FXML
    private TextField txtBuscarGenero;

    @FXML
    void buscarPlaylistPorGenero(ActionEvent event) {

    }

}
