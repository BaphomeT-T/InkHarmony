package UserInterface.GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class Login extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Cargar el FXML
        Parent root = FXMLLoader.load(getClass().getResource(
            "/UserInterface/GUI/AdminUserControl/login.fxml"));

        Scene scene = new Scene(root);
        primaryStage.setTitle("Inicio de sesión");
        primaryStage.setScene(scene);

        // Establecer tamaño mínimo (para que no se pueda achicar más)
        primaryStage.setMinWidth(1280);
        primaryStage.setMinHeight(680);

        primaryStage.show();
    }

}