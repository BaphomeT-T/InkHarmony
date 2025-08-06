package UserInterface.CustomerControl;

import BusinessLogic.ReproductorMP3;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.util.Timer;

/**
 * Controlador para la interfaz de reproducción de múltiples canciones.
 * Esta clase maneja la interfaz de usuario para reproducir listas de canciones,
 * gestionar bibliotecas musicales, y navegar entre diferentes colecciones de música.
 * 
 * @author Grupo B
 * @version 1.0
 * @since 2025
 */
public class ReproduccionVariasCancionesController {

    private ReproductorMP3 reproductor;
    private Timer timerActualizacion;
    private boolean actualizandoProgress = false;
    private double duracionRealCancion = 0;
    
    /** Panel que contiene la interfaz de la biblioteca musical */
    @FXML
    private Pane panBiblioteca;
    
    /** Panel que muestra la imagen del álbum de la canción o lista actual */
    @FXML
    private Pane panImageAlbum1;

    /** Barra de progreso que indica el avance de reproducción de la canción */
    @FXML
    private ProgressBar pgbProgresoCancion;

    /** Panel con scroll que contiene la lista de reproducción de canciones */
    @FXML
    private ScrollPane panListaReproduccion;
    
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
     * Maneja el evento de clic en el botón de búsqueda de biblioteca.
     * Permite al usuario seleccionar una biblioteca específica de la lista disponible.
     * 
     * @param event El evento de acción generado por el clic del botón
     */
    @FXML
    void clickBuscarBiblioteca(ActionEvent event) {
        System.out.println("Escoja la biblioteca de la lista.");
    }
    
    /**
     * Maneja el evento de clic en el botón de búsqueda de canciones.
     * Permite al usuario buscar canciones específicas dentro de la biblioteca actual.
     * 
     * @param event El evento de acción generado por el clic del botón
     */
    @FXML
    void clickBuscarCancion(ActionEvent event) {
        System.out.println("Ingrese la cancion a buscar.");
    }
    
    /**
     * Maneja el evento de clic en el botón "Nueva Biblioteca".
     * Permite al usuario crear una nueva biblioteca seleccionando canciones específicas.
     * 
     * @param event El evento de acción generado por el clic del botón
     */
    @FXML
    void clickNuevaBiblioteca(ActionEvent event) {
        System.out.println("Escoja las canciones para la nueva biblioteca.");
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
     * Inicia o pausa la reproducción de la canción actual en la lista.
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

