/*
|-----------------------------------------------|
| © 2025 EPN-FIS, Todos los derechos reservados |
| GR1SW                                         |
|-----------------------------------------------|
Autor: Grupo - A
Descripción: Controlador del catálogo de canciones.
*/

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

/**
 * Clase EliminarCancionesController que maneja la eliminación de canciones en el catálogo.
 * Esta clase se encarga de mostrar la información de la canción a eliminar,
 * confirmar la eliminación y actualizar el catálogo de canciones.
 * @author Grupo - A
 * @version 1.0
 * @since 18-07-2025
 */
public class EliminarCancionesController {
// FXML Annotations
    @FXML private ImageView imagenCancionImageView;
    @FXML private Label tituloCancionLabel;
    @FXML private Button registrarButton;    // Cancelar
    @FXML private Button registrarButton1;   // Confirmar
    private CancionDTO cancion;
    private CatalogoCancionesController catalogoController;

    private final CancionDAO cancionDAO = new CancionDAO();
/*
    Constructor de la clase EliminarCancionesController.
    Este constructor inicializa el controlador y no requiere parámetros adicionales.
    */
    public EliminarCancionesController() {
        // Constructor vacío, se puede agregar lógica de inicialización si es necesario
    }

    /**
     * Método para establecer la canción a eliminar.
     * Este método recibe un objeto CancionDTO y actualiza los campos de la interfaz
     * con la información de la canción.
     * @param cancion Objeto CancionDTO que contiene la información de la canción a eliminar.
 */
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
/*
    Método para establecer el controlador del catálogo de canciones.
    Este método permite que el controlador de eliminación tenga acceso al controlador del catálogo
    para poder actualizar la vista después de eliminar una canción.
    @param controller Controlador del catálogo de canciones.
 */
    public void setCatalogoController(CatalogoCancionesController controller) {
        this.catalogoController = controller;
    }
/*
    Método de inicialización que se llama al cargar el controlador.
    En este caso, no se realiza ninguna acción adicional al iniciar.
    Este método es parte del ciclo de vida del controlador en JavaFX.
 */
    @FXML
    void initialize() {
        // No se hace nada adicional al iniciar
    }
/*
    Método para cancelar la operación de eliminación.
    Este método cierra la ventana actual sin realizar ninguna acción adicional.
    @param event Evento de acción que se dispara al hacer clic en el botón de cancelar.
 */
    @FXML
    void cancelar(ActionEvent event) {
        cerrarVentana();
    }
/*
    Método para confirmar la eliminación de la canción.
    Este método verifica si hay una canción seleccionada, intenta eliminarla utilizando el DAO,
    y muestra un mensaje de éxito o error según el resultado de la operación.
    Si la eliminación es exitosa, también actualiza el catálogo de canciones.
    @param event Evento de acción que se dispara al hacer clic en el botón de confirmar eliminación.
 */
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
/*
    Método para cerrar la ventana actual.
    Este método obtiene la ventana actual del botón de registrar y la cierra.
    Es útil para finalizar la operación de eliminación y regresar al catálogo de canciones.
 */
    private void cerrarVentana() {
        Stage stage = (Stage) registrarButton.getScene().getWindow();
        stage.close();
    }
/*
    Método para mostrar una alerta con un mensaje específico.
    Este método crea una alerta de tipo información y la muestra al usuario.
    Es útil para informar al usuario sobre el resultado de la operación de eliminación.
    @param mensaje El mensaje que se mostrará en la alerta.
 */
    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Información");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
