package UserInterface.CustomerControl.AdminUserControl;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

public class LoginController {

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtContrasenia;

    @FXML
    private Button iniciarSesion;
    private void iniciarSesion() {
        String correoElectronico = txtEmail.getText();
        String contrasenia = txtContrasenia.getText();
        // Aquí va la lógica de validación
        System.out.println("Usuario: " + correoElectronico + ", Contraseña: " + contrasenia);
    }
}