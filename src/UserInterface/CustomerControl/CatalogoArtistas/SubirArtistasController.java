package UserInterface.CustomerControl.CatalogoArtistas;
import BusinessLogic.Artista;
import BusinessLogic.Genero;
import DataAccessComponent.DAO.ArtistaDAO;
import BusinessLogic.ServicioValidacion;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    private CatalogoArtistasController catalogoController;
    private List<Genero> generosSeleccionados;

    public SubirArtistasController() {
        generosSeleccionados = new ArrayList<>();
    }


    @FXML
    void publicar(ActionEvent event) {
        String nombre = nombreTextField.getText();
        String biografia = biografiaTextArea.getText();

        if (nombre.isBlank() || biografia.isBlank() || artistaImageView.getImage() == null) {
            mostrarAlerta("Completa todos los campos antes de publicar.");
            return;
        }

        List<Genero> generosSeleccionados = generoMenuButton.getItems().stream()
                .filter(item -> item instanceof CheckMenuItem && ((CheckMenuItem) item).isSelected())
                .map(item -> (Genero) item.getUserData())
                .toList();

        byte[] imagenBytes = null;
        try {
            java.net.URI uri = new java.net.URI(artistaImageView.getImage().getUrl());
            java.nio.file.Path path = java.nio.file.Paths.get(uri);
            imagenBytes = java.nio.file.Files.readAllBytes(path);
        } catch (Exception e) {
            mostrarAlerta("Error al procesar la imagen.");
            return;
        }

        // Validación usando ServicioValidacion
        ServicioValidacion validador = new ServicioValidacion();
        if (!validador.esNombreUnico(nombre)) {
            mostrarAlerta("El nombre del artista ya existe.");
            return;
        }

        // Registrar con lógica de negocio
        try {
            Artista artistaLogic = new Artista();
            boolean exito = artistaLogic.registrar(nombre, generosSeleccionados, biografia, imagenBytes);

            if (exito) {
                mostrarExito("Artista registrado con éxito.");
                limpiarCampos();
                if (catalogoController != null) {
                    catalogoController.actualizarTabla(); // 🔁 Recargar la tabla del catálogo
                }
                cerrarVentana(null);
            } else {
                mostrarAlerta("No se pudo registrar el artista.");
            }
        } catch (Exception e) {
            mostrarAlerta("Error al registrar el artista.");
            e.printStackTrace();
        }
    }


    private void mostrarExito(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Éxito");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void limpiarCampos() {
        nombreTextField.clear();
        biografiaTextArea.clear();
        artistaImageView.setImage(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/UserInterface/Resources/img/CatalogoArtistas/camara.png")))
        );
        mensajeNombreLabel.setText("");
        generoMenuButton.setText("Seleccione");
        for (MenuItem item : generoMenuButton.getItems()) {
            if (item instanceof CheckMenuItem checkItem) {
                checkItem.setSelected(false);
            }
        }


    }


    @FXML
    public void seleccionarImagen(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen del artista");

        // Permitir PNG, JPG y JPEG en un solo filtro
        FileChooser.ExtensionFilter extFilterImagenes = new FileChooser.ExtensionFilter(
                "Imágenes (*.png, *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg"
        );
        fileChooser.getExtensionFilters().add(extFilterImagenes);

        File archivoSeleccionado = fileChooser.showOpenDialog(null);
        if (archivoSeleccionado != null) {
            try {
                Image imagen = new Image(archivoSeleccionado.toURI().toString());

                if (imagen.getWidth() == 264 && imagen.getHeight() == 264) {
                    artistaImageView.setImage(imagen);
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
        //Sirve para redondear los bordes de la imagen

        Rectangle clip = new Rectangle(306, 264);
        clip.setArcWidth(30);
        clip.setArcHeight(30);
        artistaImageView.setClip(clip);

    }

    public void cerrarVentana(ActionEvent actionEvent) {
        Stage stage = (Stage) cerrarButton.getScene().getWindow();
        stage.close();
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

    public void setCatalogoController(CatalogoArtistasController controller) {
        this.catalogoController = controller;
    }

}
