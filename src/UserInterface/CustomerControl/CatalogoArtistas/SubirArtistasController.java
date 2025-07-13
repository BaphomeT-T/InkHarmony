package UserInterface.CustomerControl.CatalogoArtistas;
import DataAccessComponent.DTO.CatalogoArtistas.ServicioValidacion;
import DataAccessComponent.DTO.CatalogoCanciones.Genero;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SubirArtistasController {

    @FXML
    private ImageView artistaImageView;

    @FXML
    private Label biografiaLabel;

    @FXML
    private TextArea biografiaTextArea;

    @FXML
    private Button cerrarButton;

    //@FXML
    //private ComboBox<Genero> generoComboBox;

    @FXML
    private MenuButton generoMenuButton;

    @FXML
    private Label generoLabel;

    @FXML
    private Label nombreLabel;

    @FXML
    private TextField nombreTextField;

    @FXML
    private Label mensajeNombreLabel;

    @FXML
    private Button publicarButton;

    @FXML
    private Label seleccionarLabel;

    private List<Genero> generosSeleccionados;

    public SubirArtistasController() {
        generosSeleccionados = new ArrayList<>();
    }

    @FXML
    void publicar(ActionEvent event) {
        List<Genero> generosSeleccionados = generoMenuButton.getItems().stream()
                .filter(item -> item instanceof CheckMenuItem && ((CheckMenuItem) item).isSelected())
                .map(item -> (Genero) item.getUserData())
                .toList();

        System.out.println("Géneros seleccionados:");
        for (Genero genero : generosSeleccionados) {
            System.out.println(genero);
        }
    }

    @FXML
    public void seleccionarImagen(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen del artista");

        // Solo permitir archivos PNG
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Archivos PNG (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);

        // Mostrar el diálogo
        File archivoSeleccionado = fileChooser.showOpenDialog(null);
        if (archivoSeleccionado != null) {
            try {
                Image imagen = new Image(archivoSeleccionado.toURI().toString());

                // Validar dimensiones exactas
                if (imagen.getWidth() == 264 && imagen.getHeight() == 264) {
                    artistaImageView.setImage(imagen);
                    // Opcional: puedes guardar la ruta para guardarla después
                    System.out.println("Imagen cargada correctamente: " + archivoSeleccionado.getAbsolutePath());
                } else {
                    mostrarAlerta("La imagen debe tener exactamente 264x264 píxeles.");
                }
            } catch (Exception e) {
                mostrarAlerta("Error al cargar la imagen.");
            }
        }
    }
    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Advertencia");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }


    @FXML
    public void initialize() {

        for (Genero genero : Genero.values()) {
            CheckMenuItem item = new CheckMenuItem(formatearGenero(genero));
            item.setUserData(genero);

            item.setOnAction(e -> {
                e.consume(); // evita que se cierre el menú al hacer clic
                actualizarTextoMenuButton();
            });
            generoMenuButton.getItems().add(item);
        }
        // Validación del nombre único mientras se esta escribiendo
        nombreTextField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText != null && !newText.trim().isEmpty()) {
                ServicioValidacion servicioValidacion = new ServicioValidacion();
                boolean esUnico = servicioValidacion.esNombreUnico(newText);
                if (!esUnico) {
                    mensajeNombreLabel.setText("El nombre del artista ya está en uso");
                    mensajeNombreLabel.setStyle("-fx-text-fill: red;");
                } else {
                    mensajeNombreLabel.setText("");
                }
            } else {
                mensajeNombreLabel.setText("");
            }
        });
    }

    private void actualizarTextoMenuButton() {
        List<String> seleccionados = generoMenuButton.getItems().stream()
                .filter(item -> item instanceof CheckMenuItem && ((CheckMenuItem) item).isSelected())
                .map(item -> ((CheckMenuItem) item).getText())
                .toList();

        generoMenuButton.setText(seleccionados.isEmpty() ? "Seleccione" : String.join(", ", seleccionados));
    }

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



}
