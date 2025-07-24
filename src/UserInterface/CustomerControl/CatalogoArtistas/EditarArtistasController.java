package UserInterface.CustomerControl.CatalogoArtistas;

import BusinessLogic.Artista;
import BusinessLogic.Genero;
import DataAccessComponent.DTO.ArtistaDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class EditarArtistasController implements Initializable{
    private File nuevaImagenSeleccionada = null;
    private ArtistaDTO artista;
    private Artista artistaNuevo = new Artista();
    @FXML
    private TextField txtNombre;

    @FXML
    private TextArea txtBiografia;
    @FXML private MenuButton generoMenuButton;
    @FXML private ImageView artistaImageView;
    @FXML private Button actualizarButton;
    @FXML private Button cerrarButton;

    /**
     *
     * @param artista
     */
    public void setArtista(ArtistaDTO artista) {
        this.artista = artista;
        txtNombre.setText(artista.getNombre());
        txtBiografia.setText(artista.getBiografia());
        if (artista.getImagen()!= null && artista.getImagen().length>0){
            artistaImageView.setImage(new Image(new ByteArrayInputStream(artista.getImagen())));
        }
        List<String> nombresGeneros = artista.getGenero().stream().map(Enum::name).collect(Collectors.toList());
        for (MenuItem item : generoMenuButton.getItems()){
            if (item instanceof CheckMenuItem){
                CheckMenuItem checkMenuItem = (CheckMenuItem) item;
                if (nombresGeneros.contains(checkMenuItem.getText())){
                    checkMenuItem.setSelected(true);
                }
            }
        }
        actualizarTextoGeneroButton();
        actualizarTextoMenuButton();
    }

    public void actualizarArtista(ActionEvent actionEvent) {
        try {
            if (nuevaImagenSeleccionada != null) {
                byte[] imagenBytes = Files.readAllBytes(nuevaImagenSeleccionada.toPath());
                artista.setImagen(imagenBytes);
            }
            String nombre = txtNombre.getText();
            String biografia = txtBiografia.getText();

            List<Genero> generosSeleccionados = new ArrayList<>();
            for (MenuItem item : generoMenuButton.getItems()) {
                if (item instanceof CheckMenuItem && ((CheckMenuItem) item).isSelected()) {
                    generosSeleccionados.add(Genero.valueOf(item.getText()));
                }
            }
            if (nombre.trim().isEmpty() || generosSeleccionados.isEmpty()) {
                mostrarAlerta("El nombre y al menos un género son obligatorios.");
                return;
            }

            List<ArtistaDTO> existentes = artistaNuevo.buscarTodo();
            for (ArtistaDTO a : existentes) {
                if (a.getNombre().equalsIgnoreCase(nombre) && a.getId() != artista.getId()) {
                    mostrarAlerta("El nombre y al menos un género son obligatorios.");
                    return;
                }
            }

            artista.setNombre(nombre);
            artista.setBiografia(biografia);
            artista.setGeneros(generosSeleccionados);
            boolean exito = artistaNuevo.actualizar(artista);
            if (exito) {
                mostrarExito("El artista se ha actualizado correctamente.");
                cerrarVentana(null);
            } else {
                mostrarAlerta("No se pudo actualizar el artista.");
            }

        } catch (Exception e) {
            mostrarAlerta("Ocurrió un error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     *
     * @param mensaje
     */
    private void mostrarExito(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Éxito");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for (Genero g: Genero.values()){
            CheckMenuItem menuItem = new CheckMenuItem(g.toString());
            menuItem.setOnAction(e ->{
                actualizarTextoMenuButton();
            } );
            generoMenuButton.getItems().add(menuItem);
        }
    }

    /**
     *
     */
    private void actualizarTextoMenuButton() {
        String texto = generoMenuButton.getItems().stream()
                .filter(item -> item instanceof CheckMenuItem && ((CheckMenuItem) item).isSelected())
                .map(MenuItem::getText)
                .collect(Collectors.joining(", "));

        if (texto.isEmpty()) {
            generoMenuButton.setText("Selecciona");
        } else {
            generoMenuButton.setText(texto);
        }
    }

    /**
     *
     * @param actionEvent
     */
    public void cerrarVentana(ActionEvent actionEvent) {
        Stage stage = (Stage) cerrarButton.getScene().getWindow();
        stage.close();
    }

    /**
     *
     */
    private void actualizarTextoGeneroButton() {
        String texto = generoMenuButton.getItems().stream()
                .filter(item -> item instanceof CheckMenuItem && ((CheckMenuItem) item).isSelected())
                .map(MenuItem::getText)
                .collect(Collectors.joining(", "));

        if (texto.isEmpty()) {
            generoMenuButton.setText("Selecciona");
        } else {
            generoMenuButton.setText(texto);
        }
    }

    /**
     *
     * @param mensaje
     */
    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Advertencia");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

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
                    this.nuevaImagenSeleccionada = archivoSeleccionado;
                    System.out.println("Imagen cargada correctamente: " + archivoSeleccionado.getAbsolutePath());
                } else {
                    mostrarAlerta("La imagen debe tener exactamente 264x264 píxeles.");
                }
            } catch (Exception e) {
                mostrarAlerta("Error al cargar la imagen.");
            }
        }
    }
}
