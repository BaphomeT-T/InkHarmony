package UserInterface.CustomerControl;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

/**
 * Controlador para la interfaz de reproducción de canciones.
 * Esta clase maneja la interfaz de usuario para reproducir música,
 * incluyendo controles de reproducción, navegación y búsqueda de canciones.
 * 
 * @author Grupo B
 * @version 1.0
 * @since 2025
 */
public class ReproduccionCancionController {
    
    /** Etiqueta que muestra el nombre del artista de la canción actual */
    @FXML
    private Label lblArtista;
    
    /** Etiqueta adicional para mostrar información del artista */
    @FXML
    private Label lblArtista1;
    
    /** Etiqueta que muestra el nombre de la canción actual */
    @FXML
    private Label lblNombreCancion;
    
    /** Etiqueta adicional para mostrar el nombre de la canción */
    @FXML
    private Label lblNombreCancion1;
    
    /** Etiqueta que muestra la duración o tiempo transcurrido de la canción */
    @FXML
    private Label lblTiempoCancion;
    
    /** Panel que contiene la imagen del álbum de la canción actual */
    @FXML
    private Pane panImageAlbum;
    
    /** Panel adicional para la imagen del álbum */
    @FXML
    private Pane panImageAlbum1;
    
    /** Barra de progreso que indica el avance de reproducción de la canción */
    @FXML
    private ProgressBar pgbProgresoCancion;
    
    /** Campo de texto para ingresar términos de búsqueda de canciones */
    @FXML
    private TextField txtBuscarCancion;
    
    /**
     * Maneja el evento de clic en el botón "Anterior".
     * Cambia la reproducción a la canción anterior en la lista de reproducción.
     * 
     * @param event El evento de acción generado por el clic del botón
     */
    @FXML
    void clickAnterior(ActionEvent event) {
        System.out.println("Se ha cambiado a la canción anterior.");
    }
    
    /**
     * Maneja el evento de clic en el botón de búsqueda de canciones.
     * Permite al usuario buscar canciones específicas en la biblioteca musical.
     * 
     * @param event El evento de acción generado por el clic del botón
     */
    @FXML
    void clickBuscarCancion(ActionEvent event) {
        System.out.println("Ingrese la cancion a buscar.");
    }
    
    /**
     * Maneja el evento de clic en el botón "Regresar".
     * Navega de vuelta a la página anterior en la aplicación.
     * 
     * @param event El evento de acción generado por el clic del botón
     */
    @FXML
    void clickRegresarPagina(ActionEvent event) {
        System.out.println("Se ha regresado la pagina.");
    }
    
    /**
     * Maneja el evento de clic en el botón de reproducción/pausa.
     * Inicia o pausa la reproducción de la canción actual.
     * 
     * @param event El evento de acción generado por el clic del botón
     */
    @FXML
    void clickReproducir(ActionEvent event) {
        System.out.println("Reproduciendo...");
    }
    
    /**
     * Maneja el evento de clic en el botón "Siguiente".
     * Cambia la reproducción a la siguiente canción en la lista de reproducción.
     * 
     * @param event El evento de acción generado por el clic del botón
     */
    @FXML
    void clickSiguiente(ActionEvent event) {
        System.out.println("Se ha cambiado a la canción siguiente.");
    }
}
