package UserInterface.Controlers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class AdministracionUsuarioController {

    @FXML
    private Button btnActivarCuenta;

    @FXML
    private Button btnDesactivarCuenta;

    @FXML
    private Button btnEliminarCuenta;

    @FXML
    private TextField txtApellido;

    @FXML
    private TextField txtNombre;

    @FXML
    void activarCuenta(ActionEvent event) {
        System.out.println("hola hola");
    }

}
