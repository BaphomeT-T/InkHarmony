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
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class EditarArtistasController implements Initializable{
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

    public void editarArtista(ActionEvent actionEvent) {
    }

    // Temporal (se creo para conectar la pantalla)
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
    }

    public void actualizarArtista(ActionEvent actionEvent) {
        try {
            String nombre = txtNombre.getText();
            String biografia = txtBiografia.getText();

            List<Genero> generosSeleccionados = new ArrayList<>();
            for (MenuItem item : generoMenuButton.getItems()) {
                if (item instanceof CheckMenuItem && ((CheckMenuItem) item).isSelected()) {
                    generosSeleccionados.add(Genero.valueOf(item.getText()));
                }
            }
            if (nombre.trim().isEmpty() || generosSeleccionados.isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Datos incompletos", "El nombre y al menos un género son obligatorios.");
                return;
            }

            List<ArtistaDTO> existentes = artistaNuevo.buscarTodo();
            for (ArtistaDTO a : existentes) {
                if (a.getNombre().equalsIgnoreCase(nombre) && a.getId() != artista.getId()) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Nombre duplicado", "Ya existe otro artista con ese nombre.");
                    return;
                }
            }

            artista.setNombre(nombre);
            artista.setBiografia(biografia);
            artista.setGeneros(generosSeleccionados);
            boolean exito = artistaNuevo.actualizar(artista);
            if (exito) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "El artista se ha actualizado correctamente.");
                // 5. Cerrar la ventana
                cerrarVentana(null);
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo actualizar el artista.");
            }

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Sistema", "Ocurrió un error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for (Genero g: Genero.values()){
            CheckMenuItem menuItem = new CheckMenuItem(g.toString());
            generoMenuButton.getItems().add(menuItem);
        }
    }

    public void cerrarVentana(ActionEvent actionEvent) {
        Stage stage = (Stage) cerrarButton.getScene().getWindow();
        stage.close();
    }
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
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
