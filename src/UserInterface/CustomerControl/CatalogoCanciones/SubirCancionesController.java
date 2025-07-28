/*
|-----------------------------------------------|
| © 2025 EPN-FIS, Todos los derechos reservados |
| GR1SW                                         |
|-----------------------------------------------|
Autor: Duncan Licuy
Descripción: Controlador para la subir canciones.
*/
package UserInterface.CustomerControl.CatalogoCanciones;

import BusinessLogic.Cancion;
import BusinessLogic.Genero;
import BusinessLogic.ServicioValidacionCancion;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class SubirCancionesController {
    // Componentes de la interfaz gráfica
    @FXML private ImageView portadaImageView;
    @FXML private Label letraLabel;
    @FXML private TextArea letraTextArea;
    @FXML private Button cerrarButton;
    @FXML private MenuButton generoMenuButton;
    @FXML private Label generoLabel;
    @FXML private Label tituloLabel;
    @FXML private TextField tituloTextField;
    @FXML private TextField artistaTextField;
    @FXML private TextField duracionTextField;
    @FXML private Label mensajeTituloLabel;
    @FXML private Button publicarButton;
    @FXML private Label seleccionarLabel;

    private List<Genero> generosSeleccionados = new ArrayList<>();

    @FXML
    void publicar(ActionEvent event) {
        String titulo = this.tituloTextField.getText();
        String artista = this.artistaTextField.getText();
        String duracion = this.duracionTextField.getText();
        String letra = this.letraTextArea.getText();

        if (!titulo.isBlank() && !artista.isBlank() && !duracion.isBlank() &&
                !letra.isBlank() && this.portadaImageView.getImage() != null) {

            List<Genero> generosSeleccionados = this.generoMenuButton.getItems().stream()
                    .filter(item -> item instanceof CheckMenuItem && ((CheckMenuItem)item).isSelected())
                    .map(item -> (Genero)item.getUserData())
                    .toList();

            byte[] imagenBytes = null;

            try {
                URI uri = new URI(this.portadaImageView.getImage().getUrl());
                Path path = Paths.get(uri);
                imagenBytes = Files.readAllBytes(path);
            } catch (Exception e) {
                this.mostrarAlerta("Error al procesar la carátula.");
                return;
            }

            ServicioValidacionCancion validador = new ServicioValidacionCancion();
            if (!validador.esNombreUnico(titulo)) {
                this.mostrarAlerta("El título de la canción ya existe.");
            } else {
                try {
                    Cancion cancionLogic = new Cancion();
                    boolean exito = cancionLogic.registrar(
                            titulo,
                            artista,
                            duracion,
                            generosSeleccionados,
                            letra,
                            imagenBytes
                    );

                    if (exito) {
                        this.mostrarExito("Canción subida con éxito.");
                        this.limpiarCampos();
                    } else {
                        this.mostrarAlerta("No se pudo subir la canción.");
                    }
                } catch (Exception e) {
                    this.mostrarAlerta("Error al subir la canción: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            this.mostrarAlerta("Completa todos los campos antes de publicar.");
        }
    }

    private void mostrarExito(String mensaje) {
        Alert alerta = new Alert(AlertType.INFORMATION);
        alerta.setTitle("Éxito");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void limpiarCampos() {
        this.tituloTextField.clear();
        this.artistaTextField.clear();
        this.duracionTextField.clear();
        this.letraTextArea.clear();
        this.portadaImageView.setImage(new Image(
                Objects.requireNonNull(this.getClass().getResourceAsStream(
                        "/UserInterface/Resources/img/CatalogoCanciones/caratula_default.png"
                ))
        ));
        this.mensajeTituloLabel.setText("");
        this.generoMenuButton.setText("Seleccione");

        for(MenuItem item : this.generoMenuButton.getItems()) {
            if (item instanceof CheckMenuItem checkItem) {
                checkItem.setSelected(false);
            }
        }
    }

    @FXML
    public void seleccionarCaratula(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar carátula de la canción");
        FileChooser.ExtensionFilter extFilterImagenes = new FileChooser.ExtensionFilter(
                "Imágenes (*.png, *.jpg, *.jpeg)",
                new String[]{"*.png", "*.jpg", "*.jpeg"}
        );
        fileChooser.getExtensionFilters().add(extFilterImagenes);
        File archivoSeleccionado = fileChooser.showOpenDialog(null);

        if (archivoSeleccionado != null) {
            try {
                Image imagen = new Image(archivoSeleccionado.toURI().toString());
                if (imagen.getWidth() == 264.0 && imagen.getHeight() == 264.0) {
                    this.portadaImageView.setImage(imagen);
                    System.out.println("Carátula cargada correctamente: " +
                            archivoSeleccionado.getAbsolutePath());
                } else {
                    this.mostrarAlerta("La carátula debe tener exactamente 264x264 píxeles.");
                }
            } catch (Exception e) {
                this.mostrarAlerta("Error al cargar la carátula.");
            }
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(AlertType.WARNING);
        alerta.setTitle("Advertencia");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    public void initialize() {
        for(Genero genero : Genero.values()) {
            CheckMenuItem item = new CheckMenuItem(this.formatearGenero(genero));
            item.setUserData(genero);
            item.setOnAction(e -> {
                e.consume();
                this.actualizarTextoMenuButton();
            });
            this.generoMenuButton.getItems().add(item);
        }

        this.tituloTextField.textProperty().addListener(this::changed);

        Rectangle clip = new Rectangle(306.0, 264.0);
        clip.setArcWidth(30.0);
        clip.setArcHeight(30.0);
        this.portadaImageView.setClip(clip);
    }

    private void actualizarTextoMenuButton() {
        List<String> seleccionados = this.generoMenuButton.getItems().stream()
                .filter(item -> item instanceof CheckMenuItem && ((CheckMenuItem)item).isSelected())
                .map(item -> ((CheckMenuItem)item).getText())
                .toList();

        this.generoMenuButton.setText(seleccionados.isEmpty() ? "Seleccione" : String.join(", ", seleccionados));
    }

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

    private void changed(ObservableValue<? extends String> obs, String oldText, String newText) {
        if (newText != null && !newText.trim().isEmpty()) {
            ServicioValidacionCancion servicioValidacion = new ServicioValidacionCancion();
            boolean esUnico = servicioValidacion.esNombreUnico(newText);
            if (!esUnico) {
                this.mensajeTituloLabel.setText("El título de la canción ya está en uso");
                this.mensajeTituloLabel.setStyle("-fx-text-fill: red;");
            } else {
                this.mensajeTituloLabel.setText("");
            }
        } else {
            this.mensajeTituloLabel.setText("");
        }
    }
}