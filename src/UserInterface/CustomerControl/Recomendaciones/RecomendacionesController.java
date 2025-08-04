package UserInterface.CustomerControl.Recomendaciones;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;


public class RecomendacionesController {

    private boolean isPressedGenero = false;
    private boolean isPressedArtista = false;
    private boolean isPressedPersonalizadas = false;
    private boolean isPressedEstreno = false;

    private final String baseStyle = "-fx-background-color: #5A5A80; -fx-text-fill: white; -fx-background-radius: 40; -fx-padding: 20 40;";
    private final String pressedStyle = "-fx-background-color: #48486A; -fx-text-fill: white; -fx-background-radius: 40; -fx-padding: 20 40;";

    @FXML
    private Button btnEstrenos;

    @FXML
    private Button btnGenero;

    @FXML
    private Button btnPersonalizadas;

    @FXML
    private Button btnArtista;

    @FXML
    private Button cerrarButton;

    @FXML
    private Button btnLimpiarFiltros;

    @FXML
    private TableColumn<?, ?> colAnio;

    @FXML
    private TableColumn<?, ?> colArtista;

    @FXML
    private TableColumn<?, ?> colDuracion;

    @FXML
    private TableColumn<?, ?> colImagen;

    @FXML
    private TableColumn<?, ?> colTitulo;

    @FXML
    private ComboBox<?> comboArtista;

    @FXML
    private ComboBox<?> comboGenero;

    @FXML
    private StackPane contentPane;

    @FXML
    private HBox filtrosBox;

    @FXML
    private TableView<?> tablaCanciones;

    @FXML
    private Label mensajeBienvenida;

    @FXML
    private Label mensajeSelecciona;

    @FXML
    private TextField txtBuscarArtista;

    @FXML
    private TextField txtBuscarGenero;


    @FXML
    void cerrarVentana(ActionEvent event) {

    }

    // Función para alternar estilo
    private void toggleButtonStyle(Button btn, boolean isPressed) {
        if (isPressed) {
            btn.setStyle(pressedStyle);
        } else {
            btn.setStyle(baseStyle);
        }
    }

    @FXML
    private void initialize() {
        // Ocultamos todo al inicio
        filtrosBox.setVisible(false);
        filtrosBox.setManaged(false);

        txtBuscarGenero.setVisible(false);
        txtBuscarGenero.setManaged(false);

        txtBuscarArtista.setVisible(false);
        txtBuscarArtista.setManaged(false);
    }

    @FXML
    private void handlePorArtista() {
        if (!isPressedArtista){
            isPressedArtista = true;
            toggleButtonStyle(btnArtista, true);  // Cambia el estilo

            filtrosBox.setVisible(true);
            filtrosBox.setManaged(true);

            txtBuscarArtista.setVisible(true);
            txtBuscarArtista.setManaged(true);

            mensajeBienvenida.setVisible(false);
            mensajeBienvenida.setManaged(false);

            mensajeSelecciona.setVisible(false);
            mensajeSelecciona.setManaged(false);
        }
    }

    @FXML
    private void handlePorGenero() {
        if (!isPressedGenero) {
            isPressedGenero = true;
            toggleButtonStyle(btnGenero, true);

            filtrosBox.setVisible(true);
            filtrosBox.setManaged(true);

            txtBuscarGenero.setVisible(true);
            txtBuscarGenero.setManaged(true);

            mensajeBienvenida.setVisible(false);
            mensajeBienvenida.setManaged(false);

            mensajeSelecciona.setVisible(false);
            mensajeSelecciona.setManaged(false);
        }
    }

    @FXML
    private void handlePersonalizadas(ActionEvent event) {
        if (!isPressedPersonalizadas) {
            isPressedPersonalizadas = true;
            toggleButtonStyle(btnPersonalizadas, true);
            filtrosBox.setVisible(true);
            filtrosBox.setManaged(true);

            mensajeBienvenida.setVisible(false);
            mensajeBienvenida.setManaged(false);

            mensajeSelecciona.setVisible(false);
            mensajeSelecciona.setManaged(false);
        }
    }

    @FXML
    private void handleEstrenos(ActionEvent event) {
        if (!isPressedEstreno) {
            isPressedEstreno = true;
            toggleButtonStyle(btnEstrenos, true);
            filtrosBox.setVisible(true);
            filtrosBox.setManaged(true);

            mensajeBienvenida.setVisible(false);
            mensajeBienvenida.setManaged(false);

            mensajeSelecciona.setVisible(false);
            mensajeSelecciona.setManaged(false);
        }
    }

    @FXML
    private void handleLimpiarFiltros() {
        isPressedGenero = false;
        toggleButtonStyle(btnGenero, isPressedGenero);

        isPressedArtista = false;
        toggleButtonStyle(btnArtista, isPressedArtista);

        isPressedPersonalizadas = false;
        toggleButtonStyle(btnPersonalizadas, isPressedPersonalizadas);

        isPressedEstreno = false;
        toggleButtonStyle(btnEstrenos, isPressedEstreno);

        // Ocultar combos
        txtBuscarGenero.setVisible(false);
        txtBuscarGenero.setManaged(false);

        txtBuscarArtista.setVisible(false);
        txtBuscarArtista.setManaged(false);

        // Si no hay filtros activos, ocultar el contenedor completo
        filtrosBox.setVisible(false);
        filtrosBox.setManaged(false);

        mensajeBienvenida.setVisible(true);
        mensajeBienvenida.setManaged(true);

        mensajeSelecciona.setVisible(true);
        mensajeSelecciona.setManaged(true);

    }


}
