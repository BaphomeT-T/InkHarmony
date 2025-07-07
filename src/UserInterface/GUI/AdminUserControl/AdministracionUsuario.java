package UserInterface.GUI.AdminUserControl;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class AdministracionUsuario extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Cargar el FXML
        Parent root = FXMLLoader.load(getClass().getResource(
            "/UserInterface/GUI/AdminUserControl/registro.fxml"));

        Scene scene = new Scene(root);
        primaryStage.setTitle("Administración de Usuarios");
        primaryStage.setScene(scene);

        // Establecer tamaño mínimo (para que no se pueda achicar más)
        primaryStage.setMinWidth(1280);
        primaryStage.setMinHeight(680);

        primaryStage.show();
    }

}