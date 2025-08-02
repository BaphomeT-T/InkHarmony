import javafx.application.Application;
import javafx.stage.Stage;
import UserInterface.GUI.CatalogoPlaylist.VentanaManager;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        VentanaManager ventanaManager = new VentanaManager();
        ventanaManager.start(primaryStage);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}