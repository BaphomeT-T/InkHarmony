package UserInterface.CustomerControl.CatalogoArtistas;

import BusinessLogic.Artista;
import DataAccessComponent.DTO.ArtistaDTO;
import BusinessLogic.Genero;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CatalogoArtistasController {

    @FXML
    private TableView<ArtistaDTO> tableArtistas;

    @FXML
    private TableColumn<ArtistaDTO, String> colGenero;

    @FXML
    private TableColumn<ArtistaDTO, String> colBiografia;

    @FXML
    private TableColumn<ArtistaDTO, Void> colAcciones;

    @FXML
    private TextField txtBuscar;

    private Artista artistaBL = new Artista();
    private ObservableList<ArtistaDTO> listaObservable;

    @FXML
    private TableColumn<ArtistaDTO, ArtistaDTO> colNombreConImagen;

    @FXML
    public void initialize() {
        try {
            configurarTabla();
            cargarArtistas();
            configurarBusqueda();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarArtistas(newValue);
        });
    }

    private void filtrarArtistas(String filtro) {
        if (filtro == null || filtro.isBlank()) {
            tableArtistas.setItems(listaObservable);
            agregarColumnaAcciones();
            return;
        }

        ObservableList<ArtistaDTO> filtrados = FXCollections.observableArrayList(
                listaObservable.stream()
                        .filter(artista -> artista.getNombre().toLowerCase().contains(filtro.toLowerCase()))
                        .collect(Collectors.toList())
        );

        tableArtistas.setItems(filtrados);
        agregarColumnaAcciones();
    }


    private void configurarTabla() {
        colGenero.setCellValueFactory(cellData -> {
            List<Genero> generos = cellData.getValue().getGenero();
            String texto = generos.stream().map(Enum::name).collect(Collectors.joining(", "));
            return SimpleStringProperty.stringExpression(
                    Bindings.createStringBinding(() -> texto));
        });

        colBiografia.setCellValueFactory(new PropertyValueFactory<>("biografia"));

        // Artista + imagen
        colNombreConImagen.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));
        colNombreConImagen.setCellFactory(column -> new TableCell<>() {
            private final HBox contenedor = new HBox(10);
            private final ImageView imageView = new ImageView();
            private final Label label = new Label();

            {
                imageView.setFitHeight(40);
                imageView.setFitWidth(40);
                contenedor.getChildren().addAll(imageView, label);
            }

            @Override
            protected void updateItem(ArtistaDTO artista, boolean empty) {
                super.updateItem(artista, empty);
                if (empty || artista == null) {
                    setGraphic(null);
                } else {
                    if (artista.getImagen() != null) {
                        try {
                            imageView.setImage(new Image(new ByteArrayInputStream(artista.getImagen())));
                        } catch (Exception e) {
                            imageView.setImage(loadDefaultImage());
                        }
                    } else {
                        imageView.setImage(loadDefaultImage());
                    }

                    label.setText(artista.getNombre());
                    setGraphic(contenedor);
                }
            }
        });

        agregarColumnaAcciones();
    }

    private Image loadDefaultImage() {
        try {
            return new Image(getClass().getResourceAsStream("/UserInterface/Resources/img/CatalogoArtistas/usuario.png"), 40, 40, true, true);
        } catch (Exception e) {
            System.out.println("Imagen por defecto no encontrada.");
            return null;
        }
    }

    private void cargarArtistas() throws Exception {
        List<ArtistaDTO> lista = artistaBL.buscarTodo();
        System.out.println("Artistas cargados desde DAO:");
        lista.forEach(System.out::println);
        listaObservable = FXCollections.observableArrayList(lista);
        tableArtistas.setItems(listaObservable);
    }

    private void agregarColumnaAcciones() {
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar = new Button();
            private final Button btnEliminar = new Button();

            {
                btnEditar.setGraphic(loadIcon("/UserInterface/Resources/img/CatalogoArtistas/tuerca.png"));
                btnEliminar.setGraphic(loadIcon("/UserInterface/Resources/img/CatalogoArtistas/eliminar.png"));

                btnEditar.setStyle("-fx-background-color: transparent;");
                btnEliminar.setStyle("-fx-background-color: transparent;");

                btnEditar.setOnAction(event -> {
                    ArtistaDTO artista = getTableView().getItems().get(getIndex());
                    irAPantallaEditarArtista(artista);
                });

                btnEliminar.setOnAction(event -> {
                    ArtistaDTO artista = getTableView().getItems().get(getIndex());
                    irAPantallaEliminarArtista(artista);
                });
            }

            private final HBox contenedor = new HBox(10, btnEditar, btnEliminar);
            {
                contenedor.setPadding(new Insets(5));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : contenedor);
            }
        });
    }

    private ImageView loadIcon(String path) {
        try {
            return new ImageView(new Image(getClass().getResourceAsStream(path), 18, 18, true, true));
        } catch (Exception e) {
            System.out.println("Imagen no encontrada: " + path);
            return new ImageView();
        }
    }

    private void irAPantallaSubirArtista() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/CatalogoArtistas/PantallaSubirArtista.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Subir Artista");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePantallaSubirArtista() {
        irAPantallaSubirArtista();
    }

    private void irAPantallaEliminarArtista(ArtistaDTO artista) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/CatalogoArtistas/PantallaEliminarArtista.fxml"));
            Parent root = loader.load();

            EliminarArtistasController controller = loader.getController();
            controller.setArtista(artista);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Eliminar Artista");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void irAPantallaEditarArtista(ArtistaDTO artistaSeleccionado) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/GUI/CatalogoArtistas/PantallaEditarArtista.fxml"));
            Parent root = loader.load();

            EditarArtistasController controller = loader.getController();
            controller.setArtista(artistaSeleccionado);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Editar Artista");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

