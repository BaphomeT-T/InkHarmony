package UserInterface.CustomerControl.AdminUserControl;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.PasswordField;

public class LoginController {

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtContrasenia;

    @FXML
    private Button iniciarSesion;
    @FXML
    private void iniciarSesion() {
        String email = txtEmail.getText();
        String password = txtContrasenia.getText();

        // Validación simulada
        if (email.equals("admin") && password.equals("1234")) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/AdminUserControl/administracion_Usuarios.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Administración de Usuarios");
                stage.setMinWidth(1280);
                stage.setMinHeight(680);

                stage.show();

                // Cerrar la ventana de login
                ((Stage) txtEmail.getScene().getWindow()).close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Usuario o contraseña incorrectos");
        }
        
        
    }
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }
    @FXML
    private void registrarUsuario() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/AdminUserControl/registro.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Registro de Usuario");
            stage.setMinWidth(1280);
            stage.setMinHeight(680);

            stage.show();

            // Cerrar la ventana de login
            ((Stage) txtEmail.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}