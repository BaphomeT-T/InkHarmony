package DataAccessComponent.DAO;

import DataAccessComponent.DTO.ArtistaDTO;
import DataAccessComponent.SQLiteDataHelper;
import BusinessLogic.Genero;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO que gestiona operaciones CRUD para la entidad Artista.
 */
public class ArtistaDAO extends SQLiteDataHelper implements IDAO<ArtistaDTO> {

    @Override
    public boolean registrar(ArtistaDTO artista) throws Exception {
        String sqlInsert = "INSERT INTO Artista(nombre, biografia, imagen) VALUES (?, ?, ?)";
        try (Connection conn = openConnection();
             PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, artista.getNombre());
            ps.setString(2, artista.getBiografia());
            ps.setBytes(3, artista.getImagen());
            ps.executeUpdate();

            // Obtener ID generado
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int idGenerado = rs.getInt(1);
                artista.setId(idGenerado);

                // Insertar relación con géneros
                String sqlGenero = "INSERT INTO Artista_Genero(id_artista, id_genero) VALUES (?, ?)";
                PreparedStatement psg = conn.prepareStatement(sqlGenero);
                for (Genero genero : artista.getGenero()) {
                    psg.setInt(1, idGenerado);
                    psg.setInt(2, genero.ordinal() + 1); // ordinal + 1 → coincide con ID en la tabla
                    psg.addBatch();
                }
                psg.executeBatch();
            }
            return true;
        } catch (SQLException e) {
            throw new Exception("Error al registrar artista: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ArtistaDTO> buscarTodo() throws Exception {
        List<ArtistaDTO> lista = new ArrayList<>();
        String sql = "SELECT id_artista, nombre, biografia, imagen FROM Artista";
        try (Connection conn = openConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ArtistaDTO artista = new ArtistaDTO();
                artista.setId(rs.getInt("id_artista"));
                artista.setNombre(rs.getString("nombre"));
                artista.setBiografia(rs.getString("biografia"));
                artista.setImagen(rs.getBytes("imagen"));
                artista.setGeneros(getGenerosPorArtista(artista.getId()));
                lista.add(artista);
            }
            return lista;
        } catch (SQLException e) {
            throw new Exception("Error al obtener artistas: " + e.getMessage(), e);
        }
    }

    @Override
    public ArtistaDTO buscarPorId(Integer id) throws Exception {
        String sql = "SELECT id_artista, nombre, biografia, imagen FROM Artista WHERE id_artista = ?";
        try (Connection conn = openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ArtistaDTO artista = new ArtistaDTO();
                artista.setId(rs.getInt("id_artista"));
                artista.setNombre(rs.getString("nombre"));
                artista.setBiografia(rs.getString("biografia"));
                artista.setImagen(rs.getBytes("imagen"));
                artista.setGeneros(getGenerosPorArtista(id));
                return artista;
            }
            return null;
        } catch (SQLException e) {
            throw new Exception("Error al buscar artista: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean actualizar(ArtistaDTO artista) throws Exception {
        String sqlUpdate = "UPDATE Artista SET nombre = ?, biografia = ?, imagen = ? WHERE id_artista = ?";
        try (Connection conn = openConnection();
             PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {

            ps.setString(1, artista.getNombre());
            ps.setString(2, artista.getBiografia());
            ps.setBytes(3, artista.getImagen());
            ps.setInt(4, artista.getId());
            ps.executeUpdate();

            // Actualizar géneros (borrar y volver a insertar)
            String deleteSQL = "DELETE FROM Artista_Genero WHERE id_artista = ?";
            PreparedStatement del = conn.prepareStatement(deleteSQL);
            del.setInt(1, artista.getId());
            del.executeUpdate();

            String insertSQL = "INSERT INTO Artista_Genero(id_artista, id_genero) VALUES (?, ?)";
            PreparedStatement insert = conn.prepareStatement(insertSQL);
            for (Genero genero : artista.getGenero()) {
                insert.setInt(1, artista.getId());
                insert.setInt(2, genero.ordinal() + 1);
                insert.addBatch();
            }
            insert.executeBatch();

            return true;
        } catch (SQLException e) {
            throw new Exception("Error al actualizar artista: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(Integer id) throws Exception {
        String sqlDeleteGenero = "DELETE FROM Artista_Genero WHERE id_artista = ?";
        String sqlDeleteArtista = "DELETE FROM Artista WHERE id_artista = ?";
        try (Connection conn = openConnection()) {
            PreparedStatement ps1 = conn.prepareStatement(sqlDeleteGenero);
            ps1.setInt(1, id);
            ps1.executeUpdate();

            PreparedStatement ps2 = conn.prepareStatement(sqlDeleteArtista);
            ps2.setInt(1, id);
            ps2.executeUpdate();

            return true;
        } catch (SQLException e) {
            throw new Exception("Error al eliminar artista: " + e.getMessage(), e);
        }
    }

    /**
     * Consulta los géneros musicales asociados a un artista desde la tabla puente.
     */
    private List<Genero> getGenerosPorArtista(int idArtista) throws Exception {
        List<Genero> generos = new ArrayList<>();
        String sql = "SELECT id_genero FROM Artista_Genero WHERE id_artista = ?";
        try (Connection conn = openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idArtista);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int idGenero = rs.getInt("id_genero");
                if (idGenero >= 1 && idGenero <= Genero.values().length) {
                    generos.add(Genero.values()[idGenero - 1]); // ordinal + 1
                }
            }
        }
        return generos;
    }
}
