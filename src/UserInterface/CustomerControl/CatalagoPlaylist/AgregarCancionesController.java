package UserInterface.CustomerControl.CatalagoPlaylist;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class AgregarCancionesController {

    @FXML
    private TableView<CancionData> tableCanciones;

    @FXML
    private TableColumn<CancionData, String> colTitulo;

    @FXML
    private TableColumn<CancionData, String> colArtista;

    @FXML
    private TableColumn<CancionData, String> colFechaAgregacion;

    @FXML
    private TableColumn<CancionData, String> colAnoCancion;

    @FXML
    private TableColumn<CancionData, String> colDuracion;

    @FXML
    private TableColumn<CancionData, Void> colAcciones;

    @FXML
    private TextField txtBuscarCancion;

    private ObservableList<CancionData> listaObservable;

    @FXML
    public void initialize() {
        configurarTabla();
        cargarCancionesEjemplo();
        configurarBusqueda();
    }

    private void configurarTabla() {
        // Configurar columnas con imagen del álbum y título
        colTitulo.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getTitulo()));
        colTitulo.setCellFactory(column -> new TableCell<CancionData, String>() {
            private final HBox contenedor = new HBox(10);
            private final ImageView imageView = new ImageView();
            private final Label label = new Label();

            {
                imageView.setFitHeight(40);
                imageView.setFitWidth(40);
                contenedor.getChildren().addAll(imageView, label);
            }

            @Override
            protected void updateItem(String titulo, boolean empty) {
                super.updateItem(titulo, empty);
                if (empty || titulo == null) {
                    setGraphic(null);
                } else {
                    CancionData cancion = getTableView().getItems().get(getIndex());
                    // Cargar imagen por defecto o del álbum
                    imageView.setImage(loadDefaultAlbumImage());
                    label.setText(titulo);
                    label.setStyle("-fx-text-fill: white;");
                    setGraphic(contenedor);
                }
            }
        });

        colArtista.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getArtista()));
        colFechaAgregacion.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFechaAgregacion()));
        colAnoCancion.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getAnoCancion()));
        colDuracion.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getDuracion()));

        // Configurar columna de acciones con botón de agregar
        colAcciones.setCellFactory(col -> new TableCell<CancionData, Void>() {
            private final Button btnAgregar = new Button();

            {
                btnAgregar.setGraphic(loadAddIcon());
                btnAgregar.setStyle("-fx-background-color: #4CAF50; -fx-background-radius: 50%; -fx-min-width: 30px; -fx-min-height: 30px; -fx-max-width: 30px; -fx-max-height: 30px;");

                btnAgregar.setOnAction(event -> {
                    CancionData cancion = getTableView().getItems().get(getIndex());
                    System.out.println("Agregando canción: " + cancion.getTitulo());
                    // Aquí iría la lógica para agregar la canción
                });
            }

            private final HBox contenedor = new HBox(10, btnAgregar);
            {
                contenedor.setPadding(new Insets(5));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : contenedor);
            }
        });

        // Estilo para las celdas
        tableCanciones.setRowFactory(tv -> {
            TableRow<CancionData> row = new TableRow<>();
            row.setStyle("-fx-background-color: #1B1A55; -fx-text-fill: white;");
            return row;
        });
    }

    private void configurarBusqueda() {
        txtBuscarCancion.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarCanciones(newValue);
        });
    }

    private void filtrarCanciones(String filtro) {
        if (filtro == null || filtro.isBlank()) {
            tableCanciones.setItems(listaObservable);
            return;
        }

        ObservableList<CancionData> filtrados = FXCollections.observableArrayList();
        for (CancionData cancion : listaObservable) {
            if (cancion.getTitulo().toLowerCase().contains(filtro.toLowerCase()) ||
                    cancion.getArtista().toLowerCase().contains(filtro.toLowerCase())) {
                filtrados.add(cancion);
            }
        }

        tableCanciones.setItems(filtrados);
    }

    private void cargarCancionesEjemplo() {
        listaObservable = FXCollections.observableArrayList();

        // Datos de ejemplo basados en la imagen
        listaObservable.add(new CancionData("Dreams - 2004 Remaster", "Fleetwood Mac", "20/06/2025", "2004", "4:18"));
        listaObservable.add(new CancionData("Buddy Holly", "Weezer", "20/06/2025", "1994", "2:39"));
        listaObservable.add(new CancionData("Fake Plastic Trees", "Radiohead", "20/06/2025", "1995", "4:50"));
        listaObservable.add(new CancionData("Starman - 2012 Remaster", "David Bowie", "20/06/2025", "2012", "4:14"));
        listaObservable.add(new CancionData("Yesterday - 2009 Remaster", "The Beatles", "20/06/2025", "2009", "2:06"));
        listaObservable.add(new CancionData("Cornfield Chase", "Hans Zimmer", "20/06/2025", "2014", "2:06"));
        listaObservable.add(new CancionData("That's The Way It Is", "Daniel Lanois", "20/06/2025", "2019", "4:08"));

        tableCanciones.setItems(listaObservable);
    }

    private Image loadDefaultAlbumImage() {
        try {
            return new Image(getClass().getResourceAsStream("/UserInterface/Resources/img/CatalogoArtistas/album-default.png"), 40, 40, true, true);
        } catch (Exception e) {
            System.out.println("Imagen por defecto del álbum no encontrada.");
            return null;
        }
    }

    private ImageView loadAddIcon() {
        try {
            return new ImageView(new Image(getClass().getResourceAsStream("/UserInterface/Resources/img/CatalogoArtistas/add-icon.png"), 16, 16, true, true));
        } catch (Exception e) {
            // Crear un símbolo + simple
            Label plusLabel = new Label("+");
            plusLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
            return new ImageView();
        }
    }

    // Clase interna para manejar los datos de las canciones
    public static class CancionData {
        private String titulo;
        private String artista;
        private String fechaAgregacion;
        private String anoCancion;
        private String duracion;

        public CancionData(String titulo, String artista, String fechaAgregacion, String anoCancion, String duracion) {
            this.titulo = titulo;
            this.artista = artista;
            this.fechaAgregacion = fechaAgregacion;
            this.anoCancion = anoCancion;
            this.duracion = duracion;
        }

        // Getters
        public String getTitulo() { return titulo; }
        public String getArtista() { return artista; }
        public String getFechaAgregacion() { return fechaAgregacion; }
        public String getAnoCancion() { return anoCancion; }
        public String getDuracion() { return duracion; }
    }
}
