/*
|-----------------------------------------------|
| © 2025 EPN-FIS, Todos los derechos reservados |
| GR1SW                                         |
|-----------------------------------------------|
Autor: Grupo - A
Descripción: Controlador del catálogo de canciones.
*/

package UserInterface.CustomerControl.CatalogoCanciones;

import BusinessLogic.Cancion;
import BusinessLogic.Genero;
import BusinessLogic.Artista; // AGREGADO
import BusinessLogic.ServicioValidacionCancion;
import DataAccessComponent.DTO.ArtistaDTO; // AGREGADO
import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors; // AGREGADO

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.collections.FXCollections; // AGREGADO
import javafx.collections.ObservableList; // AGREGADO

/**
 * La clase SubirCancionesController maneja la lógica para subir nuevas canciones al catálogo.
 * Esta clase se encarga de validar los datos ingresados por el usuario,
 * registrar la canción en el sistema y actualizar la interfaz gráfica.
 * @author Grupo - A
 * @version 1.0
 * @since 18-07-2025
 */
public class SubirCancionesController {

    // Componentes de la interfaz gráfica - nombres exactos del FXML
    @FXML private Label tituloLabel;
    @FXML private Label añoLanzamientoLabel;
    @FXML private TextField nombreTextField;  // Para el título
    @FXML private TextField nombreTextField1; // Para el año
    @FXML private Label artistaLabel;
    @FXML private ComboBox<String> artistaComboBox;
    @FXML private ListView<String> cancionesListView;
    @FXML private MenuButton generoMenuButton;
    @FXML private ImageView cancionImageView;
    @FXML private Button cargarImagenButton;
    @FXML private Button registrarButton;
    @FXML private Button seleccionarArchivoButton;
    @FXML private Label archivoSeleccionadoLabel;
    @FXML private Label seleccionarLabel;
    @FXML private Button cerrarButton;

    private List<Genero> generosSeleccionados = new ArrayList<>();

    // Referencia al controlador del catálogo para actualizar la tabla
    private CatalogoCancionesController catalogoController;

    // Variables para almacenar archivos seleccionados
    private byte[] imagenBytes = null;
    private byte[] archivoMP3 = null;
    private double duracionSegundos = 0.0;
    private String nombreArchivoMP3 = "";

    // NUEVAS VARIABLES PARA AUTOCOMPLETADO DE ARTISTAS
    private Artista artistaLogic = new Artista();
    private List<ArtistaDTO> todosLosArtistas = new ArrayList<>();
    private ArtistaDTO artistaSeleccionado = null;

    // Método de inicialización del controlador
    @FXML
    public void initialize() {
        System.out.println("Inicializando SubirCancionController...");

        // Configurar los géneros musicales en el menú
        for(Genero genero : Genero.values()) {
            CheckMenuItem item = new CheckMenuItem(formatearGenero(genero));
            item.setUserData(genero);
            item.setOnAction(e -> {
                e.consume();
                actualizarTextoMenuButton();
            });
            generoMenuButton.getItems().add(item);
        }

        // NUEVA SECCIÓN: Configurar el ComboBox de artistas para autocompletado
        configurarAutocompletadoArtistas();

        // Listener para validar el título en tiempo real
        nombreTextField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText != null && !newText.trim().isEmpty()) {
                ServicioValidacionCancion servicioValidacionCancion = new ServicioValidacionCancion();
                boolean esUnico = servicioValidacionCancion.esNombreUnico(newText);
                if (!esUnico) {
                    // Mostrar mensaje de error si el título ya existe
                    tituloLabel.setText("⚠ El título ya está en uso");
                    tituloLabel.setStyle("-fx-text-fill: red;");
                } else {
                    tituloLabel.setText("Título de la canción");
                    tituloLabel.setStyle("-fx-text-fill: white;");
                }
            } else {
                tituloLabel.setText("Título de la canción");
                tituloLabel.setStyle("-fx-text-fill: white;");
            }
        });

        // Configurar imagen por defecto
        try {
            Image imagenDefault = new Image(
                    Objects.requireNonNull(getClass().getResourceAsStream(
                            "/UserInterface/Resources/img/CatalogoCanciones/camara.png"
                    ))
            );
            cancionImageView.setImage(imagenDefault);
        } catch (Exception e) {
            System.out.println("No se pudo cargar la imagen por defecto");
        }

        // Inicializar el label del archivo seleccionado como invisible
        if (archivoSeleccionadoLabel != null) {
            archivoSeleccionadoLabel.setVisible(false);
        }
    }

    /**
     * NUEVO MÉTODO: Configura el ComboBox de artistas para autocompletado
     * usando el mismo patrón que CatalogoArtistasController
     */
    private void configurarAutocompletadoArtistas() {
        // Cargar todos los artistas al inicio
        cargarTodosLosArtistas();

        // Hacer el ComboBox editable para permitir escritura
        artistaComboBox.setEditable(true);
        artistaComboBox.setDisable(false);

        // Usar el mismo patrón de listener que en CatalogoArtistasController
        artistaComboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarArtistasParaComboBox(newValue);
        });

        // Manejar la selección de un artista
        artistaComboBox.setOnAction(e -> {
            String nombreSeleccionado = artistaComboBox.getSelectionModel().getSelectedItem();
            if (nombreSeleccionado != null && !nombreSeleccionado.isEmpty()) {
                // Buscar el artista completo por nombre
                artistaSeleccionado = todosLosArtistas.stream()
                        .filter(artista -> artista.getNombre().equals(nombreSeleccionado))
                        .findFirst()
                        .orElse(null);

                if (artistaSeleccionado != null) {
                    System.out.println("Artista seleccionado: " + artistaSeleccionado.getNombre() +
                            " (ID: " + artistaSeleccionado.getId() + ")");
                }
            }
        });
    }

    /**
     * NUEVO MÉTODO: Carga todos los artistas desde la base de datos
     */
    private void cargarTodosLosArtistas() {
        try {
            todosLosArtistas = artistaLogic.buscarTodo();
            mostrarTodosLosArtistasEnComboBox();
            System.out.println("Cargados " + todosLosArtistas.size() + " artistas para autocompletado");
        } catch (Exception e) {
            System.err.println("Error al cargar artistas: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error al cargar la lista de artistas");
            todosLosArtistas = new ArrayList<>();
        }
    }

    /**
     * NUEVO MÉTODO: Filtra los artistas para el ComboBox usando el mismo patrón que CatalogoArtistasController
     */
    private void filtrarArtistasParaComboBox(String filtro) {
        if (filtro == null || filtro.isBlank()) {
            // Si no hay filtro, mostrar todos los artistas
            mostrarTodosLosArtistasEnComboBox();
            artistaSeleccionado = null;
            return;
        }

        // Filtrar usando el mismo patrón que CatalogoArtistasController
        List<ArtistaDTO> artistasFiltrados = todosLosArtistas.stream()
                .filter(artista -> artista.getNombre().toLowerCase().contains(filtro.toLowerCase()))
                .collect(Collectors.toList());

        actualizarComboBoxConArtistas(artistasFiltrados);
    }

    /**
     * NUEVO MÉTODO: Muestra todos los artistas en el ComboBox
     */
    private void mostrarTodosLosArtistasEnComboBox() {
        actualizarComboBoxConArtistas(todosLosArtistas);
    }

    /**
     * NUEVO MÉTODO: Actualiza el ComboBox con la lista de artistas proporcionada
     */
    private void actualizarComboBoxConArtistas(List<ArtistaDTO> artistas) {
        // Crear lista de nombres para el ComboBox
        ObservableList<String> nombresArtistas = FXCollections.observableArrayList(
                artistas.stream()
                        .map(ArtistaDTO::getNombre)
                        .collect(Collectors.toList())
        );

        // Guardar el texto actual del editor y la posición del cursor
        String textoActual = artistaComboBox.getEditor().getText();
        int posicionCursor = artistaComboBox.getEditor().getCaretPosition();

        // Actualizar las opciones del ComboBox
        artistaComboBox.setItems(nombresArtistas);

        // Restaurar el texto y posición del cursor
        if (!textoActual.isEmpty()) {
            artistaComboBox.getEditor().setText(textoActual);
            artistaComboBox.getEditor().positionCaret(Math.min(posicionCursor, textoActual.length()));
        }

        // Mostrar dropdown si hay opciones y texto
        if (!nombresArtistas.isEmpty() && !textoActual.isEmpty()) {
            if (!artistaComboBox.isShowing()) {
                artistaComboBox.show();
            }
        }
    }

    /**
     * NUEVO MÉTODO: Obtiene el artista actualmente seleccionado
     */
    public ArtistaDTO getArtistaSeleccionado() {
        return artistaSeleccionado;
    }

    // Método para registrar la canción - conectado al botón "Publicar"
    @FXML
    void registrarCancion(ActionEvent event) {
        System.out.println("Registrando canción...");

        // Obtener los valores de los campos
        String titulo = nombreTextField.getText().trim();
        String anioStr = nombreTextField1.getText().trim();

        // Crear instancia del validador
        ServicioValidacionCancion validador = new ServicioValidacionCancion();

        // 1. Validar título
        if (!validador.validarTitulo(titulo)) {
            mostrarAlerta("El título es inválido. Debe tener entre 1 y 100 caracteres.");
            return;
        }

        // 2. Validar que el título sea único
        if (!validador.esNombreUnico(titulo)) {
            mostrarAlerta("El título de la canción ya existe. Por favor elige otro.");
            return;
        }

        // 3. Validar año con rango específico
        int anio;
        try {
            anio = Integer.parseInt(anioStr);
        } catch (NumberFormatException e) {
            mostrarAlerta("El año debe ser un número válido.");
            return;
        }

        // Validación adicional de rango personalizada
        if (anio < 1900) {
            mostrarAlerta("El año debe ser mayor o igual a 1900.");
            return;
        }

        if (!validador.validarAnio(anio)) {
            int currentYear = java.time.LocalDate.now().getYear();
            mostrarAlerta("El año debe ser un valor positivo y no mayor al año actual (" + currentYear + ").");
            return;
        }

        // 4. Obtener y validar géneros seleccionados
        List<Genero> generosSeleccionados = generoMenuButton.getItems().stream()
                .filter(item -> item instanceof CheckMenuItem && ((CheckMenuItem)item).isSelected())
                .map(item -> (Genero)item.getUserData())
                .toList();

        if (!validador.validarGeneros(generosSeleccionados)) {
            mostrarAlerta("Debes seleccionar al menos un género musical.");
            return;
        }

        // 5. Validar archivo MP3
        if (!validador.validarArchivoMP3(archivoMP3)) {
            if (archivoMP3 == null) {
                mostrarAlerta("Debes seleccionar un archivo MP3.");
            } else {
                mostrarAlerta("El archivo MP3 es demasiado grande. Máximo permitido: 10MB.");
            }
            return;
        }

        // 6. Validar portada/imagen
        if (!validador.validarPortada(imagenBytes)) {
            if (imagenBytes == null) {
                mostrarAlerta("Debes seleccionar una imagen para la canción.");
            } else {
                mostrarAlerta("La imagen es demasiado grande. Máximo permitido: 5MB.");
            }
            return;
        }

        // 7. Registrar la canción en el sistema
        try {
            Cancion cancionLogic = new Cancion();

            // Convertir duración de segundos a formato "mm:ss"
            String duracionFormateada = formatearDuracion(duracionSegundos);

            boolean exito = cancionLogic.registrar(
                    titulo,
                    anioStr,
                    duracionFormateada,
                    generosSeleccionados,
                    "", // Letra vacía por ahora
                    imagenBytes
            );

            if (exito) {
                mostrarExito("Canción registrada con éxito.");

                // Actualizar el catálogo si existe la referencia
                if (catalogoController != null) {
                    catalogoController.refrescarTabla();
                }

                cerrarVentana(); // Cerrar la ventana después de registrar exitosamente
            } else {
                mostrarAlerta("No se pudo registrar la canción. Inténtalo nuevamente.");
            }
        } catch (Exception e) {
            mostrarAlerta("Error al registrar la canción: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // MÉTODO CORREGIDO - Ahora conectado correctamente al FXML
    @FXML
    public void seleccionarArchivo(ActionEvent actionEvent) {
        System.out.println("Seleccionando archivo MP3...");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo de audio");

        // Filtro para mostrar solo archivos de audio
        FileChooser.ExtensionFilter extFilterAudio = new FileChooser.ExtensionFilter(
                "Archivos de audio (*.mp3, *.wav, *.flac)",
                "*.mp3", "*.wav", "*.flac"
        );
        fileChooser.getExtensionFilters().add(extFilterAudio);

        File archivoSeleccionado = fileChooser.showOpenDialog(seleccionarArchivoButton.getScene().getWindow());

        if (archivoSeleccionado != null) {
            try {
                // Convertir archivo a bytes para almacenamiento
                Path path = archivoSeleccionado.toPath();
                archivoMP3 = Files.readAllBytes(path);
                nombreArchivoMP3 = archivoSeleccionado.getName();

                // Validar tamaño del archivo usando el validador
                ServicioValidacionCancion validador = new ServicioValidacionCancion();
                if (!validador.validarArchivoMP3(archivoMP3)) {
                    mostrarAlerta("El archivo MP3 es demasiado grande. Máximo permitido: 10MB.");
                    archivoMP3 = null;
                    nombreArchivoMP3 = "";
                    duracionSegundos = 0.0;
                    // Ocultar el label del archivo seleccionado
                    if (archivoSeleccionadoLabel != null) {
                        archivoSeleccionadoLabel.setVisible(false);
                    }
                    return;
                }

                // Calcular duración automáticamente usando el método de la clase Cancion
                Cancion cancionLogic = new Cancion();
                try {
                    // Usar reflection para acceder al método privado obtenerDuracionDesdeMP3
                    java.lang.reflect.Method metodo = Cancion.class.getDeclaredMethod("obtenerDuracionDesdeMP3", byte[].class);
                    metodo.setAccessible(true);
                    duracionSegundos = (Double) metodo.invoke(cancionLogic, archivoMP3);

                    if (duracionSegundos > 0) {
                        String duracionFormateada = formatearDuracion(duracionSegundos);
                        System.out.println("Duración calculada: " + duracionFormateada + " (" + duracionSegundos + " segundos)");

                        // Actualizar el label con el archivo seleccionado
                        if (archivoSeleccionadoLabel != null) {
                            archivoSeleccionadoLabel.setText("♪ " + nombreArchivoMP3 + " (" + duracionFormateada + ")");
                            archivoSeleccionadoLabel.setVisible(true);
                        }

                        // Cambiar el texto del botón para indicar que ya hay un archivo seleccionado
                        seleccionarArchivoButton.setText("Cambiar archivo");
                        seleccionarArchivoButton.setStyle("-fx-background-color: #9190C2; -fx-background-radius: 20;");

                        mostrarExito("Archivo de audio seleccionado correctamente:\n" + nombreArchivoMP3 +
                                "\nDuración: " + duracionFormateada);
                    } else {
                        System.out.println("No se pudo calcular la duración, usando valor por defecto");
                        duracionSegundos = 180.0; // 3 minutos por defecto
                        String duracionFormateada = formatearDuracion(duracionSegundos);

                        if (archivoSeleccionadoLabel != null) {
                            archivoSeleccionadoLabel.setText("♪ " + nombreArchivoMP3 + " (" + duracionFormateada + " estimado)");
                            archivoSeleccionadoLabel.setVisible(true);
                        }

                        seleccionarArchivoButton.setText("Cambiar archivo");
                        seleccionarArchivoButton.setStyle("-fx-background-color: #9190C2; -fx-background-radius: 20;");

                        mostrarExito("Archivo de audio seleccionado:\n" + nombreArchivoMP3 +
                                "\n(Duración estimada: " + duracionFormateada + ")");
                    }
                } catch (Exception e) {
                    System.out.println("Error al calcular duración: " + e.getMessage());
                    duracionSegundos = 180.0; // 3 minutos por defecto
                    String duracionFormateada = formatearDuracion(duracionSegundos);

                    if (archivoSeleccionadoLabel != null) {
                        archivoSeleccionadoLabel.setText("♪ " + nombreArchivoMP3 + " (" + duracionFormateada + " estimado)");
                        archivoSeleccionadoLabel.setVisible(true);
                    }

                    seleccionarArchivoButton.setText("Cambiar archivo");
                    seleccionarArchivoButton.setStyle("-fx-background-color: #9190C2; -fx-background-radius: 20;");

                    mostrarExito("Archivo de audio seleccionado:\n" + nombreArchivoMP3 +
                            "\n(Duración estimada: " + duracionFormateada + ")");
                }

                System.out.println("Archivo MP3 cargado correctamente: " + archivoSeleccionado.getAbsolutePath());

            } catch (Exception e) {
                mostrarAlerta("Error al cargar el archivo de audio: " + e.getMessage());
                e.printStackTrace();
                archivoMP3 = null;
                nombreArchivoMP3 = "";
                duracionSegundos = 0.0;

                // Restaurar el estado del botón y ocultar el label
                seleccionarArchivoButton.setText("Seleccionar archivo");
                seleccionarArchivoButton.setStyle("-fx-background-color: #575a81; -fx-background-radius: 20;");
                if (archivoSeleccionadoLabel != null) {
                    archivoSeleccionadoLabel.setVisible(false);
                }
            }
        }
    }

    /**
     * Convierte duración en segundos a formato "mm:ss"
     */
    private String formatearDuracion(double segundos) {
        int minutos = (int) (segundos / 60);
        int segs = (int) (segundos % 60);
        return String.format("%d:%02d", minutos, segs);
    }

    // Método para seleccionar una imagen - conectado al botón invisible sobre la imagen
    @FXML
    public void seleccionarImagen(ActionEvent actionEvent) {
        System.out.println("Seleccionando imagen...");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen de la canción");

        // Filtro para mostrar solo imágenes
        FileChooser.ExtensionFilter extFilterImagenes = new FileChooser.ExtensionFilter(
                "Imágenes (*.png, *.jpg, *.jpeg)",
                "*.png", "*.jpg", "*.jpeg"
        );
        fileChooser.getExtensionFilters().add(extFilterImagenes);

        File archivoSeleccionado = fileChooser.showOpenDialog(cargarImagenButton.getScene().getWindow());

        if (archivoSeleccionado != null) {
            try {
                // Cargar la imagen seleccionada
                Image imagen = new Image(archivoSeleccionado.toURI().toString());

                // Validar las dimensiones de la imagen (opcional)
                if (imagen.getWidth() == 264.0 && imagen.getHeight() == 264.0) {
                    cancionImageView.setImage(imagen);

                    // Convertir imagen a bytes para almacenamiento
                    URI uri = archivoSeleccionado.toURI();
                    Path path = Paths.get(uri);
                    imagenBytes = Files.readAllBytes(path);

                    // Validar tamaño usando el validador
                    ServicioValidacionCancion validador = new ServicioValidacionCancion();
                    if (!validador.validarPortada(imagenBytes)) {
                        mostrarAlerta("La imagen es demasiado grande. Máximo permitido: 5MB.");
                        imagenBytes = null;
                        // Restaurar imagen por defecto
                        try {
                            Image imagenDefault = new Image(
                                    Objects.requireNonNull(getClass().getResourceAsStream(
                                            "/UserInterface/Resources/img/CatalogoCanciones/camara.png"
                                    ))
                            );
                            cancionImageView.setImage(imagenDefault);
                        } catch (Exception ex) {
                            System.out.println("No se pudo restaurar la imagen por defecto");
                        }
                        return;
                    }

                    System.out.println("Imagen cargada correctamente: " + archivoSeleccionado.getAbsolutePath());
                } else {
                    // Cargar la imagen aunque no tenga las dimensiones exactas
                    cancionImageView.setImage(imagen);

                    URI uri = archivoSeleccionado.toURI();
                    Path path = Paths.get(uri);
                    imagenBytes = Files.readAllBytes(path);

                    // Validar tamaño usando el validador
                    ServicioValidacionCancion validador = new ServicioValidacionCancion();
                    if (!validador.validarPortada(imagenBytes)) {
                        mostrarAlerta("La imagen es demasiado grande. Máximo permitido: 5MB.");
                        imagenBytes = null;
                        // Restaurar imagen por defecto
                        try {
                            Image imagenDefault = new Image(
                                    Objects.requireNonNull(getClass().getResourceAsStream(
                                            "/UserInterface/Resources/img/CatalogoCanciones/camara.png"
                                    ))
                            );
                            cancionImageView.setImage(imagenDefault);
                        } catch (Exception ex) {
                            System.out.println("No se pudo restaurar la imagen por defecto");
                        }
                        return;
                    }

                    mostrarAdvertencia("La imagen no tiene las dimensiones recomendadas (264x264), pero se ha cargado.");
                }
            } catch (Exception e) {
                mostrarAlerta("Error al cargar la imagen.");
                e.printStackTrace();
            }
        }
    }

    // Método para cerrar la ventana
    @FXML
    void cerrarVentana() {
        Stage stage = (Stage) cerrarButton.getScene().getWindow();
        stage.close();
    }

    // Actualiza el texto del botón de géneros con los seleccionados
    private void actualizarTextoMenuButton() {
        List<String> seleccionados = generoMenuButton.getItems().stream()
                .filter(item -> item instanceof CheckMenuItem && ((CheckMenuItem)item).isSelected())
                .map(item -> ((CheckMenuItem)item).getText())
                .toList();

        generoMenuButton.setText(seleccionados.isEmpty() ? "Selecciona" : String.join(", ", seleccionados));
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

    // Muestra una alerta con el mensaje especificado
    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(AlertType.WARNING);
        alerta.setTitle("Advertencia");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // Muestra un mensaje de éxito
    private void mostrarExito(String mensaje) {
        Alert alerta = new Alert(AlertType.INFORMATION);
        alerta.setTitle("Éxito");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // Muestra una advertencia
    private void mostrarAdvertencia(String mensaje) {
        Alert alerta = new Alert(AlertType.WARNING);
        alerta.setTitle("Advertencia");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Establece la referencia al controlador del catálogo para poder actualizar la tabla
     * cuando se registre una nueva canción.
     *
     * @param catalogoController Referencia al controlador del catálogo
     */
    public void setCatalogoController(CatalogoCancionesController catalogoController) {
        this.catalogoController = catalogoController;
    }
}