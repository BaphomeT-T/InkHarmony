package UserInterface.GUI.controllers.AdminUserControl;

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
    private PasswordField txtRepetirContrasena;

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
            mostrarAlerta("Contraseñas no coinciden", "Las contraseñas ingresadas no son iguales.", javafx.scene.control.Alert.AlertType.ERROR);
            return;
        }

        mostrarAlerta("Registro exitoso", "¡Cuenta registrada correctamente!", javafx.scene.control.Alert.AlertType.INFORMATION);
    }

    private void mostrarAlerta(String titulo, String mensaje, javafx.scene.control.Alert.AlertType tipo) {
        javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

}
