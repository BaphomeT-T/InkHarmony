package UserInterface.CustomerControl.ReproductorMusical;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

public class ReproduccionVariasCancionesController extends BaseReproductorController implements Initializable {

    private static final String ICON_PATH = "/UserInterface/Resources/ReproductorMusical/img/";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inicializarBase(ICON_PATH);
    }

    public void clickReproducir(ActionEvent event) {
        super.clickReproducir(event, ICON_PATH);
    }
}
