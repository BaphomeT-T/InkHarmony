package UserInterface.CustomerControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class ReproduccionVariasCancionesController {
    @FXML
    private Pane panBiblioteca;

    @FXML
    private Pane panImageAlbum1;

    @FXML
    private ScrollPane panListaReproduccion;

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
    void clickNuevaBiblioteca(ActionEvent event) {
        System.out.println("Escoja las canciones para la nueva biblioteca.");
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
