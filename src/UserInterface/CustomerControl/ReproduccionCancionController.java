package UserInterface.CustomerControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class ReproduccionCancionController {
    @FXML
    private Label lblArtista;

    @FXML
    private Label lblArtista1;

    @FXML
    private Label lblNombreCancion;

    @FXML
    private Label lblNombreCancion1;

    @FXML
    private Label lblTiempoCancion;

    @FXML
    private Pane panImageAlbum;

    @FXML
    private Pane panImageAlbum1;

    @FXML
    private ProgressBar pgbProgresoCancion;

    @FXML
    private TextField txtBuscarCancion;

    @FXML
    void clickAnterior(ActionEvent event) {
        System.out.println("Se ha cambiado a la canción anterior.");
    }

    @FXML
    void clickBuscarCancion(ActionEvent event) {
        System.out.println("Ingrese la cancion a buscar.");
    }

    @FXML
    void clickRegresarPagina(ActionEvent event) {
        System.out.println("Se ha regresado la pagina.");
    }

    @FXML
    void clickReproducir(ActionEvent event) {
        System.out.println("Reproduciendo...");
    }

    @FXML
    void clickSiguiente(ActionEvent event) {
        System.out.println("Se ha cambiado a la canción siguiente.");
    }

}
