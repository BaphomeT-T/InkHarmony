package DataAccessComponent.DAO;

import DataAccessComponent.DTO.ArtistaDTO;
import DataAccessComponent.SQLiteDataHelper;
import BusinessLogic.Genero;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArtistaDAO extends SQLiteDataHelper implements IDAO<ArtistaDTO> {

    /**
     *Registra un nuevo artista en la base de datos, incluyendo sus géneros musicales.
     *
     * @param artista Objeto ArtistaDTO a registrar
     * @return true si el registro fue exitoso
     * @throws Exception si ocurre un error al registrar el artista
     */
    @Override
    public boolean registrar(ArtistaDTO artista) throws Exception {
        String sqlInsert = "INSERT INTO Artista(nombre, biografia, imagen) VALUES (?, ?, ?)";
        try {

            Connection conn = openConnection();
            PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, artista.getNombre());
            ps.setString(2, artista.getBiografia());
            ps.setBytes(3, artista.getImagen());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    artista.setId(idGenerado);
                    // Insertar géneros
                        for (Genero genero : artista.getGenero()) {
                            String sqlGenero = "INSERT INTO Artista_Genero(id_artista, id_genero) VALUES (?, ?)";
                            PreparedStatement psg = conn.prepareStatement(sqlGenero);
                            psg.setInt(1, idGenerado);
                            psg.setInt(2, genero.ordinal() + 1);
                            psg.addBatch();
                            psg.executeBatch();
                        }
                }

            return true;
        } catch (Exception e) {
            throw new Exception("Error al registrar artista: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene todos los artistas registrados en la base de datos junto con sus géneros musicales.
     *
     * @return Lista de artistas encontrados
     * @throws Exception si ocurre un error al obtener los artistas
     */
    @Override
    public List<ArtistaDTO> buscarTodo() throws Exception {
        List<ArtistaDTO> lista = new ArrayList<>();
        String sql = "SELECT id_artista, nombre, biografia, imagen FROM Artista";

        try  {
            Connection conn = openConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                ArtistaDTO artista = new ArtistaDTO();
                artista.setId(rs.getInt("id_artista"));
                artista.setNombre(rs.getString("nombre"));
                artista.setBiografia(rs.getString("biografia"));
                artista.setImagen(rs.getBytes("imagen"));
                // Obtener géneros con una nueva conexión
                artista.setGeneros(getGenerosPorArtista(artista.getId()));
                lista.add(artista);
            }
        } catch (Exception e) {
            throw new Exception("Error al obtener artistas: " + e.getMessage(), e);
        }
        return lista;
    }

    /**
     * Busca un artista por su ID.
     *
     * @param id ID del artista a buscar
     * @return Objeto ArtistaDTO correspondiente al ID especificado
     * @throws Exception si ocurre un error durante la búsqueda
     */
    @Override
    public ArtistaDTO buscarPorId(Integer id) throws Exception {
        ArtistaDTO artista = new ArtistaDTO();
        String sql = "SELECT id_artista, nombre, biografia, imagen FROM Artista WHERE id_artista = ?";

        try {
            Connection conn = openConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    artista.setId(rs.getInt("id_artista"));
                    artista.setNombre(rs.getString("nombre"));
                    artista.setBiografia(rs.getString("biografia"));
                    artista.setImagen(rs.getBytes("imagen"));
                    artista.setGeneros(getGenerosPorArtista(id));
                }
        } catch (Exception e) {
            throw new Exception("Error al buscar artista: " + e.getMessage(), e);
        }
        return artista;
    }

    /**
     * Actualiza la información de un artista existente, incluyendo sus géneros asociados.
     *
     * @param artista Objeto ArtistaDTO con los datos actualizados
     * @return true si la actualización fue exitosa
     * @throws Exception si ocurre un error al actualizar
     */
    @Override
    public boolean actualizar(ArtistaDTO artista) throws Exception {
        String sqlUpdate = "UPDATE Artista SET nombre = ?, biografia = ?, imagen = ? WHERE id_artista = ?";
        try {
            Connection conn = openConnection();
            PreparedStatement ps = conn.prepareStatement(sqlUpdate);

            ps.setString(1, artista.getNombre());
            ps.setString(2, artista.getBiografia());
            ps.setBytes(3, artista.getImagen());
            ps.setInt(4, artista.getId());
            ps.executeUpdate();

            // Actualizar géneros
                String eliminarGeneros = "DELETE FROM Artista_Genero WHERE id_artista = ?";
                try (PreparedStatement psDelete = conn.prepareStatement(eliminarGeneros)) {
                psDelete.setInt(1, artista.getId());
                psDelete.executeUpdate();
            }
                for (Genero genero : artista.getGenero()) {
                    PreparedStatement insert = conn.prepareStatement("INSERT INTO Artista_Genero(id_artista, id_genero) VALUES (?, ?)");
                    insert.setInt(1, artista.getId());
                    insert.setInt(2, genero.ordinal() + 1);
                    insert.addBatch();
                    insert.executeBatch();
                }
            return true;
        } catch (Exception e) {
            throw new Exception("Error al actualizar artista: " + e.getMessage(), e);
        }
    }


    /**
     * Elimina un artista y sus asociaciones con géneros musicales.
     *
     * @param id ID del artista a eliminar
     * @return true si la eliminación fue exitosa
     * @throws Exception si ocurre un error al eliminar el artista
     */
    @Override
    public boolean eliminar(Integer id) throws Exception {
        String sqlEliminarGeneros = "DELETE FROM Artista_Genero WHERE id_artista = ?";
        String sqlEliminarArtista = "DELETE FROM Artista WHERE id_artista = ?";

        try {
            Connection conn = openConnection();

            PreparedStatement ps1 = conn.prepareStatement(sqlEliminarGeneros);
            ps1.setInt(1, id);
            ps1.executeUpdate();

            PreparedStatement ps2 = conn.prepareStatement(sqlEliminarArtista);
            ps2.setInt(1, id);
            ps2.executeUpdate();

            return true;

        } catch (Exception e) {
            throw new Exception("Error al eliminar artista: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene los géneros musicales asociados a un artista específico.
     *
     * @param idArtista ID del artista a obtener
     * @return Lista de géneros asociados
     * @throws Exception si ocurre un error al obtener los géneros
     */
    private List<Genero> getGenerosPorArtista(int idArtista) throws Exception {
        List<Genero> generos = new ArrayList<>();
        String sql = "SELECT id_genero FROM Artista_Genero WHERE id_artista = ?";
        Connection conn = openConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, idArtista);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int idGenero = rs.getInt("id_genero");
                    if (idGenero >= 1 && idGenero <= Genero.values().length) {
                        generos.add(Genero.values()[idGenero - 1]);
                    }
                }
        return generos;
    }

    /**
     * Verifica si el artista con el ID especificado tiene canciones asociadas.
     *
     * @param idArtista ID del artista a verificar
     * @return true si existen canciones asociadas al artista, false en caso contrario
     * @throws Exception si ocurre un error durante la consulta
     */
    public boolean tieneCancionesAsociadas(int idArtista) throws Exception {
        String sql = "SELECT COUNT(*) FROM Cancion_Artista WHERE id_artista = ?";

        try {
            Connection conn = openConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idArtista);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;

        } catch (Exception e) {
            throw new Exception("Error al verificar canciones asociadas: " + e.getMessage(), e);
        }
    }


}