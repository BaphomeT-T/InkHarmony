package UserInterface.CustomerControl.AdminUserControl;


import BusinessLogic.Sesion;
import DataAccessComponent.DTO.AdministradorDTO;
import DataAccessComponent.DTO.PerfilDTO;
import UserInterface.Utils.RecursosPerfil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.List;

public class HomeAdminController {
    private Sesion sesion = Sesion.getSesion();
    private List<String> rutasImagenes = RecursosPerfil.obtenerRutasImagenes();
    private PerfilDTO administradorPerfil = new AdministradorDTO(sesion.obtenerUsuarioActual());
    private int indiceActual = Integer.parseInt(administradorPerfil.getFoto());
    @FXML
    private MenuButton menuPerfil;
    @FXML
    private Circle imgPerfil;
    @FXML
    private MenuItem btnCerrarSesion;


    @FXML
    public void initialize() {
        // Cargar la imagen de perfil
        actualizarImagenPerfil();
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
    void irAUsuarios() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/AdminUserControl/administracion_Usuarios.fxml"));// colocar la ruta de la ventana
            Parent root = loader.load();

            Stage stage = (Stage) menuPerfil.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Administración de Usuarios");
            stage.setMinWidth(1280);
            stage.setMinHeight(680);
            stage.show();


            stage.show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void irACanciones() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/CatalogoCanciones/frameCatalogoCanciones.fxml"));// colocar la ruta de la ventana
            Parent root = loader.load();
            Stage stage = (Stage) menuPerfil.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Administración de Usuarios");
            stage.setMinWidth(1280);
            stage.setMinHeight(680);
            stage.show();


            stage.show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void irAArtistas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/CatalogoArtistas/PantallaCatalogoArtista.fxml"));// colocar la ruta de la ventana
            Parent root = loader.load();
            Stage stage = (Stage) menuPerfil.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Administración de Usuarios");
            stage.setMinWidth(1280);
            stage.setMinHeight(680);
            stage.show();


            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
