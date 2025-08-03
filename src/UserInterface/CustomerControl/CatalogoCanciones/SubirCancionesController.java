/*
|-----------------------------------------------|
| © 2025 EPN-FIS, Todos los derechos reservados |
| GR1SW                                         |
|-----------------------------------------------|
Autor: Grupo - A
Descripción: Controlador para subir nuevas canciones al catálogo del sistema InkHarmony.
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
 * Controlador para la funcionalidad de subir nuevas canciones al catálogo del sistema InkHarmony.
 *
 * Esta clase maneja la interfaz de usuario para registrar canciones, permitiendo al usuario
 * ingresar información como título, año, géneros, artistas, archivo de audio e imagen de portada.
 * Incluye validaciones completas de todos los campos y soporte para selección múltiple de artistas
 * con funcionalidad de búsqueda en tiempo real.
 *
 * Funcionalidades principales:
 * - Validación de datos en tiempo real
 * - Selección múltiple de artistas con búsqueda
 * - Carga y validación de archivos MP3
 * - Carga y validación de imágenes de portada (264x264 píxeles)
 * - Integración con la capa de lógica de negocio
 *
 * FXML asociado: frameNuevaCancion.fxml
 *
 * @author Grupo - A
 * @version 2.0
 * @since 18-07-2025
 */
public class SubirCancionesController {

    // Componentes de la interfaz gráfica - nombres exactos del FXML
    @FXML private Label tituloLabel;
    @FXML private Label añoLanzamientoLabel;
    @FXML private TextField nombreTextField;  // Para el título
    @FXML private TextField nombreTextField1; // Para el año
    @FXML private Label artistaLabel;

    // MenuButton para selección múltiple de artistas (cambio de ComboBox)
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

    // Lista para almacenar los géneros musicales seleccionados por el usuario
    private List<Genero> generosSeleccionados = new ArrayList<>();

    // Lista para almacenar los artistas seleccionados por el usuario
    private List<ArtistaDTO> artistasSeleccionados = new ArrayList<>();

    // Referencia al controlador del catálogo para actualizar la tabla tras el registro
    private CatalogoCancionesController catalogoController;

    // Variables para almacenar archivos seleccionados y sus metadatos
    private byte[] imagenBytes = null;
    private byte[] archivoMP3 = null;
    private double duracionSegundos = 0.0;
    private String nombreArchivoMP3 = "";

    // Instancias para el manejo de artistas y lógica de negocio
    private Artista artistaLogic = new Artista();
    private List<ArtistaDTO> todosLosArtistas = new ArrayList<>();

    /**
     * Inicializa el controlador configurando todos los componentes de la interfaz.
     * Se ejecuta automáticamente después de cargar el FXML.
     *
     * Configura:
     * - MenuButton de géneros musicales
     * - MenuButton de artistas con búsqueda
     * - Validación en tiempo real del título
     * - Imagen por defecto
     * - Estado inicial de componentes
     */
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

        // Configurar el MenuButton de artistas para selección múltiple
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

    /*
     * Configura el MenuButton de artistas para permitir selección múltiple.
     * Carga todos los artistas disponibles desde la base de datos y configura
     * el texto inicial del MenuButton. También agrega la funcionalidad de búsqueda.
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

        // Agregar TextField de búsqueda como primer item del menú
        agregarCampoBusquedaAlMenu();
    }

    /*
     * Agrega un campo de texto de búsqueda como primer elemento del MenuButton.
     * Permite filtrar artistas en tiempo real mientras el usuario escribe.
     * El campo incluye un separador visual y está configurado para no cerrar
     * el menú al interactuar con él.
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

    /*
     * Filtra los artistas mostrados en el MenuButton según el texto de búsqueda ingresado.
     * Actualiza dinámicamente la lista de CheckMenuItems manteniendo las selecciones previas.
     *
     * @param filtro Texto ingresado para filtrar artistas por nombre
     * @param busquedaField Campo de texto de búsqueda para mantener el foco
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

    /*
     * Actualiza únicamente los CheckMenuItems del MenuButton preservando el campo de búsqueda
     * y el separador. Mantiene todas las selecciones previas del usuario, incluso para artistas
     * que no están visibles debido al filtro de búsqueda.
     *
     * @param artistas Lista de artistas a mostrar después del filtrado
     * @param busquedaField Campo de búsqueda para mantener el foco
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
                // Actualizar la lista de seleccionados directamente
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

    /*
     * Carga todos los artistas disponibles desde la base de datos utilizando
     * la capa de lógica de negocio. En caso de error, inicializa una lista vacía
     * y muestra un mensaje de alerta al usuario.
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

    /*
     * Actualiza el MenuButton de artistas con todos los artistas disponibles.
     * Método de conveniencia que llama a actualizarMenuButtonConArtistas
     * con la lista completa de artistas.
     */
    private void actualizarMenuButtonArtistas() {
        actualizarMenuButtonConArtistas(todosLosArtistas);
    }

    /*
     * Actualiza el MenuButton de artistas con una lista específica de artistas.
     * Preserva las selecciones actuales del usuario al reconstruir los CheckMenuItems.
     *
     * @param artistas Lista de artistas a mostrar en el MenuButton
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

    /*
     * Actualiza la lista interna de artistas seleccionados basándose en el estado
     * de los CheckMenuItems del MenuButton. Solo se ejecuta cuando no hay filtro
     * activo para evitar conflictos durante la búsqueda.
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

    /*
     * Actualiza el texto mostrado en el MenuButton de artistas según los artistas seleccionados.
     * Muestra diferentes formatos dependiendo de la cantidad de artistas:
     * - 0 artistas: "Selecciona artistas"
     * - 1 artista: nombre del artista
     * - 2 artistas: "Artista1, Artista2"
     * - Más de 2: "Artista1, Artista2... +N más"
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
     * Obtiene una copia de la lista de artistas actualmente seleccionados.
     *
     * @return Lista de ArtistaDTO seleccionados por el usuario
     */
    public List<ArtistaDTO> getArtistasSeleccionados() {
        return new ArrayList<>(artistasSeleccionados);
    }

    /*
     * Registra una nueva canción en el sistema tras validar todos los datos ingresados.
     * Método conectado al botón "Publicar" de la interfaz.
     *
     * Proceso de validación:
     * 1. Validar título (longitud y unicidad)
     * 2. Validar año (formato y rango)
     * 3. Validar géneros seleccionados
     * 4. Validar archivo MP3
     * 5. Validar imagen de portada
     * 6. Validar artistas seleccionados
     * 7. Registrar en base de datos
     * 8. Actualizar catálogo y cerrar ventana
     *
     * @param event Evento del botón de registro
     */
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

        // 7. Validar que hay al menos un artista seleccionado
        if (artistasSeleccionados.isEmpty()) {
            mostrarAlerta("Debes seleccionar al menos un artista.");
            return;
        }

        // 8. Registrar la canción con múltiples artistas
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
                    artistasSeleccionados // Lista de artistas seleccionados
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

    /*
     * Permite al usuario seleccionar un archivo de audio MP3 desde el sistema de archivos.
     * Valida el formato, tamaño y calcula automáticamente la duración del archivo.
     * Actualiza la interfaz con la información del archivo seleccionado.
     *
     * Proceso:
     * 1. Mostrar FileChooser con filtros de audio
     * 2. Validar formato y tamaño del archivo
     * 3. Calcular duración usando reflexión
     * 4. Actualizar interfaz con información del archivo
     * 5. Mostrar confirmación al usuario
     *
     * @param actionEvent Evento del botón de selección de archivo
     */
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
     * Convierte una duración en segundos al formato de tiempo "mm:ss".
     * Utilizado para mostrar la duración de las canciones de manera legible.
     *
     * @param segundos Duración en segundos como número decimal
     * @return String con formato "mm:ss" (ejemplo: "3:45")
     */
    private String formatearDuracion(double segundos) {
        int minutos = (int) (segundos / 60);
        int segs = (int) (segundos % 60);
        return String.format("%d:%02d", minutos, segs);
    }

    /*
     * Permite al usuario seleccionar una imagen para usar como portada de la canción.
     * Valida que la imagen tenga exactamente 264x264 píxeles y no exceda el tamaño máximo.
     * Método conectado al botón invisible sobre la imagen en la interfaz.
     *
     * Proceso:
     * 1. Mostrar FileChooser con filtros de imagen
     * 2. Validar dimensiones exactas (264x264 píxeles)
     * 3. Validar tamaño del archivo (máximo 5MB)
     * 4. Actualizar vista previa de la imagen
     * 5. Almacenar bytes para registro posterior
     *
     * @param actionEvent Evento del botón de selección de imagen
     */
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

                // Validación estricta: Solo permitir imágenes de 264x264 píxeles
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

    /*
     * Cierra la ventana actual del formulario de subir canciones.
     * Método conectado al botón "Cerrar" de la interfaz.
     */
    @FXML
    void cerrarVentana() {
        Stage stage = (Stage) cerrarButton.getScene().getWindow();
        stage.close();
    }

    /*
     * Actualiza el texto mostrado en el MenuButton de géneros con los géneros seleccionados.
     * Si no hay géneros seleccionados, muestra "Selecciona". Si hay varios, los une con comas.
     */
    private void actualizarTextoMenuButton() {
        List<String> seleccionados = generoMenuButton.getItems().stream()
                .filter(item -> item instanceof CheckMenuItem && ((CheckMenuItem)item).isSelected())
                .map(item -> ((CheckMenuItem)item).getText())
                .collect(Collectors.toList());

        generoMenuButton.setText(seleccionados.isEmpty() ? "Selecciona" : String.join(", ", seleccionados));
    }

    /*
     * Formatea el nombre de un género musical para mostrarlo de manera legible en la interfaz.
     * Convierte nombres de enum (ROCK_ALTERNATIVO) a formato de título (Rock Alternativo).
     *
     * @param genero Enum del género a formatear
     * @return String con el nombre formateado del género
     */
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

    /*
     * Muestra una alerta de advertencia al usuario con el mensaje especificado.
     * Utilizada para mostrar errores de validación y otros problemas.
     *
     * @param mensaje Texto del mensaje a mostrar al usuario
     */
    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(AlertType.WARNING);
        alerta.setTitle("Advertencia");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /*
     * Muestra un mensaje de éxito al usuario tras completar una operación correctamente.
     * Utilizada para confirmar acciones como carga de archivos o registro exitoso.
     *
     * @param mensaje Texto del mensaje de éxito a mostrar
     */
    private void mostrarExito(String mensaje) {
        Alert alerta = new Alert(AlertType.INFORMATION);
        alerta.setTitle("Éxito");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /*
     * Muestra una advertencia específica al usuario.
     * Método adicional para mostrar advertencias con formato consistente.
     *
     * @param mensaje Texto de la advertencia a mostrar
     */
    private void mostrarAdvertencia(String mensaje) {
        Alert alerta = new Alert(AlertType.WARNING);
        alerta.setTitle("Advertencia");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Establece la referencia al controlador del catálogo principal.
     * Permite actualizar la tabla del catálogo cuando se registre una nueva canción,
     * manteniendo la sincronización entre las diferentes ventanas del sistema.
     *
     * @param catalogoController Referencia al controlador del catálogo de canciones
     */
    public void setCatalogoController(CatalogoCancionesController catalogoController) {
        this.catalogoController = catalogoController;
    }
}