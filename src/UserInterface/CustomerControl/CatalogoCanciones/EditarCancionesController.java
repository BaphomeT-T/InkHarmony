/*
|-----------------------------------------------|
| © 2025 EPN-FIS, Todos los derechos reservados |
| GR1SW                                         |
|-----------------------------------------------|
Autor: Sergio Rodríguez
Descripción: Controlador para la edición de canciones.
*/

package UserInterface.CustomerControl.CatalogoCanciones;

import DataAccessComponent.DTO.CancionDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditarCancionesController {

    // Objeto canción que será modificado en esta pantalla
    private CancionDTO cancion;

    // Campos de entrada en la interfaz gráfica
    @FXML
    private TextField txtTitulo;

    @FXML
    private TextField txtDuracion;

    @FXML
    private TextField txtAnio;

    /**
     * Método que se ejecuta cuando se presiona el botón "Guardar".
     * Valida los datos y actualiza el objeto CancionDTO.
     */
    @FXML
    public void editarCancion(ActionEvent actionEvent) {
        // Validar campos vacíos
        if (txtTitulo.getText().isBlank() || txtDuracion.getText().isBlank() || txtAnio.getText().isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos incompletos", "Todos los campos son obligatorios.");
            return;
        }

        // Validar que duración y año sean números enteros
        int duracion;
        int anio;
        try {
            duracion = Integer.parseInt(txtDuracion.getText());
            anio = Integer.parseInt(txtAnio.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Formato inválido", "Duración y año deben ser números enteros.");
            return;
        }

        // Actualizar el objeto CancionDTO con los nuevos datos
        cancion.setTitulo(txtTitulo.getText().trim());
        cancion.setDuracion(duracion);
        cancion.setAnio(anio);

        // Mostrar mensaje de éxito
        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "La canción se actualizó correctamente.");

        // Cerrar la ventana
        cerrarVentana();
    }

    /**
     * Método que recibe una canción desde otro controlador
     * y carga sus datos en los campos del formulario.
     */
    public void setCancion(CancionDTO cancion) {
        this.cancion = cancion;
        txtTitulo.setText(cancion.getTitulo());
        txtDuracion.setText(String.valueOf(cancion.getDuracion()));
        txtAnio.setText(String.valueOf(cancion.getAnio()));
    }

    /**
     * Muestra una alerta simple con el mensaje indicado.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Cierra la ventana actual.
     */
    private void cerrarVentana() {
        Stage stage = (Stage) txtTitulo.getScene().getWindow();
        stage.close();
    }
}
