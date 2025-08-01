import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Cargar el archivo FXML del catálogo de playlists
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/Playlist/frameCatalogoPlaylist.fxml"));
        Parent root = loader.load();

        // Configurar la ventana principal
        primaryStage.setTitle("InkHarmony - Catálogo de Playlists");
        primaryStage.setScene(new Scene(root, 1280, 832));
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}