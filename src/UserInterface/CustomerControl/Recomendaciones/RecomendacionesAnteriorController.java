package UserInterface.CustomerControl.Recomendaciones;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class RecomendacionesAnteriorController {

    @FXML private Button btnPlaylist;
    @FXML private Button btnGenero;
    @FXML private Button btnPersonalizadas;
    @FXML private Button btnEstrenos;

    @FXML private Button btnVolver;

    @FXML private StackPane contentPane;

    @FXML
    private Button cerrarButton;

    @FXML
    public void initialize() {
        // Acciones de los botones
        btnPlaylist.setOnAction(e -> cargarVista("playlist.fxml"));
        btnGenero.setOnAction(e -> cargarVista("porGenero.fxml"));
        btnPersonalizadas.setOnAction(e -> cargarVista("personalizadas.fxml"));
        btnEstrenos.setOnAction(e -> cargarVista("estrenos.fxml"));
    }

    private void cargarVista(String fxml) {
        try {
            AnchorPane vista = FXMLLoader.load(getClass().getResource("/UserInterface/GUI/Recomendaciones/" + fxml));

            contentPane.getChildren().setAll(vista);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void cerrarVentana(ActionEvent event) {
        try {
            // Obtener el Stage actual a partir del botón cerrar
            Stage stage = (Stage) this.cerrarButton.getScene().getWindow();

            // Cargar el nuevo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/UserInterface/GUI/AdminUserControl/.fxml")); //Aquí iría la vista del menu principal
            Parent root = loader.load();

            // Crear la nueva escena con la interfaz de administración
            Scene scene = new Scene(root);

            // Cambiar la escena del Stage
            stage.setScene(scene);
            stage.setTitle("Administración de Usuarios");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
