package UserInterface.CustomerControl.Playlist;

import BusinessLogic.Playlist;
import BusinessLogic.PlaylistDAO;

public class EliminarPlaylistController {
    @FXML private Label lblTitulo;
    @FXML private Label lblDescripcion;
    @FXML private Label lblCanciones;
    @FXML private Label lblDuracion;
    @FXML private Button btnCancelar;
    @FXML private Button btnEliminar;

    private Playlist playlistAEliminar;
    private PlaylistDAO playlistDAO = new PlaylistDAO();
    private Runnable onEliminacionExitosa;

    public void setPlaylist(Playlist playlist) {
        this.playlistAEliminar = playlist;
        cargarInformacionPlaylist();
    }

    public void setOnEliminacionExitosa(Runnable callback) {
        this.onEliminacionExitosa = callback;
    }

    @FXML
    public void initialize() {
        btnCancelar.setOnAction(e -> cancelarEliminacion());
        btnEliminar.setOnAction(e -> confirmarEliminacion());
    }

    private void cargarInformacionPlaylist() {
        if (playlistAEliminar != null) {
            lblTitulo.setText(playlistAEliminar.getTitulo());
            lblDescripcion.setText(playlistAEliminar.getDescripcion());
            
            int cantidadCanciones = playlistAEliminar.calcularCantidadCanciones();
            lblCanciones.setText(cantidadCanciones + " canciones");
            
            double duracionTotal = playlistAEliminar.obtenerDuracion();
            int horas = (int) (duracionTotal / 3600);
            int minutos = (int) ((duracionTotal % 3600) / 60);
            lblDuracion.setText(String.format("%dh %dm", horas, minutos));
        }
    }

    @FXML
    private void cancelarEliminacion() {
        cerrarVentana();
    }

    @FXML
    private void confirmarEliminacion() {
        if (playlistAEliminar == null) {
            mostrarAlerta("Error: No se ha especificado la playlist a eliminar", Alert.AlertType.ERROR);
            return;
        }

        try {
            // Eliminar la playlist de la base de datos
            playlistDAO.eliminarPlaylist(playlistAEliminar);
            
            mostrarAlerta("Playlist eliminada exitosamente", Alert.AlertType.INFORMATION);
            
            // Ejecutar callback si existe
            if (onEliminacionExitosa != null) {
                onEliminacionExitosa.run();
            }
            
            cerrarVentana();
            
        } catch (Exception e) {
            mostrarAlerta("Error al eliminar la playlist: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Eliminar Playlist");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
} 