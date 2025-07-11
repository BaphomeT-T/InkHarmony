package UserInterface.CustomerControl.AdminUserControl;

import java.util.List;

import DataAccessComponent.DTO.Administrador;
import DataAccessComponent.DTO.Perfil;
import DataAccessComponent.DTO.TipoUsuario;
import UserInterface.Utils.RecursosPerfil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.paint.ImagePattern;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.shape.Circle;
import javafx.scene.image.Image;
import javafx.beans.property.SimpleStringProperty;

public class AdministracionUsuarioController {
    // private Sesion session;
    private List<String> rutasImagenes = RecursosPerfil.obtenerRutasImagenes();
    private int indiceActual = 0;
    @FXML
    private Button btnActivarCuenta;

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
    private TableView<Perfil> tblUsuarios;
    @FXML
    private TextField txtBuscarCorreo;

    // Columnas de la tabla
    @FXML
    private TableColumn<Perfil, String> colNombre;
    @FXML
    private TableColumn<Perfil, String> colApellido;
    @FXML
    private TableColumn<Perfil, String> colCorreo;
    @FXML
    private TableColumn<Perfil, String> colRol;

    // Lista original para búsqueda
    private ObservableList<Perfil> listaOriginalUsuarios = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurar cómo obtener los datos para cada columna
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        colApellido.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getApellido()));
        colCorreo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCorreo()));
        colRol.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getTipoUsuario().toString()));

        // Cargar la imagen de perfil
        actualizarImagenPerfil();

        // Cargar los usuarios en la tabla
        cargarUsuarios();

        // Listener para búsqueda activa
        txtBuscarCorreo.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarUsuariosPorCorreo(newValue);
        });

        cmbRol.setItems(FXCollections.observableArrayList(TipoUsuario.values()));
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
        Administrador administrador = new Administrador();
        List<Perfil> usuarios = administrador.consultarUsuarios();
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

    private void filtrarUsuariosPorCorreo(String filtro) {
        if (filtro == null || filtro.isEmpty()) {
            tblUsuarios.setItems(listaOriginalUsuarios);
        } else {
            ObservableList<Perfil> filtrados = FXCollections.observableArrayList();
            for (Perfil perfil : listaOriginalUsuarios) {
                if (perfil.getCorreo().toLowerCase().contains(filtro.toLowerCase())) {
                    filtrados.add(perfil);
                }
            }
            tblUsuarios.setItems(filtrados);
        }
    }

    @FXML
    void activarCuenta(ActionEvent event) {
        System.out.println("hola hola");
    }

    @FXML
    void desactivarCuenta(ActionEvent event) {
        // Implementar lógica para desactivar cuenta
        System.out.println("Desactivar cuenta");
    }

    @FXML
    void eliminarCuenta(ActionEvent event) {
        // Implementar lógica para eliminar cuenta
        System.out.println("Eliminar cuenta");
    }

    @FXML
    void actualizarRol(ActionEvent event) {
        // Implementar lógica para actualizar rol
        System.out.println("Actualizar rol");
    }

    @FXML
    void cerrarSesion(ActionEvent event) {

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

}
