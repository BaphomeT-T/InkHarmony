/*
|-----------------------------------------------|
| © 2025 EPN-FIS, Todos los derechos reservados |
| GR1SW                                         |
|-----------------------------------------------|
Autor: Sergio Rodríguez
Descripción: Controlador JavaFX para la edición de canciones en el catálogo.
*/

package UserInterface.CustomerControl.CatalogoCanciones;

// Importación del DTO (Data Transfer Object) que representa una canción
import DataAccessComponent.DTO.CancionDTO;
import DataAccessComponent.DAO.CancionDAO;
import BusinessLogic.Cancion;
import java.util.ArrayList;

// Importaciones necesarias para manejar eventos y controles de la interfaz JavaFX
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * Controlador de la vista de edición de canciones.
 * Gestiona la lógica para visualizar y editar los datos de una canción.
 */
public class EditarCancionesController {

    // Atributo privado que almacena la canción actual a editar
    private CancionDTO cancion;
    
    // Instancia del DAO para operaciones de base de datos
    private CancionDAO cancionDAO = new CancionDAO();

    // Campos de entrada de texto vinculados a la interfaz FXML
    @FXML
    private TextField nombreTextField;

    @FXML
    private TextField nombreTextField1;

    /**
     * Método invocado al hacer clic en el botón "Editar".
     * Implementa validación, persistencia y cierre de la ventana.
     *
     * @param actionEvent el evento generado por la acción del botón.
     */
    public void editarCancion(ActionEvent actionEvent) {
        System.out.println("=== EDITANDO CANCIÓN ===");
        
        // Validar que los campos no estén vacíos
        String nuevoTitulo = nombreTextField.getText().trim();
        String nuevoAnio = nombreTextField1.getText().trim();
        
        if (nuevoTitulo.isEmpty()) {
            mostrarAlerta("El título de la canción no puede estar vacío.");
            return;
        }
        
        if (nuevoAnio.isEmpty()) {
            mostrarAlerta("El año de lanzamiento no puede estar vacío.");
            return;
        }
        
        // Validar que el año sea un número válido
        int anio;
        try {
            anio = Integer.parseInt(nuevoAnio);
            if (anio < 1900 || anio > 2025) {
                mostrarAlerta("El año debe estar entre 1900 y 2025.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("El año debe ser un número válido.");
            return;
        }
        
        try {
            // Actualizar el objeto canción con los nuevos valores
            cancion.setTitulo(nuevoTitulo);
            cancion.setAnio(anio);
            
            // Asegurar que los artistas y géneros no sean null para evitar errores en el DAO
            if (cancion.getArtistas() == null) {
                cancion.setArtistas(new ArrayList<>());
            }
            if (cancion.getGeneros() == null) {
                cancion.setGeneros(new ArrayList<>());
            }
            
            // Guardar los cambios en la base de datos
            boolean exito = cancionDAO.actualizar(cancion);
            
            if (exito) {
                System.out.println("Canción actualizada exitosamente: " + nuevoTitulo);
                mostrarExito("La canción fue actualizada correctamente.");
                
                // Actualizar el catálogo si existe la referencia
                if (this.catalogoController != null) {
                    System.out.println("Llamando a refrescarTabla() desde EditarCancionesController...");
                    this.catalogoController.refrescarTabla();
                    System.out.println("refrescarTabla() llamado exitosamente");
                } else {
                    System.out.println("catalogoController es null, no se puede refrescar la tabla");
                }
                
                // Cerrar la ventana
                Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                stage.close();
            } else {
                mostrarAlerta("No se pudo actualizar la canción. Intente de nuevo.");
            }
            
        } catch (Exception e) {
            System.out.println("Error al actualizar canción: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error al actualizar la canción: " + e.getMessage());
        }
    }
    
    /**
     * Muestra una alerta de error con el mensaje especificado.
     */
    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(AlertType.WARNING);
        alerta.setTitle("Advertencia");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    
    /**
     * Muestra una alerta de éxito con el mensaje especificado.
     */
    private void mostrarExito(String mensaje) {
        Alert alerta = new Alert(AlertType.INFORMATION);
        alerta.setTitle("Éxito");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    
    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) nombreTextField.getScene().getWindow();
        stage.close();
    }

    /**
     * Recibe una canción desde otro controlador y la carga en los campos de la vista.
     * 
     * @param cancion objeto de tipo CancionDTO que contiene los datos a editar.
     */
    public void setCancion(CancionDTO cancion) {
        this.cancion = cancion;

        // Se cargan los datos actuales en los campos de texto de la interfaz
        nombreTextField.setText(cancion.getTitulo());
        nombreTextField1.setText(String.valueOf(cancion.getAnio())); // Campo para año
        
        System.out.println("Cargando canción para editar: " + cancion.getTitulo());
    }
    
    // Referencia al controlador del catálogo para actualizar la tabla
    private CatalogoCancionesController catalogoController;
    
    /**
     * Establece la referencia al controlador del catálogo para actualizar la tabla.
     * 
     * @param catalogoController Referencia al controlador del catálogo
     */
    public void setCatalogoController(CatalogoCancionesController catalogoController) {
        this.catalogoController = catalogoController;
    }
}
