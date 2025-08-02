<<<<<<< HEAD
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Cargar el archivo FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("UserInterface/GUI/CatalogoPlaylist.fxml"));
        Parent root = loader.load();
        
        // Configurar la escena
        Scene scene = new Scene(root, 1200, 800);
        
        // Configurar la ventana principal
        primaryStage.setTitle("InkHarmony - Catálogo de Playlists");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        
        // Mostrar la ventana
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
=======
public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
    }
>>>>>>> 13a7d7a8b979e880d7ed8bd7bb3eb477cc5ac00f
}