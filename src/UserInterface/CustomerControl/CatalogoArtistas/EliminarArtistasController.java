package UserInterface.CustomerControl.CatalogoArtistas;

import DataAccessComponent.DTO.ArtistaDTO;
import DataAccessComponent.DAO.ArtistaDAO;
import BusinessLogic.ServicioValidacion;
import BusinessLogic.Genero;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;

public class EliminarArtistasController {

    @FXML
    private TextField nombreTextField;

    @FXML
    private TextArea biografiaTextArea;

    @FXML
    private MenuButton generoMenuButton;

    @FXML
    private ImageView artistaImageView;

    @FXML
    private Button eliminarButton;

    @FXML
    private Button cerrarButton;

    private ArtistaDTO artista;
    private final ServicioValidacion servicioValidacion = new ServicioValidacion();
    private final ArtistaDAO artistaDAO = new ArtistaDAO();

    public void setArtista(ArtistaDTO artista) {
        this.artista = artista;
        mostrarInformacionArtista();
    }

    /**
     * Muestra la información del artista en la interfaz gráfica, incluyendo nombre, biografía, géneros y foto.
     */
    private void mostrarInformacionArtista() {
        if (artista != null) {
            nombreTextField.setText(artista.getNombre());
            biografiaTextArea.setText(artista.getBiografia());

            if (artista.getGenero() != null && !artista.getGenero().isEmpty()) {
                String generosTexto = artista.getGenero().stream()
                        .map(this::formatearGenero)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("No definido");
                generoMenuButton.setText(generosTexto);
            } else {
                generoMenuButton.setText("No definido");
            }

            if (artista.getImagen() != null) {
                try {
                    Image imagen = new Image(new ByteArrayInputStream(artista.getImagen()));
                    artistaImageView.setImage(imagen);
                } catch (Exception e) {
                    System.out.println("Error al cargar imagen: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Convierte el nombre del género a un formato legible con mayúsculas iniciales y espacios.
     *
     * @param genero Género a formatear
     * @return Nombre del género formateado para mostrar
     */
    private String formatearGenero(Genero genero) {
        String nombre = genero.name().replace('_', ' ').toLowerCase();
        String[] palabras = nombre.split(" ");
        StringBuilder resultado = new StringBuilder();
        for (String palabra : palabras) {
            if (!palabra.isEmpty()) {
                resultado.append(Character.toUpperCase(palabra.charAt(0)))
                        .append(palabra.substring(1)).append(" ");
            }
        }
        return resultado.toString().trim();
    }


    /**
     * Elimina el artista actual si no tiene elementos asociados. Muestra alertas según el resultado.
     *
     * @param event Evento de acción generado por el botón de eliminar
     */
    @FXML
    void eliminarArtista(ActionEvent event) {
        if (artista != null) {
            if (servicioValidacion.tieneElementosAsociados(artista)) {
                mostrarAlerta("No se puede eliminar: el artista tiene elementos asociados (canciones o playlists).");
            } else {
                try {
                    boolean eliminado = artistaDAO.eliminar(artista.getId());
                    if (eliminado) {
                        mostrarAlerta("El artista fue eliminado correctamente.");
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
    }

    /**
     * Inicializa los componentes de la interfaz deshabilitando campos no editables.
     */
    @FXML
    void initialize() {
        nombreTextField.setEditable(false);
        biografiaTextArea.setEditable(false);
        generoMenuButton.setDisable(true);
    }

    /**
     * Cierra la ventana actual.
     */
    @FXML
    void cerrarVentana() {
        Stage stage = (Stage) cerrarButton.getScene().getWindow();
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
}
