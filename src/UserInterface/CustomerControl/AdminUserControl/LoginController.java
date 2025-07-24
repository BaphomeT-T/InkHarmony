package UserInterface.CustomerControl.AdminUserControl;

import BusinessLogic.Sesion;
import DataAccessComponent.DTO.PerfilDTO;
import DataAccessComponent.DTO.TipoUsuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.PasswordField;

import BusinessLogic.ServicioPerfil;

public class LoginController {

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtContrasenia;

    @FXML
    private Button iniciarSesion;

    @FXML
    private void initialize() {
        txtContrasenia.setOnAction(event -> iniciarSesion());
        txtEmail.setOnAction(event -> iniciarSesion());
    }


    @FXML
    private void iniciarSesion() {
        String correo = txtEmail.getText();
        String contraseniaIngresada = txtContrasenia.getText();

        ServicioPerfil perfilService = new ServicioPerfil();
        PerfilDTO PerfilDTO = perfilService.autenticar(correo, contraseniaIngresada);

        if (PerfilDTO != null) {
            Sesion sesion = Sesion.getSesion();
            sesion.iniciarSesion(PerfilDTO);
            mostrarAlerta("Correcto","Inicio correcto", Alert.AlertType.INFORMATION);

            // Mostrar nueva vista "Reproductor de Música"
            try {
                javafx.fxml.FXMLLoader loader;
                if (PerfilDTO.getTipoUsuario() == TipoUsuario.ADMINISTRADOR) {
                    loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/AdminUserControl/administracion_Usuarios.fxml"));
                } else {
                    loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/AdminUserControl/reproductor.fxml"));
                }
                javafx.scene.Parent root = loader.load();
                javafx.stage.Stage stage = new javafx.stage.Stage();
                stage.setScene(new javafx.scene.Scene(root));
                stage.setTitle("InkHarmony");
                stage.setMinWidth(1280);
                stage.setMinHeight(680);
                stage.show();

                // Cerrar login
                ((Stage) txtEmail.getScene().getWindow()).close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mostrarAlerta("Error", "Usuario o contraseña incorrectos", Alert.AlertType.ERROR);
        }
    }
    private void mostrarAlerta(String titulo, String mensaje, javafx.scene.control.Alert.AlertType tipo) {
        javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
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