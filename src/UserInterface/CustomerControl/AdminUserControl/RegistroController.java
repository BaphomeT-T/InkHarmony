package UserInterface.CustomerControl.AdminUserControl;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

public class RegistroController {

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
    private Polyline btnAnterior;

    @FXML
    private Button btnRegistrar;

    @FXML
    private Polyline btnSiguiente;

    @FXML
    private Circle imgPerfil;

    @FXML
    private Label lblSalirRegistro;

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
    private PasswordField txtRepetirContrasena;

    @FXML
    public void initialize() {
        actualizarImagenPerfil();
    }

    @FXML
    void imagenAnterior(MouseEvent event) {
        if (indiceActual == 0) {
            indiceActual = rutasImagenes.size() - 1; // si está en la primera, vuelve a la última
        } else {
            indiceActual--;
        }
        actualizarImagenPerfil();
    }

    @FXML
    void imagenSiguiente(MouseEvent event) {
        if (indiceActual == rutasImagenes.size() - 1) {
            indiceActual = 0; // si está en la última, vuelve a la primera
        } else {
            indiceActual++;
        }
        actualizarImagenPerfil();
    }

    @FXML
    void registrarCuenta(ActionEvent event) {
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String correo = txtCorreo.getText().trim();
        String contrasena = txtContrasena.getText().trim();
        String repetirContrasena = txtRepetirContrasena.getText().trim();

        // Verificar campos vacíos
        StringBuilder camposVacios = new StringBuilder("Debe llenar los siguientes campos:\n");

        boolean hayVacios = false;
        if (nombre.isEmpty()) {
            camposVacios.append("- Nombre\n");
            hayVacios = true;
        }
        if (apellido.isEmpty()) {
            camposVacios.append("- Apellido\n");
            hayVacios = true;
        }
        if (correo.isEmpty()) {
            camposVacios.append("- Correo\n");
            hayVacios = true;
        }
        if (contrasena.isEmpty()) {
            camposVacios.append("- Contraseña\n");
            hayVacios = true;
        }
        if (repetirContrasena.isEmpty()) {
            camposVacios.append("- Verificar Contraseña\n");
            hayVacios = true;
        }

        if (hayVacios) {
            mostrarAlerta("Campos incompletos", camposVacios.toString(), javafx.scene.control.Alert.AlertType.WARNING);
            return;
        }

        if (!contrasena.equals(repetirContrasena)) {
            mostrarAlerta("Contraseñas no coinciden", "Las contraseñas ingresadas no son iguales.",
                    javafx.scene.control.Alert.AlertType.ERROR);
            return;
        }

        mostrarAlerta("Registro exitoso", "¡Cuenta registrada correctamente!",
                javafx.scene.control.Alert.AlertType.INFORMATION);
    }

    private void mostrarAlerta(String titulo, String mensaje, javafx.scene.control.Alert.AlertType tipo) {
        javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
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
    private void salirRegistro() {
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
        javafx.stage.Stage stage = (javafx.stage.Stage) lblSalirRegistro.getScene().getWindow();
        stage.close();

    }
}
