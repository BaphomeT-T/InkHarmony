package UserInterface.CustomerControl;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class NuevaPlaylistController implements Initializable {
    @FXML
    private TextField txtTitulo;
    @FXML
    private TextArea txtDescripcion;
    @FXML
    private ImageView imgPortada;
    @FXML
    private Button btnCrear;
    @FXML
    private Button btnCerrar;
    @FXML
    private Button btnRegresar;
    @FXML
    private Button btnGuardarPlaylist;
    @FXML
    private Button btnSeleccionarPortada;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Aquí puedes inicializar eventos si lo necesitas
    }

    @FXML
    private void handleCerrar() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleRegresar() {
        Stage stage = (Stage) btnRegresar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCrearPlaylist() {
        // Lógica para crear la playlist
    }

    @FXML
    private void handleSeleccionarPortada() {
        // Crear y mostrar un mensaje de futura implementación
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText("Futura implementación");
        alert.showAndWait();
    }
    
    @FXML
    private void handleGuardarPlaylist() {
        // Lógica para guardar la playlist
        String titulo = txtTitulo.getText();
        String descripcion = txtDescripcion.getText();
        
        if (titulo.isEmpty()) {
            System.out.println("Por favor, ingresa un título para la playlist");
            return;
        }
        
        System.out.println("Guardando playlist: " + titulo);
        System.out.println("Descripción: " + descripcion);
        // Aquí iría la lógica para guardar en la base de datos
    }
}
