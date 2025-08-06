import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/ReproductorMusical/reproduccionVariasCanciones.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    getClass().getResource("/UserInterface/Resources/ReproductorMusical/css/slider.css").toExternalForm()
            );

            primaryStage.setScene(scene);
            primaryStage.setTitle("Reproduccion de Canciones - InkHarmony");
            primaryStage.setMaximized(true);
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args); // inicia la aplicación JavaFX
    }
}
