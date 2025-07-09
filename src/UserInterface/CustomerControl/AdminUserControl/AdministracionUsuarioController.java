package UserInterface.CustomerControl.AdminUserControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class AdministracionUsuarioController {

    @FXML
    private Button btnActivarCuenta;
    @FXML
    private Button btnCerrarSesion;

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
    @FXML
    void cerrarSesion(ActionEvent event) {
       
         // Cerrar la ventana de registro y abrir de nuevo la ventana de login
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/UserInterface/GUI/AdminUserControl/login.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Login");
            stage.setMinWidth(1280);
            stage.setMinHeight(680);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        javafx.stage.Stage stage = (javafx.stage.Stage) btnCerrarSesion.getScene().getWindow();
        stage.close();


    }

}
