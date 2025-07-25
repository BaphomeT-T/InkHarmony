/*
|-----------------------------------------------|
| © 2025 EPN-FIS, Todos los derechos reservados |
| GR1SW                                         |
|-----------------------------------------------|
Autores: Samira Arízaga, Paul Dávila, Sebastián Ramos
Descripción: Objeto de transferencia de datos (DTO) que representa una canción dentro del sistema InkHarmony.
*/

package DataAccessComponent.DAO;

import DataAccessComponent.DTO.CancionDTO;
import DataAccessComponent.DTO.ArtistaDTO;
import BusinessLogic.Genero;
import DataAccessComponent.SQLiteDataHelper;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase CancionDAO que implementa operaciones CRUD sobre la entidad Cancion.
 * Se comunica con la base de datos SQLite y transforma resultados en objetos CancionDTO.
 *
 * Esta clase gestiona canciones, su metadata (título, duración, año), archivos binarios (MP3 y portada),
 * así como sus relaciones con artistas y géneros musicales.</p>
 *
 * @author Grupo A
 * @version 1.0
 * @since 18-07-2025
 */
public class CancionDAO extends SQLiteDataHelper implements IDAO<CancionDTO> {

    /**
     * Inserta una nueva canción en la base de datos, incluyendo su relación con artistas y géneros.
     * El ID generado por la base de datos se asigna automáticamente al objeto DTO.
     *
     * @param cancion DTO que contiene los datos de la canción a registrar.
     * @return true si la operación fue exitosa.
     */
    @Override
    public boolean registrar(CancionDTO cancion) throws Exception {
        String query = "INSERT INTO Cancion(titulo, archivo_mp3, duracion, anio, portada, fecha_registro) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = openConnection();
            PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, cancion.getTitulo());
            ps.setBytes(2, cancion.getArchivoMP3());
            ps.setDouble(3, cancion.getDuracion());
            ps.setInt(4, cancion.getAnio());
            ps.setBytes(5, cancion.getPortada());
            ps.setString(6, cancion.getFechaRegistro().toString());
            ps.executeUpdate();

            // Recupera el ID generado automáticamente
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int idGenerado = rs.getInt(1);
                cancion.setIdCancion(idGenerado);

                // Inserta relaciones con artistas
                for (ArtistaDTO artista : cancion.getArtistas()) {
                    String insertArtista = "INSERT INTO Cancion_Artista(id_cancion, id_artista) VALUES (?, ?)";
                    PreparedStatement psa = conn.prepareStatement(insertArtista);
                    psa.setInt(1, idGenerado);
                    psa.setInt(2, artista.getIdArtista());
                    psa.executeUpdate();
                }

                // Inserta relaciones con géneros (usando ordinal del enum + 1)
                for (Genero genero : cancion.getGeneros()) {
                    String insertGenero = "INSERT INTO Cancion_Genero(id_cancion, id_genero) VALUES (?, ?)";
                    PreparedStatement psg = conn.prepareStatement(insertGenero);
                    psg.setInt(1, idGenerado);
                    psg.setInt(2, genero.ordinal() + 1); // Se asume que el ID en BD coincide con el orden del enum
                    psg.executeUpdate();
                }
            }

            return true;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Recupera todas las canciones registradas en la base de datos.
     * Incluye la metadata y las relaciones con artistas y géneros.
     *
     * @return Lista de canciones.
     */
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
                cancion.setFechaRegistro(LocalDateTime.parse(rs.getString("fecha_registro")));
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

    /**
     * Recupera una canción específica según su ID.
     *
     * @param id ID de la canción.
     * @return Objeto CancionDTO completo.
     */
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
                cancion.setFechaRegistro(LocalDateTime.parse(rs.getString("fecha_registro")));
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

    /**
     * Busca canciones por coincidencia exacta de su título.
     *
     * @param nombre Título de la canción a buscar.
     * @return Lista de canciones con ese título.
     */
    public List<CancionDTO> buscarPorNombre(String nombre) throws Exception {
        List<CancionDTO> lista = new ArrayList<>();
        String query = "SELECT id_cancion, titulo, duracion, anio, fecha_registro, archivo_mp3, portada FROM Cancion WHERE titulo = ?";

        try (Connection conn = openConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                CancionDTO cancion = new CancionDTO();
                cancion.setIdCancion(rs.getInt("id_cancion"));
                cancion.setTitulo(rs.getString("titulo"));
                cancion.setDuracion(rs.getDouble("duracion"));
                cancion.setAnio(rs.getInt("anio"));
                cancion.setFechaRegistro(LocalDateTime.parse(rs.getString("fecha_registro")));
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

    /**
     * Actualiza los atributos básicos de una canción (también actualiza artistas y géneros).
     *
     * @param entity Objeto DTO con los nuevos valores.
     * @return true si se actualizó correctamente.
     */
    @Override
    public boolean actualizar(CancionDTO entity) throws Exception {
        StringBuilder queryBuilder = new StringBuilder("UPDATE Cancion SET titulo = ?, anio = ?");
        List<Object> parametros = new ArrayList<>();
        parametros.add(entity.getTitulo());
        parametros.add(entity.getAnio());

        // Agregar campos binarios solo si el usuario los proporcionó
        if (entity.getArchivoMP3() != null) {
            queryBuilder.append(", archivo_mp3 = ?");
            parametros.add(entity.getArchivoMP3());
        }
        if (entity.getPortada() != null) {
            queryBuilder.append(", portada = ?");
            parametros.add(entity.getPortada());
        }

        queryBuilder.append(" WHERE id_cancion = ?");
        parametros.add(entity.getIdCancion());

        try (Connection conn = openConnection()) {
            PreparedStatement ps = conn.prepareStatement(queryBuilder.toString());

            // Asignar parámetros dinámicamente
            for (int i = 0; i < parametros.size(); i++) {
                Object valor = parametros.get(i);
                if (valor instanceof String) {
                    ps.setString(i + 1, (String) valor);
                } else if (valor instanceof Integer) {
                    ps.setInt(i + 1, (Integer) valor);
                } else if (valor instanceof byte[]) {
                    ps.setBytes(i + 1, (byte[]) valor);
                }
            }

            ps.executeUpdate();

            //Actualizar artistas
            String eliminarArtistas = "DELETE FROM Cancion_Artista WHERE id_cancion = ?";
            try (PreparedStatement psDelete = conn.prepareStatement(eliminarArtistas)) {
                psDelete.setInt(1, entity.getIdCancion());
                psDelete.executeUpdate();
            }

            for (ArtistaDTO artista : entity.getArtistas()) {
                String insertarArtista = "INSERT INTO Cancion_Artista(id_cancion, id_artista) VALUES (?, ?)";
                try (PreparedStatement psInsert = conn.prepareStatement(insertarArtista)) {
                    psInsert.setInt(1, entity.getIdCancion());
                    psInsert.setInt(2, artista.getIdArtista());
                    psInsert.executeUpdate();
                }
            }

            //  Actualizar géneros
            String eliminarGeneros = "DELETE FROM Cancion_Genero WHERE id_cancion = ?";
            try (PreparedStatement psDelete = conn.prepareStatement(eliminarGeneros)) {
                psDelete.setInt(1, entity.getIdCancion());
                psDelete.executeUpdate();
            }

            for (Genero genero : entity.getGeneros()) {
                String insertarGenero = "INSERT INTO Cancion_Genero(id_cancion, id_genero) VALUES (?, ?)";
                try (PreparedStatement psInsert = conn.prepareStatement(insertarGenero)) {
                    psInsert.setInt(1, entity.getIdCancion());
                    psInsert.setInt(2, genero.ordinal() + 1); // Se asume ID = ordinal + 1
                    psInsert.executeUpdate();
                }
            }

            return true;
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * Elimina una canción de la base de datos según su ID.
     *
     * @param id ID de la canción a eliminar.
     * @return true si se eliminó correctamente.
     */
    @Override
    public boolean eliminar(Integer id) throws Exception {
        String query = "DELETE FROM Cancion WHERE id_cancion = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = openConnection(); // que abra una conexión nueva
            ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } finally {
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        }
    }


    /**
     * Recupera los artistas asociados a una canción específica.
     *
     * @param idCancion ID de la canción.
     * @return Lista de artistas relacionados.
     */
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

    /**
     * Recupera los géneros asociados a una canción.
     * Se espera que el nombre en la tabla Género coincida con los valores del Enum Genero.
     *
     * @param idCancion ID de la canción.
     * @return Lista de géneros como enums.
     */
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
    public boolean existeCancionConTitulo(String titulo) {
        String sql = "SELECT COUNT(*) FROM Cancion WHERE Titulo = ? AND Estado = 'A'";

        try (Connection conn = openConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, titulo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
