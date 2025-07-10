package UserInterface.CustomerControl.AdminUserControl;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.ImagePattern;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.shape.Circle;
import javafx.scene.image.Image;

public class AdministracionUsuarioController {
    private List<String> rutasImagenes = List.of(   
        "/UserInterface/Resources/img/Perfil/perfilH1.jpg",
        "/UserInterface/Resources/img/Perfil/perfilH2.jpg",
        "/UserInterface/Resources/img/Perfil/perfilH3.jpg",
        "/UserInterface/Resources/img/Perfil/perfilM1.jpg",
        "/UserInterface/Resources/img/Perfil/perfilM2.jpg",
        "/UserInterface/Resources/img/Perfil/perfilM3.jpg"
        );
    private int indiceActual = 0;
    @FXML
    private Button btnActivarCuenta;

    @FXML
    private MenuButton menuPerfil;
    @FXML
    private Circle imgPerfil;
    @FXML
    private MenuItem btnCerrarSesion;

    @FXML
    private Button btnDesactivarCuenta;

    @FXML
    private Button btnEliminarCuenta;

    @FXML
    private TextField txtApellido;

    @FXML
    private TextField txtNombre;
    @FXML
    public void initialize() {
        actualizarImagenPerfil();
    }
    private void actualizarImagenPerfil() {
        try {
            String ruta = rutasImagenes.get(indiceActual);
            Image imagen = new Image(getClass().getResourceAsStream(ruta));
            ImagePattern patron = new ImagePattern(imagen);
            imgPerfil.setFill(patron);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
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
        javafx.stage.Stage stage = (javafx.stage.Stage) menuPerfil.getScene().getWindow();
        stage.close();


    }

}
