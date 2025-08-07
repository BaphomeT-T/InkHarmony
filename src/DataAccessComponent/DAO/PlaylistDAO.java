/*
|-----------------------------------------------|
| © 2025 EPN-FIS, Todos los derechos reservados |
| GR1SW                                         |
|-----------------------------------------------|
Autores: Grupo C
DAO para gestionar operaciones CRUD de playlists en la base de datos.
*/
package DataAccessComponent.DAO;

import DataAccessComponent.DTO.PlaylistDTO;
import DataAccessComponent.DTO.CancionDTO;
import DataAccessComponent.SQLiteDataHelper;
import BusinessLogic.ServicioValidacionPlaylist;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase PlaylistDAO que implementa operaciones CRUD sobre la entidad Playlist.
 * Se comunica con la base de datos SQLite y transforma resultados en objetos PlaylistDTO.
 *
 * @author Grupo C
 * @version 1.0
 * @since 31-07-2025
 */
public class PlaylistDAO extends SQLiteDataHelper implements IDAO<PlaylistDTO> {

    private ServicioValidacionPlaylist validador = new ServicioValidacionPlaylist();

    /**
     * Inserta una nueva playlist en la base de datos.
     * El ID generado por la base de datos se asigna automáticamente al objeto DTO.
     *
     * @param playlist DTO que contiene los datos de la playlist a registrar.
     * @return true si la operación fue exitosa.
     */
    @Override
    public boolean registrar(PlaylistDTO playlist) throws Exception {
        // Validar datos antes de insertar
        if (!validador.validarDatosCompletos(playlist)) {
            throw new Exception("Datos de playlist incompletos o inválidos");
        }

        String insertPlaylist = "INSERT INTO Playlist(titulo, descripcion, id_propietario, imagen_portada, fecha_creacion) VALUES (?, ?, ?, ?, ?)";
        String insertCancionPlaylist = "INSERT INTO playlist_elementos(id_playlist, id_cancion, orden) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = openConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            // Insertar playlist
            PreparedStatement psPlaylist = conn.prepareStatement(insertPlaylist, Statement.RETURN_GENERATED_KEYS);
            psPlaylist.setString(1, playlist.getTituloPlaylist());
            psPlaylist.setString(2, playlist.getDescripcion());
            psPlaylist.setInt(3, playlist.getIdPropietario());
            psPlaylist.setBytes(4, playlist.getImagenPortada());
            psPlaylist.setString(5, playlist.getFechaCreacion().toString());

            psPlaylist.executeUpdate();

            // Obtener ID generado
            ResultSet rs = psPlaylist.getGeneratedKeys();
            if (rs.next()) {
                int idPlaylist = rs.getInt(1);
                playlist.setIdPlaylist(idPlaylist);

                // Insertar canciones de la playlist
                if (playlist.getCancionesIds() != null && !playlist.getCancionesIds().isEmpty()) {
                    PreparedStatement psCancion = conn.prepareStatement(insertCancionPlaylist);
                    int orden = 1;

                    for (Integer idCancion : playlist.getCancionesIds()) {
                        psCancion.setInt(1, idPlaylist);
                        psCancion.setInt(2, idCancion);
                        psCancion.setInt(3, orden++);
                        psCancion.executeUpdate();
                    }
                }
            }

            conn.commit(); // Confirmar transacción
            return true;

        } catch (Exception e) {
            if (conn != null) {
                conn.rollback(); // Revertir cambios si hay error
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Recupera todas las playlist registradas en la base de datos.
     *
     * @return Lista de playlists.
     */
    @Override
    public List<PlaylistDTO> buscarTodo() throws Exception {
        List<PlaylistDTO> playlists = new ArrayList<>();
        String query = "SELECT id_playlist, titulo, descripcion, id_propietario, imagen_portada, fecha_creacion FROM Playlist";

        Connection conn = openConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            PlaylistDTO playlist = new PlaylistDTO();
            playlist.setIdPlaylist(rs.getInt("id_playlist"));
            playlist.setTituloPlaylist(rs.getString("titulo"));
            playlist.setDescripcion(rs.getString("descripcion"));
            playlist.setIdPropietario(rs.getInt("id_propietario"));
            playlist.setImagenPortada(rs.getBytes("imagen_portada"));
            playlist.setFechaCreacion(LocalDateTime.parse(rs.getString("fecha_creacion")));

            // Obtener IDs de canciones
            playlist.setCancionesIds(obtenerCancionesDePlaylist(playlist.getIdPlaylist()));

            playlists.add(playlist);
        }

        return playlists;
    }

    /**
     * Recupera una playlist específica según su ID.
     *
     * @param id ID de la playlist.
     * @return Objeto PlaylistDTO completo.
     */
    @Override
    public PlaylistDTO buscarPorId(Integer id) throws Exception {
        String query = "SELECT id_playlist, titulo, descripcion, id_propietario, imagen_portada, fecha_creacion FROM Playlist WHERE id_playlist = ?";

        Connection conn = openConnection();
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            PlaylistDTO playlist = new PlaylistDTO();
            playlist.setIdPlaylist(rs.getInt("id_playlist"));
            playlist.setTituloPlaylist(rs.getString("titulo"));
            playlist.setDescripcion(rs.getString("descripcion"));
            playlist.setIdPropietario(rs.getInt("id_propietario"));
            playlist.setImagenPortada(rs.getBytes("imagen_portada"));
            playlist.setFechaCreacion(LocalDateTime.parse(rs.getString("fecha_creacion")));

            // Obtener IDs de canciones
            playlist.setCancionesIds(obtenerCancionesDePlaylist(id));

            return playlist;
        }

        return null;
    }

    @Override
    public boolean actualizar(PlaylistDTO playlist) throws Exception {
        if (!validador.validarDatosCompletos(playlist)) {
            throw new Exception("Datos de playlist incompletos o inválidos");
        }

        String updatePlaylist = "UPDATE Playlist SET titulo = ?, descripcion = ?, imagen_portada = ? WHERE id_playlist = ?";
        String deleteCancionesPlaylist = "DELETE FROM Playlist_Cancion WHERE id_playlist = ?";
        String insertCancionPlaylist = "INSERT INTO Playlist_Cancion(id_playlist, id_cancion, orden) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = openConnection();
            conn.setAutoCommit(false);

            // Actualizar playlist
            PreparedStatement psUpdate = conn.prepareStatement(updatePlaylist);
            psUpdate.setString(1, playlist.getTituloPlaylist());
            psUpdate.setString(2, playlist.getDescripcion());
            psUpdate.setBytes(3, playlist.getImagenPortada());
            psUpdate.setInt(4, playlist.getIdPlaylist());
            psUpdate.executeUpdate();

            // Eliminar canciones actuales
            PreparedStatement psDelete = conn.prepareStatement(deleteCancionesPlaylist);
            psDelete.setInt(1, playlist.getIdPlaylist());
            psDelete.executeUpdate();

            // Insertar nuevas canciones
            if (playlist.getCancionesIds() != null && !playlist.getCancionesIds().isEmpty()) {
                PreparedStatement psInsert = conn.prepareStatement(insertCancionPlaylist);
                int orden = 1;

                for (Integer idCancion : playlist.getCancionesIds()) {
                    psInsert.setInt(1, playlist.getIdPlaylist());
                    psInsert.setInt(2, idCancion);
                    psInsert.setInt(3, orden++);
                    psInsert.executeUpdate();
                }
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Elimina una playlist de la base de datos según su ID.
     *
     * @param id ID de la playlist a eliminar.
     * @return true si se eliminó correctamente.
     */
    @Override
    public boolean eliminar(Integer id) throws Exception {
        String query = "DELETE FROM Playlist WHERE id_playlist = ?";

        Connection conn = openConnection();
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, id);
        ps.executeUpdate();

        return true;
    }

    /**
     * Busca playlists por nombre.
     */
    public List<PlaylistDTO> buscarPorNombre(String nombre) throws Exception {
        List<PlaylistDTO> playlists = new ArrayList<>();
        String query = "SELECT id_playlist, titulo, descripcion, id_propietario, imagen_portada, fecha_creacion FROM Playlist WHERE titulo LIKE ?";

        Connection conn = openConnection();
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, "%" + nombre + "%");
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            PlaylistDTO playlist = new PlaylistDTO();
            playlist.setIdPlaylist(rs.getInt("id_playlist"));
            playlist.setTituloPlaylist(rs.getString("titulo"));
            playlist.setDescripcion(rs.getString("descripcion"));
            playlist.setIdPropietario(rs.getInt("id_propietario"));
            playlist.setImagenPortada(rs.getBytes("imagen_portada"));
            playlist.setFechaCreacion(LocalDateTime.parse(rs.getString("fecha_creacion")));

            playlist.setCancionesIds(obtenerCancionesDePlaylist(playlist.getIdPlaylist()));

            playlists.add(playlist);
        }

        return playlists;
    }

    /**
     * Obtiene playlists por creador
     */
    public List<PlaylistDTO> obtenerPlaylistPorUsuario(int idUsuario) throws Exception {
        List<PlaylistDTO> playlists = new ArrayList<>();
        String query = "SELECT id_playlist, titulo, descripcion, id_propietario, imagen_portada, fecha_creacion FROM Playlist WHERE id_propietario = ?";

        Connection conn = openConnection();
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, idUsuario);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            PlaylistDTO playlist = new PlaylistDTO();
            playlist.setIdPlaylist(rs.getInt("id_playlist"));
            playlist.setTituloPlaylist(rs.getString("titulo"));
            playlist.setDescripcion(rs.getString("descripcion"));
            playlist.setIdPropietario(rs.getInt("id_propietario"));
            playlist.setImagenPortada(rs.getBytes("imagen_portada"));
            playlist.setFechaCreacion(LocalDateTime.parse(rs.getString("fecha_creacion")));

            playlist.setCancionesIds(obtenerCancionesDePlaylist(playlist.getIdPlaylist()));

            playlists.add(playlist);
        }

        return playlists;
    }

    /**
     * Obtiene los IDs de las canciones de una playlist en orden.
     */
    private List<Integer> obtenerCancionesDePlaylist(int idPlaylist) throws Exception {
        List<Integer> cancionesIds = new ArrayList<>();
        String query = "SELECT id_cancion FROM Playlist_Cancion WHERE id_playlist = ? ORDER BY orden";

        Connection conn = openConnection();
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, idPlaylist);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            cancionesIds.add(rs.getInt("id_cancion"));
        }

        return cancionesIds;
    }

    /**
     * Obtiene los datos completos de las canciones de una playlist.
     */
    public List<CancionDTO> obtenerCancionesCompletasDePlaylist(int idPlaylist) throws Exception {
        List<CancionDTO> canciones = new ArrayList<>();
        String query = """
            SELECT c.id_cancion, c.titulo, c.duracion, c.anio, c.fecha_registro, 
                   c.archivo_mp3, c.portada
            FROM Cancion c 
            JOIN Playlist_Cancion pc ON c.id_cancion = pc.id_cancion 
            WHERE pc.id_playlist = ? 
            ORDER BY pc.orden
        """;

        Connection conn = openConnection();
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, idPlaylist);
        ResultSet rs = ps.executeQuery();

        CancionDAO cancionDAO = new CancionDAO();

        while (rs.next()) {
            int idCancion = rs.getInt("id_cancion");
            CancionDTO cancion = cancionDAO.buscarPorId(idCancion);
            if (cancion != null) {
                canciones.add(cancion);
            }
        }

        return canciones;
    }
}