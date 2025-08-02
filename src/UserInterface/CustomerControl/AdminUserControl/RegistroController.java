package UserInterface.CustomerControl.AdminUserControl;

import UserInterface.Utils.*;
import BusinessLogic.ServicioPerfil;
import BusinessLogic.Sesion;
import BusinessLogic.Genero;
import DataAccessComponent.DAO.PerfilDAO;
import DataAccessComponent.DAO.UsuarioDAO;
import DataAccessComponent.DTO.PerfilDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Font;

public class RegistroController {

    private final ServicioPerfil servicioPerfil = new ServicioPerfil();

    private List<String> rutasImagenes = RecursosPerfil.obtenerRutasImagenes();
    private int indiceActual = 0;

    private List<Genero> todosLosGeneros;
    private List<Genero> generosMostrados;
    private List<Genero> generosSeleccionados = new ArrayList<>();


    @FXML
    private Polyline btnAnterior;

    @FXML
    private Button btnRegistrar;

    @FXML
    private Polyline btnSiguiente;

    @FXML
    private GridPane contenedorGeneros;

    @FXML
    private Circle imgPerfil;

    @FXML
    private Label lblSalirRegistro;

    @FXML
    private TextField txtApellido;

    @FXML
    private TextField txtBusquedaGenero;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private TextField txtCorreo;

    @FXML
    private TextField txtNombre;

    @FXML
    private PasswordField txtRepetirContrasena;

    @FXML
    public void initialize() {
        actualizarImagenPerfil();
        cargarGenerosMusicales();
        configurarBusquedaGeneros();
    }

    @FXML
    void imagenAnterior(MouseEvent event) {
        if (indiceActual == 0) {
            indiceActual = RecursosPerfil.totalImagenes() - 1; // si está en la primera, vuelve a la última
        } else {
            indiceActual--;
        }
        actualizarImagenPerfil();
    }

    @FXML
    void imagenSiguiente(MouseEvent event) {
        if (indiceActual == RecursosPerfil.totalImagenes() - 1) {
            indiceActual = 0; // si está en la última, vuelve a la primera
        } else {
            indiceActual++;
        }
        actualizarImagenPerfil();
    }


    @FXML
    void registrarCuenta(ActionEvent event) {
        // 1. Obtener datos del formulario
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String correo = txtCorreo.getText().trim();
        String contrasena = txtContrasena.getText().trim();
        String repetirContrasena = txtRepetirContrasena.getText().trim();
        
        // 2. Validaciones básicas (incluye validación de géneros)
        if (!validarCampos(nombre, apellido, correo, contrasena, repetirContrasena)) {
            return;
        }
        
        // 3. Verificar si el correo ya existe (puedes seguir usando perfilDAO o delegar a ServicioPerfil)
        PerfilDAO perfilDAO = new PerfilDAO();
        if (perfilDAO.buscarPorEmail(correo) != null) {
            mostrarAlerta("Correo ya registrado", 
                "El correo electrónico ya está en uso. Por favor use otro.",
                Alert.AlertType.ERROR);
            return;
        }
        
        try {
            // 4. Registrar usuario usando ServicioPerfil (encripta contraseña)
            servicioPerfil.registrarUsuario(nombre, apellido, correo, contrasena, String.valueOf(indiceActual));
            PerfilDTO usuario = perfilDAO.buscarPorEmail(correo);
            
/*            // 5. Guardar preferencias directamente con enum Genero
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            if (!usuarioDAO.guardarPreferencias(usuario, generosSeleccionados)) {
                // Si fallan las preferencias, eliminar perfil
                perfilDAO.eliminar(usuario);
                throw new RuntimeException("No se pudieron guardar las preferencias");
            }*/
            
            mostrarAlerta("Éxito", "Registro completo con preferencias", Alert.AlertType.INFORMATION);
            salirRegistro();
            
        } catch (Exception e) {
            mostrarAlerta("Error", "No se completó el registro: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }



    private boolean validarCampos(String nombre, String apellido, String correo, 
                                String contrasena, String repetirContrasena) {
        // Validar campos vacíos
        StringBuilder camposVacios = new StringBuilder("Debe llenar los siguientes campos:\n");
        boolean hayVacios = false;
        
        if (nombre.isEmpty()) { camposVacios.append("- Nombre\n"); hayVacios = true; }
        if (apellido.isEmpty()) { camposVacios.append("- Apellido\n"); hayVacios = true; }
        if (correo.isEmpty()) { camposVacios.append("- Correo\n"); hayVacios = true; }
        if (contrasena.isEmpty()) { camposVacios.append("- Contraseña\n"); hayVacios = true; }
        if (repetirContrasena.isEmpty()) { camposVacios.append("- Verificar Contraseña\n"); hayVacios = true; }

        if (hayVacios) {
            mostrarAlerta("Campos incompletos", camposVacios.toString(), 
                Alert.AlertType.WARNING);
            return false;
        }

        // Validar que el nombre y apellido no contengan números ni espacios
        if (!nombre.matches("[a-zA-Z]+") || !apellido.matches("[a-zA-Z]+")) {
            mostrarAlerta("Nombre o Apellido inválido", 
                "El nombre y apellido solo pueden contener letras.",
                Alert.AlertType.ERROR);
            return false;
        }

        // Validar formato de correo
        if (!correo.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            mostrarAlerta("Correo inválido", 
                "Por favor ingrese un correo electrónico válido.",
                Alert.AlertType.ERROR);
            return false;
        }

        // Validar que las contraseñas coincidan
        if (!contrasena.equals(repetirContrasena)) {
            mostrarAlerta("Contraseñas no coinciden", 
                "Las contraseñas ingresadas no son iguales.",
                Alert.AlertType.ERROR);
            return false;
        }

        // Validar fortaleza de contraseña (opcional)
        if (contrasena.length() < 8) {
            mostrarAlerta("Contraseña débil", 
                "La contraseña debe tener al menos 8 caracteres.",
                Alert.AlertType.WARNING);
            return false;
        }

        // Validar que se hayan seleccionado géneros
        if (generosSeleccionados.isEmpty()) {
            mostrarAlerta("Géneros no seleccionados", 
                "Debe seleccionar al menos un género musical.", 
                Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    private void mostrarAlerta(String titulo, String mensaje, javafx.scene.control.Alert.AlertType tipo) {
        javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
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

    private void actualizarBotonesGeneros() {
        // Limpiar el GridPane
        contenedorGeneros.getChildren().clear();
        
        int columna = 0;
        int fila = 0;
        
        for (Genero genero : generosMostrados) {
            if (columna >= 3) { // Máximo 3 columnas
                columna = 0;
                fila++;
            }
            
            if (fila >= 2) break; // Máximo 2 filas
            
            Button botonGenero = crearBotonGenero(genero);
            contenedorGeneros.add(botonGenero, columna, fila);
            
            columna++;
        }
        
        // Rellenar espacios vacíos si hay menos de 6 géneros
        while (fila < 2) {
            while (columna < 3) {
                Button botonVacio = crearBotonVacio();
                contenedorGeneros.add(botonVacio, columna, fila);
                columna++;
            }
            columna = 0;
            fila++;
        }
    }

    private void cargarGenerosMusicales() {
        todosLosGeneros = Arrays.asList(Genero.values());

        generosMostrados = new ArrayList<>(todosLosGeneros.subList(0, Math.min(6, todosLosGeneros.size())));
        actualizarBotonesGeneros();
    }

    private Button crearBotonGenero(Genero genero) {
        Button boton = new Button(genero.name().replace('_', ' ')); // Opcional: mostrar nombre legible
        boton.setMaxWidth(120);
        boton.setMaxHeight(40);
        // resto igual, pero comparando con generosSeleccionados de tipo Genero
        boton.setStyle("-fx-background-color: " + 
                    (generosSeleccionados.contains(genero) ? "#9190C2" : "#575a81") + 
                    "; -fx-text-fill: white; -fx-background-radius: 15;");
        boton.setFont(new Font(14));
        
        boton.setOnAction(event -> {
            if (generosSeleccionados.contains(genero)) {
                generosSeleccionados.remove(genero);
                boton.setStyle("-fx-background-color: #575a81; -fx-text-fill: white; -fx-background-radius: 15;");
            } else {
                generosSeleccionados.add(genero);
                boton.setStyle("-fx-background-color: #9190C2; -fx-text-fill: white; -fx-background-radius: 15;");
            }
        });
        
        return boton;
    }


    private Button crearBotonVacio() {
        Button boton = new Button();
        boton.setMaxWidth(Double.MAX_VALUE);
        boton.setMaxHeight(Double.MAX_VALUE);
        boton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        boton.setDisable(true);
        return boton;
    }

    private void configurarBusquedaGeneros() {
        txtBusquedaGenero.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                // Mostrar primeros 6 géneros si no hay búsqueda
                generosMostrados = new ArrayList<>(todosLosGeneros.subList(0, Math.min(6, todosLosGeneros.size())));
            } else {
                // Filtrar géneros y tomar los primeros 6 coincidentes
                generosMostrados = todosLosGeneros.stream()
                    .filter(g -> g.name().toLowerCase().contains(newValue.toLowerCase()))
                    .limit(6)
                    .collect(Collectors.toList());
            }
            actualizarBotonesGeneros();
        });
    }

    @FXML
    private void salirRegistro() {
        Sesion sesion = Sesion.getSesion();
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
        javafx.stage.Stage stage = (javafx.stage.Stage) lblSalirRegistro.getScene().getWindow();
        stage.close();

    }
}
