/*
-----------------------------------------------
 © 2025 EPN-FIS, Todos los derechos reservados
 GR1SW
-----------------------------------------------
Autor: Duncan Licuy
Descripción: Controlador para eliminar canciones.
*/
package UserInterface.CustomerControl.CatalogoCanciones;

import BusinessLogic.Genero;
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
    @FXML private MenuButton generoMenuButton;
    @FXML private ImageView portadaImageView;
    @FXML private Button eliminarButton;
    @FXML private Button cerrarButton;

    private CancionDTO cancion;                     // Objeto que contiene los datos de la canción
    private final CancionDAO cancionDAO = new CancionDAO(); // DAO para operaciones con la base de datos

    // Método para establecer la canción a mostrar
    public void setCancion(CancionDTO cancion) {
        this.cancion = cancion;
        this.mostrarInformacionCancion();
    }

    // Muestra la información de la canción en los campos correspondientes
    private void mostrarInformacionCancion() {
        if (this.cancion != null) {
            this.tituloTextField.setText(this.cancion.getTitulo());
            this.artistaTextField.setText(this.cancion.getArtistas().toString());
            this.duracionTextField.setText(this.cancion.getDuracion());

            // Procesar y mostrar los géneros musicales
            if (this.cancion.getGeneros() != null && !this.cancion.getGeneros().isEmpty()) {
                String generosTexto = this.cancion.getGeneros().stream()
                        .map(this::formatearGenero)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("No definido");
                this.generoMenuButton.setText(generosTexto);
            } else {
                this.generoMenuButton.setText("No definido");
            }

            // Cargar la imagen de la carátula si existe
            if (this.cancion.getPortada() != null) {
                try {
                    Image imagen = new Image(new ByteArrayInputStream(this.cancion.getPortada()));
                    this.portadaImageView.setImage(imagen);
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

    // Inicialización del controlador
    @FXML
    void initialize() {
        // Configurar campos como no editables
        this.tituloTextField.setEditable(false);
        this.artistaTextField.setEditable(false);
        this.duracionTextField.setEditable(false);
        this.generoMenuButton.setDisable(true);
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