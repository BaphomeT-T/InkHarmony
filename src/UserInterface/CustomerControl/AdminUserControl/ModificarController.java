package UserInterface.CustomerControl.AdminUserControl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import BusinessLogic.Sesion;
import DataAccessComponent.DAO.GeneroDAO;
import DataAccessComponent.DAO.PerfilDAO;
import DataAccessComponent.DAO.UsuarioDAO;
import DataAccessComponent.DTO.GeneroDTO;
import DataAccessComponent.DTO.PerfilDTO;
import UserInterface.Utils.RecursosPerfil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ModificarController {

    private List<String> rutasImagenes = RecursosPerfil.obtenerRutasImagenes();
    private int indiceActual = 0;
    //sincronizar
    private List<String> todosLosGeneros;
    private List<String> generosMostrados;
    private List<String> generosSeleccionados = new ArrayList<>();

    @FXML
    private Polyline btnAnterior;

    @FXML
    private Button btnGuardar;

    @FXML
    private Polyline btnSiguiente;

    @FXML
    private Button cerrarButton;

    @FXML
    private GridPane contenedorGeneros;

    @FXML
    private Circle imgPerfil;

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
        rutasImagenes = RecursosPerfil.obtenerRutasImagenes();
        indiceActual = 0;

        // Obtener el perfil actual
        PerfilDTO perfil = Sesion.getSesion().obtenerUsuarioActual();
        if (perfil == null) {
            mostrarAlerta("Error", "No hay sesión activa.", javafx.scene.control.Alert.AlertType.ERROR);
            return;
        }

        txtNombre.setText(perfil.getNombre());
        txtApellido.setText(perfil.getApellido());
        txtCorreo.setText(perfil.getCorreo());

        // Establecer imagen de perfil si existe
        String foto = perfil.getFoto();
        if (foto != null) {
            try {
                int index = Integer.parseInt(foto.trim());
                if (index >= 0 && index < rutasImagenes.size()) {
                    indiceActual = index;
                } else {
                    System.out.println("Índice de imagen fuera de rango: " + index);
                }
            } catch (NumberFormatException e) {
                System.out.println("La foto no es un índice válido: " + foto);
            }
        }
        actualizarImagenPerfil();

        // Cargar géneros desde BD
        todosLosGeneros = GeneroDAO.obtenerTodos();
        generosSeleccionados = new UsuarioDAO().obtenerPreferencias(perfil).stream()
                                        .map(GeneroDTO::getNombreGenero)
                                        .collect(Collectors.toList());

        // Mostrar los primeros 6 géneros al inicio
        generosMostrados = new ArrayList<>(todosLosGeneros.subList(0, Math.min(6, todosLosGeneros.size())));
        actualizarBotonesGeneros();

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
        
        for (String genero : generosMostrados) {
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

    private Button crearBotonGenero(String genero) {
        Button boton = new Button(genero);
        boton.setMaxWidth(120);
        boton.setMaxHeight(40);
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
                    .filter(genero -> genero.toLowerCase().contains(newValue.toLowerCase()))
                    .limit(6)
                    .collect(Collectors.toList());
            }
            actualizarBotonesGeneros();
        });
    }


    private boolean hayCambios(PerfilDTO perfilActual, PerfilDTO perfilNuevo, List<String> generosSeleccionadosNuevos) {
        if (!perfilActual.getNombre().equals(perfilNuevo.getNombre())) return true;
        if (!perfilActual.getApellido().equals(perfilNuevo.getApellido())) return true;
        if (!perfilActual.getCorreo().equals(perfilNuevo.getCorreo())) return true;
        if (!perfilActual.getFoto().equals(perfilNuevo.getFoto())) return true;

        // Comparar géneros musicales
        List<GeneroDTO> generosDTOActuales = new UsuarioDAO().obtenerPreferencias(perfilActual);
        List<String> generosActuales = generosDTOActuales.stream()
                                    .map(GeneroDTO::getNombreGenero)
                                    .collect(Collectors.toList());

        if (generosActuales.size() != generosSeleccionadosNuevos.size()) return true;

        for (String genero : generosSeleccionadosNuevos) {
            if (!generosActuales.contains(genero)) return true;
        }

        // Si no hubo diferencias
        return false;
    }



    private boolean validarCampos(String nombre, String apellido, String correo, 
                                String contrasena, String repetirContrasena) {
        // Validar campos vacíos
        StringBuilder camposVacios = new StringBuilder("Debe llenar los siguientes campos:\n");
        boolean hayVacios = false;
        
        if (nombre.isEmpty()) { camposVacios.append("- Nombre\n"); hayVacios = true; }
        if (apellido.isEmpty()) { camposVacios.append("- Apellido\n"); hayVacios = true; }
        if (correo.isEmpty()) { camposVacios.append("- Correo\n"); hayVacios = true; }
        if (contrasena.isEmpty() && !repetirContrasena.isEmpty()) {
            camposVacios.append("- Contraseña\n"); hayVacios = true;
        }
        if (!contrasena.isEmpty() && repetirContrasena.isEmpty()) {
            camposVacios.append("- Verificar contraseña\n"); hayVacios = true;
        }
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
        if (!contrasena.isEmpty() && !repetirContrasena.isEmpty()){
            if (contrasena.length() < 8) {
                mostrarAlerta("Contraseña débil", 
                    "La contraseña debe tener al menos 8 caracteres.",
                    Alert.AlertType.WARNING);
                return false;
            }
        }

        // Validar que se hayan seleccionado géneros
        if (generosSeleccionados.isEmpty()) {
            mostrarAlerta("Géneros no seleccionados", 
                "Debe seleccionar al menos un género musical.", 
                Alert.AlertType.WARNING);
            return false;
        }

        if (generosSeleccionados.isEmpty()) {
            mostrarAlerta("Preferencias musicales", "Debes seleccionar al menos un género musical.", javafx.scene.control.Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    @FXML
    void cerrarVentana(ActionEvent event) {
        // Obtiene el nodo origen (el botón u otro control que disparó el evento)
        Node source = (Node) event.getSource();
        // Obtiene la ventana (Stage) donde está ese nodo
        Stage stage = (Stage) source.getScene().getWindow();
        // Cierra esa ventana
        stage.close();
    }


    @FXML
    void guardarCambios(ActionEvent event) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String correo = txtCorreo.getText().trim();
        String contrasena = txtContrasena.getText();
        String repetirContrasena = txtRepetirContrasena.getText();
        String foto = String.valueOf(indiceActual);

        // 2. Validaciones básicas (incluye validación de géneros)
        if (!validarCampos(nombre, apellido, correo, contrasena, repetirContrasena)) {
            return;
        }

        // Construir DTO actualizado
        PerfilDTO perfilActual = Sesion.getSesion().obtenerUsuarioActual();
        PerfilDTO perfilActualizado = new PerfilDTO();
        perfilActualizado.setNombre(nombre);
        perfilActualizado.setApellido(apellido);
        perfilActualizado.setCorreo(correo);
        perfilActualizado.setFoto(foto);
        perfilActualizado.setContrasenia(contrasena.isEmpty() ? null : encoder.encode(contrasena));
        // Convertir géneros seleccionados
        List<GeneroDTO> generosDTO = generosSeleccionados.stream()
                .map(GeneroDTO::new)
                .collect(Collectors.toList());
        
        // Validación anticipada de contraseña
        if (!contrasena.isEmpty()) {
            if (encoder.matches(contrasena, perfilActual.getContrasenia())) {
                mostrarAlerta("Contraseña inválida", 
                    "La nueva contraseña es igual a la anterior. No se realizaron cambios.", 
                    Alert.AlertType.WARNING);
                return; 
            }
        }

        // Luego de esa validación estricta, ahora sí validamos si hay cambios
        if (!hayCambios(perfilActual, perfilActualizado, generosSeleccionados) 
            && contrasena.isEmpty()) {
            mostrarAlerta("Sin cambios", "No hay cambios para actualizar.", Alert.AlertType.INFORMATION);
            return;
        }

        // En este punto: hay cambios, y la contraseña (si la puso) es diferente.
        // Entonces, codificar si hay contraseña nueva
        if (!contrasena.isEmpty()) {
            perfilActualizado.setContrasenia(encoder.encode(contrasena));
        }

        // Validar si el correo ya está en uso por otro usuario
        if (!correo.equals(perfilActual.getCorreo())) {
            PerfilDAO perfilDAO = new PerfilDAO();
            PerfilDTO perfilExistente = perfilDAO.buscarPorEmail(correo);
            if (perfilExistente != null) {
                mostrarAlerta("Correo en uso", "El correo ingresado ya está registrado por otro usuario.", Alert.AlertType.WARNING);
                return;
            }
        }

        // Proceder con update en la BD
        boolean actualizado = new UsuarioDAO().actualizarPerfil(perfilActualizado, perfilActual.getCorreo(), false, generosDTO);
        
        if (actualizado) {
            mostrarAlerta("Éxito", "Perfil actualizado correctamente.", javafx.scene.control.Alert.AlertType.INFORMATION);
            
            Sesion.getSesion().iniciarSesion(perfilActualizado);

            // Cerrar la ventana actual
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();

        } else {
            mostrarAlerta("Error", "No se pudo actualizar el perfil.", javafx.scene.control.Alert.AlertType.ERROR);
        }
    }

}
