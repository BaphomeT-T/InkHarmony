/*
|-----------------------------------------------|
| © 2025 EPN-FIS, Todos los derechos reservados |
| GR1SW                                         |
|-----------------------------------------------|
Autor: Grupo - A
Descripción: Controlador para la eliminación de canciones del catálogo del sistema InkHarmony.
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
 * Controlador para la funcionalidad de eliminación de canciones del catálogo del sistema InkHarmony.
 *
 * Esta clase maneja la interfaz de confirmación de eliminación, mostrando la información
 * de la canción seleccionada y permitiendo al usuario confirmar o cancelar la operación.
 * Se integra con la capa de acceso a datos para ejecutar la eliminación y actualiza
 * el catálogo principal tras una eliminación exitosa.
 *
 * Funcionalidades principales:
 * - Mostrar información de la canción a eliminar (título e imagen)
 * - Confirmar eliminación con validaciones
 * - Integración con CancionDAO para eliminación en base de datos
 * - Actualización automática del catálogo tras eliminación
 * - Manejo de errores y mensajes informativos
 *
 * FXML asociado: frameEliminarCancion.fxml
 *
 * @author Grupo - A
 * @version 1.0
 * @since 18-07-2025
 */
public class EliminarCancionesController {

    // Componentes de la interfaz gráfica vinculados al FXML
    @FXML private ImageView imagenCancionImageView;
    @FXML private Label tituloCancionLabel;
    @FXML private Button registrarButton;    // Botón cancelar
    @FXML private Button registrarButton1;   // Botón confirmar eliminación

    // Objeto que contiene la información de la canción a eliminar
    private CancionDTO cancion;

    // Referencia al controlador del catálogo para actualizar la vista tras eliminar
    private CatalogoCancionesController catalogoController;

    // Instancia del DAO para realizar operaciones de eliminación en la base de datos
    private final CancionDAO cancionDAO = new CancionDAO();

    /*
     * Constructor de la clase EliminarCancionesController.
     * Inicializa el controlador sin parámetros adicionales. La configuración
     * específica se realiza a través de los métodos setCancion() y setCatalogoController().
     */
    public EliminarCancionesController() {
        // Constructor vacío, la inicialización se realiza mediante métodos setter
    }

    /**
     * Establece la canción que será eliminada y actualiza la interfaz con su información.
     *
     * Este método configura la vista de confirmación mostrando el título y la imagen
     * de portada de la canción seleccionada. Si la canción no tiene portada,
     * la imagen se mantiene vacía.
     *
     * @param cancion Objeto CancionDTO que contiene toda la información de la canción a eliminar
     */
    public void setCancion(CancionDTO cancion) {
        this.cancion = cancion;
        if (cancion != null) {
            System.out.println("Título recibido: " + cancion.getTitulo()); // Log de verificación

            // Establecer el título de la canción en el label
            tituloCancionLabel.setText(cancion.getTitulo());

            // Cargar y mostrar la imagen de portada si existe
            if (cancion.getPortada() != null) {
                Image imagen = new Image(new ByteArrayInputStream(cancion.getPortada()));
                imagenCancionImageView.setImage(imagen);
            }
        }
    }

    /*
     * Establece la referencia al controlador del catálogo principal.
     * Esta referencia permite actualizar automáticamente la tabla del catálogo
     * después de una eliminación exitosa, manteniendo la sincronización de datos
     * entre las diferentes ventanas del sistema.
     *
     * @param controller Instancia del controlador del catálogo de canciones
     */
    public void setCatalogoController(CatalogoCancionesController controller) {
        this.catalogoController = controller;
    }

    /*
     * Método de inicialización que se ejecuta automáticamente al cargar el FXML.
     * En esta implementación no se requiere configuración adicional al inicializar,
     * ya que la configuración específica se realiza cuando se establecen la canción
     * y el controlador del catálogo.
     */
    @FXML
    void initialize() {
        // No se requiere configuración adicional en la inicialización
        // La configuración se realiza mediante setCancion() y setCatalogoController()
    }

    /*
     * Cancela la operación de eliminación cerrando la ventana actual.
     * Este método se ejecuta cuando el usuario hace clic en el botón "Cancelar",
     * permitiendo salir del proceso de eliminación sin realizar cambios.
     *
     * @param event Evento de acción generado por el botón cancelar
     */
    @FXML
    void cancelar(ActionEvent event) {
        cerrarVentana();
    }

    /*
     * Confirma y ejecuta la eliminación de la canción seleccionada.
     *
     * Proceso de eliminación:
     * 1. Verificar que existe una canción seleccionada
     * 2. Intentar eliminar usando CancionDAO
     * 3. Mostrar mensaje de resultado (éxito o error)
     * 4. Actualizar el catálogo si la eliminación fue exitosa
     * 5. Cerrar la ventana de confirmación
     *
     * @param event Evento de acción generado por el botón confirmar eliminación
     */
    @FXML
    void confirmarEliminacion(ActionEvent event) {
        if (this.cancion != null) {
            boolean eliminado = false;
            try {
                // Intentar eliminar la canción usando el DAO
                eliminado = cancionDAO.eliminar(this.cancion.getIdCancion());
            } catch (Exception e) {
                // Propagar la excepción como RuntimeException para manejo apropiado
                throw new RuntimeException(e);
            }

            if (eliminado) {
                // Eliminación exitosa: mostrar confirmación y actualizar catálogo
                mostrarAlerta("Canción eliminada correctamente.");
                if (catalogoController != null) {
                    catalogoController.refrescarTabla();
                }
            } else {
                // Error en eliminación: informar al usuario
                mostrarAlerta("No se pudo eliminar la canción.");
            }
        }
        // Cerrar la ventana independientemente del resultado
        cerrarVentana();
    }

    /*
     * Cierra la ventana actual de confirmación de eliminación.
     * Obtiene el Stage a partir del botón registrar y ejecuta el cierre.
     * Este método es utilizado tanto para cancelar como para finalizar
     * el proceso de eliminación.
     */
    private void cerrarVentana() {
        Stage stage = (Stage) registrarButton.getScene().getWindow();
        stage.close();
    }

    /*
     * Muestra una alerta informativa al usuario con el mensaje especificado.
     * Utilizada para comunicar el resultado de la operación de eliminación,
     * ya sea éxito o error. La alerta es de tipo INFORMACIÓN y requiere
     * confirmación del usuario antes de continuar.
     *
     * @param mensaje Texto del mensaje a mostrar en la alerta
     */
    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Información");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}