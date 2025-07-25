package UserInterface.CustomerControl.Recomendaciones;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class RecomendacionesController {

    @FXML private Button btnPlaylist;
    @FXML private Button btnGenero;
    @FXML private Button btnPersonalizadas;
    @FXML private Button btnEstrenos;

    @FXML private StackPane contentPane;

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
}
