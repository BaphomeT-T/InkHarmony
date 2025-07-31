package UserInterface.CustomerControl.AdminUserControl;

import java.util.List;

import BusinessLogic.Administrador;
import BusinessLogic.Sesion;
import DataAccessComponent.DTO.AdministradorDTO;
import DataAccessComponent.DTO.PerfilDTO;
import DataAccessComponent.DTO.TipoUsuario;
import UserInterface.Utils.RecursosPerfil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.paint.ImagePattern;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.shape.Circle;
import javafx.scene.image.Image;
import javafx.beans.property.SimpleStringProperty;


public class AdministracionUsuarioController {
    private Sesion sesion = Sesion.getSesion();
    private List<String> rutasImagenes = RecursosPerfil.obtenerRutasImagenes();
    private Administrador administrador = new Administrador();
    private PerfilDTO administradorPerfil = new AdministradorDTO(sesion.obtenerUsuarioActual());
    private int indiceActual = Integer.parseInt(administradorPerfil.getFoto());
    @FXML
    private Button btnActivarCuenta;
    @FXML
    private Button btnAgregarCancion;
    @FXML
    private Button btnAgregarArtista;
    @FXML
    private Button btnActualizarRol;
    

    @FXML
    private MenuButton menuPerfil;
    @FXML
    private Circle imgPerfil;
    @FXML
    private MenuItem btnCerrarSesion;

    @FXML
    private Button btnDesactivarCuenta;

    @FXML
    private Button btnEliminarCuenta;

    @FXML
    private TextField txtApellido;

    @FXML
    private TextField txtNombre;
    @FXML
    private ComboBox<TipoUsuario> cmbRol;
    @FXML
    private TableView<PerfilDTO> tblUsuarios;
    @FXML
    private TextField txtBuscarCorreo;

    // Columnas de la tabla
    @FXML
    private TableColumn<PerfilDTO, String> colNombre;
    @FXML
    private TableColumn<PerfilDTO, String> colApellido;
    @FXML
    private TableColumn<PerfilDTO, String> colCorreo;
    @FXML
    private TableColumn<PerfilDTO, String> colRol;

    // Lista original para búsqueda
    private ObservableList<PerfilDTO> listaOriginalUsuarios = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurar componentes de la interfaz
        configurarColumnasTabla();
        configurarComboBox();
        
        // Cargar la imagen de perfil
        actualizarImagenPerfil();
        
        // Actualizar tabla con datos y estilos
        actualizarTablaUsuarios();

        // Configurar listeners
        configurarListeners();
    }

    private void configurarColumnasTabla() {
        // Configurar cómo obtener los datos para cada columna
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        colApellido.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getApellido()));
        colCorreo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCorreo()));
        colRol.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getTipoUsuario().toString()));
    }

    private void configurarComboBox() {
        cmbRol.setItems(FXCollections.observableArrayList(TipoUsuario.values()));
    }

    private void actualizarTablaUsuarios() {
        // Cargar los usuarios en la tabla
        cargarUsuarios();
        // Configurar el estilo de las filas según el estado de la cuenta
        configurarEstiloFilas();
    }

    private void configurarListeners() {
        // Listener para búsqueda activa
        txtBuscarCorreo.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarUsuariosPorCorreo(newValue);
        });

        // Listener para selección de fila
        tblUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtNombre.setText(newSelection.getNombre());
                txtApellido.setText(newSelection.getApellido());
                // Si tu ComboBox de rol está bien configurado:
                cmbRol.setValue(newSelection.getTipoUsuario());
            } else {
                txtNombre.clear();
                txtApellido.clear();
                cmbRol.setValue(null);
            }
        });
    }

    private void cargarUsuarios() {
        List<PerfilDTO> usuarios = administrador.consultarUsuarios();
        listaOriginalUsuarios.setAll(usuarios);
        tblUsuarios.setItems(listaOriginalUsuarios);
    
    }

    private void actualizarImagenPerfil() {
        try {
            String ruta = rutasImagenes.get(indiceActual);
            Image imagen = new Image(getClass().getResourceAsStream(ruta));
            ImagePattern patron = new ImagePattern(imagen);
            imgPerfil.setFill(patron);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configurarEstiloFilas() {
        // Configurar el estilo de las filas según el estado de la cuenta
        tblUsuarios.setRowFactory(tv -> new TableRow<PerfilDTO>() {
            @Override
            protected void updateItem(PerfilDTO item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || item == null) {
                    setStyle("");
                } else {
                    // Aplicar estilo según el estado de la cuenta
                    String estado = item.getEstado_cuenta();
                    if ("Activo".equalsIgnoreCase(estado) || "1".equals(estado)) {
                        setStyle("-fx-background-color: #24E23E; -fx-text-fill: #Ffffff;"); // Verde claro
                    } else if ("desactivado".equalsIgnoreCase(estado) || "0".equals(estado)) {
                        setStyle("-fx-background-color: #FF0000 ; -fx-text-fill: #ffffff;"); // Rojo claro
                    } else {
                        setStyle(""); // Sin estilo para estados desconocidos
                    }
                }
            }
        });
    }

    private void filtrarUsuariosPorCorreo(String filtro) {

        if (filtro == null || filtro.trim().isEmpty()) {
            tblUsuarios.setItems(listaOriginalUsuarios);
        } else {
            ObservableList<PerfilDTO> filtrados = FXCollections.observableArrayList();
            String filtroLower = filtro.toLowerCase().trim();
            
            for (PerfilDTO perfil : listaOriginalUsuarios) {
                String correo = perfil.getCorreo();
                if (correo != null && correo.toLowerCase().startsWith(filtroLower)) {
                    filtrados.add(perfil);
                }
            }
            tblUsuarios.setItems(filtrados);
        }
    }

    @FXML
    void activarCuenta(ActionEvent event) {
        PerfilDTO usuarioSeleccionado = tblUsuarios.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado == null) {
            mostrarAlerta("Advertencia", "Debe seleccionar un usuario de la tabla", Alert.AlertType.WARNING);
            return;
        }
       administrador.activarCuenta(usuarioSeleccionado);
        actualizarTablaUsuarios();
    }

    @FXML
    void desactivarCuenta(ActionEvent event) {
        PerfilDTO usuarioSeleccionado = tblUsuarios.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado == null) {
            mostrarAlerta("Advertencia", "Debe seleccionar un usuario de la tabla", Alert.AlertType.WARNING);
            return;
        }
        if (usuarioSeleccionado.getCorreo().equals(sesion.obtenerUsuarioActual().getCorreo())) {
            mostrarAlerta("Error", "No se puede desactivar la cuenta actual", Alert.AlertType.ERROR);
            return;
        } else {
            administrador.desactivarCuenta(usuarioSeleccionado);
            actualizarTablaUsuarios();
        }
    }

    @FXML
    void eliminarCuenta(ActionEvent event) {
        PerfilDTO usuarioSeleccionado = tblUsuarios.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado == null) {
            mostrarAlerta("Advertencia", "Debe seleccionar un usuario de la tabla", Alert.AlertType.WARNING);
            return;
        }
        if (usuarioSeleccionado.getCorreo().equals(sesion.obtenerUsuarioActual().getCorreo())) {
            mostrarAlerta("Error", "No se puede eliminar la cuenta actual", Alert.AlertType.ERROR);
            return;
        } else {
            administrador.eliminarCuenta(usuarioSeleccionado);
            actualizarTablaUsuarios();
        }
    }

    private void mostrarAlerta(String string, String string2, AlertType error) {
        Alert alert = new Alert(error);
        alert.setTitle(string);
        alert.setContentText(string2);
        alert.showAndWait();
    }

    @FXML
    void actualizarRol(ActionEvent event) {
        PerfilDTO usuarioSeleccionado = tblUsuarios.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado == null) {
            mostrarAlerta("Advertencia", "Debe seleccionar un usuario de la tabla", Alert.AlertType.WARNING);
            return;
        }
        if (usuarioSeleccionado.getCorreo().equals(sesion.obtenerUsuarioActual().getCorreo())) {
            mostrarAlerta("Error", "No se puede cambiar el rol de la cuenta actual", Alert.AlertType.ERROR);
            return;
        } else {
          administrador.cambiarTipoUsuario(usuarioSeleccionado, cmbRol.getValue());
            actualizarTablaUsuarios();
        }
    }

    @FXML
    void cerrarSesion(ActionEvent event) {
        sesion.cerrarSesion();
        // Cerrar la ventana de registro y abrir de nuevo la ventana de login
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/UserInterface/GUI/AdminUserControl/login.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Login");
            stage.setMinWidth(1280);
            stage.setMinHeight(680);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        javafx.stage.Stage stage = (javafx.stage.Stage) menuPerfil.getScene().getWindow();
        stage.close();

    }
    @FXML
    void agregarCancionesFxml() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/CatalogoCanciones/frameCatalogoCanciones.fxml"));// colocar la ruta de la ventana
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Registro de Usuario");
            stage.setMinWidth(1280);
            stage.setMinHeight(680);

            stage.show();

            // Cerrar la ventana de login
            ((Stage) btnAgregarArtista.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void agregarArtistaFxml() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/CatalogoArtistas/PantallaCatalogoArtista.fxml"));// colocar la ruta de la ventana
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Registro de Usuario");
            stage.setMinWidth(1280);
            stage.setMinHeight(680);

            stage.show();

            // Cerrar la ventana de login
            ((Stage) btnAgregarArtista.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
