package BusinessLogic;

import java.util.*;
import java.sql.*;

public class PlaylistDAO {
    private DatabaseConnection dbConnection;
    private ServicioValidacionPlaylist validador;

    public PlaylistDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.validador = new ServicioValidacionPlaylist();
        // Crear tablas si no existen
        dbConnection.crearTablas();
    }

    public void registrarPlaylist(Playlist playlist) throws SQLException {
        if (!validador.validarDatosCompletos(playlist)) {
            throw new IllegalArgumentException("Los datos de la playlist no son válidos");
        }

        String sql = """
            INSERT INTO playlists (titulo_playlist, descripcion, propietario_correo) 
            VALUES (?, ?, ?)
        """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, playlist.getTitulo());
            pstmt.setString(2, playlist.getDescripcion());
            pstmt.setString(3, playlist.getPropietario().getCorreo());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        playlist.setIdPlaylist(generatedKeys.getInt(1));

                        // Guardar componentes si los hay
                        guardarComponentes(playlist);
                    }
                }
            }
        }
    }

    private void guardarComponentes(Playlist playlist) throws SQLException {
        String sql = """
            INSERT INTO playlist_elementos (id_playlist, id_cancion, orden_elemento) 
            VALUES (?, ?, ?)
        """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int orden = 1;
            for (ComponentePlaylist componente : playlist.getComponentes()) {
                if (componente instanceof ElementoCancion) {
                    ElementoCancion elemento = (ElementoCancion) componente;
                    pstmt.setInt(1, playlist.getIdPlaylist());
                    pstmt.setInt(2, elemento.getCancion().getIdCancion());
                    pstmt.setInt(3, orden++);
                    pstmt.executeUpdate();
                }
            }
        }
    }

    public void actualizarPlaylist(Playlist playlist) throws SQLException {
        if (!validador.validarDatosCompletos(playlist)) {
            throw new IllegalArgumentException("Los datos de la playlist no son válidos");
        }

        String sql = """
            UPDATE playlists 
            SET titulo_playlist = ?, descripcion = ?, fecha_modificacion = CURRENT_TIMESTAMP 
            WHERE id_playlist = ?
        """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playlist.getTitulo());
            pstmt.setString(2, playlist.getDescripcion());
            pstmt.setInt(3, playlist.getIdPlaylist());

            pstmt.executeUpdate();

            // Actualizar componentes
            eliminarComponentes(playlist.getIdPlaylist());
            guardarComponentes(playlist);
        }
    }

    private void eliminarComponentes(int idPlaylist) throws SQLException {
        String sql = "DELETE FROM playlist_elementos WHERE id_playlist = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idPlaylist);
            pstmt.executeUpdate();
        }
    }

    public void eliminarPlaylist(Playlist playlist) throws SQLException {
        String sql = "DELETE FROM playlists WHERE id_playlist = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, playlist.getIdPlaylist());
            pstmt.executeUpdate();
        }
    }

    public List<Playlist> buscarPlaylist() throws SQLException {
        List<Playlist> playlists = new ArrayList<>();
        String sql = "SELECT * FROM playlists ORDER BY fecha_creacion DESC";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Playlist playlist = crearPlaylistDesdeResultSet(rs);
                cargarComponentes(playlist);
                playlists.add(playlist);
            }
        }

        return playlists;
    }

    public Playlist buscarPlaylistPorId(int id) throws SQLException {
        String sql = "SELECT * FROM playlists WHERE id_playlist = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Playlist playlist = crearPlaylistDesdeResultSet(rs);
                    cargarComponentes(playlist);
                    return playlist;
                }
            }
        }

        return null;
    }

    public List<Playlist> buscarPlaylistPorNombre(String nombre) throws SQLException {
        List<Playlist> playlists = new ArrayList<>();
        String sql = "SELECT * FROM playlists WHERE titulo_playlist LIKE ? ORDER BY titulo_playlist";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + nombre + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Playlist playlist = crearPlaylistDesdeResultSet(rs);
                    cargarComponentes(playlist);
                    playlists.add(playlist);
                }
            }
        }

        return playlists;
    }

    public List<Playlist> obtenerPlaylistPorUsuario(Perfil usuario) throws SQLException {
        List<Playlist> playlists = new ArrayList<>();
        String sql = "SELECT * FROM playlists WHERE propietario_correo = ? ORDER BY fecha_creacion DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getCorreo());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Playlist playlist = crearPlaylistDesdeResultSet(rs);
                    cargarComponentes(playlist);
                    playlists.add(playlist);
                }
            }
        }

        return playlists;
    }

    private Playlist crearPlaylistDesdeResultSet(ResultSet rs) throws SQLException {
        // Crear un perfil temporal (en implementación real vendría del módulo correspondiente)
        Perfil propietario = new Perfil("Usuario", rs.getString("propietario_correo"));

        Playlist playlist = new Playlist(
                rs.getInt("id_playlist"),
                rs.getString("titulo_playlist"),
                rs.getString("descripcion"),
                propietario,
                rs.getTimestamp("fecha_creacion"),
                rs.getTimestamp("fecha_modificacion")
        );

        return playlist;
    }

    private void cargarComponentes(Playlist playlist) throws SQLException {
        String sql = """
            SELECT id_cancion, orden_elemento, fecha_agregado 
            FROM playlist_elementos 
            WHERE id_playlist = ? 
            ORDER BY orden_elemento
        """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, playlist.getIdPlaylist());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Crear canción temporal (en implementación real vendría del módulo correspondiente)
                    Cancion cancion = new Cancion(
                            rs.getInt("id_cancion"),
                            "Canción " + rs.getInt("id_cancion"),
                            3.5
                    );

                    ElementoCancion elemento = new ElementoCancion(cancion, rs.getInt("orden_elemento"));
                    playlist.agregar(elemento);
                }
            }
        }
    }
}