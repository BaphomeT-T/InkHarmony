package UserInterface.CustomerControl.Playlist;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.application.Platform;
import BusinessLogic.Playlist;
import DataAccessComponent.DTO.PlaylistDTO;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.io.ByteArrayInputStream;

public class EditarPlaylistController implements Initializable {

    // Constantes
    private static final long TAMAÑO_MAXIMO_IMAGEN_MB = 5;
    private static final long TAMAÑO_MAXIMO_IMAGEN_BYTES = TAMAÑO_MAXIMO_IMAGEN_MB * 1024 * 1024;
    private static final String RUTA_IMAGEN_CAMARA = "/UserInterface/Resources/img/CatalogoPlaylist/camara.png";
    private static final String RUTA_IMAGEN_SIMBOLO = "/UserInterface/Resources/img/CatalogoPlaylist/simbolo-aplicacion.png";

    // Estilos para botones
    private static final String ESTILO_BOTON_GUARDAR_CAMBIOS =
            "-fx-background-color: #9190C2; -fx-background-radius: 20; -fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold;";
    private static final String ESTILO_BOTON_SIN_CAMBIOS =
            "-fx-background-color: #4CAF50; -fx-background-radius: 20; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;";
    private static final String ESTILO_BOTON_DESHABILITADO =
            "-fx-background-color: #6B6B6B; -fx-background-radius: 20; -fx-text-fill: #999999; -fx-font-size: 16px; -fx-font-weight: bold;";

    // Componentes FXML
    @FXML private TextField txtTitulo;
    @FXML private TextArea txtDescripcion;
    @FXML private ImageView imgPortada;
    @FXML private Button btnRegresar;
    @FXML private Button btnCerrar;
    @FXML private Button btnActualizarPlaylist;
    @FXML private ImageView imgRegresar;

    // Variables de estado
    private File imagenSeleccionada;
    private String tituloOriginal;
    private String descripcionOriginal;
    private String rutaImagenOriginal;
    private PlaylistDTO playlistActual;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarEventosDeValidacion();
        configurarEventosDeInteraccion();
    }

    // =================== CONFIGURACIÓN INICIAL ===================

    private void configurarEventosDeValidacion() {
        txtTitulo.textProperty().addListener((observable, oldValue, newValue) -> validarCampos());
        txtDescripcion.textProperty().addListener((observable, oldValue, newValue) -> validarCampos());
    }

    private void configurarEventosDeInteraccion() {
        imgPortada.setOnMouseClicked(event -> seleccionarNuevaPortada());
    }

    // =================== VALIDACIÓN Y ESTADO DE UI ===================

    private void validarCampos() {
        boolean tituloEsValido = esTituloValido();
        boolean hayCambiosPendientes = verificarCambiosPendientes();

        actualizarEstadoBotonActualizar(tituloEsValido, hayCambiosPendientes);
    }

    private boolean esTituloValido() {
        return !obtenerTextoLimpio(txtTitulo).isEmpty();
    }

    private void actualizarEstadoBotonActualizar(boolean tituloValido, boolean hayCambios) {
        btnActualizarPlaylist.setDisable(!tituloValido);

        if (!tituloValido) {
            configurarBotonComoDeshabilitado();
        } else if (hayCambios) {
            configurarBotonParaGuardarCambios();
        } else {
            configurarBotonSinCambios();
        }
    }

    private void configurarBotonComoDeshabilitado() {
        btnActualizarPlaylist.setText("Título Requerido");
        btnActualizarPlaylist.setStyle(ESTILO_BOTON_DESHABILITADO);
    }

    private void configurarBotonParaGuardarCambios() {
        btnActualizarPlaylist.setText("Guardar Cambios");
        btnActualizarPlaylist.setStyle(ESTILO_BOTON_GUARDAR_CAMBIOS);
    }

    private void configurarBotonSinCambios() {
        btnActualizarPlaylist.setText("Sin Cambios");
        btnActualizarPlaylist.setStyle(ESTILO_BOTON_SIN_CAMBIOS);
    }

    // =================== MANEJO DE EVENTOS FXML ===================

    @FXML
    private void handleRegresar() {
        cerrarVentana();
    }

    @FXML
    private void handleRegresarImagen() {
        handleRegresar();
    }

    @FXML
    private void handleCerrar() {
        cerrarVentana();
    }

    @FXML
    private void handleSeleccionarPortada() {
        seleccionarNuevaPortada();
    }

    @FXML
    private void handleActualizarPlaylist() {
        procesarActualizacionPlaylist();
    }

    @FXML
    private void handleRestablecerImagen() {
        restablecerImagenOriginal();
    }

    // =================== SELECCIÓN DE IMAGEN ===================

    private void seleccionarNuevaPortada() {
        File archivoSeleccionado = mostrarSelectorDeArchivos();

        if (archivoSeleccionado != null) {
            procesarArchivoDeImagen(archivoSeleccionado);
        }
    }

    private File mostrarSelectorDeArchivos() {
        FileChooser fileChooser = crearSelectorDeImagenes();
        Stage ventanaActual = obtenerVentanaActual();
        return fileChooser.showOpenDialog(ventanaActual);
    }

    private FileChooser crearSelectorDeImagenes() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar nueva imagen de portada");

        FileChooser.ExtensionFilter filtroImagenes = new FileChooser.ExtensionFilter(
                "Archivos de imagen (*.jpg, *.jpeg, *.png, *.gif)",
                "*.jpg", "*.jpeg", "*.png", "*.gif"
        );
        fileChooser.getExtensionFilters().add(filtroImagenes);

        return fileChooser;
    }

    private void procesarArchivoDeImagen(File archivo) {
        try {
            if (!esImagenValida(archivo)) {
                return;
            }

            if (!esTamañoImagenPermitido(archivo)) {
                return;
            }

            aplicarNuevaImagen(archivo);
            registrarSeleccionDeImagen(archivo);
            validarCampos();

        } catch (Exception e) {
            manejarErrorEnSeleccionImagen(e);
        }
    }

    private boolean esImagenValida(File archivo) {
        Image imagen = new Image(archivo.toURI().toString());

        if (imagen.isError()) {
            mostrarMensajeError("Error", "El archivo seleccionado no es una imagen válida.");
            return false;
        }

        return true;
    }

    private boolean esTamañoImagenPermitido(File archivo) {
        long tamañoArchivo = archivo.length();

        if (tamañoArchivo > TAMAÑO_MAXIMO_IMAGEN_BYTES) {
            mostrarMensajeError("Error",
                    "La imagen es demasiado grande. Por favor, selecciona una imagen menor a "
                            + TAMAÑO_MAXIMO_IMAGEN_MB + "MB.");
            return false;
        }

        return true;
    }

    private void aplicarNuevaImagen(File archivo) {
        Image nuevaImagen = new Image(archivo.toURI().toString());
        imgPortada.setImage(nuevaImagen);
        imagenSeleccionada = archivo;
    }

    private void registrarSeleccionDeImagen(File archivo) {
        long tamañoEnKB = archivo.length() / 1024;
        imprimirLog("Nueva imagen de portada seleccionada: " + archivo.getName());
        imprimirLog("Tamaño: " + tamañoEnKB + " KB");
    }

    private void manejarErrorEnSeleccionImagen(Exception e) {
        String mensajeError = "No se pudo cargar la imagen seleccionada: " + e.getMessage();
        imprimirLog("Error al cargar la imagen: " + e.getMessage());
        mostrarMensajeError("Error", mensajeError);
    }

    // =================== ACTUALIZACIÓN DE PLAYLIST ===================

    private void procesarActualizacionPlaylist() {
        if (!validarDatosParaActualizacion()) {
            return;
        }

        if (!verificarCambiosPendientes()) {
            mostrarMensajeInformativo("Información", "No se han realizado cambios en la playlist.");
            return;
        }

        ejecutarActualizacionPlaylist();
    }

    private boolean validarDatosParaActualizacion() {
        String titulo = obtenerTextoLimpio(txtTitulo);

        if (titulo.isEmpty()) {
            mostrarMensajeError("Error", "Por favor, ingresa un título para la playlist.");
            return false;
        }

        if (playlistActual == null) {
            mostrarMensajeError("Error", "No hay playlist cargada para editar.");
            imprimirLog("ERROR: playlistActual es null");
            return false;
        }

        return true;
    }

    private void ejecutarActualizacionPlaylist() {
        try {
            imprimirInicioActualizacion();

            actualizarDatosPlaylist();
            procesarImagenSiEsNecesario();

            boolean resultadoActualizacion = guardarCambiosEnBaseDatos();
            manejarResultadoActualizacion(resultadoActualizacion);

        } catch (Exception e) {
            manejarErrorEnActualizacion(e);
        }
    }

    private void imprimirInicioActualizacion() {
        String titulo = obtenerTextoLimpio(txtTitulo);
        String descripcion = obtenerTextoLimpio(txtDescripcion);

        imprimirLog("=== INICIANDO ACTUALIZACIÓN DE PLAYLIST ===");
        imprimirLog("Título: " + titulo);
        imprimirLog("Descripción: " + descripcion);
        imprimirLog("Nueva imagen seleccionada: " + (imagenSeleccionada != null ? "Sí" : "No"));
        imprimirLog("Playlist actual ID: " + playlistActual.getIdPlaylist());
        imprimirLog("Título original: " + playlistActual.getTituloPlaylist());
    }

    private void actualizarDatosPlaylist() {
        String titulo = obtenerTextoLimpio(txtTitulo);
        String descripcion = obtenerTextoLimpio(txtDescripcion);

        playlistActual.setTituloPlaylist(titulo);
        playlistActual.setDescripcion(descripcion.isEmpty() ? null : descripcion);
    }

    private void procesarImagenSiEsNecesario() {
        if (imagenSeleccionada == null) {
            imprimirLog("No hay nueva imagen seleccionada, manteniendo imagen actual");
            return;
        }

        try {
            byte[] bytesImagen = convertirImagenABytes(imagenSeleccionada);
            playlistActual.setImagenPortada(bytesImagen);
            imprimirLog("Imagen convertida a bytes: " + bytesImagen.length + " bytes");
        } catch (Exception e) {
            imprimirLog("Error al procesar imagen: " + e.getMessage());
            mostrarMensajeAdvertencia("Advertencia",
                    "Se actualizará la playlist sin cambiar la imagen debido a un error al procesarla.");
        }
    }

    private byte[] convertirImagenABytes(File archivoImagen) throws Exception {
        java.nio.file.Path rutaArchivo = archivoImagen.toPath();
        return java.nio.file.Files.readAllBytes(rutaArchivo);
    }

    private boolean guardarCambiosEnBaseDatos() throws Exception {
        Playlist logicaPlaylist = new Playlist();
        imprimirLog("Llamando a playlistLogic.actualizar()...");

        boolean resultado = logicaPlaylist.actualizar(playlistActual);
        imprimirLog("Resultado de actualización: " + resultado);

        return resultado;
    }

    private void manejarResultadoActualizacion(boolean exitoso) {
        if (exitoso) {
            mostrarMensajeExitoYCerrar();
        } else {
            mostrarMensajeError("Error", "No se pudo actualizar la playlist.");
            imprimirLog("ERROR: No se pudo actualizar la playlist");
        }
    }

    private void mostrarMensajeExitoYCerrar() {
        String mensaje = construirMensajeExito();
        mostrarMensajeInformativo("Éxito", mensaje);

        imprimirLog("Playlist actualizada exitosamente, cerrando ventana...");
        Platform.runLater(this::cerrarVentana);
    }

    private String construirMensajeExito() {
        String mensajeBase = "Playlist actualizada correctamente";
        return imagenSeleccionada != null ? mensajeBase + " con nueva imagen de portada" : mensajeBase;
    }

    private void manejarErrorEnActualizacion(Exception e) {
        imprimirLog("EXCEPCIÓN durante actualización: " + e.getMessage());
        e.printStackTrace();
        mostrarMensajeError("Error", "Error al actualizar playlist: " + e.getMessage());
    }

    // =================== CARGA DE DATOS ===================

    public void setPlaylist(PlaylistDTO playlist) {
        imprimirLog("=== CARGANDO PLAYLIST PARA EDITAR ===");
        this.playlistActual = playlist;

        if (playlist == null) {
            imprimirLog("ERROR: Playlist recibida es null");
            return;
        }

        imprimirDatosPlaylist(playlist);
        cargarDatosEnControles(playlist);
        guardarValoresOriginales(playlist);
        cargarImagenDePortada(playlist);
        validarCampos();

        imprimirLog("Playlist cargada completamente para edición");
    }

    private void imprimirDatosPlaylist(PlaylistDTO playlist) {
        imprimirLog("Playlist ID: " + playlist.getIdPlaylist());
        imprimirLog("Título: " + playlist.getTituloPlaylist());
        imprimirLog("Descripción: " + (playlist.getDescripcion() != null ? playlist.getDescripcion() : "null"));
        imprimirLog("Tiene imagen: " + (playlist.getImagenPortada() != null ?
                "Sí (" + playlist.getImagenPortada().length + " bytes)" : "No"));
    }

    private void cargarDatosEnControles(PlaylistDTO playlist) {
        txtTitulo.setText(playlist.getTituloPlaylist());
        txtDescripcion.setText(playlist.getDescripcion() != null ? playlist.getDescripcion() : "");
    }

    private void guardarValoresOriginales(PlaylistDTO playlist) {
        tituloOriginal = playlist.getTituloPlaylist();
        descripcionOriginal = playlist.getDescripcion();
    }

    private void cargarImagenDePortada(PlaylistDTO playlist) {
        if (playlist.getImagenPortada() != null) {
            cargarImagenDesdeBytes(playlist.getImagenPortada());
        } else {
            imprimirLog("No hay imagen de portada, cargando imagen por defecto");
            cargarImagenPorDefecto();
        }
    }

    private void cargarImagenDesdeBytes(byte[] bytesImagen) {
        try {
            ByteArrayInputStream streamImagen = new ByteArrayInputStream(bytesImagen);
            Image imagen = new Image(streamImagen);
            imgPortada.setImage(imagen);
            imprimirLog("Imagen de portada cargada exitosamente");
        } catch (Exception e) {
            imprimirLog("Error al cargar imagen de portada: " + e.getMessage());
            cargarImagenPorDefecto();
        }
    }

    // =================== CARGA DE DATOS LEGACY ===================

    public void cargarDatosPlaylist(String titulo, String descripcion, String rutaImagen) {
        guardarDatosOriginalesLegacy(titulo, descripcion, rutaImagen);
        cargarDatosEnControlesLegacy(titulo, descripcion);
        cargarImagenExistente(rutaImagen);
        validarCampos();
    }

    private void guardarDatosOriginalesLegacy(String titulo, String descripcion, String rutaImagen) {
        this.tituloOriginal = titulo;
        this.descripcionOriginal = descripcion;
        this.rutaImagenOriginal = rutaImagen;
    }

    private void cargarDatosEnControlesLegacy(String titulo, String descripcion) {
        if (titulo != null) {
            txtTitulo.setText(titulo);
        }

        if (descripcion != null) {
            txtDescripcion.setText(descripcion);
        }
    }

    private void cargarImagenExistente(String rutaImagen) {
        try {
            if (rutaImagen != null && !rutaImagen.isEmpty()) {
                Image imagenExistente = crearImagenDesdeRuta(rutaImagen);
                imgPortada.setImage(imagenExistente);
                imprimirLog("Imagen de playlist cargada: " + rutaImagen);
            } else {
                cargarImagenPorDefecto();
            }
        } catch (Exception e) {
            imprimirLog("Error al cargar imagen existente: " + e.getMessage());
            cargarImagenPorDefecto();
        }
    }

    private Image crearImagenDesdeRuta(String rutaImagen) {
        if (rutaImagen.startsWith("file:") || new File(rutaImagen).exists()) {
            return new Image(rutaImagen);
        } else {
            return new Image(getClass().getResourceAsStream(rutaImagen));
        }
    }

    // =================== MANEJO DE IMÁGENES ===================

    private void cargarImagenPorDefecto() {
        if (!intentarCargarImagen(RUTA_IMAGEN_CAMARA, "cámara")) {
            if (!intentarCargarImagen(RUTA_IMAGEN_SIMBOLO, "símbolo aplicación")) {
                imprimirLog("No se pudo cargar ninguna imagen por defecto");
                imgPortada.setImage(null);
            }
        }
    }

    private boolean intentarCargarImagen(String ruta, String descripcion) {
        try {
            Image imagen = new Image(getClass().getResourceAsStream(ruta));
            imgPortada.setImage(imagen);
            imprimirLog("Imagen por defecto (" + descripcion + ") cargada");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void restablecerImagenOriginal() {
        if (tieneImagenOriginal()) {
            restablecerDesdeBytes();
        } else {
            restablecerAImagenPorDefecto();
        }
    }

    private boolean tieneImagenOriginal() {
        return playlistActual != null && playlistActual.getImagenPortada() != null;
    }

    private void restablecerDesdeBytes() {
        try {
            ByteArrayInputStream streamImagen = new ByteArrayInputStream(playlistActual.getImagenPortada());
            Image imagenOriginal = new Image(streamImagen);
            imgPortada.setImage(imagenOriginal);
            imagenSeleccionada = null;
            imprimirLog("Imagen restablecida a la original");
        } catch (Exception e) {
            imprimirLog("Error al restablecer imagen original: " + e.getMessage());
            cargarImagenPorDefecto();
        }
    }

    private void restablecerAImagenPorDefecto() {
        cargarImagenPorDefecto();
        imagenSeleccionada = null;
    }

    // =================== VERIFICACIÓN DE CAMBIOS ===================

    public boolean verificarCambiosPendientes() {
        String tituloActual = obtenerTextoLimpio(txtTitulo);
        String descripcionActual = obtenerTextoLimpio(txtDescripcion);

        boolean cambioTitulo = !tituloActual.equals(obtenerTextoSeguro(tituloOriginal));
        boolean cambioDescripcion = !descripcionActual.equals(obtenerTextoSeguro(descripcionOriginal));
        boolean cambioImagen = imagenSeleccionada != null;

        imprimirVerificacionCambios(cambioTitulo, cambioDescripcion, cambioImagen);

        return cambioTitulo || cambioDescripcion || cambioImagen;
    }

    private void imprimirVerificacionCambios(boolean cambioTitulo, boolean cambioDescripcion, boolean cambioImagen) {
        imprimirLog("=== VERIFICANDO CAMBIOS ===");
        imprimirLog("Cambio título: " + cambioTitulo);
        imprimirLog("Cambio descripción: " + cambioDescripcion);
        imprimirLog("Cambio imagen: " + cambioImagen);
    }

    // Método legacy para compatibilidad
    public boolean huboCambios() {
        return verificarCambiosPendientes();
    }

    // =================== MÉTODOS UTILITARIOS ===================

    private String obtenerTextoLimpio(TextField campo) {
        return campo.getText().trim();
    }

    private String obtenerTextoLimpio(TextArea campo) {
        return campo.getText().trim();
    }

    private String obtenerTextoSeguro(String texto) {
        return texto != null ? texto : "";
    }

    private Stage obtenerVentanaActual() {
        return (Stage) imgPortada.getScene().getWindow();
    }

    private void cerrarVentana() {
        Stage ventana = (Stage) btnRegresar.getScene().getWindow();
        ventana.close();
    }

    private void imprimirLog(String mensaje) {
        System.out.println(mensaje);
    }

    // =================== MENSAJES DE USUARIO ===================

    private void mostrarMensajeError(String titulo, String mensaje) {
        mostrarAlerta(Alert.AlertType.ERROR, titulo, mensaje);
    }

    private void mostrarMensajeInformativo(String titulo, String mensaje) {
        mostrarAlerta(Alert.AlertType.INFORMATION, titulo, mensaje);
    }

    private void mostrarMensajeAdvertencia(String titulo, String mensaje) {
        mostrarAlerta(Alert.AlertType.WARNING, titulo, mensaje);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // Método legacy para compatibilidad
    private void mostrarAlerta(String titulo, String mensaje) {
        mostrarMensajeInformativo(titulo, mensaje);
    }
}