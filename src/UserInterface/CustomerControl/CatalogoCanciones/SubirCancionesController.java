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
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.SeparatorMenuItem;

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

    // CAMBIADO: De ComboBox a MenuButton para selección múltiple
    @FXML private MenuButton artistaMenuButton;

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

    // NUEVA VARIABLE: Lista de artistas seleccionados
    private List<ArtistaDTO> artistasSeleccionados = new ArrayList<>();

    // Referencia al controlador del catálogo para actualizar la tabla
    private CatalogoCancionesController catalogoController;

    // Variables para almacenar archivos seleccionados
    private byte[] imagenBytes = null;
    private byte[] archivoMP3 = null;
    private double duracionSegundos = 0.0;
    private String nombreArchivoMP3 = "";

    // VARIABLES PARA MANEJO DE ARTISTAS
    private Artista artistaLogic = new Artista();
    private List<ArtistaDTO> todosLosArtistas = new ArrayList<>();

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

        // NUEVA SECCIÓN: Configurar el MenuButton de artistas para selección múltiple
        configurarMenuButtonArtistas();

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
     * NUEVO MÉTODO: Configura el MenuButton de artistas para selección múltiple
     * similar al patrón usado para géneros
     */
    private void configurarMenuButtonArtistas() {
        // Cargar todos los artistas al inicio
        cargarTodosLosArtistas();

        // Configurar texto inicial del MenuButton
        artistaMenuButton.setText("Selecciona artistas");

        // Hacer el MenuButton editable para permitir búsqueda
        artistaMenuButton.setMnemonicParsing(false);

        // Poblar el MenuButton con CheckMenuItems para cada artista
        actualizarMenuButtonArtistas();

        // NUEVO: Agregar TextField de búsqueda como primer item del menú
        agregarCampoBusquedaAlMenu();
    }

    /**
     * NUEVO MÉTODO: Agrega un TextField de búsqueda como primer elemento del MenuButton
     */
    private void agregarCampoBusquedaAlMenu() {
        // Crear TextField para búsqueda
        TextField busquedaField = new TextField();
        busquedaField.setPromptText("Buscar artistas...");
        busquedaField.setStyle("-fx-background-color: #575a81; -fx-text-fill: white; -fx-prompt-text-fill: #cccccc;");
        busquedaField.setPrefWidth(180);

        // Listener para filtrar mientras se escribe
        busquedaField.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarArtistasEnMenuButton(newValue, busquedaField);
        });

        // Evitar que el menú se cierre al escribir
        busquedaField.setOnMouseClicked(e -> e.consume());
        busquedaField.setOnKeyPressed(e -> e.consume());

        // Crear CustomMenuItem que contenga el TextField
        CustomMenuItem searchItem = new CustomMenuItem(busquedaField);
        searchItem.setHideOnClick(false); // No cerrar el menú al hacer clic

        // Agregar separador visual
        SeparatorMenuItem separator = new SeparatorMenuItem();

        // Insertar al inicio del menú
        artistaMenuButton.getItems().add(0, searchItem);
        artistaMenuButton.getItems().add(1, separator);
    }

    /**
     * NUEVO MÉTODO: Filtra los artistas en el MenuButton basado en el texto de búsqueda
     */
    private void filtrarArtistasEnMenuButton(String filtro, TextField busquedaField) {
        List<ArtistaDTO> artistasFiltrados;

        if (filtro == null || filtro.trim().isEmpty()) {
            artistasFiltrados = todosLosArtistas;
        } else {
            artistasFiltrados = todosLosArtistas.stream()
                    .filter(artista -> artista.getNombre().toLowerCase().contains(filtro.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Actualizar solo los CheckMenuItems (mantener el campo de búsqueda)
        actualizarCheckMenuItems(artistasFiltrados, busquedaField);
    }

    /**
     * MÉTODO CORREGIDO: Actualiza solo los CheckMenuItems manteniendo el campo de búsqueda
     * y preservando todas las selecciones previas
     */
    private void actualizarCheckMenuItems(List<ArtistaDTO> artistas, TextField busquedaField) {
        // Guardar las selecciones actuales de TODOS los artistas (no solo los visibles)
        List<Integer> artistasSeleccionadosIds = artistasSeleccionados.stream()
                .map(ArtistaDTO::getId)
                .collect(Collectors.toList());

        // Remover solo los CheckMenuItems (mantener búsqueda y separador)
        artistaMenuButton.getItems().removeIf(item ->
                item instanceof CheckMenuItem ||
                        (item instanceof SeparatorMenuItem && artistaMenuButton.getItems().indexOf(item) > 1)
        );

        // Agregar CheckMenuItem para cada artista filtrado
        for (ArtistaDTO artista : artistas) {
            CheckMenuItem item = new CheckMenuItem(artista.getNombre());
            item.setUserData(artista);

            // Verificar si este artista estaba previamente seleccionado
            boolean estabaSeleccionado = artistasSeleccionadosIds.contains(artista.getId());
            item.setSelected(estabaSeleccionado);

            // Configurar el evento de selección
            item.setOnAction(e -> {
                e.consume();
                // CORREGIDO: Actualizar la lista de seleccionados directamente
                if (item.isSelected()) {
                    // Solo agregar si no está ya en la lista
                    if (!artistasSeleccionados.stream().anyMatch(a -> a.getId() == artista.getId())) {
                        artistasSeleccionados.add(artista);
                    }
                } else {
                    // Remover de la lista de seleccionados
                    artistasSeleccionados.removeIf(a -> a.getId() == artista.getId());
                }
                actualizarTextoMenuButtonArtistas();
            });

            artistaMenuButton.getItems().add(item);
        }

        // Mantener el foco en el campo de búsqueda
        busquedaField.requestFocus();
    }

    /**
     * NUEVO MÉTODO: Carga todos los artistas desde la base de datos
     */
    private void cargarTodosLosArtistas() {
        try {
            todosLosArtistas = artistaLogic.buscarTodo();
            System.out.println("Cargados " + todosLosArtistas.size() + " artistas para selección múltiple");
        } catch (Exception e) {
            System.err.println("Error al cargar artistas: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error al cargar la lista de artistas");
            todosLosArtistas = new ArrayList<>();
        }
    }

    /**
     * NUEVO MÉTODO: Actualiza el MenuButton con todos los artistas disponibles
     */
    private void actualizarMenuButtonArtistas() {
        actualizarMenuButtonConArtistas(todosLosArtistas);
    }

    /**
     * NUEVO MÉTODO: Actualiza el MenuButton con una lista específica de artistas
     */
    private void actualizarMenuButtonConArtistas(List<ArtistaDTO> artistas) {
        // Guardar las selecciones actuales
        List<Integer> artistasSeleccionadosIds = artistasSeleccionados.stream()
                .map(ArtistaDTO::getId)
                .collect(Collectors.toList());

        // Limpiar items existentes
        artistaMenuButton.getItems().clear();

        // Agregar CheckMenuItem para cada artista filtrado
        for (ArtistaDTO artista : artistas) {
            CheckMenuItem item = new CheckMenuItem(artista.getNombre());
            item.setUserData(artista);

            // Verificar si este artista estaba previamente seleccionado
            boolean estabaSeleccionado = artistasSeleccionadosIds.contains(artista.getId());
            item.setSelected(estabaSeleccionado);

            // Configurar el evento de selección
            item.setOnAction(e -> {
                e.consume();
                actualizarListaArtistasSeleccionados();
                actualizarTextoMenuButtonArtistas();
            });

            artistaMenuButton.getItems().add(item);
        }

        // Actualizar la lista de seleccionados después de reconstruir el menú
        actualizarListaArtistasSeleccionados();
    }

    /**
     * MÉTODO MODIFICADO: Solo actualiza la lista cuando no hay filtro activo
     * para evitar conflictos durante la búsqueda
     */
    private void actualizarListaArtistasSeleccionados() {
        // Solo reconstruir la lista si estamos viendo todos los artistas (sin filtro)
        // Durante la búsqueda, la lista se mantiene manualmente en el onAction del CheckMenuItem

        artistasSeleccionados.clear();

        for (javafx.scene.control.MenuItem item : artistaMenuButton.getItems()) {
            if (item instanceof CheckMenuItem) {
                CheckMenuItem checkItem = (CheckMenuItem) item;
                if (checkItem.isSelected()) {
                    ArtistaDTO artista = (ArtistaDTO) checkItem.getUserData();
                    if (artista != null) {
                        artistasSeleccionados.add(artista);
                    }
                }
            }
        }

        System.out.println("Artistas seleccionados: " + artistasSeleccionados.size());
    }

    /**
     * NUEVO MÉTODO: Actualiza el texto del MenuButton de artistas con los seleccionados
     */
    private void actualizarTextoMenuButtonArtistas() {
        List<String> nombresSeleccionados = artistasSeleccionados.stream()
                .map(ArtistaDTO::getNombre)
                .collect(Collectors.toList());

        if (nombresSeleccionados.isEmpty()) {
            artistaMenuButton.setText("Selecciona artistas");
        } else if (nombresSeleccionados.size() == 1) {
            artistaMenuButton.setText(nombresSeleccionados.get(0));
        } else if (nombresSeleccionados.size() <= 2) {
            artistaMenuButton.setText(String.join(", ", nombresSeleccionados));
        } else {
            // Si hay más de 2 artistas, mostrar los primeros 2 y "... +N más"
            String texto = nombresSeleccionados.get(0) + ", " + nombresSeleccionados.get(1) +
                    "... +" + (nombresSeleccionados.size() - 2) + " más";
            artistaMenuButton.setText(texto);
        }
    }

    /**
     * NUEVO MÉTODO: Obtiene la lista de artistas actualmente seleccionados
     */
    public List<ArtistaDTO> getArtistasSeleccionados() {
        return new ArrayList<>(artistasSeleccionados);
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

        if (!validador.validarAnio(anio)) {
            int currentYear = java.time.LocalDate.now().getYear();
            mostrarAlerta("El año debe ser un valor positivo y no mayor al año actual (" + currentYear + ").");
            return;
        }

        // 4. Obtener y validar géneros seleccionados
        List<Genero> generosSeleccionados = generoMenuButton.getItems().stream()
                .filter(item -> item instanceof CheckMenuItem && ((CheckMenuItem)item).isSelected())
                .map(item -> (Genero)item.getUserData())
                .collect(Collectors.toList());

        if (!validador.validarGeneros(generosSeleccionados)) {
            mostrarAlerta("Debes seleccionar al menos un género musical.");
            return;
        }


        // Validar archivo para subir
        if (archivoMP3 == null || archivoMP3.length == 0) {
            mostrarAlerta("Debes seleccionar un archivo MP3 válido.");
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

        // 7. MODIFICADO: Validar que hay al menos un artista seleccionado
        if (artistasSeleccionados.isEmpty()) {
            mostrarAlerta("Debes seleccionar al menos un artista.");
            return;
        }

        // 8. MODIFICADO: Registrar la canción con múltiples artistas
        try {
            Cancion cancionLogic = new Cancion();

            // Convertir duración de segundos a formato "mm:ss"
            String duracionFormateada = formatearDuracion(duracionSegundos);

            // Registrar la canción con múltiples artistas
            boolean exito = cancionLogic.registrarConMultiplesArtistas(
                    titulo,
                    anioStr,
                    duracionFormateada,
                    generosSeleccionados,
                    "", // Letra vacía por ahora
                    imagenBytes,
                    archivoMP3,
                    artistasSeleccionados // MODIFICADO: ahora es una lista
            );

            if (exito) {
                String artistasTexto = artistasSeleccionados.size() == 1 ?
                        "artista: " + artistasSeleccionados.get(0).getNombre() :
                        "artistas: " + artistasSeleccionados.stream()
                                .map(ArtistaDTO::getNombre)
                                .collect(Collectors.joining(", "));

                mostrarExito("Canción registrada con éxito.\n" +
                        "Título: " + titulo + "\n" +
                        "Con " + artistasTexto);

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

    // MÉTODO CORREGIDO - Ahora conectado correctamente al FXML y con filtro de archivos arreglado
    @FXML
    public void seleccionarArchivo(ActionEvent actionEvent) {
        System.out.println("Seleccionando archivo MP3...");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo de audio");

        // CORRECCIÓN: Filtro para mostrar solo archivos de audio - usar * en todas las extensiones
        FileChooser.ExtensionFilter extFilterAudio = new FileChooser.ExtensionFilter(
                "Archivos de audio (*.mp3, *.wav, *.flac)",
                "*.mp3", "*.wav", "*.flac"  // CORREGIDO: todas las extensiones con asterisco
        );
        fileChooser.getExtensionFilters().add(extFilterAudio);

        File archivoSeleccionado = fileChooser.showOpenDialog(seleccionarArchivoButton.getScene().getWindow());

        if (archivoSeleccionado != null) {
            try {
                // Convertir archivo a bytes para almacenamiento
                Path path = archivoSeleccionado.toPath();
                archivoMP3 = Files.readAllBytes(path);
                nombreArchivoMP3 = archivoSeleccionado.getName();

                // Validar tamaño y formato del archivo usando el validador
                ServicioValidacionCancion validador = new ServicioValidacionCancion();
                if (!validador.validarArchivoMP3(archivoMP3)) {
                    // Si el archivo no es válido por tipo
                    if (!(archivoMP3[0] == 'I' && archivoMP3[1] == 'D' && archivoMP3[2] == '3') &&
                            !((archivoMP3[0] & 0xFF) == 0xFF && (archivoMP3[1] & 0xE0) == 0xE0)) {
                        mostrarAlerta("Debes seleccionar un archivo MP3.");
                    } else {
                        // Si falló por tamaño u otra razón
                        mostrarAlerta("El archivo MP3 es demasiado grande. Máximo permitido: 10MB.");
                    }

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

                // VALIDACIÓN ESTRICTA: Solo permitir imágenes de 264x264 píxeles
                if (imagen.getWidth() != 264.0 || imagen.getHeight() != 264.0) {
                    mostrarAlerta("La imagen debe tener exactamente 264x264 píxeles.\n" +
                            "Dimensiones actuales: " + (int)imagen.getWidth() + "x"
                            + (int)imagen.getHeight() + " píxeles.\n\n" +
                            "Por favor, redimensiona tu imagen y vuelve a intentarlo.");
                    return; // No procesar la imagen
                }

                // Si llegamos aquí, la imagen tiene las dimensiones correctas
                cancionImageView.setImage(imagen);

                // Convertir imagen a bytes para almacenamiento
                URI uri = archivoSeleccionado.toURI();
                Path path = Paths.get(uri);
                imagenBytes = Files.readAllBytes(path);

                // Validar tamaño del archivo usando el validador
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
                mostrarExito("Imagen cargada correctamente.\nDimensiones: 264x264 píxeles ✓");

            } catch (Exception e) {
                mostrarAlerta("Error al cargar la imagen: " + e.getMessage());
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
                .collect(Collectors.toList());

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