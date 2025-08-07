package UserInterface.GUI.Playlist;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PruebaPlaylist extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Cargar el FXML
        Parent root = FXMLLoader.load(getClass().getResource(
                "/UserInterface/GUI/Playlist/frameCatalogoPlaylist.fxml"));

        Scene scene = new Scene(root, 1280, 800);
        primaryStage.setTitle("Playlist");
        primaryStage.setScene(scene);

        // Establecer tamaño mínimo (para que no se pueda achicar más)
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(480);
        primaryStage.setResizable(true);

        primaryStage.show();
    }
}
