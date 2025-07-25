package UserInterface.CustomerControl.Recomendaciones;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class EstrenoController {

    @FXML
    private Button btnActualizarEstrenos;

    @FXML
    private TableColumn<?, ?> colAnio;

    @FXML
    private TableColumn<?, ?> colArtista;

    @FXML
    private TableColumn<?, ?> colDuracion;

    @FXML
    private TableColumn<?, ?> colFechaAgregacion;

    @FXML
    private TableColumn<?, ?> colImagen;

    @FXML
    private TableColumn<?, ?> colTitulo;

    @FXML
    private TableView<?> tablaCancionesPersonalizadas;

    @FXML
    void actualizarEstrenos(ActionEvent event) {

    }

}
