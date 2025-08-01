package UserInterface.CustomerControl.CatalogoCanciones;

import BusinessLogic.Genero;
import BusinessLogic.ServicioValidacionCancion;
import DataAccessComponent.DAO.CancionDAO;
import DataAccessComponent.DTO.ArtistaDTO;
import DataAccessComponent.DTO.CancionDTO;
import java.io.ByteArrayInputStream;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class EliminarCancionesController {
    @FXML private TextField nombreTextField;
    @FXML private TextField nombreTextField1;
    @FXML private MenuButton generoMenuButton;
    @FXML private Button cerrarButton;

    private CancionDTO cancion;
    private final ServicioValidacionCancion servicioValidacion = new ServicioValidacionCancion();
    private final CancionDAO cancionDAO = new CancionDAO();

    public void setCancion(CancionDTO cancion) {
        this.cancion = cancion;
        this.mostrarInformacionCancion();
    }

    private void mostrarInformacionCancion() {
        if (this.cancion != null) {
            this.nombreTextField.setText(this.cancion.getTitulo());
            this.nombreTextField1.setText(String.valueOf(this.cancion.getAnio()));

            // Mostrar géneros
            if (this.cancion.getGeneros() != null && !this.cancion.getGeneros().isEmpty()) {
                String generosTexto = this.cancion.getGeneros().stream()
                        .map(this::formatearGenero)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("No definido");
                this.generoMenuButton.setText(generosTexto);
            } else {
                this.generoMenuButton.setText("No definido");
            }
        }
    }

    private String formatearGenero(Genero genero) {
        String nombre = genero.name().replace('_', ' ').toLowerCase();
        String[] palabras = nombre.split(" ");
        StringBuilder resultado = new StringBuilder();

        for(String palabra : palabras) {
            if (!palabra.isEmpty()) {
                resultado.append(Character.toUpperCase(palabra.charAt(0)))
                        .append(palabra.substring(1))
                        .append(" ");
            }
        }

        return resultado.toString().trim();
    }

    @FXML
    void eliminarCancion(ActionEvent event) {
        if (this.cancion != null) {
            try {
                // Ya no se valida si tiene elementos asociados
                boolean eliminado = this.cancionDAO.eliminar(this.cancion.getIdCancion());
                if (eliminado) {
                    this.mostrarAlerta("La canción fue eliminada correctamente.");
                    
                    // Actualizar el catálogo si existe la referencia
                    if (this.catalogoController != null) {
                        this.catalogoController.refrescarTabla();
                    }
                    
                    this.cerrarVentana();
                } else {
                    this.mostrarAlerta("No se pudo eliminar la canción. Intente de nuevo.");
                }
            } catch (Exception e) {
                this.mostrarAlerta("Error al eliminar la canción: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void initialize() {
        this.nombreTextField.setEditable(false);
        this.nombreTextField1.setEditable(false);
        this.generoMenuButton.setDisable(true);
    }

    @FXML
    void cerrarVentana() {
        Stage stage = (Stage)this.cerrarButton.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(AlertType.INFORMATION);
        alerta.setTitle("Información");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    
    // Referencia al controlador del catálogo para actualizar la tabla
    private CatalogoCancionesController catalogoController;
    
    /**
     * Establece la referencia al controlador del catálogo para poder actualizar la tabla
     * cuando se elimine una canción.
     * 
     * @param catalogoController Referencia al controlador del catálogo
     */
    public void setCatalogoController(CatalogoCancionesController catalogoController) {
        this.catalogoController = catalogoController;
    }
}
