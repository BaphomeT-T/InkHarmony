package DataAccessComponent.DAO;

import DataAccessComponent.DTO.CancionDTO;
import DataAccessComponent.DTO.ArtistaDTO;
import DataAccessComponent.DTO.Genero;
import DataAccessComponent.SQLiteDataHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CancionDAO extends SQLiteDataHelper implements IDAO<CancionDTO> {

    @Override
    public boolean registrar(CancionDTO cancion) throws Exception {
        String query = "INSERT INTO Cancion(titulo, archivo_mp3, duracion, anio, portada) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = openConnection();
            PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, cancion.getTitulo());
            ps.setBytes(2, cancion.getArchivoMP3());
            ps.setDouble(3, cancion.getDuracion());
            ps.setInt(4, cancion.getAnio());
            ps.setBytes(5, cancion.getPortada());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int idGenerado = rs.getInt(1);
                cancion.setIdCancion(idGenerado);

                for (ArtistaDTO artista : cancion.getArtistas()) {
                    String insertArtista = "INSERT INTO Cancion_Artista(id_cancion, id_artista) VALUES (?, ?)";
                    PreparedStatement psa = conn.prepareStatement(insertArtista);
                    psa.setInt(1, idGenerado);
                    psa.setInt(2, artista.getIdArtista());
                    psa.executeUpdate();
                }

                for (Genero genero : cancion.getGeneros()) {
                    String insertGenero = "INSERT INTO Cancion_Genero(id_cancion, id_genero) VALUES (?, ?)";
                    PreparedStatement psg = conn.prepareStatement(insertGenero);
                    psg.setInt(1, idGenerado);
                    psg.setInt(2, genero.ordinal() + 1);
                    psg.executeUpdate();
                }
            }

            return true;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<CancionDTO> buscarTodo() throws Exception {
        List<CancionDTO> lista = new ArrayList<>();
        String query = "SELECT id_cancion, titulo, duracion, anio, fecha_registro, archivo_mp3, portada FROM Cancion";
        try {
            Connection conn = openConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                CancionDTO cancion = new CancionDTO();
                cancion.setIdCancion(rs.getInt("id_cancion"));
                cancion.setTitulo(rs.getString("titulo"));
                cancion.setDuracion(rs.getDouble("duracion"));
                cancion.setAnio(rs.getInt("anio"));
                cancion.setFechaRegistro(rs.getString("fecha_registro"));
                cancion.setArchivoMP3(rs.getBytes("archivo_mp3"));
                cancion.setPortada(rs.getBytes("portada"));
                cancion.setArtistas(getArtistasPorCancion(cancion.getIdCancion()));
                cancion.setGeneros(getGenerosPorCancion(cancion.getIdCancion()));
                lista.add(cancion);
            }
        } catch (Exception e) {
            throw e;
        }
        return lista;
    }

    @Override
    public CancionDTO buscarPorId(Integer id) throws Exception {
        CancionDTO cancion = new CancionDTO();
        String query = "SELECT id_cancion, titulo, duracion, anio, fecha_registro, archivo_mp3, portada FROM Cancion WHERE id_cancion = ?";
        try {
            Connection conn = openConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                cancion.setIdCancion(rs.getInt("id_cancion"));
                cancion.setTitulo(rs.getString("titulo"));
                cancion.setDuracion(rs.getDouble("duracion"));
                cancion.setAnio(rs.getInt("anio"));
                cancion.setFechaRegistro(rs.getString("fecha_registro"));
                cancion.setArchivoMP3(rs.getBytes("archivo_mp3"));
                cancion.setPortada(rs.getBytes("portada"));
                cancion.setArtistas(getArtistasPorCancion(id));
                cancion.setGeneros(getGenerosPorCancion(id));
            }
        } catch (Exception e) {
            throw e;
        }
        return cancion;
    }


    public List<CancionDTO> buscarPorNombre(Integer id) throws Exception {
        return new ArrayList<>(); // Método de relleno por si no se usa todavía
    }

    @Override
    public boolean actualizar(CancionDTO entity) throws Exception {
        String query = "UPDATE Cancion SET titulo = ?, archivo_mp3 = ?, duracion = ?, anio = ?, portada = ? WHERE id_cancion = ?";
        try {
            Connection conn = openConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, entity.getTitulo());
            ps.setBytes(2, entity.getArchivoMP3());
            ps.setDouble(3, entity.getDuracion());
            ps.setInt(4, entity.getAnio());
            ps.setBytes(5, entity.getPortada());
            ps.setInt(6, entity.getIdCancion());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public boolean eliminar(Integer id) throws Exception {
        String query = "DELETE FROM Cancion WHERE id_cancion = ?";
        try {
            Connection conn = openConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            throw e;
        }
    }

    private List<ArtistaDTO> getArtistasPorCancion(int idCancion) throws Exception {
        List<ArtistaDTO> lista = new ArrayList<>();
        String query = "SELECT a.id_artista, a.nombre FROM Artista a " +
                "JOIN Cancion_Artista ca ON a.id_artista = ca.id_artista " +
                "WHERE ca.id_cancion = ?";
        Connection conn = openConnection();
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, idCancion);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            ArtistaDTO artista = new ArtistaDTO();
            artista.setIdArtista(rs.getInt("id_artista"));
            artista.setNombre(rs.getString("nombre"));
            lista.add(artista);
        }
        return lista;
    }

    private List<Genero> getGenerosPorCancion(int idCancion) throws Exception {
        List<Genero> lista = new ArrayList<>();
        String query = "SELECT g.nombre_genero FROM Genero g " +
                "JOIN Cancion_Genero cg ON g.id_genero = cg.id_genero " +
                "WHERE cg.id_cancion = ?";
        Connection conn = openConnection();
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, idCancion);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            String nombre = rs.getString("nombre_genero");
            lista.add(Genero.valueOf(nombre));
        }
        return lista;
    }
}
