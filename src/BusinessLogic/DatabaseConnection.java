package BusinessLogic;

import java.sql.*;

//Creando tablas por separado porque no tengo idea como piensan manejar eso el curso
class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:playlist.db";
    private static DatabaseConnection instance;

    private DatabaseConnection() {}

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public void crearTablas() {
        String sqlPlaylist = """
            CREATE TABLE IF NOT EXISTS playlists (
                id_playlist INTEGER PRIMARY KEY AUTOINCREMENT,
                titulo_playlist TEXT NOT NULL,
                descripcion TEXT,
                fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
                fecha_modificacion DATETIME DEFAULT CURRENT_TIMESTAMP,
                propietario_correo TEXT NOT NULL
            )
        """;

        String sqlElementos = """
            CREATE TABLE IF NOT EXISTS playlist_elementos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_playlist INTEGER NOT NULL,
                id_cancion INTEGER NOT NULL,
                orden_elemento INTEGER DEFAULT 0,
                fecha_agregado DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (id_playlist) REFERENCES playlists(id_playlist) ON DELETE CASCADE
            )
        """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlPlaylist);
            stmt.execute(sqlElementos);

            System.out.println("Tablas creadas exitosamente");

        } catch (SQLException e) {
            System.err.println("Error creando tablas: " + e.getMessage());
        }
    }
}