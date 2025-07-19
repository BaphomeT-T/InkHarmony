package BusinessLogic;

import DataAccessComponent.DTO.CatalogoArtistas.ArtistaDTO;

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

    public boolean validarCampos(ArtistaDTO artista) {
        // Implementar validacion de campos
        // Agregar
        return true; // Temporal
    }

    public boolean esNombreUnico(String nombre) {
//        // Implementacion para verificar si el nombre es unico
//        /*Aqui ponemos la consulta SQL que se va a realizar para buscar el
//         * nombre en la base de datos
//         */
//        String sql = "SELECT COUNT(*) FROM Artista WHERE nombre = ?";
//
//        try (Connection conexion = ConexionBD.getConexion();
//             PreparedStatement stmt = conexion.prepareStatement(sql)) {
//            stmt.setString(1, nombre);
//            ResultSet rs = stmt.executeQuery();
//            if (rs.next()) {
//                return rs.getInt(1) == 0; // true si no existe
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        return false;
    }

    //implementando todos los metodos de la interfaz
    @Override
    public boolean tieneElementosAsociados(ArtistaDTO artista) {
//        int id = artista.getId();
//
//        String sqlCanciones = "SELECT COUNT(*) FROM Cancion WHERE artista_id = ?";
//
//        try (Connection conexion = ConexionBD.getConexion()) {
//            // Verifica canciones asociadas
//            try (PreparedStatement stmt1 = conexion.prepareStatement(sqlCanciones)) {
//                stmt1.setInt(1, id);
//                ResultSet rs1 = stmt1.executeQuery();
//                if (rs1.next() && rs1.getInt(1) > 0) {
//                    return true;
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

        return false;
    }

}
