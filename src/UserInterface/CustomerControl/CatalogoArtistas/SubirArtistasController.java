package UserInterface.CustomerControl.CatalogoArtistas;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class SubirArtistasController {

    @FXML
    private ImageView artistaImageView;

    @FXML
    private Label biografiaLabel;

    @FXML
    private TextArea biografiaTextArea;

    @FXML
    private Button cerrarButton;

    @FXML
    private ComboBox<?> generoComboBox;

    @FXML
    private Label generoLabel;

    @FXML
    private Label nombreLabel;

    @FXML
    private TextField nombreTextField;

    @FXML
    private Button publicarButton;

    @FXML
    private Label seleccionarLabel;

    @FXML
    void publicar(ActionEvent event) {

    }

}
