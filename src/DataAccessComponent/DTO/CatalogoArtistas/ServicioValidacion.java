package DataAccessComponent.DTO.CatalogoArtistas;

import DataAccessComponent.DAO.CatalogoArtistas.ConexionBD;
import java.util.HashSet;
import java.util.Set;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServicioValidacion implements UnicoNombreValidable, AsociacionValidable {

    // Simulación: artistas con canciones y playlists
    private Set<Integer> artistasConCanciones = new HashSet<>();
    private Set<Integer> artistasEnPlaylist = new HashSet<>();

    public ServicioValidacion() {
        // Simula que los artistas con ID 1 y 2 tienen asociaciones
        artistasConCanciones.add(1);
        artistasEnPlaylist.add(2);
    }

    public boolean validarCampos(Artista artista) {
        // Implementar validacion de campos
        // Agregar
        return true; // Temporal
    }

    public boolean esNombreUnico(String nombre) {
        // Implementacion para verificar si el nombre es unico
        /*Aqui ponemos la consulta SQL que se va a realizar para buscar el
         * nombre en la base de datos
         */
        String sql = "SELECT COUNT(*) FROM Artista WHERE nombre = ?";

        try (Connection conexion = ConexionBD.getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0; // true si no existe
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //implementando todos los metodos de la interfaz
    @Override
    public boolean tieneElementosAsociados(Artista artista) {
        int id = artista.getId();
        return artistasConCanciones.contains(id) || artistasEnPlaylist.contains(id);
    }

}
