package UserInterface.CustomerControl.CatalogoCanciones;

import BusinessLogic.Genero;
import BusinessLogic.ServicioValidacionCancion;
import DataAccessComponent.DAO.CancionDAO;
import DataAccessComponent.DTO.CancionDTO;
import java.io.ByteArrayInputStream;
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
    // Componentes de la interfaz gráfica
    @FXML private TextField tituloTextField;
    @FXML private TextField artistaTextField;
    @FXML private TextField duracionTextField;
    @FXML private TextArea letraTextArea;
    @FXML private MenuButton generoMenuButton;
    @FXML private ImageView caratulaImageView;
    @FXML private Button eliminarButton;
    @FXML private Button cerrarButton;

    private CancionDTO cancion;                     // Objeto que contiene los datos de la canción
    private final ServicioValidacionCancion servicioValidacion = new ServicioValidacionCancion(); // Servicio para validaciones
    private final CancionDAO cancionDAO = new CancionDAO();                         // DAO para operaciones con la base de datos

    // Método para establecer la canción a mostrar
    public void setCancion(CancionDTO cancion) {
        this.cancion = cancion;
        this.mostrarInformacionCancion(); // Mostrar la información al asignar la canción
    }

    // Muestra la información de la canción en los campos correspondientes
    private void mostrarInformacionCancion() {
        if (this.cancion != null) {
            // Rellenar campos con los datos de la canción
            this.tituloTextField.setText(this.cancion.getTitulo());
            this.artistaTextField.setText(this.cancion.getArtistas());
            this.duracionTextField.setText(this.cancion.getDuracion());


            // Procesar y mostrar los géneros musicales
            if (this.cancion.getGeneros() != null && !this.cancion.getGeneros().isEmpty()) {
                String generosTexto = this.cancion.getGeneros().stream()
                        .map(this::formatearGenero) // Formatear cada género
                        .reduce((a, b) -> a + ", " + b) // Unirlos con comas
                        .orElse("No definido"); // Valor por defecto
                this.generoMenuButton.setText(generosTexto);
            } else {
                this.generoMenuButton.setText("No definido");
            }

            // Cargar la imagen de la carátula si existe
            if (this.cancion.getPortada() != null) {
                try {
                    Image imagen = new Image(new ByteArrayInputStream(this.cancion.getPortada()));
                    this.caratulaImageView.setImage(imagen);
                } catch (Exception e) {
                    System.out.println("Error al cargar carátula: " + e.getMessage());
                }
            }
        }
    }

    // Formatea el nombre del género para mostrarlo correctamente
    private String formatearGenero(Genero genero) {
        String nombre = genero.name().replace('_', ' ').toLowerCase();
        String[] palabras = nombre.split(" ");
        StringBuilder resultado = new StringBuilder();

        for(String palabra : palabras) {
            if (!palabra.isEmpty()) {
                // Capitalizar la primera letra de cada palabra
                resultado.append(Character.toUpperCase(palabra.charAt(0)))
                        .append(palabra.substring(1))
                        .append(" ");
            }
        }

        return resultado.toString().trim();
    }

    // Método para manejar el evento de eliminar canción
    @FXML
    void eliminarCancion(ActionEvent event) {
        if (this.cancion != null) {
            // Verificar si la canción tiene elementos asociados (playlists)
            if (this.servicioValidacion.tieneElementosAsociados(this.cancion)) {
                this.mostrarAlerta("No se puede eliminar: La canción está en alguna playlist");
            } else {
                try {
                    // Intentar eliminar la canción de la base de datos
                    boolean eliminado = this.cancionDAO.eliminar(this.cancion.getIdCancion());
                    if (eliminado) {
                        this.mostrarAlerta("La canción fue eliminada correctamente.");
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
    }

    // Inicialización del controlador
    @FXML
    void initialize() {
        // Configurar campos como no editables
        this.tituloTextField.setEditable(false);
        this.artistaTextField.setEditable(false);
        this.duracionTextField.setEditable(false);
        this.letraTextArea.setEditable(false);
        this.generoMenuButton.setDisable(true); // Deshabilitar el menú de géneros
    }

    // Método para cerrar la ventana
    @FXML
    void cerrarVentana() {
        Stage stage = (Stage)this.cerrarButton.getScene().getWindow();
        stage.close();
    }

    // Muestra una alerta con el mensaje especificado
    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(AlertType.INFORMATION);
        alerta.setTitle("Información");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}