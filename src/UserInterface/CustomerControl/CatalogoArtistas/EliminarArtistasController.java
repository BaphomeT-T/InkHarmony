package UserInterface.CustomerControl.CatalogoArtistas;

import BusinessLogic.ServicioValidacionArtista;
import DataAccessComponent.DTO.ArtistaDTO;
import DataAccessComponent.DAO.ArtistaDAO;
import UserInterface.CustomerControl.CatalogoArtistas.CatalogoArtistasController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;

public class EliminarArtistasController {

    @FXML private Button registrarButton;    // Cancelar
    @FXML private Button registrarButton1;   // Confirmar
    @FXML private ImageView imagenArtistaImageView;
    @FXML private Label nombreArtistaLabel;

    private ArtistaDTO artista;
    private final ArtistaDAO artistaDAO = new ArtistaDAO();
    private final ServicioValidacionArtista servicioValidacionArtista = new ServicioValidacionArtista();
    private CatalogoArtistasController catalogoController;

    public void setArtista(ArtistaDTO artista) {
        this.artista = artista;
        if (artista != null) {
            System.out.println("Título recibido: " + artista.getNombre()); // Verificación

            nombreArtistaLabel.setText(artista.getNombre());

            if (artista.getImagen() != null) {
                Image imagen = new Image(new ByteArrayInputStream(artista.getImagen()));
                imagenArtistaImageView.setImage(imagen);
            }
        }
    }


    /**
     * Elimina directamente al artista si no tiene elementos asociados.
     */
    @FXML
    void confirmarEliminacion(ActionEvent event) {
        if (artista == null) return;

        if (servicioValidacionArtista.tieneElementosAsociados(artista)) {
            mostrarAlerta("No se puede eliminar: el artista tiene elementos asociados (canciones o playlists).");
        } else {
            try {
                boolean eliminado = artistaDAO.eliminar(artista.getId());
                if (eliminado) {
                    mostrarAlerta("El artista fue eliminado correctamente.");
                    if (catalogoController != null) {
                        catalogoController.actualizarTabla(); // Actualizar catálogo
                    }
                    cerrarVentana();
                } else {
                    mostrarAlerta("No se pudo eliminar el artista. Intente de nuevo.");
                }
            } catch (Exception e) {
                mostrarAlerta("Error al eliminar el artista: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }



    @FXML
    void initialize() {
        // Inicialización de componentes, si es necesario
        registrarButton.setText("Cancelar");
        registrarButton1.setText("Confirmar");
        nombreArtistaLabel.setText("");
        imagenArtistaImageView.setImage(null);
    }

    @FXML
    void cancelar(ActionEvent event) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) registrarButton.getScene().getWindow();
        stage.close();
    }


    /**
     * Muestra una alerta informativa con el mensaje proporcionado.
     *
     * @param mensaje Texto que se mostrará en la alerta
     */
    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Información");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public void setCatalogoController(CatalogoArtistasController controller) {
        this.catalogoController = controller;
    }

}
