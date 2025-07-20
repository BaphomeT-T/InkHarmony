package UserInterface.CustomerControl.CatalogoArtistas;

import DataAccessComponent.DTO.ArtistaDTO;
import BusinessLogic.ServicioValidacion;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

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

    // Este metodo se llama al iniciar el controlador con el artista a eliminar
    public void setArtista(ArtistaDTO artista) {
        this.artista = artista;
        mostrarInformacionArtista();
    }

    private void mostrarInformacionArtista() {
//        if (artista != null) {
//            nombreTextField.setText(artista.getNombre());
//            biografiaTextArea.setText(artista.getBiografia());
//            biografiaTextArea.setEditable(false);
//
//            if (artista.getGenero() != null && !artista.getGenero().isEmpty()) {
//                String generosTexto = artista.getGenero().stream()
//                        .map(this::formatearGenero)
//                        .reduce((a, b) -> a + ", " + b)
//                        .orElse("No definido");
//                generoMenuButton.setText(generosTexto);
//            } else {
//                generoMenuButton.setText("No definido");
//            }
//
//            if (artista.getImagen() != null) {
//                try {
//                    Image imagen = new Image("file:" + artista.getImagen());
//                    artistaImageView.setImage(imagen);
//                } catch (Exception e) {
//                    System.out.println("Error al cargar la imagen: " + e.getMessage());
//                }
//            }
//        }
    }

//    private String formatearGenero(Genero genero) {
//        String nombre = genero.name().replace('_', ' ').toLowerCase();
//        String[] palabras = nombre.split(" ");
//        StringBuilder resultado = new StringBuilder();
//
//        for (String palabra : palabras) {
//            if (!palabra.isEmpty()) {
//                resultado.append(Character.toUpperCase(palabra.charAt(0)))
//                        .append(palabra.substring(1)).append(" ");
//            }
//        }
//        return resultado.toString().trim();
//    }

    @FXML
    void eliminarArtista(ActionEvent event) {
        if (artista != null) {
            if (servicioValidacion.tieneElementosAsociados(artista)) {
                mostrarAlerta("No se puede eliminar: el artista tiene canciones asociadas.");
            } else {
                // Aquí iría la lógica real para eliminar de la base de datos
                System.out.println("🗑 Artista eliminado: " + artista.getNombre());
                mostrarAlerta("El artista fue eliminado correctamente.");
                cerrarVentana();
            }
        }
    }

    @FXML
    void initialize() {
        // Se asegura que los campos sean solo lectura
        nombreTextField.setEditable(false);
        biografiaTextArea.setEditable(false);
        generoMenuButton.setDisable(true);
    }

    @FXML
    void cerrarVentana() {
        Stage stage = (Stage) cerrarButton.getScene().getWindow();
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
