package UserInterface.CustomerControl.AdminUserControl;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import BusinessLogic.Sesion;

public class ReproductorController {

    @FXML private Button cerrarSesion;

    @FXML
    private void cerrarSesion() {
        Sesion sesion = Sesion.getSesion();
        sesion.cerrarSesion(); // Limpia la sesión

        try {
            // Cargar la vista de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/AdminUserControl/login.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.setTitle("Login");
            loginStage.setMinWidth(1280);
            loginStage.setMinHeight(680);
            loginStage.show();

            // Cierra la ventana actual correctamente
            Stage currentStage = (Stage) cerrarSesion.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
