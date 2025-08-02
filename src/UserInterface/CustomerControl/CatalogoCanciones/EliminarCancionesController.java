package UserInterface.CustomerControl.CatalogoCanciones;

import BusinessLogic.ServicioValidacionCancion;
import DataAccessComponent.DAO.CancionDAO;
import DataAccessComponent.DTO.CancionDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;

public class EliminarCancionesController {

    @FXML private ImageView imagenCancionImageView;
    @FXML private Label tituloCancionLabel;
    @FXML private Button registrarButton;    // Cancelar
    @FXML private Button registrarButton1;   // Confirmar

    private CancionDTO cancion;
    private CatalogoCancionesController catalogoController;

    private final CancionDAO cancionDAO = new CancionDAO();

    public void setCancion(CancionDTO cancion) {
        this.cancion = cancion;
        if (cancion != null) {
            System.out.println("Título recibido: " + cancion.getTitulo()); // Verificación

            tituloCancionLabel.setText(cancion.getTitulo());

            if (cancion.getPortada() != null) {
                Image imagen = new Image(new ByteArrayInputStream(cancion.getPortada()));
                imagenCancionImageView.setImage(imagen);
            }
        }
    }

    public void setCatalogoController(CatalogoCancionesController controller) {
        this.catalogoController = controller;
    }

    @FXML
    void initialize() {
        // No se hace nada adicional al iniciar
    }

    @FXML
    void cancelar(ActionEvent event) {
        cerrarVentana();
    }

    @FXML
    void confirmarEliminacion(ActionEvent event) {
        if (this.cancion != null) {
            boolean eliminado = false;
            try {
                eliminado = cancionDAO.eliminar(this.cancion.getIdCancion());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (eliminado) {
                mostrarAlerta("Canción eliminada correctamente.");
                if (catalogoController != null) {
                    catalogoController.refrescarTabla();
                }
            } else {
                mostrarAlerta("No se pudo eliminar la canción.");
            }
        }
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) registrarButton.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Información");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
