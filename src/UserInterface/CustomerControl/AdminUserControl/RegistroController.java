package UserInterface.CustomerControl.AdminUserControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegistroController {

    @FXML
    private Button btnRegistrar;

    @FXML
    private TextField txtApellido;

    @FXML
    private TextField txtBusquedaGenero;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private TextField txtCorreo;

    @FXML
    private TextField txtNombre;

    @FXML
    void registrarCuenta(ActionEvent event) {
        javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alerta.setTitle("Registro exitoso");
        alerta.setHeaderText(null); // Opcional: elimina el título de la cabecera
        alerta.setContentText("¡Cuenta registrada correctamente!");

        // Mostrar la alerta
        alerta.showAndWait();
    }

}
