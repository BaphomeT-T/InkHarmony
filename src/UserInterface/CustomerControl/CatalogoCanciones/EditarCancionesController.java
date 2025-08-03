/*
|-----------------------------------------------|
| © 2025 EPN-FIS, Todos los derechos reservados |
| GR1SW                                         |
|-----------------------------------------------|
Autor: Grupo - A
Descripción: Controlador JavaFX para la edición de canciones en el catálogo.
*/

package UserInterface.CustomerControl.CatalogoCanciones;

// Importación del DTO (Data Transfer Object) que representa una canción
import BusinessLogic.Artista;
import DataAccessComponent.DAO.ArtistaDAO;
import DataAccessComponent.DTO.ArtistaDTO;
import DataAccessComponent.DTO.CancionDTO;
import DataAccessComponent.DAO.CancionDAO;
import BusinessLogic.Cancion;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

// Importaciones necesarias para manejar eventos y controles de la interfaz JavaFX
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import BusinessLogic.Genero;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador de la vista de edición de canciones.
 * Gestiona la lógica para visualizar y editar los datos de una canción.
 */
public class EditarCancionesController {
    private File nuevaImagenSeleccionada = null;

    private File nuevoArchivoMP3 = null;

    // Atributo privado que almacena la canción actual a editar
    private CancionDTO cancion;

    // Instancia del DAO para operaciones de base de datos
    private CancionDAO cancionDAO = new CancionDAO();

    private CatalogoCancionesController catalogoController;

    private ArtistaDTO artista;

    // Referencia al controlador del catálogo para actualizar la tabla
    private CatalogoCancionesController catalogoCancionesController;

    // Campos de entrada de texto vinculados a la interfaz FXML
    @FXML
    private TextField nombreTextField;

    @FXML
    private TextField nombreTextField1;

    @FXML
    private MenuButton generoMenuButton;

    @FXML
    private MenuButton artistaMenuButton;

    @FXML
    private ImageView cancionImageView;

    @FXML
    private Label archivoSeleccionadoLabel;
    /**
     * Método invocado al hacer clic en el botón "Editar".
     * Implementa validación, persistencia y cierre de la ventana.
     *
     * @param actionEvent el evento generado por la acción del botón.
     */
    public void editarCancion(ActionEvent actionEvent) {

        // Validar que los campos no estén vacíos
        String nuevoTitulo = nombreTextField.getText().trim();
        String nuevoAnio = nombreTextField1.getText().trim();

        if (nuevoTitulo.isEmpty()) {
            mostrarAlerta("El título de la canción no puede estar vacío.");
            return;
        }

        if (nuevoAnio.isEmpty()) {
            mostrarAlerta("El año de lanzamiento no puede estar vacío.");
            return;
        }

        // Validar que el año sea un número válido
        int anio;
        try {
            anio = Integer.parseInt(nuevoAnio);
            if (anio < 1900 || anio > 2025) {
                mostrarAlerta("El año debe estar entre 1900 y 2025.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("El año debe ser un número válido.");
            return;
        }

        try {
            // Obtener los géneros seleccionados del MenuButton
            List<Genero> generosSeleccionados = generoMenuButton.getItems().stream()
                    .filter(item -> item instanceof CheckMenuItem && ((CheckMenuItem)item).isSelected())
                    .map(item -> (Genero)item.getUserData())
                    .toList();

            // Validar que al menos un género esté seleccionado
            if (generosSeleccionados.isEmpty()) {
                mostrarAlerta("Debes seleccionar al menos un género musical.");
                return;
            }

            List<ArtistaDTO> artistasSeleccionados = artistaMenuButton.getItems().stream()
                    .filter(item -> item instanceof CheckMenuItem && ((CheckMenuItem)item).isSelected())
                    .map(item -> (ArtistaDTO) item.getUserData())
                    .collect(Collectors.toList());

            if (artistasSeleccionados.isEmpty()) {
                mostrarAlerta("Debes seleccionar al menos un artista.");
                return;
            }

            // Actualizar el objeto canción con los nuevos valores
            cancion.setTitulo(nuevoTitulo);
            cancion.setAnio(anio);
            cancion.setGeneros(new ArrayList<>(generosSeleccionados));
            cancion.setArtistas(artistasSeleccionados);

            if (nuevaImagenSeleccionada != null) {
                byte[] imagenBytes = Files.readAllBytes(nuevaImagenSeleccionada.toPath());
                cancion.setPortada(imagenBytes);
            }

            if (nuevoArchivoMP3 != null) {
                byte[] archivoBytes = Files.readAllBytes(nuevoArchivoMP3.toPath());
                cancion.setArchivoMP3(archivoBytes);
            }

            // Guardar los cambios en la base de datos
            boolean exito = cancionDAO.actualizar(cancion);

            if (exito) {
                System.out.println("Canción actualizada exitosamente: " + nuevoTitulo);
                mostrarExito("La canción fue actualizada correctamente.");

                // Actualizar el catálogo si existe la referencia
                if (this.catalogoController != null) {
                    System.out.println("Llamando a refrescarTabla() desde EditarCancionesController...");
                    this.catalogoController.refrescarTabla();
                    System.out.println("refrescarTabla() llamado exitosamente");
                } else {
                    System.out.println("catalogoController es null, no se puede refrescar la tabla");
                }

                // Cerrar la ventana
                Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                stage.close();
            } else {
                mostrarAlerta("No se pudo actualizar la canción. Intente de nuevo.");
            }

        } catch (Exception e) {
            System.out.println("Error al actualizar canción: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error al actualizar la canción: " + e.getMessage());
        }
    }

    /**
     * Muestra una alerta de advertencia con el mensaje especificado.
     * @param mensaje El mensaje de advertencia a mostrar.
     */
    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(AlertType.WARNING);
        alerta.setTitle("Advertencia");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Muestra una alerta de éxito con el mensaje especificado.
     * @param mensaje El mensaje de éxito a mostrar.
     */
    private void mostrarExito(String mensaje) {
        Alert alerta = new Alert(AlertType.INFORMATION);
        alerta.setTitle("Éxito");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Cierra la ventana actual de edición.
     */
    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) nombreTextField.getScene().getWindow();
        stage.close();
    }

    /**
     * Recibe una canción desde otro controlador y la carga en los campos de la vista.
     * @param cancion objeto de tipo CancionDTO que contiene los datos a editar.
     */
    public void setCancion(CancionDTO cancion) {
        this.cancion = cancion;

        // Se cargan los datos actuales en los campos de texto de la interfaz
        nombreTextField.setText(cancion.getTitulo());
        nombreTextField1.setText(String.valueOf(cancion.getAnio())); // Campo para año

        if (cancion.getPortada()!= null && cancion.getPortada().length>0){
            cancionImageView.setImage(new Image(new ByteArrayInputStream(cancion.getPortada())));
        }

        // Configurar géneros en el MenuButton
        configurarGeneros();
        configurarArtistas();
        System.out.println("Cargando canción para editar: " + cancion.getTitulo());
        Platform.runLater(() -> nombreTextField.positionCaret(nombreTextField.getText().length()));

    }

    /**
     * Configura y carga los artistas disponibles en el MenuButton de artistas,
     * y marca aquellos que ya están asociados a la canción.
     */
    private void configurarArtistas() {
        artistaMenuButton.getItems().clear();
        List<ArtistaDTO> artistasDisponibles;
        try {
            artistasDisponibles = new ArtistaDAO().buscarTodo();
        } catch (Exception e) {
            mostrarAlerta("No se pudo cargar la lista de artistas.");
            return;
        }

        for (ArtistaDTO artista : artistasDisponibles) {
            CheckMenuItem item = new CheckMenuItem(artista.getNombre());
            item.setUserData(artista);
            boolean yaSeleccionado = cancion.getArtistas() != null &&
                    cancion.getArtistas().stream().anyMatch(a -> a.getId() == artista.getId());
            item.setSelected(yaSeleccionado);
            item.setOnAction(e -> actualizarTextoMenu(artistaMenuButton));
            artistaMenuButton.getItems().add(item);
        }

        actualizarTextoMenu(artistaMenuButton);
    }

    /**
     * Actualiza el texto del MenuButton de artistas según los artistas seleccionados.
     * @param artistaMenuButton El MenuButton de artistas.
     */
    private void actualizarTextoMenu(MenuButton artistaMenuButton) {
        List<String> seleccionados = artistaMenuButton.getItems().stream()
                .filter(item -> item instanceof CheckMenuItem && ((CheckMenuItem)item).isSelected())
                .map(MenuItem::getText)
                .collect(Collectors.toList());

        artistaMenuButton.setText(seleccionados.isEmpty() ? "Selecciona" : String.join(", ", seleccionados));
    }

    /**
     * Configura los géneros musicales en el MenuButton y marca los seleccionados.
     */
    private void configurarGeneros() {
        // Limpiar items existentes
        generoMenuButton.getItems().clear();

        // Configurar todos los géneros disponibles
        for (Genero genero : Genero.values()) {
            CheckMenuItem item = new CheckMenuItem(formatearGenero(genero));
            item.setUserData(genero);

            // Marcar como seleccionado si la canción ya tiene este género
            if (cancion.getGeneros() != null && cancion.getGeneros().contains(genero)) {
                item.setSelected(true);
            }

            // Agregar listener para actualizar el texto del botón
            item.setOnAction(e -> {
                e.consume();
                actualizarTextoMenuButton();
            });

            generoMenuButton.getItems().add(item);
        }

        // Actualizar el texto inicial del botón
        actualizarTextoMenuButton();
    }

    /**
     * Actualiza el texto del MenuButton con los géneros seleccionados.
     */
    private void actualizarTextoMenuButton() {
        List<String> seleccionados = generoMenuButton.getItems().stream()
                .filter(item -> item instanceof CheckMenuItem && ((CheckMenuItem)item).isSelected())
                .map(item -> ((CheckMenuItem)item).getText())
                .toList();

        generoMenuButton.setText(seleccionados.isEmpty() ? "Selecciona" : String.join(", ", seleccionados));
    }

    /**
     * Formatea el nombre del género para mostrarlo con mayúsculas iniciales.
     * @param genero El género musical a formatear.
     * @return El nombre formateado.
     */
    private String formatearGenero(Genero genero) {
        String nombre = genero.name().replace('_', ' ').toLowerCase();
        String[] palabras = nombre.split(" ");
        StringBuilder resultado = new StringBuilder();

        for (String palabra : palabras) {
            if (!palabra.isEmpty()) {
                // Capitalizar la primera letra de cada palabra
                resultado.append(Character.toUpperCase(palabra.charAt(0)))
                        .append(palabra.substring(1))
                        .append(" ");
            }
        }
        return resultado.toString().trim();
    }

    /**
     * Abre un selector de archivos para elegir una imagen PNG/JPG/JPEG.
     * Solo acepta imagen de 264x264 px.
     * @param actionEvent Evento generado al hacer clic en el botón de seleccionar imagen.
     */
    public void seleccionarImagen(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen de la canción");

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
                    cancionImageView.setImage(imagen);
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

    /**
     * Abre un selector de archivos para elegir un archivo .mp3.
     * @param event Evento generado al hacer clic en el botón de cambiar MP3.
     */
    @FXML
    public void seleccionarArchivoMP3(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo MP3");

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "Archivos MP3 (*.mp3)", "*.mp3"
        );
        fileChooser.getExtensionFilters().add(extFilter);

        File archivoSeleccionado = fileChooser.showOpenDialog(null);
        if (archivoSeleccionado != null) {
            try {
                // Solo guarda la referencia, no cargues aún los bytes
                this.nuevoArchivoMP3 = archivoSeleccionado;

                archivoSeleccionadoLabel.setText("♪ " + archivoSeleccionado.getName());
                archivoSeleccionadoLabel.setVisible(true);
                System.out.println("Archivo MP3 seleccionado: " + archivoSeleccionado.getAbsolutePath());
            } catch (Exception e) {
                mostrarAlerta("Error al cargar el archivo MP3.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Establece la referencia al controlador del catálogo para actualizar la tabla.
     * @param catalogoController Referencia al controlador del catálogo
     */
    public void setCatalogoController(CatalogoCancionesController catalogoController) {
        this.catalogoController = catalogoController;
    }
}