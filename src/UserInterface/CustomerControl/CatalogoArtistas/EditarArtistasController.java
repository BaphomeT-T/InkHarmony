package UserInterface.CustomerControl.CatalogoArtistas;

import DataAccessComponent.DTO.ArtistaDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class EditarArtistasController {
    private ArtistaDTO artista;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextArea txtBiografia;

    public void editarArtista(ActionEvent actionEvent) {
    }

    // Temporal (se creo para conectar la pantalla)
    public void setArtista(ArtistaDTO artista) {
        this.artista = artista;
        txtNombre.setText(artista.getNombre());
        txtBiografia.setText(artista.getBiografia());
    }

}
